package com.jsf.controller;

import com.jsf.model.UserInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * Description: 需要登陆
 * User: xujunfei
 * Date: 2020-06-11
 * Time: 12:07
 */
@Controller
public class HomeController extends BaseController {

    @GetMapping("/home")
    public String home() {
        UserInfo user = getUser();
        System.out.println("登陆主页：" + user);
        return "home";
    }

    // 使用@PreAuthorize注解，
    // hasRole() 角色必须以ROLE_开头，
    // hasAuthority() 任意完全匹配的字符串

    @GetMapping("/admin1")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public String admin1() {
        return "只有管理员角色才可以看到";
    }

    @GetMapping("/admin2")
    @ResponseBody
    @PreAuthorize("hasAuthority('auth:admin')")
    public String admin2() {
        return "只有管理员权限才可以看到";
    }

}
