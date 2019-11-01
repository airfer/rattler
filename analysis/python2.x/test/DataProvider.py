# -*- coding: utf-8 -*-#

#-------------------------------------------------------------------------------
# Name:         DataProvider
# Description:  
# Author:       wangyukun
# Date:         2019/11/1
#-------------------------------------------------------------------------------

expected_funclist_1 = {'repeat': [82, 92], 'get': [73, 79], 'callback': [95, 105], 'buildUniqueKey': [107, 109],'saveRecord': [41, 53], 'saveRepeat': [56, 70]}
expected_funclist_2 = {'repeat': [30, 48]}

'''
code_diff expected
'''
expected_example_1_num_dict={'sandbox-core/src/main/java/com/alibaba/jvm/sandbox/core/CoreConfigure.java': [55, 56, 57, 58, 59, 60, 61, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86], 'sandbox-core/src/main/java/com/alibaba/jvm/sandbox/core/server/jetty/servlet/ModuleHttpServlet.java': [32, 33, 34, 35, 36, 37, 38, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207]}
expected_example_2_num_dict={'sandbox-api/src/main/java/com/alibaba/jvm/sandbox/api/listener/ext/Behavior.java': [1, 2, 3, 4, 5, 6, 37, 38, 39, 40, 41, 42, 104, 105, 106, 107, 108, 109, 183, 184, 185, 186, 187, 188]}
expected_example_3_num_dict={'sandbox-api/src/main/java/com/alibaba/jvm/sandbox/api/ModuleLifecycleAdapter.java': []}
expected_example_4_num_dict={'sandbox-debug-module/src/main/java/com/alibaba/jvm/sandbox/module/debug/util/InterfaceProxyUtils.java': [15, 16, 17, 18, 19, 20, 21, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 69, 70, 71, 72, 73, 74, 75, 76]}
expected_example_5_num_dict={'sandbox-core/src/main/java/com/alibaba/jvm/sandbox/core/util/AsmUtils.java': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64]}


#待验证函数列表
data_func_verify=[
    {"file_name":"data/RecordServiceLocalImpl.java","expected_funclist":expected_funclist_1},
    {"file_name":"data/AbstractRecordService.java", "expected_funclist": expected_funclist_2},
]

#待验证codeDiff变动对应关系
data_codediff_verify=[
    {"file_name": "data/jvm_sandbox_example_1.patch", "expected_num_dict": expected_example_1_num_dict},
    {"file_name": "data/jvm_sandbox_example_2.patch", "expected_num_dict": expected_example_2_num_dict},
    {"file_name": "data/jvm_sandbox_example_3.patch", "expected_num_dict": expected_example_3_num_dict},
    {"file_name": "data/jvm_sandbox_example_4.patch", "expected_num_dict": expected_example_4_num_dict},
    {"file_name": "data/jvm_sandbox_example_5.patch", "expected_num_dict": expected_example_5_num_dict},

]