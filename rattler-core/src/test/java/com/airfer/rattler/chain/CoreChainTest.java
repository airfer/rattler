package com.airfer.rattler.chain;

import com.airfer.rattler.app.Application;
import com.airfer.rattler.aspect.CoreChain;
import com.airfer.rattler.bean.CoreChainNormal;
import com.airfer.rattler.enums.ErrorCodeEnum;
import com.airfer.rattler.models.ChainMethodSignature;
import com.airfer.rattler.provider.TestDataProvider;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import sun.misc.BASE64Decoder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static org.assertj.core.api.Assertions.*;


/**
 * Author: wangyukun
 * Date: 2019/10/23 上午9:50
 */
@Slf4j
@SpringBootTest(classes= Application.class)
public class CoreChainTest extends AbstractTestNGSpringContextTests {
    /**
     * 获取coreChain实例
     */
    @Autowired
    private CoreChain coreChain;

    @Test(
            description = "链路信息获取校验"
    )
    public void getCoreChainTest(){
        final String expectedRes="{\"airfer_rattler_test\":{\"chainName01\":[{\"chainName\":\"chainName01\",\"extend\":\"\",\"methodName\":\"MethodWithCoreChainTest02\",\"methodWeight\":1},{\"chainName\":\"chainName01\",\"extend\":\"\",\"methodName\":\"MethodWithCoreChainTest01\",\"methodWeight\":1},{\"chainName\":\"chainName01\",\"extend\":\"\",\"methodName\":\"ClassWithCoreChainTest02\",\"methodWeight\":1},{\"chainName\":\"chainName01\",\"extend\":\"\",\"methodName\":\"ClassWithCoreChainTest01\",\"methodWeight\":1},{\"chainName\":\"chainName01\",\"extend\":\"\",\"methodName\":\"ClassWithCoreChainTest03\",\"methodWeight\":1},{\"chainName\":\"chainName01\",\"extend\":\"\",\"methodName\":\"ClassWithCoreChainTest04\",\"methodWeight\":1}],\"chainName02\":[{\"chainName\":\"chainName02\",\"extend\":\"\",\"methodName\":\"MethodWithCoreChainTest03\",\"methodWeight\":1}]}}";
        try{
            String result=CoreChain.coreChainCapture();
            Map<String,Map<String,List<Map<String,String>>>> realRes=JSON.parseObject(result,new TypeReference<Map<String,Map<String,List<Map<String,String>>>>>(){});
            Map<String,Map<String,List<Map<String,String>>>> expectedMap=JSON.parseObject(expectedRes,
                    new TypeReference<Map<String,Map<String,List<Map<String,String>>>>>(){});
            //assertThat(result.equals(expectedRes)).isTrue().withFailMessage("maps diff");
        }catch(Exception e){
            e.printStackTrace();
            assertThat(true).isFalse().withFailMessage(ErrorCodeEnum.UNEXCEPTED_ERROR.getMessage());
        }
    }

}
