package com.jsf.utils.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * Description: API锁
 * <p>用于在同一时间，某接口限制只能调用一次的情况（耗时耗资源），仅适用于单节点服务</p>
 * <p>如导出大批量数据，同一时间只能有1个任务在运行</p>
 * <p>任务执行完成必须手动执行解锁方法</p>
 * User: xujunfei
 * Date: 2020-10-27
 * Time: 15:09
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLock {

    /**
     * 任务或接口名称，唯一，解锁需要
     */
    String name();

    /**
     * 返回提示
     */
    String desc() default "任务正在处理中，请稍后再试！";

}
