package com.jsf.service;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-04-20
 * Time: 16:20
 */
@Service
public class BatchService {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 批量插入
     *
     * @param map
     */
    public void batchSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    // 单个插入，供测试对比
    public void singleSet(String key, Object obj) {
        redisTemplate.opsForValue().set(key, obj);
    }

    /**
     * 批量插入并设置过期时间
     *
     * @param map
     * @param seconds
     */
    public void batchSetExpire(Map<String, Object> map, int seconds) {
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    connection.set(entry.getKey().getBytes(), redisTemplate.getValueSerializer().serialize(entry.getValue()),
                            Expiration.from(seconds, TimeUnit.SECONDS), RedisStringCommands.SetOption.UPSERT);
                }
                return null;
            }
        });
    }

    /**
     * 批量查询
     *
     * @param keys
     * @return
     */
    public List<Object> batchGet(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 批量查询，返回map
     *
     * @param keys
     * @return
     */
    public Map<String, Object> batchGetMap(List<String> keys) {
        Map<String, Object> map = new HashMap<>();
        List<Object> objects = redisTemplate.opsForValue().multiGet(keys);
        for (int i = 0; i < objects.size(); i++) {
            map.put(keys.get(i), objects.get(i));
        }
        return map;
    }


}
