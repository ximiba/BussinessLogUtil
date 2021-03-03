package com.tian.li.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: lixiaotian
 * @date: 2021/2/26 11:27
 * @description: 用于标识需要记录日志的字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface LogRecord {

    //字段含义
    String name() default "";

    //对应的数据字典值
    String dicCode() default "";

    //用于翻译布尔值，1标识是布尔类型
    int remark() default 0;

    //运算处理，数据处理用
    String divide() default "";
}
