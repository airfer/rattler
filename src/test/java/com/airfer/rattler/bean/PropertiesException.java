package com.airfer.rattler.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: wangyukun
 * Date: 2019/10/23 下午2:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertiesException {
    /**
     * 熟悉key值
     */
    private String propertyKey;
}
