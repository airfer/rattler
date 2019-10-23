package com.airfer.rattler.chain;

import com.airfer.rattler.app.Application;
import com.airfer.rattler.aspect.CoreChain;
import com.airfer.rattler.bean.CoreChainNormal;
import com.airfer.rattler.provider.TestDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;


/**
 * Author: wangyukun
 * Date: 2019/10/23 上午9:50
 */
@Slf4j
@SpringBootTest(classes= Application.class)
public class CoreChainTest  extends AbstractTestNGSpringContextTests {
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
        assertThat(
                StringUtils.replace(CoreChain.coreChainCapture(),"\n",""))
                .contains(coreChainNormal.getCaptureResult());
    }

}
