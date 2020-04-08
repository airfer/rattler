![logo](https://airfer.github.io/images/rattler/rattler.jpg) 

## rattler 

[![License](https://img.shields.io/badge/License-GPL%203.0-red.svg)](LICENSE)
[![Build Status](https://api.travis-ci.com/airfer/rattler.svg?branch=master)](https://travis-ci.com/airfer/rattler)
[![GitHub issues](https://img.shields.io/github/issues/airfer/rattler.svg)](https://github.com/airfer/rattler/issues)
[![codecov](https://codecov.io/gh/airfer/rattler/branch/master/graph/badge.svg)](https://codecov.io/gh/airfer/rattler)


## 项目简介 

每个服务都有属于自身的核心特性。我们将服务的核心特性进行抽象，并使用核心链路这个概念来具象化核心特性。说的更直白一些，核心链路覆盖了核心服务绝大部分功能(超过80%)，例如某服务为交易服务，那么下单链路、付款链路、退单链路、退款链路都属于核心链路。

在进行迭代开发的过程中，新增或者修改的代码对核心链路的影响一直都有RD评估后给出，缺乏量化的标准。比如现在对交易服务中的某个公共service做了修改，那么这个service的变动会影响那些核心链路呢？这部分目前没有数据支撑。

rattler是一个侵入式的核心链路信息收集工具，通过CoreChainClass注解以及CoreChainMethod注解来收集服务的链路信息，然后将链路信息上送到指定的服务器，目前仅支持http上送。

代码的变更最后反馈到核心链路的变更，这样就有了一个量化的数据来衡量本次需求对原有核心链路的影响.

> **需要注意的是：这里核心链路的概念并非指的是多个微服务之间通过HTTP或者RPC组成的调用链路，而是指在单个微服务本身中函数之间的链路调用关系。本工具更加适用于偏底层的核心服务，比如支付的记账管理、搜索引擎的广告检索、金融对账结算等类似场景。**


## 链路信息采集方式对比 

目前链路采集方式，目前主要有两种方式：一种手动采集、手动记录，以文本文档的方式存储（xml,yml,xmind,xls），一种为注解方式，现在就主要的收集方式做一下对比:
<table>
    <tr>
        <td>记录方式</td>
        <td>优势</td>
        <td>不足</td>
    </tr> 
    <tr>
        <td>
            以注解形式内嵌在代码中
        </td>
        <td>
            <ul>
                <li>链路信息维护以及更新简单、更新实效性高</li>             
                <li>RD可无障碍参与核心链路信息建设</li>             
                <li>提供更细维度支持，比如方法的权重</li>
            </ul>
        </td>
        <td>
            <ul>            
                <li>需要对代码进行改造</li>       
                <li>链路信息不直观</li>
            </ul>
        </td>
    </tr>
    <tr>
        <td>
            文本文档的方式存储（xml,yml,xmind,etc）
        </td>
        <td>
            <ul>
                <li>链路调用较为直观</li>     
                <li>无需代码层面的改造</li>
            </ul>                    
        </td>
        <td>
            <ul>
                <li>链路信息的维护以及更新难度大</li>     
                <li>推动RD参与建设难度较大</li>        
                <li>无法更细维度进行统计</li>
            </ul>
        </td>
    </tr>
</table>
由于需求的快速迭代，核心链路信息的维护成本将会逐渐变高，所以优先考虑对核心链路信息的维护和更新。当前以注解的形式进行信息收集更为妥当。当然如果服务较为稳定，核心链路的变更较少，那么可以采用文本的方式记录，具体方式可根据实际情况以及自身业务特点进行灵活选择。 

## 核心原理 

1、 定义CoreChainClass、CoreChainMethod注解，使用注解在服务工程中进行标注(侵入式)

2、 利用Reflections的反射机制，静态扫描指定package的标注类或者标注方法

3、 将扫描得到的核心链路数据上送到指定的服务或者存储在本地（Local），上送方式目前仅支持HTTP

## 使用方法 

由于rattler进行链路信息采集，所以需要在项目中集成该项目SDK，通过jar包引入。

### java包引入 

在使用之前需要事先通过pom引入jar文件，可以使用本开源的jar包，也可以自行编译上传到私有maven仓库，示例：
```xml
<dependency>
    <groupId>com.github.airfer</groupId>
    <artifactId>rattler-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 注解引入 

注解引入，有两种引入方式。可以通过CoreChainClass注解和CoreChainMethod注解引入。由于目前是侵入式收集，会直接修改代码，所以最好和RD一起来做这个事情。
由于链路收集的收集采用静态扫描方式，所以理论上不会对注入服务造成性能影响。为了避免对线上服务造成潜在影响，可以在线上环境可以关闭链路信息收集以及信息上送服务，如何关闭请参见配置说明部分。

> 由于[CoreChain](/src/main/java/com/airfer/rattler/aspect/CoreChain.java)通过Component引入，所以在注解引入后需要在ComponentScan配置中添加rattler所在的包名，示例如下所示。如果对本项目重新打包并变更包名则可省去扫描包添加的步骤。当前对注解标注的权重信息留在下一期支持


- CoreChainClass注解引入
```java
@CoreChainClass(coreChainName="查询链路",weightEnum=MethodWeightEnum.LOW)
public class QueryService{
   /**
   * 通过Class方式引入，所有在class中的方法都将被标注相同的链路名称，以及链路权重
   */ 
}
``` 

- CoreChainMethod注解引入
```java
@Serivice
public class QueryService{
   /**
   * 通过Method方式引入，只有该Method会被标注，其他方法不会被标注
   */ 
   @Autowired
   private CommonService commonService;
   
   @CoreChainMethod(coreChainName="查询链路",weightEnum=MethodWeightEnum.LOW)
   private String queryOrderNum(){
       try{
           return commonService.queryOrderInfo();
       }catch (RuntimeException re){
           log.error("runtime error");
       }
   }
}
```

- ComponentScan扫描包添加 
```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,MongoAutoConfiguration.class,MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@ComponentScan(basePackages = {"com.airfer.rattler"})
public class Application {
}
```

## 配置文件使用 

服务引入jar包，并添加链路注解后，需要添加rattler配置文件。添加方式为在resource目录下新增rattler.properties文件，内容示例如下：
```text
#链路上报开关，true开启，false关闭。在线上环境可以选择关闭【必填】
chain_capture_switch=true

#链路信息的上送接口，采用post方式进行上送【选填】|【链路开关打开必填】
#upload_url=-1 表示不进行上送
upload_url=http://upload.domain.info.com/chain/test

#待扫描的服务package名称，目前不支持多package配置【必填】|【链路开关打开必填】
package_name=com.airfer.rattler.data

#在链路开关开启且后端服务启动后，rattler会按照指定的刷新频率刷新收集，单位秒【选填】|【链路开关打开必填】
# refresh_interval=-1 表示不启动定时刷新
refresh_interval=-1

#server_id唯一标识一个服务，和美团的appkey是同一个概念【选填】|【链路开关打开必填】
server_id=airfer_rattler_test
```

## rattler-func-detection

rattler-func-detetion 是一个基于Git Patch文件的开源代码分析工具(仅限java服务)，主要用于函数变动探测，目前支持python2.x以及python3.x版本。通过以下命令：
```bash
git diff feature/* master >result.patch
```
获取patch文件后，解析patch文件，获取变动的函数列表，并将函数列表信息上送到指定服务器，目前仅支持Http协议

### 主要用途
* 基于git patch文件分析函数变动，进行服务核心链路冲撞率(collision rate)计算，评估影响面大小
* 统计核心链路的变更频率，为服务的重构以及优化提供数据支持


### 安装方法

#### 通过pip安装

如果不需要对代码进行查看或者二次开发，可以使用pip来进行安装，支持python2.x以及python3.x，命令如下：
```text
pip install rattler-func-detection
```

#### 通过源码安装
1. 下载源代码，使用python setup.py install进行安装
2. 使用rattler-func-detection 命令运行,可通过 --help命令查看可选项


### 命令行信息详解

```text
Usage: rattler-func-detection [OPTIONS]

  :param rootPath: 扫描根路径 :param diffFilePath: :return:

Options:
  --root_path  TEXT  扫描根路径,git clone代码后根目录
  --diff_path  TEXT  git diff文件路径
  --server_id  TEXT  server_id,服务唯一标识
  --upload_url TEXT  数据上送地址
  --help             Show this message and exit.

```

## 链路碰撞分析 

假设我们现在有两个核心链路A和A1(***单个服务内部函数之间的链路调用关系，非微服务之间的调用，A-G、A1-G1皆为函数***),两条链路中的所有调用函数都可通过CoreChain类注解进行收集。而需求迭代所造成的代码变动可通过rattler-func-detection进行收集，链路碰撞示意图如下所示：
![detect](https://airfer.github.io/images/rattler/func-detection.jpg)

## 结果预览
拿到链路数据之后，就可以计算每条链路中有多少函数处于被影响的范围。虽然在进行链路信息标注时设置了权重值，但是目前权重值还没有被应用于计算，预计下期项目提供支持
```text
{
    u'data': u'{
        "all": "0.36", // 整体冲撞率
        "A"  : "0.57", // 消费链路冲撞率
        "A1" : "0.23"  // 开户链路冲撞率
    }',
    u'resCode': u'00'
}
```

## 基于核心链路的故障注入

核心链路应用范围远不止链路碰撞分析，核心链路可以被认为是服务的主脉络，它撑起了整个服务。如果脉络中的某个节点或者一批节点发生故障，对整个服务的影响如何呢？
发生故障以后，是否会按照故障预期将结果返回给上层调用或者直接返回给用户端？上述在通常的功能测试中涉及的很少，而我们在收集核心链路的基础上，可以自动完成故障注入。

故障注入的方式可以分为2种，手动注入和自动注入。

### 手动注入 

手动注入顾名思义就是手动选择需要注入的类或者方法，自动注入则可按照自定义策略或者默认策略来对方法进行注入。手动注入的方式可参照下方的架构设计：

![manul-injection](https://airfer.github.io/images/rattler/manul-injection.jpg) 

在上述架构中，核心链路信息被上送到外部处理系统后，会有6步操作完成手动方法注入 

```text
step1: 上送的核心链路信息被处理并存储在存储介质中如DB 
step2: 配置管理模块获取链路信息,并形成配置项比如可选择那些类、那些方法进行注入,供用户填写 
step3: 用户配置完注入信息后,会将个性化定制信息进行存储 
step4: 本地缓存系统,会根据特定标识ID去定时拉取需要已配置的故障注入配置项
step5: 配置系统交互返回
step6: 故障注入系统在刷新时,会强制从本地缓存中读取配置信息，完成注入过程 
```

### 自动注入

自动注入，通过程序中预设的注入策略来进行故障（Exception）注入，无需人工手动在平台上进行选择。
目前提供以下自动注入方式：

- 根据链路信息进行注入,比如基于核心链路名称的注入方式 

- 基于自定义策略的注入,策略可自己来实现, 满足定制化需求

```java
/**
 * Author: wangyukun
 * Date: 2020/4/2 下午5:42
 */
@MetaInfServices(Module.class)
@Information(id = "auto-injection-rattler",author = "wangyukun",version = "0.0.1")
public class AutoInjectionModule implements Module{
    @Resource
    private ModuleEventWatcher moduleEventWatcher;
    /**
         * 基于真实的链路方法,自动注入示例
         */
        @Command("autoInjectForLocal")
        public void autoInjectForLocal() {
            new EventWatchBuilder(moduleEventWatcher)
                    .onClass("com.*")
                    /**
                     * onChain基于链路的注入,会根据服务标识以及链路的名称获取方法列表进行注入
                     * @param identityId 服务唯一标识
                     * @name 链路名称
                     */
                    .onChain("airfer_rattler_test","red-alarm",
                            new LocalChainAgre())
                    /**
                     * 如果设置了策略信息，则会根据策略来对方法进行过滤
                     * 例如：下方设置的为随机50%策略,那么核心链路中50%的方法会进行故障注入
                     * 策略可自行定义，实现InjectionStrategy接口即可
                     * 可能存在的情况：
                     * (1) 核心链路中指定前置的方法进行注入
                     * (2) 核心链路中对包含某个关键词的方法进行注入
                     */
                    .onStrategy(new RandomStrategy())
                    /**
                     * 示例：针对获取的方法直接抛出异常
                     */
                    .onWatch(new AdviceListener() {
                        @Override
                        protected void before(Advice advice) throws Throwable {
    
                            // 在此，你可以通过ProcessController来改变原有方法的执行流程
                            // 这里的代码意义是：改变原方法抛出异常的行为，变更为立即返回；void返回值用null表示
                            ProcessController.throwsImmediately(new RuntimeException("autoInjection"));
                        }
                    });
        }
}

```

## 使用限制

- rattler基于JDK8开发，使用了很多新特性，比如stream，对于基于低于JDK8的服务版本暂不支持
- 目前rattler仅适用于JAVA后端服务，对于非JAVA服务以及前端暂不支持

## 排期计划

- 方法权重值纳入链路冲撞分析范围 预计2020/05

