package com.airfer.rattler.properties;

import com.airfer.rattler.utils.PropertiesProvider;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.*;


/**
 * Author: wangyukun
 * Date: 2019/10/8 下午7:57
 */
@Slf4j
public class ProtertiesTest {

    @Test(description = "获取熟悉测试")
    public void propertiesGet(){
        String key="refresh_interval";
        String value= PropertiesProvider.getProperties(key);
        assertThat(value).contains("30");
    }

}
