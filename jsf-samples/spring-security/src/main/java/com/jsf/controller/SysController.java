package com.jsf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description: 通过AccessPermission.hasPermission校验权限
 * User: xujunfei
 * Date: 2022-08-09
 * Time: 11:43
 */
@RestController
@RequestMapping("/sys")
public class SysController extends BaseController {

    @GetMapping("/list")
    public String list() {
        return "list";
    }

    @GetMapping("/view")
    public String view() {
        return "view";
    }

    @GetMapping("/delete")
    public String delete() {
        return "delete";
    }

    @GetMapping("/edit")
    public String edit() {
        return "edit";
    }

}
