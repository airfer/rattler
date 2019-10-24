package com.airfer.rattler.chain;

import com.airfer.rattler.app.Application;
import com.airfer.rattler.bean.PropertiesException;
import com.airfer.rattler.bean.PropertiesNormal;
import com.airfer.rattler.provider.TestDataProvider;
import com.airfer.rattler.utils.PropertiesProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;


/**
 * Author: wangyukun
 * Date: 2019/10/8 下午7:57
 */
@Slf4j
@SpringBootTest(classes= Application.class)
public class ProtertiesTest extends AbstractTestNGSpringContextTests {

    /**
     * 正常情况下获取属性信息，属性信息获取正常
     * @param propertiesNormal data
     */
    @Test(
        description = "属性信息存在的情况下，获取属性信息",
        dataProvider = "PropertiesNormalTestDataProvider",
        dataProviderClass = TestDataProvider.class
    )
    public void propertiesNormalTest(PropertiesNormal propertiesNormal){
        assertThat(PropertiesProvider.getProperties(propertiesNormal.getPropertyKey())).contains(propertiesNormal.getPropertyValue());
    }

    /**
     * 异常情况下获取属性信息：属性信息不对或者属性传递为空，抛出异常
     * @param propertiesException data
     */
    @Test(
        description = "属性值异常情况下，抛出运行时异常",
        dataProvider= "PropertiesExceptionTestDataProvider",
        dataProviderClass = TestDataProvider.class,
        expectedExceptions= RuntimeException.class
    )
    public void propertiesGetUndefined(PropertiesException propertiesException){
        PropertiesProvider.getProperties(propertiesException.getPropertyKey());
    }
}
