package com.jsf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2019-07-02
 * Time: 13:55
 */
@Controller
public class IndexController {

    /**
     * 点位到管理登陆页，也可以去掉
     *
     * @return
     */
    @GetMapping("/")
    //@ResponseBody
    public String index() {
        //return "manager";
        return "redirect:/admin/login";
    }

}
