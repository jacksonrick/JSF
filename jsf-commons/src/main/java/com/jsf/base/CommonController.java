package com.jsf.base;

import com.jsf.system.conf.IConstant;
import com.jsf.system.conf.SysConfig;
import com.jsf.utils.date.DateUtil;
import com.jsf.utils.entity.CaptchaConfig;
import com.jsf.utils.entity.UploadRet;
import com.jsf.utils.file.CaptchaUtil;
import com.jsf.utils.sdk.fdfs.domain.StorePath;
import com.jsf.utils.sdk.fdfs.service.AppendFileStorageClient;
import com.jsf.utils.sdk.fdfs.service.FastFileStorageClient;
import com.jsf.utils.string.StringUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 通用工具类控制器
 * <p> 验证码、编辑器、文件上传 </p>
 *
 * @author rick
 * @version 2.0
 */
@Controller
public class CommonController {

    @Autowired(required = false)
    private FastFileStorageClient storageClient;
    @Autowired(required = false)
    private AppendFileStorageClient appendFileStorageClient;
    @Value("${fdfs.nginx}")
    private String fdfsNginx;

    /**
     * 错误页面
     *
     * @return
     */
    @GetMapping("/error/{path}")
    public String error(@PathVariable("path") String path) {
        return "error/" + path;
    }

