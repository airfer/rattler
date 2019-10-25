package com.airfer.rattler.aspect;

import org.aspectj.lang.annotation.Pointcut;

/**
 * 定义切点
 * <pre>https://docs.spring.io/spring/docs/2.0.x/reference/aop.html</pre>
 * Author: wangyukun
 * Date: 2019/10/25 上午9:33
 */
public class PointCuts {

    /**
     * 定义核心链路类切点
     */
    @Pointcut(value ="execution(* com.airfer.rattler.data.*.*(..)) " +
            "&& !@annotation(com.airfer.rattler.annotations.CoreChainMethod)")
    public void chainClassAroundPoint(){

    }

    /**
     * 定义核心方法切点
     */
    @Pointcut(value ="execution(* com.airfer.rattler.data.*.*(..)) " +
            "&& @annotation(com.airfer.rattler.annotations.CoreChainMethod)"
    )
    public void chainMethodAroundPoint(){

    }


}
