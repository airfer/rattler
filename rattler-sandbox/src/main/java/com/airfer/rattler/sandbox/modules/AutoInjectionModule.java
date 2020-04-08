package com.airfer.rattler.sandbox.modules;


import com.airfer.rattler.sandbox.chains.LocalChainAgre;
import com.airfer.rattler.sandbox.chains.MockChainAgre;
import com.airfer.rattler.sandbox.strategys.RandomStrategy;
import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ProcessController;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;

import com.airfer.rattler.sandbox.watcher.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;

/**
 * Author: wangyukun
 * Date: 2020/4/2 下午5:42
 */
@MetaInfServices(Module.class)
@Information(id = "auto-injection-rattler",author = "wangyukun06@meituan.com",version = "0.0.1")
public class AutoInjectionModule implements Module{
    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    /**
     * 基于真实的链路方法,自动注入示例
     */
    @Command("autoInjectForLocal")
    public void autoInjectForLocal() {
        new EventWatchBuilder(moduleEventWatcher)
                .onClass("com.*")
                /**
                 * onChain基于链路的注入,会根据服务标识以及链路的名称获取方法列表进行注入
                 * @param identityId 服务唯一标识
                 * @name 链路名称
                 */
                .onChain("airfer_rattler_test","red-alarm",
                        new LocalChainAgre())
                /**
                 * 如果设置了策略信息，则会根据策略来对方法进行过滤
                 * 例如：下方设置的为随机50%策略,那么核心链路中50%的方法会进行故障注入
                 * 策略可自行定义，实现InjectionStrategy接口即可
                 * 可能存在的情况：
                 * (1) 核心链路中指定前置的方法进行注入
                 * (2) 核心链路中对包含某个关键词的方法进行注入
                 */
                .onStrategy(new RandomStrategy())
                /**
                 * 示例：针对获取的方法直接抛出异常
                 */
                .onWatch(new AdviceListener() {
                    @Override
                    protected void before(Advice advice) throws Throwable {

                        // 在此，你可以通过ProcessController来改变原有方法的执行流程
                        // 这里的代码意义是：改变原方法抛出异常的行为，变更为立即返回；void返回值用null表示
                        ProcessController.throwsImmediately(new RuntimeException("autoInjection"));
                    }
                });
    }
    /**
     * mock方法用来注入
     */
    @Command("autoInjectForMock")
    public void autoInjectForMock() {

        new EventWatchBuilder(moduleEventWatcher)
                .onClass("*")
                /**
                 * onChain基于链路的注入,会根据服务标识以及链路的名称获取方法列表进行注入
                 * @param identityId 服务唯一标识
                 * @name 链路名称
                 */
                .onChain("airfer_rattler_test","chainName01",
                        new MockChainAgre())
                .onStrategy(new RandomStrategy())
                /**
                 * 示例：抛出运行时异常
                 */
                .onWatch(new AdviceListener() {
                    @Override
                    protected void before(Advice advice) throws Throwable {

                        // 在此，你可以通过ProcessController来改变原有方法的执行流程
                        // 这里的代码意义是：改变原方法抛出异常的行为，变更为立即返回；void返回值用null表示
                        ProcessController.throwsImmediately(new RuntimeException("autoInjection"));
                    }
                });
    }


}
