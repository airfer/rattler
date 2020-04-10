package com.airfer.rattler.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: wangyukun
 * Date: 2020/4/10 下午2:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChainMethodSignature {
    private String chainName;
    private String methodName;
    private Integer methodWeight;
    private String extend;
}
