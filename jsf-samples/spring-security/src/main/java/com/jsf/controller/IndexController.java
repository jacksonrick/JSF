package com.jsf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created with IntelliJ IDEA.
 * Description: 无需登陆
 * User: xujunfei
 * Date: 2020-06-11
 * Time: 12:07
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(String logout, ModelMap map) {
        if (logout != null) {
            map.addAttribute("msg", "你已经成功退出");
        }
        return "login";
    }

    @GetMapping("/bye")
    public String bye() {
        return "bye";
    }

    @GetMapping("/forgot")
    public String forgot() {
        return "forgot";
    }

}
