package com.airfer.rattler.sandbox.watcher;

/**
 * Author: wangyukun
 * Date: 2020/4/1 下午11:16
 */
public interface EventWatcher extends EventWatchBuilder.IBuildingForUnWatching {

    /**
     * 获取本次观察事件ID
     *
     * @return 本次观察事件ID
     */
    int getWatchId();

}
