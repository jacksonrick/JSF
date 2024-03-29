package com.jsf.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.github.pagehelper.PageInfo;
import com.jsf.base.BaseController;
import com.jsf.controller.view.ViewExcel;
import com.jsf.database.enums.ResCode;
import com.jsf.database.model.custom.IdText;
import com.jsf.database.model.manage.*;
import com.jsf.service.system.*;
import com.jsf.system.conf.IConstant;
import com.jsf.system.conf.SysConfigStatic;
import com.jsf.system.db.DataConfig;
import com.jsf.utils.annotation.AuthPassport;
import com.jsf.utils.bean.BeanUtil;
import com.jsf.utils.date.DateUtil;
import com.jsf.utils.entity.ResMsg;
import com.jsf.utils.entity.Tree;
import com.jsf.utils.exception.SysException;
import com.jsf.utils.entity.Directory;
import com.jsf.utils.file.FileUtil;
import com.jsf.utils.http.HttpUtil;
import com.jsf.utils.json.JacksonUtil;
import com.jsf.utils.math.Convert;
import com.jsf.utils.string.StringUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统管理模块
 *
 * @author rick
 * @version 2.0
 */
@Controller
@RequestMapping("/admin/system")
public class SystemBackController extends BaseController {

    @Resource
    private AdminService adminService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private SystemService systemService;
    @Resource
    private AddrService addrService;
    @Resource
    private ConfigService configService;

    @RequestMapping("/file")
    @AuthPassport
    public String file() {
        return "system/file";
    }

    @RequestMapping("/icons")
    @AuthPassport(right = false)
    public String icons() {
        return "system/icons";
    }

    @RequestMapping("/tools")
    @AuthPassport(right = false)
    public String formBuilder() {
        return "system/tools";
    }

    @RequestMapping("/jenkins")
    @AuthPassport(right = false)
    public String jenkins(HttpServletRequest request) {
        Admin admin = getSession(request, IConstant.SESSION_ADMIN);
        if (admin == null || admin.getRole().getFlag() != 0) {
            return "error/refuse";
        }
        return "system/jenkins";
    }

    /**
     * jenkins自动化部署
     *
     * @param ip
     * @param type
     * @param auth
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/jenkins")
    @ResponseBody
    @AuthPassport(right = false)
    public ResMsg jenkins(String ip, String type, String auth, HttpServletRequest request) throws Exception {
        if (StringUtil.isBlank(ip) || StringUtil.isBlank(type) || StringUtil.isBlank(auth)) {
            return new ResMsg(1, "invalid param");
        }
        Admin admin = getSession(request, IConstant.SESSION_ADMIN);
        if (admin == null || admin.getRole().getFlag() != 0) {
            return new ResMsg(1, "refuse if not superadmin");
        }

        String json = "";
        if ("post".equals(type)) {
            json = HttpUtil.postWithAuthorization(ip, null, auth);
        } else if ("get".equals(type)) {
            json = HttpUtil.getWithAuthorization(ip, auth);
        }
        if (json != null && json.startsWith("<html>")) {
            return new ResMsg(ResCode.REFUSE.code(), ResCode.REFUSE.msg(), json);
        }
        if (HttpUtil.STR_ERROR.equals(json)) {
            return new ResMsg(ResCode.ERROR.code(), ResCode.ERROR.msg());
        }
        if (HttpUtil.STR_TIMEOUT.equals(json)) {
            return new ResMsg(ResCode.TIMEOUT.code(), ResCode.TIMEOUT.msg());
        }
        return new ResMsg(ResCode.SUCCESS.code(), ResCode.SUCCESS.msg(), json);
    }

    /**
     * 管理员列表
     *
     * @param condition
     * @param map
     * @return
     */
    @RequestMapping("/adminList")
    @AuthPassport
    public String adminList(Admin condition, ModelMap map) {
        PageInfo pageInfo = adminService.findAdminByPage(condition);
        map.addAttribute("pageInfo", pageInfo);
        map.addAllAttributes(BeanUtil.beanToMap(condition));

        List<Role> roles = moduleService.findRoleList();
        map.addAttribute("roles", roles);
        return "system/admin_list";
    }

