package com.airfer.rattler.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: wangyukun
 * Date: 2019/10/23 下午2:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertiesNormal {
    /**
     * 属性key值
     */
    private String propertyKey;
    /**
     * 属性value值
     */
    private String propertyValue;
}
