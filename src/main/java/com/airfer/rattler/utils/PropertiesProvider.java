package com.airfer.rattler.utils;

import com.airfer.rattler.enums.ErrorCodeEnum;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Author: wangyukun
 * Date: 2019/9/27 上午11:11
 */
@Slf4j
public class PropertiesProvider {

    /**
     * 获取配置信息
     * @param keyWord 属性信息
     * @return 属性值
     */
    public static String getProperties(String keyWord) {
        if(StringUtils.isBlank(keyWord)){
            throw new RuntimeException(ErrorCodeEnum.PARAM_LOST.getMessage());
        }
        InputStream in = PropertiesProvider.class.getClassLoader().getResourceAsStream("rattler.properties");
        //String filePath = System.getProperty("user.dir") + "/resource/unicorn.properties";
        Properties properties=null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("utf-8")));
            properties = new Properties();
            properties.load(br);
        } catch (IOException e) {
            log.error(ErrorCodeEnum.UNEXCEPTED_ERROR.getMessage(),e);
            e.printStackTrace();
        }
        String propValue=properties.getProperty(keyWord);
        Preconditions.checkNotNull(propValue,ErrorCodeEnum.GET_PROPERTIES_ERROR.getMessage());
        return propValue;
    }
}
