package com.jsf.system.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * Description: 系统配置，静态属性。 {@link SysConfig}
 * User: xujunfei
 * Date: 2017-11-28
 * Time: 15:52
 */
@Configuration
public class SysConfigStatic {

    public static String version;
    public static Boolean dev;
    public static String appkey;
    public static SysConfig.Upload upload;

    @Autowired
    public void setVersion(SysConfig config) {
        SysConfigStatic.version = config.getVersion();
    }

    @Autowired
    public void setDev(SysConfig config) {
        SysConfigStatic.dev = config.dev();
    }

    @Autowired
    public void setAppkey(SysConfig config) {
        SysConfigStatic.appkey = config.getAppkey();
    }

    @Autowired
    public void setUpload(SysConfig config) {
        SysConfigStatic.upload = config.getUpload();
    }

}