package com.airfer.rattler.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: wangyukun
 * Date: 2019/10/23 下午5:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreChainNormal {
    /**
     * 用于存储捕获的信息
     */
    private String captureResult;
}