    /**
     * 管理员详细
     *
     * @param adminId
     * @param map
     * @return
     */
    @RequestMapping("/adminDetail")
    @AuthPassport(right = false)
    public String adminDetail(Integer adminId, ModelMap map) {
        List<Role> roles = moduleService.findRoleList();
        map.addAttribute("roles", roles);
        if (adminId == null) {
            map.addAttribute("add", true);
        } else {
            Admin admin = adminService.findAdminById(adminId);
            map.addAttribute("adm", admin);
        }
        return "system/admin_edit";
    }

    /**
     * 启用和禁用管理员
     *
     * @param adminId
     * @return
     */
    @RequestMapping("/adminEnable")
    @AuthPassport
    @ResponseBody
    public ResMsg adminEnable(Integer adminId, HttpServletRequest request) {
        if (adminId == null) {
            return new ResMsg(ResCode.CODE_22.code(), ResCode.CODE_22.msg());
        }
        if (adminService.deleteAdmin(adminId) > 0) {
            systemService.addAdminLog(request, "禁用/启用管理员", "id=" + adminId);
            return new ResMsg(ResCode.OPERATE_SUCCESS.code(), ResCode.OPERATE_SUCCESS.msg());
        }
        return new ResMsg(ResCode.OPERATE_FAIL.code(), ResCode.OPERATE_FAIL.msg());
    }

    /**
     * 管理员编辑
     *
     * @param admin
     * @param br
     * @return
     */
    @RequestMapping("/adminEdit")
    @AuthPassport
    @ResponseBody
    public ResMsg adminEdit(@Valid Admin admin, BindingResult br, HttpServletRequest request) {
        if (br.hasErrors()) {
            return new ResMsg(1, br.getFieldError().getDefaultMessage());
        }
        if (admin.getId() == null) {
            if (StringUtil.isBlank(admin.getPassword())) {
                return new ResMsg(5, "新增用户密码不能为空");
            }
            if (adminService.findAdminCountByName(admin.getLoginName()) > 0) {
                return new ResMsg(6, "用户名已存在");
            }
            adminService.insertAdmin(admin);
            systemService.addAdminLog(request, "新增管理员", "username=" + admin.getLoginName());
            return new ResMsg(ResCode.INSERT_SUCCESS.code(), ResCode.INSERT_SUCCESS.msg());
        } else {
            adminService.updateAdmin(admin);
            systemService.addAdminLog(request, "更新管理员", "username=" + admin.getLoginName());
            return new ResMsg(ResCode.UPDATE_SUCCESS.code(), ResCode.UPDATE_SUCCESS.msg());
        }
    }

    /**
     * 模块管理页
     *
     * @return
     */
    @RequestMapping("/module")
    @AuthPassport
    public String module() {
        return "system/module";
    }

