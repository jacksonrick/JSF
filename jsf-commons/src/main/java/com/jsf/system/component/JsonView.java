package com.jsf.system.component;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Created with IntelliJ IDEA.
 * Description: 适用于可能需要同时支持页面和数据的接口
 * User: xujunfei
 * Date: 2020-09-11
 * Time: 11:12
 */
@Component
public class JsonView {

    @Bean(name = "jackson2View")
    public View jackson2View() {
        return new MappingJackson2JsonView();
    }

}
