package com.jsf.system.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 系统配置application.yml。 应用配置见{@link com.jsf.system.conf.AppConfig}。静态属性配置见{@link com.jsf.system.conf.SysConfigStatic}
 * User: xujunfei
 * Date: 2017-11-28
 * Time: 15:52
 */
@Configuration
@ConfigurationProperties(prefix = "system")
public class SysConfig {

    // 系统版本
    private String version;
    // 是否开发环境
    private Boolean dev;
    // 应用访问校验码
    private String appkey;
    // 上传
    private Upload upload;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean dev() {
        return dev;
    }

    public void setDev(Boolean dev) {
        this.dev = dev;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    @Override
    public String toString() {
        return "SysConfig{" +
                "version='" + version + '\'' +
                ", dev='" + dev + '\'' +
                ", appkey='" + appkey + '\'' +
                ", upload=" + upload +
                '}';
    }

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }


    public static class Upload {
        // 上传目录
        private String filePath;
        // 如果为false, 则上传到filePath配置目录
        private Boolean fdfs;
        // 图片大小
        private Integer imgSize;
        // 图片类型
        private List<String> imgType;
        // 文件大小【非图片】
        private Integer fileSize;
        // 文件类型
        private List<String> fileType;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public Boolean getFdfs() {
            return fdfs;
        }

        public void setFdfs(Boolean fdfs) {
            this.fdfs = fdfs;
        }

        public Integer getImgSize() {
            return imgSize;
        }

        public void setImgSize(Integer imgSize) {
            this.imgSize = imgSize;
        }

        public List<String> getImgType() {
            return imgType;
        }

        public void setImgType(List<String> imgType) {
            this.imgType = imgType;
        }

        public Integer getFileSize() {
            return fileSize;
        }

        public void setFileSize(Integer fileSize) {
            this.fileSize = fileSize;
        }

        public List<String> getFileType() {
            return fileType;
        }

        public void setFileType(List<String> fileType) {
            this.fileType = fileType;
        }

        @Override
        public String toString() {
            return "Upload{" +
                    "fdfs='" + fdfs + '\'' +
                    ", imgSize=" + imgSize +
                    ", imgType=" + imgType +
                    ", fileSize=" + fileSize +
                    ", fileType=" + fileType +
                    '}';
        }
    }

}