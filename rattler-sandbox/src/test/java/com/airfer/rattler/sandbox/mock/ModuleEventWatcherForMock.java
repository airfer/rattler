package com.airfer.rattler.sandbox.mock;

import com.alibaba.jvm.sandbox.api.event.Event;
import com.alibaba.jvm.sandbox.api.filter.Filter;
import com.alibaba.jvm.sandbox.api.listener.EventListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchCondition;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;

/**
 * Author: wangyukun
 * Date: 2020/4/8 下午2:18
 */
public class ModuleEventWatcherForMock implements ModuleEventWatcher {
    @Override
    public void delete(int watcherId, Progress progress) {
        return;
    }
    @Override
    public void delete(int watcherId) {
    }

    @Override
    public void watching(Filter filter, EventListener listener, WatchCallback watchCb, Event.Type... eventType) throws Throwable {
        return;
    }

    @Override
    public int watch(Filter filter, EventListener listener, Event.Type... eventType) {
        return 0;
    }

    @Override
    public int watch(Filter filter, EventListener listener, Progress progress, Event.Type... eventType) {
        return 0;
    }

    @Override
    public int watch(EventWatchCondition condition, EventListener listener, Progress progress, Event.Type... eventType) {
        return 0;
    }

    @Override
    public void watching(Filter filter, EventListener listener, Progress wProgress, WatchCallback watchCb, Progress dProgress, Event.Type... eventType) throws Throwable {

    }
}
