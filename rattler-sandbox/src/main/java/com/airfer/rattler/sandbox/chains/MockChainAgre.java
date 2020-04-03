package com.airfer.rattler.sandbox.chains;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Splitter;

import java.util.List;
import java.util.Map;

/**
 * Author: wangyukun
 * Date: 2020/4/3 下午3:32
 */
public class MockChainAgre implements ChainAgreInterface{

    String errorMsgForIdentity="can not find identity in res";
    String errorMsgForChainName="can not find chain in res";

    /**
     * mock获取指定identityId下的链路信息
     * @param identityId 服务唯一标识,如果不存在可为空
     * @param name 链路名称
     * @return String[]
     */
    @Override
    public String[] getChainMethods(String identityId,String name) {
        String mockRes =
                "{\"airfer_rattler_test\":" +
                        "{\"chainName01\":\"MethodWithCoreChainTest02,MethodWithCoreChainTest01,ClassWithCoreChainTest01,ClassWithCoreChainTest02,ClassWithCoreChainTest03,ClassWithCoreChainTest04\"," +
                        "\"chainName02\":\"MethodWithCoreChainTest03\"}}";
        Map<String, Map<String, String>> mockResMap = JSONObject.parseObject(mockRes, new TypeReference<Map<String, Map<String, String>>>() {
        });
        if (!mockResMap.keySet().contains(identityId)) {
            throw new RuntimeException(errorMsgForIdentity);
        }
        if (!mockResMap.get(identityId).keySet().contains(name)) {
            throw new RuntimeException(errorMsgForChainName);
        }
        String methods = mockResMap.get(identityId).get(name);
        return Splitter.on(",").omitEmptyStrings().splitToList(methods).toArray(new String[0]);
    }
}
