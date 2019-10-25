package com.airfer.rattler.data;

import com.airfer.rattler.annotations.CoreChainClass;
import org.springframework.stereotype.Component;

/**
 * Author: wangyukun
 * Date: 2019/10/23 下午3:30
 */
@CoreChainClass(coreChainName = "chainName01")
@Component
public class ClassWithCoreChain {

    /**
     * 根据定义待搜索的类，收集类中的函数
     */
    //ClassWithCoreChainTest01
    public void ClassWithCoreChainTest01(){
        return;
    }

    //ClassWithCoreChainTest02
    private void ClassWithCoreChainTest02(){
        return;
    }

    //ClassWithCoreChainTest03
    private void ClassWithCoreChainTest03(){
        return;
    }

    //ClassWithCoreChainTest05
    private void ClassWithCoreChainTest04(){
        return;
    }
}
