package com.jsf.service;

import com.jsf.database.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @param username
     * @return
     */
    public User findByUsername(String username) {
        List<User> list = jdbcTemplate.query("SELECT role, name, pwd, disabled, locks FROM oauth_user WHERE name = ?", new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int i) throws SQLException {
                User user = new User();
                user.setRoles(rs.getString(1));
                user.setUsername(rs.getString(2));
                user.setPassword(rs.getString(3));
                user.setDisabled(rs.getBoolean(4));
                user.setLocks(rs.getInt(5));
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
}
