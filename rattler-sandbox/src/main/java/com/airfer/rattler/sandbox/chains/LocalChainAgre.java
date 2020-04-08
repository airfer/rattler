package com.airfer.rattler.sandbox.chains;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Splitter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Author: wangyukun
 * Date: 2020/4/3 下午3:32
 */
public class LocalChainAgre implements ChainAgreInterface{

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
        String res = getResultFromLocalFile();
        Map<String, Map<String, String>> mockResMap = JSONObject.parseObject(res, new TypeReference<Map<String, Map<String, String>>>() {
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

    /**
     * 从本地获取链路存储数据
     * @return string
     */
    public String getResultFromLocalFile(){
        String file=System.getProperty("user.home") + "/" + ".rattler/" + "chain.json";
        try{
            return FileUtils.readFileToString(new File(file),"utf-8");
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("从本地读取链路数据错误");
        }
    }
}
