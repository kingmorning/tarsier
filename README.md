** tarsier **

说明：本系统主要目的是通过配置规则，对接入的数据进行规则匹配。对于符合规则的数据进行预警处理，可以通过邮件、短信、微信等多种方式进行预警。

一、下载代码
1: 通过如下命令 下载仓库
git clone https://github.com/kingmorning/tarsier.git

二：项目模块说明：
   tarsier-antlr：使用antlr语法解析器，构造出规则的过滤器。
   tarsier-manager：规则及收集器管理系统，可以对收集器及规则进行增删改查，Web项目。
   tarsier-rule：规则匹配系统，可启动多个实例，Web项目。
   tarsier-util：通用工具包

三：修改配置
   1：tarsier-manager/src/main/resource/conf 目录下提供了 对应环境的配置文件，可进行修改
   2：tarsier-rule/src/main/resource/conf 目录下提供了 对应环境的配置文件，可进行修改
   
四：编译打包
   进入tarsier项目根目录
   编译打包开发环境命令：mvn clean install -Pdev
   编译打包测试环境命令：mvn clean install -Ptest
   编译打包生产环境命令：mvn clean install -Pprod
   
五：部署tarsier-manager
    将tarsier-manager/target/tarsier-manager.war 拷贝到tomcat发布目录下，启动tomcat后，
    可以通过http://${managerHostPort}/tarsier-manager/pages/collect/index.html?userName=root来访问收集器配置页面。
    可以通过http://${managerHostPort}/tarsier-manager/pages/rules/index.html?userName=root来访问规则配置页面。
    
六：部署tarsier-rule
   将tarsier-rule/target/tarsier-rule.war 拷贝到tomcat发布目录下，启动tomcat后。
   可通过http://${ruleHostPort}/tarsier-rule/healthCheck 来验证系统启动是否成功。
   
七：部署agent
   在agent机器上执行如下命令
   1：下载部署脚本文件 curl -o deploy_collect.sh http://127.0.0.1:8080/tarsier-manager/collect/shell
   2：添加脚本权限 chmod +x deploy_collect.sh
   3：执行部署脚本 ./deploy_collect.sh ${managerHostPort}

八：登录tarsier-manager系统，配置对应的收集器和规则即可 使用。



API详情
[收集器](./tarsier-manager/CollectAPI.md)
[规则](./tarsier-manager/RuleAPI.md)
      
