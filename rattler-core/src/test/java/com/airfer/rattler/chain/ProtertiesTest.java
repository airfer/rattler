package com.airfer.rattler.chain;

import com.airfer.rattler.app.Application;
import com.airfer.rattler.bean.PropertiesException;
import com.airfer.rattler.bean.PropertiesNormal;
import com.airfer.rattler.provider.TestDataProvider;
import com.airfer.rattler.utils.PropertiesProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

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

    @Test
    public void  storeResultToLocal(){
        String result="{\"airfer_rattler_test\":{\"chainName01\":\"MethodWithCoreChainTest02,MethodWithCoreChainTest01,ClassWithCoreChainTest01,ClassWithCoreChainTest02,ClassWithCoreChainTest03,ClassWithCoreChainTest04\",\"chainName02\":\"MethodWithCoreChainTest03\"}}";
        String fileName="chainResult.json";
        try{
            String folder=System.getProperty("user.home") + "/.rattler";
            if(! new File(folder).exists()){
                //一次可以创建单级或者多级目录
                FileUtils.forceMkdir(new File(folder));
            }
            File file = new File(folder + "/"+fileName);
            //创建一个文件夹，如果由于某些原因导致不能创建，则抛出异常
            //将结果写入文件
            FileUtils.writeStringToFile(file,result,"utf-8",false);
        }catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException("链路检测结果写入文件失败");
        }
    }
}
