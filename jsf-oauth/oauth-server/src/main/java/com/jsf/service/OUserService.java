package com.jsf.service;

import com.jsf.model.OAuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 用户服务类
 * User: xujunfei
 * Date: 2018-10-31
 * Time: 15:35
 */
@Service
public class OUserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @param username
     * @return
     */
    public OAuthUser findByUsername(String username) {
        List<OAuthUser> list = jdbcTemplate.query("SELECT id, role, name, pwd, disabled, locks FROM oauth_user WHERE name = ?", new RowMapper<OAuthUser>() {
            @Override
            public OAuthUser mapRow(ResultSet rs, int i) throws SQLException {
                OAuthUser user = new OAuthUser();
                user.setId(rs.getInt(1));
                user.setRoles(rs.getString(2));
                user.setUsername(rs.getString(3));
                user.setPassword(rs.getString(4));
                user.setDisabled(rs.getBoolean(5));
                user.setLocks(rs.getInt(6));
                return user;
            }
        }, username);

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * @param username
     */
    public void updateLocks(String username) {
        jdbcTemplate.update("UPDATE oauth_user SET locks = locks + 1 WHERE name = ?", username);
    }

    /**
     * @param username
     */
    public void updateLock0(String username) {
        jdbcTemplate.update("UPDATE oauth_user SET locks = 0 WHERE name = ?", username);
    }

    /**
     * 新增用户
     *
     * @param username
     * @param password
     * @param roles
     * @param origin
     */
    public void addUser(String username, String password, String roles, String origin) {
        jdbcTemplate.update("INSERT INTO oauth_user(role,name,pwd,origin) VALUES (?,?,?,?)", roles, username, password, origin);
    }

    /**
     * 更新用户
     *
     * @param username
     * @param password
     * @param roles
     * @param disabled
     */
    public void updateUser(String username, String password, String roles, Boolean disabled) {
        StringBuilder fields = new StringBuilder("");
        List<Object> params = new ArrayList<>(4);
        if (StringUtils.hasText(password)) {
            fields.append(",pwd=?");
            params.add(password);
        }
        if (roles != null) {
            fields.append(",role=?");
            params.add(roles);
        }
        if (disabled != null) {
            fields.append(",disabled=?");
            params.add(disabled);
        }
        if (params.isEmpty()) {
            return;
        }
        params.add(username);
        Object[] p = new Object[params.size()];
        params.toArray(p);
        jdbcTemplate.update("UPDATE oauth_user SET " + fields.substring(1) + " WHERE name = ?", p);
    }
}
