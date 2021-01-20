package com.cylt.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis索引信息
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Redis {
    /**
     * 是否模糊查询
     */
    boolean vagueQuery() default false;


    /**
     * 是否正序
     */
    boolean isAsc() default true;

    /**
     * 字段是否排序 默认为 -1 不排序
     */
    int sort() default -1;
}
