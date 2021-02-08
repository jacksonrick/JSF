package com.jsf.service;

import com.jsf.database.model.ResMsg;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 16:09
 */
@FeignClient(value = "ms-member", fallback = MemberFallback.class)
public interface MemberService {

    @PostMapping("/updateMember")
    ResMsg updateMember(@RequestParam("userId") Integer userId, @RequestParam("money") Integer money);

}
