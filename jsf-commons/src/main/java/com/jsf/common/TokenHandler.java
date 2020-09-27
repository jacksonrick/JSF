package com.jsf.common;

import com.jsf.database.enums.ResCode;
import com.jsf.database.mapper.TokenMapper;
import com.jsf.database.model.Token;
import com.jsf.system.conf.IConstant;
import com.jsf.utils.date.DateUtil;
import com.jsf.utils.exception.ApiTokenException;
import com.jsf.utils.string.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description: Token处理器 支持Redis与关系型数据库
 * User: xujunfei
 * Date: 2019-05-05
 * Time: 16:13
 */
@Component
public class TokenHandler {

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TokenMapper tokenMapper;

    /**
     * 绑定token [redis]
     *
     * @param userId
     */
    public String bindToken(Long userId) {
        String newToken = StringUtil.getTokenId();
        String oldToken = (String) stringRedisTemplate.opsForValue().getAndSet(IConstant.TOKEN_UID_PREFIX + userId, newToken);
        if (oldToken != null) {
            stringRedisTemplate.delete(IConstant.TOKEN_PREFIX + oldToken); // 删除旧token
        }
        // 绑定用户唯一token
        stringRedisTemplate.opsForValue().set(IConstant.TOKEN_PREFIX + newToken, userId + "", IConstant.DEFAULT_TIMEOUT_DAYS, TimeUnit.DAYS);
        return newToken;
    }

    /**
     * 绑定token [db]
     *
     * @param userId
     * @return
     */
    public String bindTokenToDb(Long userId) {
        String newToken = StringUtil.getTokenId();
        Token oldToken = tokenMapper.findByUid(String.valueOf(userId));
        if (oldToken == null) {
            Token tk = new Token(String.valueOf(userId), newToken, DateUtil.dateAddDay(new Date(), IConstant.DEFAULT_TIMEOUT_DAYS));
            if (tokenMapper.insert(tk) > 0) {
                return newToken;
            } else {
                throw new RuntimeException("插入Token失败");
            }
        } else {
            oldToken.setToken(newToken);
            oldToken.setExpired(DateUtil.dateAddDay(new Date(), IConstant.DEFAULT_TIMEOUT_DAYS));
            if (tokenMapper.update(oldToken) > 0) {
                return newToken;
            } else {
                throw new RuntimeException("更新Token失败");
            }
        }
    }

    /**
     * From Redis
     *
     * @param token
     * @return
     */
    public Long getIdByTokenFromRedis(String token) {
        if (token == null || token.length() < 1) {
            throw new ApiTokenException(ResCode.TOKEN_EXP.msg());
        }
        Long uid = Long.valueOf(stringRedisTemplate.opsForValue().get(IConstant.TOKEN_PREFIX + token));
        if (uid != null) {
            return uid;
        } else {
            throw new ApiTokenException(ResCode.TOKEN_EXP.msg());
        }
    }

    /**
     * From DB
     *
     * @param token
     * @return
     */
    public Long getIdByTokenFromDb(String token) {
        if (token == null || token.length() < 1) {
            throw new ApiTokenException(ResCode.TOKEN_EXP.msg());
        }
        Token tk = tokenMapper.findByToken(token);
        if (tk == null) {
            throw new ApiTokenException(ResCode.TOKEN_EXP.msg());
        }
        if (tk.getExpired().getTime() < System.currentTimeMillis()) {
            throw new ApiTokenException(ResCode.TOKEN_EXP.msg());
        }
        return Long.valueOf(tk.getUid());
    }

}
