# -*- coding: utf-8 -*-#

#-------------------------------------------------------------------------------
# Name:         funcPatternTest
# Description:  
# Author:       wangyukun
# Date:         2019/11/19
#-------------------------------------------------------------------------------
import re

funcPattern="(?P<access>public|private|protected)" \
                "(\s)*(?P<static>static)?(?P<final>final)?(\s)*(?P<returnType>[\w<>]+)(\s)*" \
                "(?P<funcName>\w+)[(](?P<params>.*)[)](?P<extra>.*)"
funcPatternExec=re.compile(funcPattern)

diffFilePattern="diff\s--git\sa/(?P<source>(.*java))(\s)b/(?P<target>(.*java))"
lineChangePattern="@@ -(?P<begin>(\d+)),(?P<interval>(\d+))\s[+](?P<begin2>\d+),(?P<interval2>\d+) @@"

diffFilePatternExec=re.compile(diffFilePattern)
lineChangePatternExec=re.compile(lineChangePattern)

line_s="@@ -17,8 +17,6 @@ public class Core {"
line_2="@@ -1,365 +0,0 @@"
str="public void queryQrAccountInfo01Test()"

a=lineChangePatternExec.search(line_2)
a_dic=a.groupdict()
print(a_dic)
