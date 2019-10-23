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
    SUCCESS                          				  (0000, "成功"),
    UNEXCEPTED_ERROR                                  (0001, "未知异常"),
    ACCESS_TOKEN_VALIDATE_FAIL                        (0002, "身份鉴权失败"),
    SUIT_NAME_ERROR                                   (0003, "用例suitName为空"),
    MCC_SUIT_ERROR                                    (0004, "MCC中无该Suit配置"),
    PARAM_ERROR                      				  (0005, "请求参数错误"),
    PARAM_LOST                       				  (0006, "请求参数缺失"),
    PARAM_ILLEGAL                      				  (0007, "请求参数非法"),
    SYSTEM_ERROR                     				  (0010, "系统错误"),
    SANDBOX_MODULE_INSTALL_ERROR                      (0011, "安装并加载失败，请查看日志确认详细原因"),
    SANDBOX_MODULE_QUERY_ERROR                        (0012, "未查询到相关安装模块"),
    GET_PROPERTIES_ERROR                              (0013, "获取配置信息异常"),
    CHAIN_INFO_UPLOAD_ERROR                           (0014, "行程上送失败"),
    CHAIN_INFO_UPLOAD_SUCCESS                         (0015, "行程上送成功"),
    CHAIN_INFO_REPEATE_ERROR                          (0016, "本次搜集的行程信息和上次相同，不进行上送更新"),
    //CSV处理时异常
    ILLEGAL_ACCESS_EXCEPTION_ERROR                    (0017, "非法越权访问异常"),
    INSTANTIATION_EXCEPTION_ERROR                     (0017, "非法越权访问异常"),
    IO_EXCEPTION_ERROR                                (0020, "文件操作异常"),
    NO_SUCH_FIELD_EXCEPTION_ERROR                     (0021, "反射域缺失异常"),
    UPLOAD_CORE_CHAIN_GET_NULL_ERROR                  (0022,"HTTP上送返回空"),
    UPLOAD_CORE_CHAIN_FAIL_ERROR                      (0023,"链路信息上送失败"),
    PACKAGE_INFO_IS_BLANK                             (0024,"Package信息为空"),
    DOES_NOT_SUPPORT_UPLOAD                           (-1,"不支持链路信息上送")
    ;

    private Integer code;
    private String message;

    private static final Map<Integer, ErrorCodeEnum> CODE_MAP = Maps.newHashMapWithExpectedSize(ErrorCodeEnum.values().length);

    static {
        Arrays.stream(ErrorCodeEnum.values()).forEach(errorCodeEnum -> CODE_MAP.computeIfAbsent(errorCodeEnum.getCode(), t -> errorCodeEnum));
    }

    public static ErrorCodeEnum getErrorCodeEnum(int errorCode) {
        return CODE_MAP.getOrDefault(errorCode, SYSTEM_ERROR);
    }
}