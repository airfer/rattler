package com.airfer.rattler.aspect;

import com.airfer.rattler.annotations.CoreChainClass;
import com.airfer.rattler.annotations.CoreChainMethod;
import com.airfer.rattler.client.HttpClients;
import com.airfer.rattler.enums.ErrorCodeEnum;
import com.airfer.rattler.utils.PropertiesProvider;
import com.alibaba.fastjson.JSON;
import com.github.rholder.retry.*;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Author: wangyukun
 * Date: 2019/9/27 上午10:32
 */
@Slf4j
@Aspect
@Component
public class CoreChain {

    private static ConcurrentMap<String,String> coreChainMap= Maps.newConcurrentMap();
    private static ConcurrentMap<String,ConcurrentMap<String,String>> response=Maps.newConcurrentMap();
    private static String lastCoreChainMapStr="";
    /**
     * 链路收集开关,true开启
     */
    private static final String CHAIN_CAPTURE_SWITCH="chain_capture_switch";
    /**
     *  链路信息上送地址，http协议
     */
    private static final String CHAIN_UPLOAD_URL="upload_url";
    /**
     * 静态待扫描package信息
     */
    private static final String PACKAGE_INFO="package_name";
    /**
     * 定时收集刷新间隔
     */
    private static final String REFRESH_INTERVAL="refresh_interval";

    /**
     * 服务唯一表示信息，和美团appkey概念相同
     */
    private static final String SERVER_ID="server_id";

    /**
     * 定义retry对象
     */
    private static Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
            .retryIfResult(Predicates.equalTo(false))
            .retryIfExceptionOfType(RuntimeException.class)
            .retryIfRuntimeException()
            //2s重试一次,最多重试3次
            .withWaitStrategy(WaitStrategies.fixedWait(2,TimeUnit.SECONDS))
            .withStopStrategy(StopStrategies.stopAfterAttempt(3))
            .build();

