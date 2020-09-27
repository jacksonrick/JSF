package com.jsf.utils.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * Description: 检验重复提交
 * User: xujunfei
 * Date: 2020-09-27
 * Time: 14:38
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {

    int timeout() default 3;
}
