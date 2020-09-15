package com.jsf.system.conf;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description: 系统全局配置
 * User: xujunfei
 * Date: 2020-09-11
 * Time: 15:52
 * Version: 2
 * Remark: 原配置是从application.yml读取，并注入此类，如需还原请看5.x版本
 */
public class SysConfig {

    public static Map<String, Object> CONFIGS = new ConcurrentHashMap<>();

    public static String get(String key) {
        return (String) CONFIGS.get(key);
    }

    public static String getString(String key) {
        return String.valueOf(CONFIGS.get(key));
    }

    public static Integer getInt(String key) {
        return (Integer) CONFIGS.get(key);
    }

    public static Long getLong(String key) {
        return (Long) CONFIGS.get(key);
    }

    public static Boolean getBoolean(String key) {
        return (Boolean) CONFIGS.get(key);
    }

    public static Map<String, Object> getJSONMap(String key) {
        return (Map<String, Object>) CONFIGS.get(key);
    }

    public static List<String> getList(String key) {
        return (List<String>) CONFIGS.get(key);
    }

    public static String[] getArray(String key) {
        return (String[]) CONFIGS.get(key);
    }

}