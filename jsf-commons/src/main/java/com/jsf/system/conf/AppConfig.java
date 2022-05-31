package com.jsf.system.conf;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description: 应用配置。 系统配置见{@link com.jsf.system.conf.SysConfig}
 * User: xujunfei
 * Date: 2020-09-11
 * Time: 15:52
 * Version: 2
 * <p>因为是单应用，没有引用过多的组件，这里使用数据库存储，可以根据需要，引入分布式配置组件如nacos</p>
 */
public class AppConfig {

    public static Map<String, Object> CONFIGS = new ConcurrentHashMap<>();

    /**
     * 获取属性 grp.key
     *
     * @param key
     * @return
     */
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