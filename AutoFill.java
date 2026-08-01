package com.gzhu.csnet.kclab.classics100common.annotation;

import com.gzhu.csnet.kclab.classics100common.constant.OperationTypeConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 数据库操作类型 update/insert
    OperationTypeConstant value();
}