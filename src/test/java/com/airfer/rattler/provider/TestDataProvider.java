package com.airfer.rattler.provider;

import com.airfer.rattler.bean.CoreChainNormal;
import com.airfer.rattler.bean.PropertiesException;
import com.airfer.rattler.bean.PropertiesNormal;
import com.airfer.rattler.utils.CsvUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;

import java.io.IOException;

/**
 * Author: wangyukun
 * Date: 2019/10/23 下午2:27
 */
@Slf4j
public class TestDataProvider {

    /**
     * 获取正常情况下的用例数据集
     * @return data
     * @throws IOException
     */
    @DataProvider(name="PropertiesNormalTestDataProvider")
    public static Object[][] getPropertiesNormalTestData() throws IOException {
        String dataFileName="properties_normal.csv";
        return CsvUtil.getTestData(dataFileName, PropertiesNormal.class);
    }

    /**
     * 获取exception情况下的用例数据集
     * @return data
     * @throws IOException
     */
    @DataProvider(name="PropertiesExceptionTestDataProvider")
    public static Object[][] getPropertiesExceptionTestData() throws IOException {
        String dataFileName="properties_exception.csv";
        return CsvUtil.getTestData(dataFileName, PropertiesException.class);
    }

    /**
     * 获取com.airfer.rattler.data包下的链路信息数据集
     * @return data
     * @throws IOException
     */
    @DataProvider(name="CoreChainInfoTestDataProvider")
    public static Object[][] getCoreChainInfoTestData() throws IOException {
        String dataFileName="core_chain_test_result.csv";
        return CsvUtil.getTestData(dataFileName, CoreChainNormal.class);
    }

}
