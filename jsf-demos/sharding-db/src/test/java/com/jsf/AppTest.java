package com.jsf;

import com.jsf.model.User;
import com.jsf.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2018-06-19
 * Time: 09:48
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {

    @Resource
    private UserService userService;

    @Test
    public void test1() {
        // insert走master库
        userService.insertUser(10, "name", 1);
    }

    @Test
    public void test2() {
        // select走slaver库
        User user = userService.findUserById(10);
        System.out.println(user);
    }

    @Test
    public void test3() {
        userService.testTransaction();
    }

}
