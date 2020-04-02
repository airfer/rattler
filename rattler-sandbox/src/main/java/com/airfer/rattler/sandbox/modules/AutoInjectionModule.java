package com.airfer.rattler.sandbox.modules;


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
@Information(id = "AutoInjectionModuleBasedOnChain")
public class AutoInjectionModule implements Module{
    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Command("autoInject")
    public void repairCheckState() {

        new EventWatchBuilder(moduleEventWatcher)
                .onClass("com.taobao.demo.Clock")
                .onBehavior("checkState")
                .onWatch(new AdviceListener() {

                    /**
                     * 拦截{@code com.taobao.demo.Clock#checkState()}方法，当这个方法抛出异常时将会被
                     * AdviceListener#afterThrowing()所拦截
                     */
                    @Override
                    protected void afterThrowing(Advice advice) throws Throwable {

                        // 在此，你可以通过ProcessController来改变原有方法的执行流程
                        // 这里的代码意义是：改变原方法抛出异常的行为，变更为立即返回；void返回值用null表示
                        ProcessController.returnImmediately(null);
                    }
                });

    }

}
