package com.airfer.rattler.enums;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

/**
 * Author: wangyukun
 * Date: 2019/9/27 上午10:14
 */
public enum MethodWeightEnum {
    LOW(1,"低权重"),
    MIDDLE(3,"中权重"),
    HIGH(5,"高权重"),
    UNKNOWN(-1,"未知");


    private int code;
    private String desc;

    private static final Map<Integer, MethodWeightEnum> CODE_MAP = Maps.newHashMapWithExpectedSize(MethodWeightEnum.values().length);

    static {
        Arrays.stream(MethodWeightEnum.values()).forEach(methodWeightEnum -> CODE_MAP.computeIfAbsent(methodWeightEnum.getCode(), t -> methodWeightEnum));
    }

    MethodWeightEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static MethodWeightEnum getMethodWeightEnumByCode(int code) {
        return CODE_MAP.getOrDefault(code, UNKNOWN);
    }
}

