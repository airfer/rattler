package com.airfer.rattler.sandbox.WatchBuilderTest;

import com.airfer.rattler.sandbox.chains.MockChainAgre;
import com.airfer.rattler.sandbox.mock.ModuleEventWatcherForMock;
import com.airfer.rattler.sandbox.strategys.RandomStrategy;
import com.airfer.rattler.sandbox.watcher.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.ProcessController;
import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.filter.Filter;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchCondition;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.testng.annotations.Test;

/**
 * Author: wangyukun
 * Date: 2020/4/8 下午2:13
 */
public class MockWatchBuildTest {

    @Test
    public void autoInjectForMock() {
        new EventWatchBuilder(new ModuleEventWatcherForMock(){})
                .onClass("com.*")
                /**
                 * onChain基于链路的注入
                 * @param identityId 服务唯一标识
                 * @name 链路名称
                 */
                .onChain("airfer_rattler_test","chainName01",
                        new MockChainAgre())
                /**
                 * 示例：抛出运行时异常
                 */
                .onStrategy(new RandomStrategy())
                .onWatch(new AdviceListener() {

                    /**
                     * 拦截{@code com.taobao.demo.Clock#checkState()}方法，当这个方法抛出异常时将会被
                     * AdviceListener#afterThrowing()所拦截
                     */
                    @Override
                    protected void afterThrowing(Advice advice) throws Throwable {

                        // 在此，你可以通过ProcessController来改变原有方法的执行流程
                        // 这里的代码意义是：改变原方法抛出异常的行为，变更为立即返回；void返回值用null表示
                        ProcessController.throwsImmediately(new RuntimeException("autoInjection"));
                    }
                });

    }
}
