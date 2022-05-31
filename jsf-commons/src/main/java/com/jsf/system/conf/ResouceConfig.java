package com.jsf.system.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2022-05-31
 * Time: 10:10
 */
@Configuration
public class ResouceConfig implements WebMvcConfigurer {

    @Resource
    private SysConfig sysConfig;

    /**
     * 上传文件路径映射，仅用于本地上传
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + sysConfig.getUpload().getFilePath() + "/upload/");

        // 配置其它映射
        // ...
    }
}