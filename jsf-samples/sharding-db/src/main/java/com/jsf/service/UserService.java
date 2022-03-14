package com.jsf.service;

import com.jsf.mapper.UserMapper;
import com.jsf.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2019-11-14
 * Time: 10:21
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public User findUserById(Integer id) {
        return userMapper.findById(id);
    }

    public void insertUser(User user) {
        userMapper.insert(user);
    }

    @Transactional
    public void testTransaction() {
        User user = userMapper.findById(1);
        user.setUsername("aaa");
        userMapper.update(user);

        User user1 = new User();
        user1.setId(33);
        user1.setUsername("zzz");
        user1.setAge(10);
        userMapper.insert(user1);

        System.out.println(1 / 0);
    }

    /**
     * 时间范围条件查询
     *
     * @param condition
     * @return
     */
    public List<User> findUserList(User condition) {
        return userMapper.findList(condition);
    }

    /**
     * 联表
     *
     * @param condition
     * @return
     */
    public List<User> findUserInfoList(User condition) {
        return userMapper.findInfoList(condition);
    }

    /**
     * 分页，由于查询总数，性能和编程均耗时，所以这里使用无限分页
     *
     * @param condition
     * @param lastId    从第二页开始传递的参数，注意如果使用时间可能有重复的风险，ID为UUID类型时不可用，尽可能使用整数递增
     * @param limit     始终使用limit n
     * @return
     */
    public List<User> findUserByPage(User condition, Integer lastId, int limit) {
        if (lastId == null) {
            lastId = 0;
        }
        return userMapper.findByPage(condition, lastId, limit);
    }

    public Integer findSum(User condition) {
        return userMapper.findSum(condition);
    }

}
