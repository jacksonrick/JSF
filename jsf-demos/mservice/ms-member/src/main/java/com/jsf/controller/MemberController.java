package com.jsf.controller;

import com.jsf.database.model.ResMsg;
import com.jsf.service.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 14:44
 */
@RestController
public class MemberController {

    @Resource
    private MemberService memberService;

    @PostMapping("/updateMember")
    public ResMsg updateMember(Long userId, Integer money) {
        System.out.println(new Date() + " 收到更新会员账户请求，userId=" + userId + ", money=" + money);
        memberService.updateMember(userId, money);
        return ResMsg.success();
    }

}
