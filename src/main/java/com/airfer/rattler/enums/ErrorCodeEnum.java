package com.airfer.rattler.enums;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

/**
 * Author: wangyukun
 * Date: 2019/9/27 上午10:13
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {
    UNEXCEPTED_ERROR                                  (0001,"未知异常"),
    SUCCESS                          				  (1000, "成功"),
    SUIT_NAME_ERROR                                   (2001, "用例suitName为空"),
    MCC_SUIT_ERROR                                    (2002, "MCC中无该Suit配置"),
    PARAM_ERROR                      				  (1001, "请求参数错误"),
    PARAM_LOST                       				  (1002, "请求参数缺失"),
    PARAM_ILLEGAL                      				  (1003, "请求参数非法"),
    ACCESS_TOKEN_VALIDATE_FAIL                        (1004, "身份鉴权失败"),
    SYSTEM_ERROR                     				  (9999, "系统错误"),
    SANDBOX_MODULE_INSTALL_ERROR                      (3000, "安装并加载失败，请查看日志确认详细原因"),
    SANDBOX_MODULE_QUERY_ERROR                        (3001, "未查询到相关安装模块"),
    GET_PROPERTIES_ERROR                              (4001,"获取配置信息异常"),
    CHAIN_INFO_UPLOAD_ERROR                           (4002,"行程上送失败"),
    CHAIN_INFO_UPLOAD_SUCCESS                         (4003,"行程上送成功"),
    CHAIN_INFO_REPEATE_ERROR                          (4004,"本次搜集的行程信息和上次相同，不进行上送更新"),
    ;

    private int code;
    private String message;

    private static final Map<Integer, ErrorCodeEnum> CODE_MAP = Maps.newHashMapWithExpectedSize(ErrorCodeEnum.values().length);

    static {
        Arrays.stream(ErrorCodeEnum.values()).forEach(errorCodeEnum -> CODE_MAP.computeIfAbsent(errorCodeEnum.getCode(), t -> errorCodeEnum));
    }

    public static ErrorCodeEnum getErrorCodeEnum(int errorCode) {
        return CODE_MAP.getOrDefault(errorCode, SYSTEM_ERROR);
    }
}