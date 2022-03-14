package com.jsf.service;

import com.jsf.database.model.ResMsg;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 16:09
 */
@Component
public class MemberFallback implements MemberService {
    @Override
    public ResMsg updateMember(Integer userId, Integer money) {
        return ResMsg.fail(-100, "会员服务不可用");
    }
}
