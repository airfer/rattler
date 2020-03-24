package com.airfer.rattler.chain;

import com.airfer.rattler.app.Application;
import com.airfer.rattler.data.ClassWithCoreChain;
import com.airfer.rattler.data.MethodWithCoreChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

/**
 * Author: wangyukun
 * Date: 2019/10/25 上午9:57
 */
@Slf4j
@SpringBootTest(classes= Application.class)
public class AspectTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MethodWithCoreChain methodWithCoreChain;

    @Autowired
    private ClassWithCoreChain classWithCoreChain;

    /**
     * 校验方法切面
     */
    @Test(description = "校验方法切面是否正常工作")
    public void chainMethodAspectTest(){
       methodWithCoreChain.MethodWithCoreChainTest01();
    }

    /**
     * 校验类切面
     */
    @Test(description = "校验类切面是否正常工作")
    public void chainClassAspectTest(){
        classWithCoreChain.ClassWithCoreChainTest01();
    }
}
