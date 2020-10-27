package com.jsf.system.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2020-10-27
 * Time: 15:21
 */
public class ApiLocker {

    public static final Map<String, AtomicBoolean> LOCK = new ConcurrentHashMap<>();

    /**
     * 加锁
     *
     * @param name
     * @return true成功 false失败
     */
    public static boolean lock(String name) {
        AtomicBoolean state = LOCK.putIfAbsent(name, new AtomicBoolean(true));
        // 如果缺失，加锁
        if (state == null) {
            return true;
        }
        // 如果状态值是true，则返回加锁失败
        return state.compareAndSet(false, true);
    }

    /**
     * 解锁
     *
     * @param name
     * @return true成功 false失败
     */
    public static boolean unlock(String name) {
        AtomicBoolean state = LOCK.get(name);
        if (state == null) {
            return false;
        }
        return state.compareAndSet(true, false);
    }

}
