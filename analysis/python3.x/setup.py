#!/usr/bin/env python
# coding=utf-8

from setuptools import setup

'''
使用rattler-func-detection命令进行冲突文件探查
'''

setup(
    name="rattler-func-detection",  #pypi中的名称，pip或者easy_install安装时使用的名称，或生成egg文件的名称
    version="1.0",
    author="wangyukun",
    author_email="wangyukun06@meituan.com",
    description=("This is a tool for function collision detect"),
    license="GPLv3",
    keywords="collision rattler-func-detec",
    url="",
    packages=['src'],  # 需要打包的目录列表

    # 需要安装的依赖
    install_requires=[
        'logging>=0.4.9.6',
        'click>=6.7'
        'setuptools>=16.0',
        'requests>=2.11.1'
    ],

    # 添加这个选项，在windows下Python目录的scripts下生成exe文件
    # 注意：模块与函数之间是冒号:
    entry_points={'console_scripts': [
        'rattler-func-detection = src.funcDetec:collisonDect',
    ]},

    # long_description=read('README.md'),
    classifiers=[  # 程序的所属分类列表
        "Development Status :: 3 - Alpha",
        "Topic :: Utilities",
        "License :: OSI Approved :: GNU General Public License (GPL)",
    ],
    # 此项需要，否则卸载时报windows error
    zip_safe=False
)