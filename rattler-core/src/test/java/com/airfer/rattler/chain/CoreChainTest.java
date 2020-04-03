package com.airfer.rattler.chain;

import com.airfer.rattler.app.Application;
import com.airfer.rattler.aspect.CoreChain;
import com.airfer.rattler.bean.CoreChainNormal;
import com.airfer.rattler.enums.ErrorCodeEnum;
import com.airfer.rattler.provider.TestDataProvider;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import sun.misc.BASE64Decoder;
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
            description = "链路信息获取校验",
            dataProvider= "CoreChainInfoTestDataProvider",
            dataProviderClass = TestDataProvider.class
    )
    public void getCoreChainTest(CoreChainNormal coreChainNormal){
        BASE64Decoder decoder = new BASE64Decoder();
        try{
            byte[] result=decoder.decodeBuffer(coreChainNormal.getCaptureResult());
            String rawExpectedStr=new String(result,"UTF-8");
            ConcurrentMap<String,ConcurrentMap<String,String>> rawExpectedMap=JSON.parseObject(rawExpectedStr,
                    new TypeReference<ConcurrentMap<String,ConcurrentMap<String,String>>>(){});
            assertThat(rawExpectedMap.equals(CoreChain.coreChainCapture())).isTrue().withFailMessage("maps diff");
        }catch(Exception e){
            e.printStackTrace();
            assertThat(true).isFalse().withFailMessage(ErrorCodeEnum.UNEXCEPTED_ERROR.getMessage());
        }
    }

}
