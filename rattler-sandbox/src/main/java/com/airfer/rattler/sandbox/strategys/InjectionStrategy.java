package com.airfer.rattler.sandbox.strategys;

/**
 * Author: wangyukun
 * Date: 2020/4/2 下午6:15
 * <p> 注入策略接口,定义了策略部分需要完成的功能
 *
 */
public interface InjectionStrategy {
    //处理并返回bool结果
    Boolean proceed();
}