    /**
     * @return
     */
    @RequestMapping("/getAllModule")
    @ResponseBody
    @AuthPassport(right = false)
    public List<Map<String, Object>> getAllModule() {
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        List<Module> list = moduleService.findModuleAll(); //所有模块
        for (int i = 0; i < list.size(); i++) {
            Module m = list.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", m.getId());
            map.put("pId", m.getParentId());
            map.put("name", m.getName());
            map.put("flag", m.getFlag());
            map.put("action", m.getAction());
            map.put("icon", m.getIconName());
            map.put("sort", m.getSort());
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 模块编辑
     *
     * @return
     */
    @RequestMapping("/moduleEdit")
    @ResponseBody
    @AuthPassport
    public ResMsg moduleEdit(Integer moduleId, Integer parentId, Integer flag, String name, String action, String icon, Integer sort,
                             HttpServletRequest request) {
        if (StringUtil.isBlank(name)) {
            return new ResMsg(1, "模块名不能为空");
        }
        if (StringUtil.isBlank(action)) {
            return new ResMsg(2, "Action不能为空");
        }
        if (moduleId == null) {
            if (parentId == null) {
                return new ResMsg(3, "请选择父模块");
            }
            if (moduleService.insertModule(flag, name, parentId, action, icon, sort) > 0) {
                systemService.addAdminLog(request, "新增模块", "name=" + name);
                return new ResMsg(ResCode.INSERT_SUCCESS.code(), ResCode.INSERT_SUCCESS.msg());
            }
            return new ResMsg(ResCode.INSERT_FAIL.code(), ResCode.INSERT_FAIL.msg());
        } else {
            if (moduleService.updateModule(moduleId, name, action, icon, sort) > 0) {
                systemService.addAdminLog(request, "编辑模块", "name=" + name);
                return new ResMsg(ResCode.UPDATE_SUCCESS.code(), ResCode.UPDATE_SUCCESS.msg());
            }
            return new ResMsg(ResCode.UPDATE_FAIL.code(), ResCode.UPDATE_FAIL.msg());
        }
    }

    /**
     * 删除模块
     *
     * @param moduleId
     * @return
     */
    @RequestMapping("/moduleDel")
    @ResponseBody
    @AuthPassport
    public ResMsg moduleDel(Integer moduleId) {
        if (moduleId == null) {
            return new ResMsg(ResCode.CODE_22.code(), ResCode.CODE_22.msg());
        }
        if (moduleService.deleteModule(moduleId) > 0) {
            return new ResMsg(ResCode.DELETE_SUCCESS.code(), ResCode.DELETE_SUCCESS.msg());
        }
        return new ResMsg(ResCode.DELETE_FAIL.code(), ResCode.DELETE_FAIL.msg());
    }

    /**
     * 权限管理
     *
     * @param map
     * @return
     */
    @RequestMapping("/rights")
    @AuthPassport
    public String rights(ModelMap map) {
        List<Role> list = moduleService.findRoleList();
        map.addAttribute("roles", list);
        return "system/rights";
    }

    /**
     * 组禁用或启用
     *
     * @param roleId
     * @return
     */
    @RequestMapping("/roleEnable")
    @AuthPassport
    @ResponseBody
    public ResMsg roleEnable(Integer roleId, HttpServletRequest request) {
        if (roleId == null) {
            return new ResMsg(ResCode.CODE_22.code(), ResCode.CODE_22.msg());
        }
        if (moduleService.deleteRole(roleId) > 0) {
            systemService.addAdminLog(request, "组禁用或启用", "roleId=" + roleId);
            return new ResMsg(ResCode.OPERATE_SUCCESS.code(), ResCode.OPERATE_SUCCESS.msg());
        }
        return new ResMsg(ResCode.OPERATE_FAIL.code(), ResCode.OPERATE_FAIL.msg());
    }

    /**
     * 组编辑
     *
     * @param roleName
     * @param roleId
     * @return
     */
    @RequestMapping("/roleEdit")
    @AuthPassport
    @ResponseBody
    public ResMsg roleEdit(String roleName, Integer roleId, HttpServletRequest request) {
        if (StringUtil.isBlank(roleName)) {
            return new ResMsg(1, "组名不能为空");
        }
        if (roleId == null) {
            moduleService.insertRole(roleName);
            systemService.addAdminLog(request, "新增组", "roleName=" + roleName);
            return new ResMsg(ResCode.INSERT_SUCCESS.code(), ResCode.INSERT_SUCCESS.msg());
        }
        moduleService.updateRole(roleId, roleName);
        systemService.addAdminLog(request, "编辑组", "roleName=" + roleName);
        return new ResMsg(ResCode.UPDATE_SUCCESS.code(), ResCode.UPDATE_SUCCESS.msg());
    }

    /**
     * 获取权限(组或用户)
     *
     * @param roleId
     * @param adminId
     * @return
     */
    @RequestMapping("/permits")
    @AuthPassport
    @ResponseBody
    public List<Tree> permits(Integer roleId, Integer adminId) {
        // ztree data
        List<Tree> treeList = new ArrayList<Tree>();
        // 所有模块
        List<Module> list = moduleService.findModuleAll();
        List<Module> mods = moduleService.findModuleByRoleOrAdmin(roleId, adminId);

        for (int i = 0, j = list.size(); i < j; i++) {
            Module m = list.get(i);
            // 组织tree
            Tree tree = new Tree();
            tree.setId(m.getId());
            tree.setpId(m.getParentId());
            tree.setName(m.getName());
            if (mods == null || mods.isEmpty()) {
                treeList.add(tree);
                continue;
            }
            for (Module roleMenu : mods) { // 匹配
                // System.out.println(roleMenu.getId()+"=="+m.getId()+"?"+(roleMenu.getId().equals(m.getId())));
                if (roleMenu.getId().equals(m.getId())) {
                    tree.setChecked(true); // 选中状态
                    break;
                }
            }
            treeList.add(tree);
        }
        return treeList;
    }

    /**
     * 授权(组或用户)
     *
     * @param roleId
     * @param adminId
     * @param params
     * @param request
     * @return
     */
    @RequestMapping("/permit")
    @AuthPassport
    @ResponseBody
    public ResMsg permit(Integer roleId, Integer adminId, String params, HttpServletRequest request) {
        if (roleId == null && adminId == null) {
            return new ResMsg(ResCode.CODE_22.code(), ResCode.CODE_22.msg());
        }

        // 模块id集合
        String[] rights = params.split(",");
        Integer[] mid = new Integer[rights.length];
        for (int i = 0, b = rights.length; i < b; i++) {
            mid[i] = Integer.parseInt(rights[i]);
        }

        if (roleId != null) {
            if (StringUtil.isBlank(params)) {
                moduleService.deleteRights(roleId);
                return new ResMsg(0, "已取消组所有权限");
            }
            moduleService.permitRole(roleId, rights);
            systemService.addAdminLog(request, "授权组", "roleId=" + roleId);
        }

        if (adminId != null) {
            if (StringUtil.isBlank(params)) {
                adminService.deleteRights(adminId);
                return new ResMsg(0, "已取消用户所有权限");
            }
            moduleService.permitAdmin(adminId, rights);
            systemService.addAdminLog(request, "授权用户", "adminId=" + adminId);
        }

        return new ResMsg(0, "授权成功");
    }

    @RequestMapping("/address")
    @AuthPassport
    public String address() {
        return "system/address";
    }

    /**
     * 获取地址
     *
     * @param id
     * @return
     */
    @RequestMapping("/getAddr")
    @AuthPassport(right = false)
    @ResponseBody
    public List<Map<String, Object>> getAddr(Integer id) {
        Address condition = new Address();
        if (id == null) {
            condition.setLevel(1);
        }
        condition.setParent(id);
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        List<Address> list = addrService.findAddrList(condition);
        for (int i = 0; i < list.size(); i++) {
            Address address = list.get(i);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", address.getId());
            map.put("pId", address.getParent());
            map.put("name", address.getName());
            map.put("level", address.getLevel());
            if (address.getLevel() != 4) {
                map.put("isParent", true);
            }
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 地址编辑
     *
     * @param id
     * @param name
     * @param parent
     * @param level
     * @return
     */
    @RequestMapping("/addrEdit")
    @ResponseBody
    @AuthPassport
    public ResMsg addrEdit(Integer id, String name, Integer parent, Integer level, HttpServletRequest request) {
        if (id == null) { //新增
            Address address = new Address();
            address.setName(name);
            address.setParent(parent);
            address.setLevel(level + 1);
            if (addrService.insert(address) > 0) {
                return new ResMsg(ResCode.INSERT_SUCCESS.code(), ResCode.INSERT_SUCCESS.msg());
            }
            return new ResMsg(ResCode.INSERT_FAIL.code(), ResCode.INSERT_FAIL.msg());
        }
        //更新
        Address address = new Address(id);
        address.setParent(parent);
        address.setName(name);
        if (addrService.update(address) > 0) {
            systemService.addAdminLog(request, "编辑地址", "name=" + name);
            return new ResMsg(ResCode.UPDATE_SUCCESS.code(), ResCode.UPDATE_SUCCESS.msg());
        }
        return new ResMsg(ResCode.UPDATE_FAIL.code(), ResCode.UPDATE_FAIL.msg());
    }

    /**
     * @param id
     * @return
     */
    @RequestMapping("/addrDel")
    @ResponseBody
    @AuthPassport
    public ResMsg addrDel(Integer id) {
        if (id == null) {
            return new ResMsg(ResCode.CODE_22.code(), ResCode.CODE_22.msg());
        }
        if (addrService.delete(id) > 0) {
            return new ResMsg(ResCode.DELETE_SUCCESS.code(), ResCode.DELETE_SUCCESS.msg());
        }
        return new ResMsg(ResCode.DELETE_FAIL.code(), ResCode.DELETE_FAIL.msg());
    }

    /**
     * 下载地址JS文件
     *
     * @param response
     */
    @RequestMapping("/addrGenc")
    @AuthPassport(right = false)
    public void addrGenc(HttpServletResponse response) {
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=city-picker.data.all.js");
        String addr = addrService.genAddr();
        try {
            OutputStream os = response.getOutputStream();
            os.write(addr.getBytes(Charset.forName("UTF-8")));
            os.close();
        } catch (Exception e) {
            throw new SysException(e.getMessage(), e);
        }
    }

    /**
     * 获取日志目录（近30天）
     *
     * @return
     */
    /*@Deprecated
    @RequestMapping("/getLogList")
    @AuthPassport
    @ResponseBody
    public Object getLogList() {
        List<Map<String, String>> list = FileUtil.getLogFileList(config.getLogPath());
        return list;
    }*/

    /**
     * 系统日志列表
     *
     * @param condition
     * @param map
     * @return
     */
    @RequestMapping("/syslogList")
    @AuthPassport
    public String syslogList(ModelMap map) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        String level = loggerContext.getLogger("com.jsf.database.mapper").getLevel().toString();
        map.addAttribute("level", level);
        return "system/syslog";
    }

    @Value("${logging.path}")
    private String logPath;

    /**
     * 获取系统日志目录
     *
     * @param path
     * @return
     */
    @RequestMapping("/getLogDirectory")
    @AuthPassport
    @ResponseBody
    public ResMsg getLogDirectory(String path) {
        if (StringUtil.isBlank(path)) {
            return new ResMsg(1, "未指定路径", null);
        }
        List<Directory> list = FileUtil.getDirectory(path, logPath);
        if (list == null) {
            return new ResMsg(3, "路径不存在", null);
        }
        return new ResMsg(ResCode.SUCCESS.code(), ResCode.SUCCESS.msg(), list);
    }

    /**
     * 读取系统日志
     * <p>流式输出</p>
     *
     * @param response
     * @param request
     * @return
     */
    @RequestMapping("/downLog")
    @AuthPassport
    public void downLog(HttpServletResponse response, HttpServletRequest request) {
        String fileName = request.getParameter("fileName");
        if (fileName == null || "".equals(fileName)) {
            return;
        }
        try {
            InputStream inputStream = new FileInputStream(new File(logPath + fileName));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream os = response.getOutputStream();

            os.write("<div id='main' style='font-family: Consolas;font-size: 14px;'>".getBytes("UTF-8"));
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                os.write(("<p>" + str + "</p>").getBytes());
            }
            os.write("</div><script src=\"/static/library/plugins/ansi/ansi_up.js\" type=\"text/javascript\"></script><script type=\"text/javascript\">var txt  = document.getElementById('main').innerHTML;var ansi_up = new AnsiUp;ansi_up.escape_for_html = false;var html = ansi_up.ansi_to_html(txt);var cdiv = document.getElementById('main');cdiv.innerHTML = html;</script>".getBytes("UTF-8"));

            os.close();
            inputStream.close();
            bufferedReader.close();
        } catch (Exception e) {
            throw new SysException(e.getMessage(), e);
        }
        return;
    }

    /**
     * 打印SQL开关
     *
     * @param level
     * @param request
     * @return
     */
    @RequestMapping("/sqlLevel")
    @AuthPassport
    @ResponseBody
    public ResMsg sqlLevel(String level, HttpServletRequest request) {
        if (StringUtil.isBlank(level)) {
            return new ResMsg(1, "level is null");
        }
        if (!"INFO".equals(level) && !"DEBUG".equals(level)) {
            return new ResMsg(1, "level must be INFO or DEBUG");
        }
        Admin admin = getSession(request, IConstant.SESSION_ADMIN);
        if (admin == null || admin.getRole().getFlag() != 0) {
            return new ResMsg(1, "refuse if superadmin");
        }

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(DataConfig.mapperPackage).setLevel(Level.valueOf(level));
        loggerContext.getLogger(DataConfig.mapperOtherPackage).setLevel(Level.valueOf(level));

        return new ResMsg(ResCode.SUCCESS.code(), ResCode.SUCCESS.msg());
    }

    /**
     * 操作日志列表
     *
     * @param condition
     * @param map
     * @return
     */
    @RequestMapping("/logList")
    @AuthPassport
    public String logList(Log condition, ModelMap map) {
        PageInfo pageInfo = systemService.findLogByPage(condition);
        map.addAttribute("pageInfo", pageInfo);
        map.addAllAttributes(BeanUtil.beanToMap(condition));
        map.put("logCount", systemService.findLogCount());
        return "system/log";
    }

    /**
     * 操作日志备份
     *
     * @param monthAgo
     * @return
     */
    @RequestMapping("/backupLog")
    @AuthPassport
    public ModelAndView backupLog(String monthAgo) {
        Integer ago = Convert.stringToInt(monthAgo, 1);
        Map<String, Object> model = null;
        try {
            model = new HashMap<String, Object>();
            model.put("list", systemService.findOldLog(ago));
            model.put("header", new String[]{"username", "remark", "params", "createDate"});
            model.put("headerVal", new String[]{"用户名", "备注", "参数", "时间"});
            model.put("ename", "操作日志备份" + DateUtil.getCurrentTime(2));
        } catch (Exception e) {
            return null;
        }
        systemService.deleteOldLog(ago);
        return new ModelAndView(new ViewExcel(), model);
    }

    /**
     * 获取服务器静态文件目录
     *
     * @return
     */
    @RequestMapping("/getDirectory")
    @AuthPassport
    @ResponseBody
    public ResMsg getDirectory(String path) {
        if (StringUtil.isBlank(path)) {
            return new ResMsg(1, "未指定路径");
        }
        // 文件管理只查看站点上传目录
        List<Directory> list = FileUtil.getDirectory(path, SysConfigStatic.upload.getFilePath() + "/upload/");
        if (list == null) {
            return new ResMsg(2, "路径不存在");
        }
        return new ResMsg(ResCode.SUCCESS.code(), ResCode.SUCCESS.msg(), list);
    }

    /**
     * 系统配置页
     *
     * @return
     */
    @RequestMapping("/sysConfig")
    @AuthPassport
    public String sysConfig(ModelMap map) {
        map.addAttribute("configs", configService.findConfigMapList());
        return "system/sys_conf";
    }

    /**
     * 更新系统配置
     *
     * @param json
     * @return
     */
    @RequestMapping("/sysConfigEdit")
    @ResponseBody
    @AuthPassport
    public ResMsg sysConfigEdit(String json) {
        if (StringUtil.isBlank(json)) {
            return ResMsg.fail("配置项为空");
        }
        List<IdText> configs = JacksonUtil.jsonToList(json, IdText.class);
        if (configs == null || configs.isEmpty()) {
            return ResMsg.fail("配置项为空");
        }
        configService.updateConfig(configs);
        return ResMsg.success("更新成功");
    }

    /**
     * 刷新配置
     * 如果从数据库修改了配置，需要点击按钮手动刷新到内存
     *
     * @return
     */
    @RequestMapping("/sysConfigRefresh")
    @ResponseBody
    @AuthPassport(right = false)
    public ResMsg sysConfigRefresh() {
        configService.refreshConfig();
        return ResMsg.success("刷新成功");
    }


    /**
     * 执行查询操作
     *
     * @param session
     * @param sql
     * @return
     */
    @RequestMapping("/executeQuery")
    @AuthPassport
    @ResponseBody
    public Map<String, Object> executeQuery(String sql, HttpSession session, HttpServletRequest request) {
        ResMsg msg = null;
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtil.isBlank(sql)) {
            msg = new ResMsg(1, "sql不能为空");
            map.put("msg", msg);
            return map;
        }
        if (!sql.startsWith("select") && !sql.startsWith("SELECT")) {
            msg = new ResMsg(2, "sql非查询语句");
            map.put("msg", msg);
            return map;
        }
        // 列名集合
        List<String> columnList = new ArrayList<String>();
        // 数据集合
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        long startTime = System.currentTimeMillis();
        Object[] arrOb = systemService.executeQuery(sql);
        long endTime = System.currentTimeMillis();
        // 存入数据库查询时间
        map.put("rTime", String.valueOf((endTime - startTime) / 1000.000));
        if (null != arrOb) {
            columnList = (List<String>) arrOb[0];
            dataList = (List<List<Object>>) arrOb[1];
            msg = new ResMsg(0, "success");
        } else {
            msg = new ResMsg(3, "fail");
        }

        // 存放字段名
        map.put("columnList", columnList);
        // 存放数据(从数据库读出来的数据)
        map.put("dataList", dataList);
        map.put("msg", msg);
        systemService.addAdminLog(request, "执行查询SQL", "sql=" + sql);
        return map;
    }

    /**
     * 执行更新操作
     *
     * @param session
     * @param sql
     * @return
     */
    @RequestMapping("/executeUpdate")
    @AuthPassport
    @ResponseBody
    public Object executeUpdate(String sql, HttpSession session, HttpServletRequest request) {
        ResMsg msg = null;
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtil.isBlank(sql)) {
            msg = new ResMsg(1, "sql不能为空");
            map.put("msg", msg);
            return map;
        }
        if (!sql.contains("update") && !sql.contains("delete") && !sql.contains("insert")) {
            msg = new ResMsg(2, "sql非更新语句");
            map.put("msg", msg);
            return map;
        }
        long startTime = System.currentTimeMillis();
        systemService.executeUpdate(sql);
        msg = new ResMsg(0, "success");
        long endTime = System.currentTimeMillis();

        map.put("rTime", String.valueOf((endTime - startTime) / 1000.000));
        map.put("msg", msg);
        systemService.addAdminLog(request, "执行更新SQL", "sql=" + sql);
        return map;
    }

