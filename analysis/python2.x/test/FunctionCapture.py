# -*- coding: utf-8 -*-#

#-------------------------------------------------------------------------------
# Name:         FunctionCapture
# Description:  
# Author:       wangyukun
# Date:         2019/11/1
#-------------------------------------------------------------------------------
import unittest,ddt
from src.funcDetec import getFunctionStatic,codeDiffStatic
from DataProvider import data_func_verify,data_codediff_verify

@ddt.ddt
class FunctionCapture(unittest.TestCase):
    '''
    核心链路探查核心功能点验证，采用DataProvider方式进行
    '''

    '''
    测试核心链路函数捕获的功能
    '''
    @ddt.data(*data_func_verify)
    def test_get_function_static_1(self,data_func_verify):
        self.assertEqual(
            getFunctionStatic(data_func_verify["file_name"]),data_func_verify["expected_funclist"])


    '''
    校验CodeDiff文件探查功能
    '''
    @ddt.data(*data_codediff_verify)
    def test_code_diff_static(self,data_codediff_verify):
        self.assertEqual(
            codeDiffStatic(data_codediff_verify["file_name"]),data_codediff_verify["expected_num_dict"])

if __name__ == '__main__':
    unittest.main()
