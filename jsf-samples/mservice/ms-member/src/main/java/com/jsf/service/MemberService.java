package com.jsf.service;

import com.jsf.database.mapper.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 14:43
 */
@Service
public class MemberService {

    @Resource
    private MemberMapper memberMapper;

    @Transactional
    public void updateMember(Long userId, Integer money) {
        try {
            // 模拟超时；此处时间必须小于hystrix..timeoutInMilliseconds配置的值
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        memberMapper.update(userId, money);
        //throw new RuntimeException("抛出异常");
    }

}