    /**
     * 代码生成页
     *
     * @return
     */
    @RequestMapping("/codeGen")
    @AuthPassport(right = false)
    public String codeGen() {
        return "system/code_gen";
    }

    /**
     * 获取数据库表
     *
     * @param dbType 数据库类型 mysql|postgresql
     * @param jdbcs  可以支持其他外部数据库
     * @param extra  额外信息
     * @return
     */
    @RequestMapping("/getTables")
    @AuthPassport(right = false)
    @ResponseBody
    public ResMsg getTables(String dbType, String jdbcs, String extra) {
        if (StringUtil.isBlank(dbType)) {
            return ResMsg.fail("请选择数据库类型");
        }
        try {
            List<String> tables = systemService.getTables(dbType, jdbcs, extra);
            return ResMsg.successdata(tables);
        } catch (Exception e) {
            return ResMsg.fail(1, e.getMessage());
        }
    }

    /**
     * 代码生成
     *
     * @param action root|zip|dict
     * @param tbls   空，生成全部
     * @return
     */
    @RequestMapping("/genFromTable")
    @AuthPassport(right = false)
    @ResponseBody
    public ResMsg genFromTable(String action, String tbls, String dbType, String jdbcs, String extra) {
        if (StringUtil.isBlank(dbType) || StringUtil.isBlank(action)) {
            return ResMsg.fail("参数不完整");
        }
        if ("dict".equals(action)) {
            return systemService.genDict(dbType, jdbcs, extra);
        } else {
            return systemService.genFromTable(action, tbls, dbType, jdbcs, extra);
        }
    }

}
