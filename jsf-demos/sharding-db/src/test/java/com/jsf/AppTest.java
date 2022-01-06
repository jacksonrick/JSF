package com.jsf;

import com.jsf.config.ShardingUtil;
import com.jsf.model.User;
import com.jsf.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static void main(String[] args) {

    }

    @Resource
    private UserService userService;

    @Test
    public void test1() {
        // insert走master库
        User user = new User();
        user.setId(25);
        user.setUsername("252525");
        user.setAddress("xx");
        user.setAge(20);
        user.setDt(new Date()); //必要值，否则会在所有表插入
        userService.insertUser(user);
    }

    @Test
    public void test2() {
        // select走slaver库
        // 不带dt条件，会查所有的表
        User user = userService.findUserById(10);
        System.out.println(user);
    }

    @Test
    public void test3() {
        userService.testTransaction();
    }

    @Test
    public void test4() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User condition = new User();
        //1、精确时间
        //condition.setDt(sdf.parse("2021-05-10 00:00:00"));
        //2、范围时间
        condition.setStart(sdf.parse("2020-05-01 00:00:00"));
        condition.setEnd(sdf.parse("2021-05-30 00:00:00"));

        List<User> userList = userService.findUserList(condition);
        for (User user : userList) {
            System.out.println(user.getId() + " - \t" + user.getUsername());
        }
    }

    @Test
    public void test5() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User condition = new User();
        condition.setStart(sdf.parse("2020-05-01 00:00:00"));
        condition.setEnd(sdf.parse("2021-05-30 00:00:00"));
        List<User> userList = userService.findUserInfoList(condition);
        for (User user : userList) {
            System.out.println(user.getId() + " - \t" + user.getUsername() + " - \t" + user.getDetail());
        }
    }

    @Test
    public void test6() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User condition = new User();
        condition.setStart(sdf.parse("2019-01-01 00:00:00"));
        condition.setEnd(sdf.parse("2021-12-30 00:00:00"));
        //List<User> userList = userService.findUserByPage(condition, null, 6);
        List<User> userList = userService.findUserByPage(condition, 6, 6);
        for (User user : userList) {
            System.out.println(user.getId() + " - \t" + user.getUsername());
        }
    }

    @Test
    public void test7() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User condition = new User();
        condition.setStart(sdf.parse("2019-01-01 00:00:00"));
        condition.setEnd(sdf.parse("2021-12-30 00:00:00"));
        Integer sum = userService.findSum(condition);
        System.out.println("sum=" + sum);
    }

}
