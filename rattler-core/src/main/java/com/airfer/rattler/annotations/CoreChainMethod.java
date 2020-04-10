package com.airfer.rattler.annotations;

import com.airfer.rattler.enums.MethodWeightEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: wangyukun
 * Date: 2019/9/27 上午10:12
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CoreChainMethod {
    String coreChainName() default "";
    MethodWeightEnum weightEnum() default MethodWeightEnum.LOW;
    String extend() default "";
}