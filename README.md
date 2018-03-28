# tarsier

说明：本系统主要目的是通过配置规则，对接入的数据进行规则匹配。对于符合规则的数据进行预警处理，可以通过邮件、短信、微信等多种方式进行预警。

一、编译步骤
1: 通过如下命令 下载仓库
git clone https://github.com/kingmorning/tarsier.git

2：进入项目根目录 tarsier内部
   模块说明如下：
   tarsier-antlr：使用antlr语法解析器，构造出规则的过滤器
   tarsier-flume：对flume-ng的定制化开发，未使用flume收集数据的课忽略此模块
   tarsier-manager：规则及收集器管理系统，普通的Web项目
   tarsier-rule：规则匹配系统，普通的Web项目
   tarsier-util：通用工具包

3：编译打包
   进入tarsier项目根目录
   编译打包开发环境命令：mvn clean install -Pdev
   编译打包测试环境命令：mvn clean install -Ptest
   编译打包生产环境命令：mvn clean install -Pprod
   
4：部署
   将tarsier-manager/