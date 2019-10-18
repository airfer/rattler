## rattler-func-detection

rattler-func-detetion 主要用于函数变动探测，其用途主要有以下两个方面:

* 基于git patch文件分析函数变动，进行服务核心链路冲撞率(collision rate)计算，评估影响面大小
* 统计核心链路的变更频率，为服务的重构以及优化提供数据支持


### 使用方法

1. 下载源代码，使用python setup.py install进行安装
2. 使用rattler-func-detection 命令运行


### 命令行信息详解

Usage: rattler-func-detection [OPTIONS]

  :param rootPath: 扫描根路径 :param diffFilePath: :return:

Options:
  --root_path  TEXT  扫描根路径,git clone代码后根目录
  --diff_path  TEXT  git diff文件路径
  --server_id  TEXT  server_id,服务唯一标识
  --upload_url TEXT  数据上送地址
  --help             Show this message and exit.


