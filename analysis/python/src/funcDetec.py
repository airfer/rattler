#encoding=utf-8
import  re,os,click,json,io,logging

try:
    import requests
except ImportError as er:
    class NullHandler(logging.Handler):
        def emit(self, record):
            pass
    raise ImportError("引入requests库失败！",er.message)
#logging.getLogger(__name__).addHandler(NullHandler())

logging.basicConfig(level = logging.INFO,format = '%(asctime)s - %(name)s - %(levelname)s - %(message)s')

def contains(string,search):
    return True if string.find(search) != -1 else False

def getFunctionStatic(filePath):
    """
    :param filePath: 扫描文件路径
    :return:
    """
    functionMapDict={}
    functionLineCount={}
    #定义函数头扫描规则
    funcPattern="(?P<access>public|private|protected)" \
                "(\s)*(?P<static>static)?(?P<final>final)?(\s)*(?P<returnType>[\w<>]+)\s" \
                "(?P<funcName>\w+)[(](?P<params>.*)[)](?P<extra>.*)"
    funcPatternExec=re.compile(funcPattern)
    lineNum=0
    if not os.path.exists(filePath):
        raise RuntimeError(u"该文件不存在: %s" %filePath)
    with io.open(filePath,"r+",encoding="utf-8") as fr:
        logging.info(u"filePath: %s" %filePath)
        lines=fr.readlines()
        funcName = ""
        for line in lines:
            lineNum+=1
            #不等于空说明这一行为函数定义
            funcSearchRes=funcPatternExec.search(line)
            if funcSearchRes:
                resDic=funcSearchRes.groupdict()
                funcName=resDic["funcName"]
                if not len(funcName):
                    raise RuntimeError(u"获取函数名称出错")
                functionMapDict[funcName]=1 if resDic["extra"] and  resDic["extra"].find("{") != -1 else 0
                functionLineCount[funcName]=[lineNum]
            else:
                #说明此时已经经过了函数头部定义或者还没有进行函数比如说注释说明
                if line.strip().startswith("//"):
                    #logging.info("被注释的代码: %s" %line)
                    continue
                if len(funcName):
                    if contains(line,'{'):
                        functionMapDict[funcName] += 1
                    if contains(line,'}'):
                        functionMapDict[funcName] -= 1
                    #此时为0，说明已经统计完毕
                    if not functionMapDict[funcName]:
                        functionLineCount[funcName].append(lineNum)
                        #此时相当于此函数已经统计完毕，将名称置空
                        funcName=""
                else:
                    continue
    logging.info(u"函数和行号的映射关系如下:"+ str(functionLineCount))
    #logging.info("The Map dict: "+ str(functionMapDict))
    return functionLineCount

def codeDiffStatic(filePath):
    """
    diff_demo   -> diff --git a/account-service/pom.xml b/account-service/pom.xml
    change_demo -> @@ -730,6 +730,19 @@
    :return:
    """
    fileName=""
    fileNameLine={}
    #定义扫描头的部分
    diffFilePattern="diff\s--git\sa/(?P<source>(.*java))(\s)b/(?P<target>(.*java))"
    lineChangePattern="@@ -(?P<begin>(\d+)),(?P<interval>(\d+))\s[+](?P<begin2>\d+),(?P<interval2>\d+) @@"

    diffFilePatternExec=re.compile(diffFilePattern)
    lineChangePatternExec=re.compile(lineChangePattern)

    with io.open(filePath,"r+",encoding="utf-8") as fr:
        lines=fr.readlines()
        for line in lines:
            diffRes=diffFilePatternExec.search(line)
            if diffRes:
                diffResDict=diffRes.groupdict()
                fileName=diffResDict['source']
                if not len(fileName):
                    raise RuntimeError(u"diff --git pattern 解析失败")
                #为变更的文件统计行数
                fileNameLine[fileName]=[]
                continue
            elif fileName:
                changeDiff = lineChangePatternExec.search(line)
                if changeDiff:
                    changeDiffRes = changeDiff.groupdict()
                    if not changeDiffRes:
                        raise RuntimeError(u"@@ -a,b +c,d @@ 解析失败")
                    #将变更的具体行数统计进去
                    for index in range(0,int(changeDiffRes['interval2'])):
                        lineNo=int(changeDiffRes['begin2']) + index
                        fileNameLine[fileName].append(lineNo)
                    fileName=None
    logging.info(u"源文件变动的行数如下: " + str(fileNameLine))
    return fileNameLine


@click.command()
@click.option('--root_path', default="",
              help=u'扫描根路径,git clone代码后根目录')
@click.option('--diff_path',default="",
              help=u"git diff文件路径")
@click.option('--server_id',default="",
              help=u"server_id,服务唯一标识")
@click.option('--upload_url',default="",
              help=u"upload_url,数据上送地址")
def collisonDect(server_id,root_path,diff_path,upload_url=""):
    """
    :param rootPath: 扫描根路径
    :param diffFilePath:
    :return:
    """
    if not (root_path and diff_path):
        raise RuntimeError(u"rootPath或者diffFilePath为空,请使用 --help查看详情")
    changeDetail=codeDiffStatic(diff_path)
    collisonFunc=[]
    for fileName,lineNumList in changeDetail.items():
        filePath=root_path+fileName
        try:
            funcLineMap=getFunctionStatic(filePath)
            #双层循环进行碰撞
            for lineNo in lineNumList:
                for funcName,lineNoList in funcLineMap.items():
                    #对于map为空的情况，则直接进行下一层计算
                    if lineNoList and int(lineNo) >= int(lineNoList[0]) \
                            and int(lineNo) <= int(lineNoList[1]) and funcName not in collisonFunc:
                        logging.debug("")
                        collisonFunc.append(funcName)
        except IOError as e:
            logging.error(u"文件打开存在异常,请检查源文件: %s" %(e.message))
        except RuntimeError as err:
            logging.error(err.message.encode("utf-8"))
    if(len(collisonFunc)):
        logging.info(u"碰撞后得到函数列表如下: %s" %(",".join(collisonFunc)))
    else:
        logging.error(u"碰撞后结果为空,请检查patch文件与源文件的对应关系！")

    """
    对于获取的信息进行上报并展示
    """
    res=""
    if server_id and collisonFunc and len(upload_url) != 0 and upload_url.strip().startswith("http"):
        headers={"Content-Type":"application/json;charset=UTF-8",
                 "Connection":"Keep-Alive",'Content-length':'200'};
        response=requests.post(url=upload_url,json={"server_id":server_id,
                                          "methodsList":",".join(collisonFunc)},
                               headers=headers)
        if not response:
            raise RuntimeError(u"上送请求返回信息为空")
        res=json.loads(response.text)
        if res["resCode"] != "0":
            logging.error(u"Result: %s" %(response.text))
            raise RuntimeError(u"服务端处理出现异常,请检查服务端日志")
        #请求信息正常，返回链路碰撞数据
        logging.info(u"Result: %s" %res)
    #如果server_id已指定则返回链路碰撞信息，否则返回函数列表
    return res if res else ",".join(collisonFunc)

if __name__ == "__main__":
    #@rootPath : 当前扫描代码的根路径
    #@diffFile : diff文件的位置信息
    res=collisonDect()