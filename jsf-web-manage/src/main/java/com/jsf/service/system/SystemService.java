package com.jsf.service.system;

import com.github.pagehelper.PageInfo;
import com.jsf.database.model.manage.Admin;
import com.jsf.database.model.manage.Log;
import com.jsf.mapper.LogMapper;
import com.jsf.system.conf.IConstant;
import com.jsf.system.db.DbSQLExecutor;
import com.jsf.utils.date.DateUtil;
import com.jsf.utils.entity.ResMsg;
import com.jsf.utils.file.ZipUtil;
import com.jsf.utils.generate.GenInfo;
import com.jsf.utils.generate.GenerateBeansAndMybatisUtil;
import com.jsf.utils.generate.GenerateDict;
import com.jsf.utils.string.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 系统Service
 *
 * @author rick
 */
@Service
public class SystemService {

    @Resource
    private LogMapper logMapper;

    @Resource
    private DbSQLExecutor dbSQLExecutor;

    /**
     * 管理操作日志
     *
     * @param request
     * @param remark
     * @param params
     */
    public void addAdminLog(HttpServletRequest request, String remark, String params) {
        Object object = WebUtils.getSessionAttribute(request, IConstant.SESSION_ADMIN);
        Admin admin = null;
        if (object != null) {
            admin = (Admin) object;
        } else {
            return;
        }
        this.addLog(admin.getLoginName(), remark, request.getRemoteAddr(), params);
    }


    /**
     * 新增日志
     *
     * @param operator
     * @param remark
     * @param ip
     * @param params
     * @return
     */
    private int addLog(String operator, String remark, String ip, String params) {
        Log log = new Log();
        log.setOperator(operator);
        log.setRemark(remark);
        log.setIp(ip);
        log.setParams(params);
        return logMapper.insert(log);
    }

    /**
     * 分页查询日志
     *
     * @param condition
     * @return
     */
    public PageInfo findLogByPage(Log condition) {
        condition.setPageSort("id DESC");
        condition.setPage(true);
        List<Log> list = logMapper.findByCondition(condition);
        return new PageInfo(list);
    }

    /**
     * 查询日志总数
     *
     * @return
     */
    public int findLogCount() {
        return logMapper.findCountAll();
    }

    /**
     * 查询旧日志
     *
     * @param monthAgo
     * @return
     */
    public List<Map<String, Object>> findOldLog(Integer monthAgo) {
        return logMapper.findOldLog(monthAgo);
    }

    /**
     * 删除旧日志
     *
     * @param monthAgo
     * @return
     */
    public int deleteOldLog(Integer monthAgo) {
        return logMapper.deleteOldLog(monthAgo);
    }

    /**************************************************/

