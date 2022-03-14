package com.jsf.config;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description: 自定义条件，范围值，可支持 >、>=、>、>=、BETWEEN..AND
 * User: xujunfei
 * Date: 2019-11-14
 * Time: 11:05
 */
public class MyRangeShardingAlgorithm implements RangeShardingAlgorithm<Date> {

    /**
     * 也可以获取availableTargetNames中的表名，这里直接写死
     */
    private static final String prefix = "t_user_";

    /**
     * @param availableTargetNames sharding.tables.xx.actual-data-nodes
     * @param shardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Date> shardingValue) {
        // 如果日期是String格式，请使用substring分割
        Range<Date> range = shardingValue.getValueRange();
        int min = -1, max = -1; // 定义年份范围
        if (range.hasLowerBound()) {
            min = ShardingUtil.getYear(range.lowerEndpoint());
        }
        if (range.hasUpperBound()) {
            max = ShardingUtil.getYear(range.upperEndpoint());
        }
        List<String> list = new ArrayList<>();
        if (min == max) {
            if (min == -1) {
                throw new IllegalArgumentException("时间参数未设置");
            }
            list.add(prefix + min);
        } else if (min == -1) {
            list.add(prefix + max);
        } else if (max == -1) {
            list.add(prefix + min);
        } else {
            list.add(prefix + min);
            while (min != max) {
                min++;
                list.add(prefix + min);
            }
        }
        return list;
    }

}
