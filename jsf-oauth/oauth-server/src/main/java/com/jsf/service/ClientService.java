package com.jsf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * Description: 接入客户端服务
 * User: xujunfei
 * Date: 2022-07-07
 * Time: 15:37
 */
//@Service
public class ClientService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 不同的客户端可能加密方式不同，按需启用
     *
     * @param clientId
     * @return
     */
    public String findEncryptWayById(String clientId) {
        if (!StringUtils.hasText(clientId)) {
            return null;
        }
        String s = jdbcTemplate.queryForObject("SELECT encrypt_way FROM oauth_client_details WHERE client_id = ?", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int i) throws SQLException {
                return rs.getString(1);
            }
        }, clientId);
        return s;
    }

}
