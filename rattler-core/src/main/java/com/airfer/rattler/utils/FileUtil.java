package com.airfer.rattler.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

/**
 * Author: wangyukun
 * Date: 2019/10/23 上午11:21
 */
@Slf4j
public class FileUtil {

    /**
     * 查找到指定的Data文件
     * @param folder 待搜索的文件目录
     * @param fileName 文件名
     * @return 已匹配文件
     */
    private static File findFileInFolder(File folder, String fileName) {
        if (StringUtils.isEmpty(fileName.trim()) || null == folder || !folder.exists()) {
            return null;
        }
        File[] files = folder.listFiles();
        if(files != null){
            for (File file : files) {
                if (file.isFile() && file.getName().equals(fileName)) {
                    return file;
                } else if (file.isDirectory()) {
                    File file2 = findFileInFolder(file, fileName);
                    if (null != file2) {
                        return file2;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取当前目录
     * @param fileName 文件名称
     * @return 已匹配文件(递归式搜索)
     */
    public static File findFileInCurrentProject(String fileName) {
        return findFileInFolder(new File(System.getProperty("user.dir")).getAbsoluteFile(), fileName);
    }

    /**
     * 将结果写入到本地文件中，用于后续信息的获取
     * @param result
     * @param fileName
     * @return
     */
    public static Boolean storeResultToLocal(String result,String fileName){
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
            return true;
        }catch(IOException e){
            e.printStackTrace();
            throw new RuntimeException("链路检测结果写入文件失败");
        }
    }
}
