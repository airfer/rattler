package com.airfer.rattler.sandbox.strategys;

/**
 * Author: wangyukun
 * Date: 2020/4/2 下午6:35
 */
public class RandomStrategy implements InjectionStrategy{

    /**
     * 该随机策略默认选取50%的方法核心链路方法进行注入
     * @return
     */
    @Override
    public Boolean proceed() {
        //获取当前时间戳
        final long ts = System.currentTimeMillis();
        return ts%2 == 0;
    }
}