    /**
     * 动态读取数据记录
     *
     * @param sql 查询语句
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Object[] executeQuery(String sql) {
        List<String> columnList = new ArrayList<String>(); // 存放字段名
        List<List<Object>> dataList = new ArrayList<List<Object>>(); // 存放数据(从数据库读出来的一条条的数据)
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = dbSQLExecutor.getConn();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            columnList = dbSQLExecutor.getFieldLsit(conn, sql);
            while (rs.next()) {
                List<Object> onedataList = new ArrayList<Object>(); // 存放每条记录里面每个字段的值
                for (int i = 1; i < columnList.size() + 1; i++) {
                    onedataList.add(rs.getObject(i));
                }
                dataList.add(onedataList); // 把每条记录放list中
            }
            Object[] arrOb = {columnList, dataList};
            return arrOb;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException throwables) {
            }
        }
        return new Object[]{};
    }

    /**
     * 执行 INSERT、UPDATE 或 DELETE
     *
     * @param sql 语句
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void executeUpdate(String sql) {
        Statement stmt = null;
        Connection conn = null;
        try {
            conn = dbSQLExecutor.getConn();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException throwables) {
            }
        }
    }

    /**
     * 获取数据库表
     *
     * @param dbType
     * @param jdbc
     * @param extra
     * @return
     */
    public List<String> getTables(String dbType, String jdbc, String extra) {
        String sql = "";
        PreparedStatement stmt = null;
        Connection conn = null;

        try {
            // 如果连接串为空，使用本地库
            if (StringUtil.isBlank(jdbc)) {
                conn = dbSQLExecutor.getConn();
            } else {
                String[] jdbcs = jdbc.split("\\|");
                if (jdbcs.length != 3) {
                    return Collections.emptyList();
                }
                conn = dbSQLExecutor.getConn(getDriver(dbType), jdbcs[0], jdbcs[1], jdbcs[2]);
            }
            if ("mysql".equals(dbType)) {
                sql = "SHOW TABLES";
            } else if ("postgresql".equals(dbType)) {
                if (StringUtil.isBlank(extra)) {
                    extra = "public";
                }
                sql = "SELECT tablename FROM pg_tables WHERE schemaname = '" + extra + "'";
            }
            List<String> tables = new ArrayList<String>();
            stmt = conn.prepareStatement(sql);
            ResultSet results = stmt.executeQuery();
            while (results.next()) {
                String tableName = results.getString(1);
                tables.add(tableName);
            }
            return tables;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException throwables) {
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 代码生成
     *
     * @param action
     * @param tbls
     * @param dbType
     * @param jdbc
     * @param extra
     */
    public ResMsg genFromTable(String action, String tbls, String dbType, String jdbc, String extra) {
        GenInfo info = new GenInfo();
        String time = DateUtil.getCurrentTime(DateUtil.HHMMSS);
        String output = "./generate/" + time;
        String author = "jsf";

        // 如果连接串为空，使用本地库
        if (StringUtil.isBlank(jdbc)) {
            info.url(dbSQLExecutor.url)
                    .driver(dbSQLExecutor.driver)
                    .username(dbSQLExecutor.username)
                    .password(dbSQLExecutor.password)
                    .path(output)
                    .author(author)
                    .removePrefix(true);
        } else {
            String[] jdbcs = jdbc.split("\\|");
            if (jdbcs.length != 3) {
                return ResMsg.fail("数据库连接串错误");
            }
            info.url(jdbcs[0])
                    .driver(getDriver(dbType))
                    .username(jdbcs[1])
                    .password(jdbcs[2])
                    .path(output)
                    .author(author)
                    .removePrefix(true);
        }
        if ("postgresql".equals(dbType)) {
            if (StringUtil.isBlank(extra)) {
                extra = "public";
            }
            info.schema(extra);
        }
        if (StringUtil.isNotBlank(tbls)) {
            info.setTables(StringUtil.strToList(tbls));
        }
        info.setGlobalPath(output);

        // start
        new GenerateBeansAndMybatisUtil().generate(info);

        // 输出方式
        if ("root".equals(action)) {
            return ResMsg.success("已生成到项目根目录");
        } else if ("zip".equals(action)) {
            // 压缩到下载路径
            String outputZip = "./upload/generate/" + time + ".zip";
            try {
                ZipUtil.zipDir(output, outputZip, true);
            } catch (Exception e) {
                throw new RuntimeException("压缩失败");
            }
            return ResMsg.success("生成成功，请稍后", outputZip.substring(1, outputZip.length()));
        } else {
            return ResMsg.fail();
        }
    }

    /**
     * 生成数据字典
     *
     * @param dbType
     * @param jdbc
     * @param extra
     * @return
     */
    public ResMsg genDict(String dbType, String jdbc, String extra) {
        GenInfo info = new GenInfo();
        String time = DateUtil.getCurrentTime(DateUtil.HHMMSS);
        String output = "./upload/generate/dict/" + time;
        String author = "jsf";

        // 如果连接串为空，使用本地库
        if (StringUtil.isBlank(jdbc)) {
            info.url(dbSQLExecutor.url)
                    .driver(dbSQLExecutor.driver)
                    .username(dbSQLExecutor.username)
                    .password(dbSQLExecutor.password)
                    .path(output)
                    .author(author)
                    .removePrefix(true);
        } else {
            String[] jdbcs = jdbc.split("\\|");
            if (jdbcs.length != 3) {
                return ResMsg.fail("数据库连接串错误");
            }
            info.url(jdbcs[0])
                    .driver(getDriver(dbType))
                    .username(jdbcs[1])
                    .password(jdbcs[2])
                    .path(output)
                    .author(author)
                    .removePrefix(true);
        }
        if ("postgresql".equals(dbType)) {
            if (StringUtil.isBlank(extra)) {
                extra = "public";
            }
            info.schema(extra);
        }
        info.path(output);

        // start
        new GenerateDict().generate(info);
        return ResMsg.success("生成成功，请稍后", output.substring(1, output.length()) + "/dict.html");
    }

    private String getDriver(String dbType) {
        if ("mysql".equals(dbType)) {
            return "com.mysql.jdbc.Driver";
        } else if ("postgresql".equals(dbType)) {
            return "org.postgresql.Driver";
        } else {
            throw new RuntimeException("不支持的数据库");
        }
    }
}
