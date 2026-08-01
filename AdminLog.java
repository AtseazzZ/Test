package com.gzhu.csnet.kclab.classics100common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminLog {
    /**
     * 操作类型
     */
    String actionType();
    
    /**
     * 操作描述
     */
    String description() default "";
}