    /**
     * 获取验证码
     * <p> sessionName:Constant.SESSION_RAND </p>
     *
     * @throws IOException
     */
    @GetMapping("/getValidCode")
    public void getValidCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CaptchaConfig config = new CaptchaConfig(6, 100, 35);
        char[] chars = CaptchaUtil.getTextChars(config);
        request.getSession().setAttribute(IConstant.SESSION_RAND, new String(chars));
        // 输出流方式的验证码，需要先获取文本，再输出
        CaptchaUtil.toStream(chars, response.getOutputStream(), config);
    }

    /**
     * 获取base64验证码
     *
     * @param request
     * @return
     */
    @GetMapping("/getValidCodeStr")
    @ResponseBody
    public String getValidCodeStr(HttpServletRequest request) {
        // 默认配置：CaptchaUtil.DEFAULT_CONFIG
        ImmutablePair<String, String> pair = CaptchaUtil.toBase64();
        // pair.getKey() 验证码明文，可以存放在数据库如redis
        request.getSession().setAttribute(IConstant.SESSION_RAND, pair.getKey());
        return pair.getValue();
    }

    /**
     * kindEditor上传图片
     *
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadwithKE")
    @ResponseBody
    public UploadRet uploadwithKE(@RequestParam("imgFile") MultipartFile file, HttpServletRequest request) throws IOException {
        // 文件限制
        long imgSize = SysConfig.getInt("upload.imgSize") * 1024 * 1024;
        if (file.getSize() > imgSize) {
            return new UploadRet(1, "", "单个文件最大" + SysConfig.getInt("upload.imgSize") + "M");
        }
        // 文件后缀
        String suffix = StringUtil.getFileType(file.getOriginalFilename());
        if (!SysConfig.getList("upload.imgType").contains(suffix.toLowerCase())) {
            return new UploadRet(1, "", "文件格式不支持");
        }

        if (!SysConfig.getBoolean("upload.fdfs")) {
            String basePathFormat = DateUtil.getYearAndMonth(false);
            String filename = StringUtil.randomFilename(file.getOriginalFilename());
            File filePath = new File("upload/" + basePathFormat);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(filePath, filename));
            return new UploadRet(0, SysConfig.get("sys.uploadHost") + "/upload/" + basePathFormat + "/" + filename, "SUCCESS");
        } else {
            // 添加水印：FDFSUtil.waterMark(file.getInputStream(), suffix)
            StorePath storePath = storageClient.uploadFile(file.getBytes(), suffix);
            String filePath = fdfsNginx + storePath.getFullPath();
            return new UploadRet(0, filePath, "SUCCESS");
        }
    }

    /**
     * kindEditor从剪切板上传
     *
     * @param editor
     * @return
     */
    @PostMapping("/uploadClipboardwithKE")
    @ResponseBody
    public String uploadClipboardwithKE(String editor) throws Exception {
        if (StringUtil.isBlank(editor)) {
            return "";
        }
        editor = editor.replace("<img src=\"data:image/png;base64,", "").replace("\" alt=\"\" />", "");

        BASE64Decoder decoder = new BASE64Decoder();
        // Base64解码
        byte[] bytes = decoder.decodeBuffer(editor);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {
                bytes[i] += 256;
            }
        }
        if (!SysConfig.getBoolean("upload.fdfs")) {
            String basePathFormat = DateUtil.getYearAndMonth(false);
            String filename = StringUtil.randomFilename() + ".png";
            File filePath = new File("upload/" + basePathFormat);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            OutputStream out = new FileOutputStream(new File(filePath, filename));
            out.write(bytes);
            out.flush();
            out.close();
            String fullname = SysConfig.get("sys.uploadHost") + "/upload/" + basePathFormat + "/" + filename;
            return "<img src=\"" + fullname + "\" alt=\"\" />";
        } else {
            StorePath storePath = storageClient.uploadFile(bytes, "png");
            String filePath = fdfsNginx + storePath.getFullPath();
            return "<img src=\"" + filePath + "\" alt=\"\" />";
        }
    }

    /**
     * Ueditor上传
     *
     * @param action
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadwithUE")
    @ResponseBody
    public Map<String, Object> uploadwithUE(String action, @RequestParam(name = "imgFile", required = false) MultipartFile file, HttpServletRequest request) throws IOException {
        Map<String, Object> map = new HashMap();
        if ("sysConfig".equals(action)) {
            map.put("imageActionName", "uploadimage");
            map.put("imageFieldName", "imgFile");
            map.put("imageAllowFiles", new String[]{".png", ".jpg", ".jpeg"});
            map.put("imageMaxSize", "2048000");
            map.put("imagePath", "");
            map.put("imageUrlPrefix", "");
            return map;
        }

        if (!SysConfig.getBoolean("upload.fdfs")) {
            String basePathFormat = DateUtil.getYearAndMonth(false);
            String filename = StringUtil.randomFilename(file.getOriginalFilename());
            File filePath = new File("upload/" + basePathFormat);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(filePath, filename));
            map.put("url", SysConfig.get("sys.uploadHost") + "/upload/" + basePathFormat + "/" + filename);
        } else {
            // 文件后缀
            String suffix = StringUtil.getFileType(file.getOriginalFilename());
            StorePath storePath = storageClient.uploadFile(file.getBytes(), suffix);
            String filePath = fdfsNginx + storePath.getFullPath();
            map.put("url", filePath);
        }
        map.put("state", "SUCCESS");
        return map;
    }

    /**
     * 上传
     *
     * @param file
     * @param t       1-图片 2-文件
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    @ResponseBody
    public UploadRet upload(@RequestParam("file") MultipartFile file, Integer t, HttpServletRequest request) throws IOException {
        // 文件后缀
        String suffix = StringUtil.getFileType(file.getOriginalFilename());
        List<String> type;
        long size;

        if (t == null) t = 1;
        if (t == 2) { // 文件
            type = SysConfig.getList("upload.fileType");
            size = SysConfig.getInt("upload.fileSize");
        } else { //图片
            type = SysConfig.getList("upload.imgType");
            size = SysConfig.getInt("upload.imgSize");
        }
        // 文件大小
        if (file.getSize() > size * 1024 * 1024) {
            return new UploadRet(1, "", "单个文件最大" + size + "MB");
        }
        // 文件格式
        if (!type.contains(suffix.toLowerCase())) {
            return new UploadRet(1, "", "文件格式不支持");
        }

        if (!SysConfig.getBoolean("upload.fdfs")) {
            String basePathFormat = DateUtil.getYearAndMonth(false);
            String filename = StringUtil.randomFilename(file.getOriginalFilename());
            File filePath = new File("upload/" + basePathFormat);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(filePath, filename));
            // 添加水印
            // FileUtils.copyInputStreamToFile(new ByteArrayInputStream(FDFSUtil.waterMark(file.getInputStream(), suffix)), new File(filePath, filename));
            return new UploadRet(0, SysConfig.get("sys.uploadHost") + "/upload/" + basePathFormat + "/" + filename, "SUCCESS");
        } else {
            // 添加水印：FDFSUtil.waterMark(file.getInputStream(), suffix)
            StorePath storePath = storageClient.uploadFile(file.getBytes(), suffix);
            String filePath = fdfsNginx + storePath.getFullPath();
            return new UploadRet(0, filePath, "SUCCESS");
        }
    }

    /**
     * FDFS断点续传【测试】
     * <pre>文件上传前需要进行分片</pre>
     *
     * @param file    文件域
     * @param append  是否追加
     * @param path    返回路径，用于追加文件
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadAppend")
    @ResponseBody
    public UploadRet uploadAppend(@RequestParam("file") MultipartFile file, Boolean append,
                                  String path, HttpServletRequest request) throws IOException {
        String suffix = StringUtil.getFileType(file.getOriginalFilename());
        if (append) {
            appendFileStorageClient.appendFile("group1", path, file.getInputStream(), file.getSize());
            return new UploadRet(0, path, "APPEND");
        } else {
            StorePath store = appendFileStorageClient.uploadAppenderFile("group1", file.getInputStream(),
                    file.getSize(), suffix);
            return new UploadRet(0, store.getPath(), "SUCCESS");
        }
    }

    /**
     * 文件上传目录URL映射
     */
    @Component
    class RecordResourceMapping extends WebMvcConfigurerAdapter {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/upload/**")
                    .addResourceLocations("file:./upload/");
            super.addResourceHandlers(registry);
        }
    }
}
