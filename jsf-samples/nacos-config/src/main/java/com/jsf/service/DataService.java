package com.jsf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2022-04-18
 * Time: 10:06
 */
@Service
@RefreshScope
public class DataService {

    @Value("${config.url}")
    private String url;

    public String getUrl() {
        return url;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 从数据库查询数据
    // 这里的数据库配置在nacos中
    public String get(Integer id) {
        List<String> list = jdbcTemplate.query("SELECT * FROM nacos WHERE id = ?", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(2);
            }
        }, id);
        if (list.isEmpty()) {
            return "";
        }
        return list.get(0);
    }
}
