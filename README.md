![logo](https://airfer.github.io/images/rattler/rattler.jpg) 

## rattler 

[![License](https://img.shields.io/badge/License-GPL%202.0-blue.svg)](LICENSE)
[![Build Status](https://api.travis-ci.org/airfer/rattler.svg?branch=master)](https://travis-ci.com/airfer/rattler)
[![GitHub issues](https://img.shields.io/github/issues/airfer/rattler.svg)](https://github.com/airfer/rattler/issues)


## 项目简介 

每个服务都有属于属于自身的核心特性。我们将服务的核心特性进行抽象，使用核心链路这个概念来具象化核心特性。说的更直白一些，核心链路覆盖了核心服务绝大部分功能(超过80%)，例如某服务为交易服务，那么下单链路、付款链路、退单链路、退款链路都属于核心链路。

在进行迭代开发的过程中，新增或者修改的代码对核心链路的影响一直都有RD评估后给出，缺乏量化的标准。比如现在对交易服务中的某个公共service做了修改，那么这个service的变动会影响那些核心链路呢？这部分目前没有数据支撑。

rattler是一个侵入式的核心链路信息收集工具，通过CoreChainClass注解以及CoreChainMethod注解来收集服务的链路信息。然后将链路信息上送到指定的服务器，目前仅支持http上送。

代码的变更最后反馈到核心链路的变更，这样就有了一个量化的数据来衡量本次需求对原有核心链路的影响

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
由于需求的快速迭代，核心链路信息的维护成本将会逐渐变高，所以优先考虑对核心链路信息的维护和更新。当前已注解的形式进行信息收集更为妥当。当然如果服务较为稳定，核心链路的变更较少，那么可以采用文本的方式记录，具体方式可根据实际情况以及自身业务特点进行灵活选择。 

## 核心原理 

1、 定义CoreChainClass、CoreChainMethod注解，使用注解在服务工程中进行标注(侵入式)

2、 利用Reflections的反射机制，静态扫描指定package的标注类或者标注方法

3、 将扫描得到的核心链路数据上送到指定的服务，目前仅支持HTTP

## 使用方法 

由于rattler进行链路信息采集，所以需要在项目中集成该项目SDK，通过jar包引入。

### java包引入 

在使用之前需要事先通过pom引入jar文件，可以使用本开源的jar包，也可以自行编译上传到私有maven仓库，示例：
```xml
<dependency>
    <groupId>com.airfer.rattler</groupId>
    <artifactId>rattler</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 注解引入 

注解引入，有两种引入方式。可以通过CoreChainClass注解和CoreChainMethod注解引入。由于目前是侵入式收集，会直接修改代码，所以最好和RD一起来做这个事情。
由于链路收集的收集采用静态扫描方式，所以理论上不会对注入服务造成性能影响。为了避免对线上服务造成潜在影响，可以在线上环境可以关闭链路信息收集以及信息上送服务，如果关闭请参见配置说明部分

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

## 配置文件使用 

服务引入jar包，并添加链路注解后，需要添加rattler配置文件。添加方式为在resource目录下新增rattler.properties文件，内容示例如下：
```text
#链路上报开关，true开启，false关闭。在线上环境可以选择关闭【必填】
chain_capture_switch="true"

#链路信息的上送接口，采用post方式进行上送【选填】|【链路开关打开必填】
upload_url="http://upload.domain.info.com/chain"

#待扫描的服务package名称，目前不支持多package配置【必填】|【链路开关打开必填】
package_name="com.airfer.rattler"

#在链路开关开启且后端服务启动后，rattler会按照指定的刷新频率刷新收集，单位秒【选填】|【链路开关打开必填】
# refresh_interval=-1 表示不启动定时刷新
refresh_interval=30

#server_id唯一标识一个服务，和美团的appkey是同一个概念【选填】|【链路开关打开必填】
server_id="airfer_rattler"
```

## rattler-func-detection

rattler-func-detetion 是一个基于Git Patch文件的开源代码分析工具(仅限java服务)，主要用于函数变动探测。通过以下命令：
```bash
git diff feature/* master >result.patch
```
获取patch文件后，解析patch文件，获取变动的函数列表，并将函数列表信息上送到指定服务器，目前仅支持Http协议

### 主要用途
* 基于git patch文件分析函数变动，进行服务核心链路冲撞率(collision rate)计算，评估影响面大小
* 统计核心链路的变更频率，为服务的重构以及优化提供数据支持


### 安装方法

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

假设我们现在有两个核心链路A和A1,两条链路中的所有调用函数都可通过CoreChain类注解进行收集。而需求迭代所造成的代码变动可通过rattler-func-detection进行收集，链路碰撞示意图如下所示：
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

## 使用限制

- rattler基于JDK8开发，使用了很多新特性，比如stream，对于基于低于JDK8的服务版本暂不支持
- 目前rattler仅适用于JAVA后端服务，对于非JAVA服务以及前端暂不支持
- rattler-func-detection工具目前仅适配了python2.x版本,python3.x待开发

## 排期计划

- rattler-func-detection python3.x版本适配 预计2019/11
- 方法权重值纳入链路冲撞分析范围 预计2019/12

