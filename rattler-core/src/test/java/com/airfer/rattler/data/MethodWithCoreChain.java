package com.airfer.rattler.data;

import com.airfer.rattler.annotations.CoreChainMethod;
import org.springframework.stereotype.Component;

/**
 * Author: wangyukun
 * Date: 2019/10/23 下午3:31
 */
@Component
public class MethodWithCoreChain {

    /**
     * 定义待搜索的函数
     */
    @CoreChainMethod(coreChainName = "chainName01")
    public void MethodWithCoreChainTest01(){
        return;
    }

    @CoreChainMethod(coreChainName = "chainName01")
    private void MethodWithCoreChainTest02(){
        return;
    }

    @CoreChainMethod(coreChainName = "chainName02")
    private void MethodWithCoreChainTest03(){
        return;
    }
}
