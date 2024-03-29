package com.jsf.redis;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * SpringSession Redis序列化
 * *注：bean的名称必须为springSessionDefaultRedisSerializer
 *
 * @see org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
 * @see org.springframework.session.data.redis.RedisIndexedSessionRepository
 */
@Component("springSessionDefaultRedisSerializer")
public class SessionSerializer extends GenericJackson2JsonRedisSerializer {

}
