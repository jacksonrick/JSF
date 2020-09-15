package com.jsf.system.handler.view;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/**
 * Created with IntelliJ IDEA.
 * Description:
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
