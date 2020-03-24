package com.airfer.rattler.utils;

/**
 * Author: wangyukun
 * Date: 2019/10/23 上午11:23
 */
import com.airfer.rattler.enums.ErrorCodeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Preconditions;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class CsvUtil {
    /**
     * 解析CSV格式数据文件
     * @param file 测试数据文件
     * @return CSVParse 解析实例
     * @throws IOException IO异常
     */
    private static CSVParser parserCSV(File file) throws IOException {
        CSVFormat formator = CSVFormat.DEFAULT.withHeader();
        FileReader fileReader = new FileReader(file);
        return new CSVParser(fileReader, formator);
    }

    /**
     * 根据文件名称以及属性信息获取测试数据
     * @param dataFileName 文件名称
     * @param classType 类型信息
     * @return 测试数据 Object[][]
     */
    public static Object[][] getTestData(String dataFileName,Class<?> classType){
        //默认没有错误
        ErrorCodeEnum errorCodeEnum=ErrorCodeEnum.SUCCESS;
        List<Object> dataBeans= Lists.newArrayList();
        Class<?> _classType=classType;
        //获取所有属性
        Field[] fields=null;
        //获取当前类以及超类
        while(_classType != Object.class){
            fields=ArrayUtils.addAll(fields,_classType.getDeclaredFields());
            _classType=_classType.getSuperclass();
        }
        //查找当前文件
        File dataFile = FileUtil.findFileInCurrentProject(dataFileName);
        try{
            Preconditions.checkNotNullOrEmpty(fields);
            CSVParser parser = parserCSV(dataFile);
            Object accountInfo;
            for(CSVRecord dataRecord : parser) {
                accountInfo = classType.newInstance();
                //设置属性信息
                List<String> properties= Arrays.stream(fields
                ).map(
                        Field::getName
                ).collect(Collectors.toList());
                for (String pro : properties) {
                    String value = dataRecord.get(pro);
                    Preconditions.checkState(!StringUtils.isBlank(value), String.format("%s is blank", pro));
                    _classType=classType;
                    Field field=null;
                    //如果属性不在当前类中，去超类中继续寻找
                    while(_classType != Object.class){
                        try{
                            field = _classType.getDeclaredField(pro);
                            break;
                        }catch(NoSuchFieldException nsf){
                            _classType=_classType.getSuperclass();
                        }
                    }
                    if(field == null){
                        throw new NoSuchFieldException(ErrorCodeEnum.NO_SUCH_FIELD_EXCEPTION_ERROR.getMessage());
                    }
                    field.setAccessible(true);
                    field.set(accountInfo, "null".equalsIgnoreCase(value) ? null:value);
                }
                dataBeans.add(accountInfo);
            }
        }catch(IllegalAccessException e){
            log.error("error:",e);
            errorCodeEnum=ErrorCodeEnum.ILLEGAL_ACCESS_EXCEPTION_ERROR;
        }catch(InstantiationException e){
            log.error("error:",e);
            errorCodeEnum=ErrorCodeEnum.INSTANTIATION_EXCEPTION_ERROR;
        }catch(IOException e){
            log.error("error:",e);
            errorCodeEnum=ErrorCodeEnum.IO_EXCEPTION_ERROR;
        }catch (NoSuchFieldException e) {
            log.error("error:",e);
            errorCodeEnum=ErrorCodeEnum.NO_SUCH_FIELD_EXCEPTION_ERROR;
        }catch (Exception e) {
            log.error("error:",e);
            errorCodeEnum=ErrorCodeEnum.UNEXCEPTED_ERROR;
        }
        //对于异常情况，抛出异常
        if(!errorCodeEnum.equals(ErrorCodeEnum.SUCCESS)){
            throw new RuntimeException(errorCodeEnum.getMessage());
        }
        //数据集不应为空
        Preconditions.checkNotNullOrEmpty(dataBeans.toArray());
        int size=dataBeans.size();
        Object[][] testData=new Object [size][1];
        //填充数据集
        for (int i = 0; i < size; i++) {
            testData[i][0] = dataBeans.get(i);
        }
        return testData;
    }
}

