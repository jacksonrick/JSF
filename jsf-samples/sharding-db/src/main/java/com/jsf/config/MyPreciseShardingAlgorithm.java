package com.jsf.config;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description: 自定义条件，精确值，可支持 =、IN
 * User: xujunfei
 * Date: 2019-11-14
 * Time: 11:05
 */
public class MyPreciseShardingAlgorithm implements PreciseShardingAlgorithm<Date> {

    /**
     * @param availableTargetNames sharding.tables.xx.actual-data-nodes
     * @param shardingValue
     * @return
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Date> shardingValue) {
        Date date = shardingValue.getValue();
        String year = String.valueOf(ShardingUtil.getYear(date)); // 获取年份
        for (String tableName : availableTargetNames) {
            // 取余的方式: shardingValue.getValue() % 3
            if (tableName.endsWith(year)) {
                return tableName;
            }
        }
        throw new IllegalArgumentException("未找到匹配的数据表");
    }
}