    /**
     * 定义callable对象
     */
    private static Callable<Boolean> callable=new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            String res=HttpClients.uploadCoreChainInfo(
                    PropertiesProvider.getProperties(CHAIN_UPLOAD_URL),
                    JSON.toJSONString(response));
            return StringUtils.isBlank(res) ? false: true;
        }
    };

    /**
     * 获取配置文件中的收集开关，true开启收集
     * @return 开关状态
     */
    private static Boolean getChainCaptureSwitch(){
        return Boolean.valueOf(PropertiesProvider.getProperties(CHAIN_CAPTURE_SWITCH));
    }

    /**
     * 获取待扫描的package
     * @return package名称
     */
    private static String getPackageInfo(){
        return PropertiesProvider.getProperties(PACKAGE_INFO);
    }

    /**
     * 获取配置文件中的定时时间
     * @return 时间
     */
    private static Long getRefreshInterval(){
        return Long.valueOf(PropertiesProvider.getProperties(REFRESH_INTERVAL));
    }

    /**
     * 获取服务标识
     * @return 服务标识
     */
    private static String getServerId(){
        return PropertiesProvider.getProperties(SERVER_ID);
    }

    /**
     * 声明定时线程池，用于收集失败重新收集以及热修复等场景
     */
    private static final ScheduledExecutorService ETCP_QA_CLIENT_SCHEDULED_POOL = new ScheduledThreadPoolExecutor(
                    1,
                    new ThreadFactoryBuilder()
                            .setNameFormat("client-scheduled-pool-%d")
                            .setUncaughtExceptionHandler(((t, e) -> log.error("async task execute exception, thread: {}", t.getName(), e)))
                            .setDaemon(true)
                            .build(),
                    new ThreadPoolExecutor.AbortPolicy()
    );

    //启动链路收集
    static{
        if(getChainCaptureSwitch()){
            log.info("链路收集开启...");
            coreChainCapture();
            //刷新间隔设置为-1,不启动定时刷新
            uploadChainMapInfoScheduleTask();
        }else{
            log.info("链路收集关闭！");
        }
    }

    /**
     * 本意是用作coreChainClass的切面，目前采用其他方式
     * @param proceedingJoinPoint
     */
    @Deprecated
    @Around(value = "PointCuts.chainClassAroundPoint()")
    public void coreChainClassAround(ProceedingJoinPoint proceedingJoinPoint){
        try{
            Object classSignature=proceedingJoinPoint.getTarget();
            Method[] methods=classSignature.getClass().getDeclaredMethods();

            List<String> methodNameList= Lists.newArrayList();
            //获取class的annotation信息
            CoreChainClass coreChainClass= AnnotationUtils.findAnnotation(classSignature.getClass(), CoreChainClass.class);
            String chainName=coreChainClass.coreChainName();
            Integer methodWeight=coreChainClass.weightEnum().getCode();
            //获取方法名称信息列表
            Arrays.stream(methods).forEach(method -> {
                methodNameList.add(method.getName());
            });
            Map<String,String> result= ImmutableMap.of(chainName,methodNameList.toString());
            log.info(JSON.toJSONString(result));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 本意是用作coreChainMethod的切面，目前采用其他方式
     * @param proceedingJoinPoint
     */
    @Deprecated
    @Around("PointCuts.chainMethodAroundPoint()")
    public void coreChainMethodAround(ProceedingJoinPoint proceedingJoinPoint){
        try{
            MethodSignature methodSignature=(MethodSignature)proceedingJoinPoint.getSignature();
            CoreChainMethod coreChainMethod=AnnotationUtils.findAnnotation(methodSignature.getMethod(),CoreChainMethod.class);
            String chainName=coreChainMethod.coreChainName();
            String methodName=methodSignature.getName();
            List<String> methodNameList=Lists.newArrayList(methodName);
            log.info("核心链路被更新: {}",JSON.toJSONString(coreChainMap));
            Map<String,String> result= ImmutableMap.of(chainName,methodNameList.toString());
            log.info(JSON.toJSONString(result));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 更新coreChainInfo的信息
     * @param chainName
     * @param methodNameList
     */
    public static void updateChainMap(String chainName,List<String> methodNameList){
        //如果信息不存在将获取信息放入Map中
        coreChainMap.computeIfAbsent(chainName,t ->
                Joiner.on(",")
                        .skipNulls()
                        .join(methodNameList)
        );
        //如果该链信息已存在则对保存的值进行去重并合并
        coreChainMap.computeIfPresent(chainName,(t,oldvalue)->{
            String rawNameStr=oldvalue + "," + Joiner.on(",").skipNulls().join(methodNameList);
            return Joiner.on(",").skipNulls().join(
                    Splitter.on(",")
                            .omitEmptyStrings()
                            .splitToList(rawNameStr)
                            .stream()
                            .distinct()
                            .collect(Collectors.toList())
            );
        });
    }

    //定时任务刷新
    public static void uploadChainMapInfoScheduleTask(){
        try{
            Long refreshInterval=getRefreshInterval();
            if(refreshInterval>0) {
                log.info("定时任务开启...");
                ETCP_QA_CLIENT_SCHEDULED_POOL.scheduleWithFixedDelay(() -> {
                    log.info("ChainMapInfo: {}", JSON.toJSONString(coreChainMap));
                    coreChainCapture();
                    //每1个小时刷洗一次
                }, 1L,refreshInterval, TimeUnit.SECONDS);
            }
        }catch(IllegalArgumentException e){
            log.error("",e);
        }catch(Exception e){
            log.error("",e);
        }
    }

    /**
     * 反射核心方法，获取方法或者类上的注解信息
     */
    private static void reflectionsCore(){
        //对package进行预先判断
        String packageInfo=getPackageInfo();
        if(StringUtils.isEmpty(packageInfo)){
            log.error(ErrorCodeEnum.PACKAGE_INFO_IS_BLANK.getMessage());
            throw new RuntimeException(ErrorCodeEnum.PACKAGE_INFO_IS_BLANK.getMessage());
        }
        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        //http://www.voidcn.com/article/p-xvhwzlaz-btn.html
        Collection<URL> result=ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0]));
        //构建反射类
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(
                        // don't exclude Object.class
                        new SubTypesScanner(false),
                        new ResourcesScanner(),
                        new MethodAnnotationsScanner(),
                        new TypeAnnotationsScanner())
                .setUrls(result)
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageInfo))));

        //先获取方法注解
        Set<Method> methodsWithAnnotationed=reflections.getMethodsAnnotatedWith(CoreChainMethod.class);
        methodsWithAnnotationed.stream()
                .map(method -> {
                    if (!method.isAnnotationPresent(CoreChainMethod.class)) {
                        throw new RuntimeException(ErrorCodeEnum.UNEXCEPTED_ERROR.getMessage());
                    }
                    CoreChainMethod coreChainMethod = method.getAnnotation(CoreChainMethod.class);
                    updateChainMap(coreChainMethod.coreChainName(), Lists.newArrayList(method.getName()));
                    return method.getName();});

        //获取指定类上的注解
        Set<Class<?>> classesWithAnnationed=reflections.getTypesAnnotatedWith(CoreChainClass.class);
        classesWithAnnationed.stream()
                .map(classinfo ->{
                    if(! classinfo.isAnnotationPresent(CoreChainClass.class)){
                        throw new RuntimeException(ErrorCodeEnum.UNEXCEPTED_ERROR.getMessage());
                    }
                    CoreChainClass coreChainClass=classinfo.getAnnotation(CoreChainClass.class);
                    Method[] methods=classinfo.getDeclaredMethods();

                    updateChainMap(coreChainClass.coreChainName(),
                            Arrays.stream(methods).map(method -> method.getName()).collect(Collectors.toList())
                    );
                    return classinfo.getName(); });
    }

    /**
     * 链路收集核心方法，用于收集链路信息
     * @return 结果信息
     */
    public static ConcurrentMap<String,ConcurrentMap<String,String>> coreChainCapture(){
        reflectionsCore();
        //定时任务开启的情况下，如果收集的链路信息和上一次相同，则不进行数据上送
        if(StringUtils.equalsIgnoreCase(lastCoreChainMapStr,JSON.toJSONString(coreChainMap))){
            log.info(ErrorCodeEnum.CHAIN_INFO_REPEATE_ERROR.getMessage());
            return null;
        }
        //将获取的链路信息和服务标识进行关联
        response.put(getServerId(),coreChainMap);
        //将信息上传到远程服务器,目前只支持http方式传送
        if(!StringUtils.equals(ErrorCodeEnum.DOES_NOT_SUPPORT_UPLOAD.getCode().toString(),PropertiesProvider.getProperties(CHAIN_UPLOAD_URL))
                && !coreChainMap.isEmpty()){
            try{
                retryer.call(callable);
                lastCoreChainMapStr=JSON.toJSONString(coreChainMap);
            }catch(Exception e){
                log.error(ErrorCodeEnum.UPLOAD_CORE_CHAIN_FAIL_ERROR.getMessage(),e);
            }
        }
        log.info(JSON.toJSONString(response));
        return response;
    }
}

