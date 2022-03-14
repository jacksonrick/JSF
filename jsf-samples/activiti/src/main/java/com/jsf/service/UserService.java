package com.jsf.service;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-04-14
 * Time: 15:25
 */
@Service
public class UserService implements UserGroupManager {

    public static Map<String, String> userGroup = new HashMap<>();
    public static Map<String, String> userRole = new HashMap<>();

    public static List<String> groups = Arrays.asList("1", "2");
    public static List<String> users = Arrays.asList("老王", "小李", "老总");
    public static List<String> roles = Arrays.asList("部门主管", "行政主管", "总经理");

    static {
        userGroup.put(users.get(0), groups.get(0));
        userGroup.put(users.get(1), groups.get(1));
        userGroup.put(users.get(2), groups.get(1));

        userRole.put(users.get(0), roles.get(0));
        userRole.put(users.get(1), roles.get(1));
        userRole.put(users.get(2), roles.get(2));
    }

    public String getUser(int i) {
        return users.get(i);
    }

    @Override
    public List<String> getUserGroups(String username) {
        return groups;
    }

    @Override
    public List<String> getUserRoles(String username) {
        return Arrays.asList(userRole.get(username));
    }

    @Override
    public List<String> getGroups() {
        return groups;
    }

    @Override
    public List<String> getUsers() {
        return users;
    }
}
