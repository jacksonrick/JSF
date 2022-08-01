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
     * 按clientId查询
     *
     * @param clientId
     * @return
     */
    public String findById(String clientId) {
        if (!StringUtils.hasText(clientId)) {
            return null;
        }
        return "";
    }

}
