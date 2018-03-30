#!/bin/bash
if [ $# = 0 ] ; then 
   echo "pls set tarsier manager host:port as parameter" 
   exit -1; 
fi 
managerHostPort=${1}
old="`crontab -l | grep '/collect;'`"
if [ -n "$old" ];then
	echo "add crontab fail, as exist crontab: $old"
	exit -1
fi

echo "start to download collect.tar.gz"
curl -o collect.tar.gz http://${managerHostPort}/tarsier-manager/collect/download
echo "download success. start to unzip collect.tar.gz"
tar -zxvf collect.tar.gz
echo "unzip success. start to download agent script"
curl -o ./collect/agent.sh http://${managerHostPort}/tarsier-manager/api/config/agent
chmod +x ./collect/agent.sh
if [ ! -x ./collect/agent.sh ];then
	echo "install agent failed, please try it again."
	exit -1
fi
echo "download success. start to add crontab"
(crontab -l 2>/dev/null; echo "* * * * * (export COLLECT_HOME=`pwd`/collect; \$COLLECT_HOME/agent.sh ${managerHostPort}>> \$COLLECT_HOME/log/crontab.out 2>&1 & ) ") | crontab -
echo "add crontab success, please add config file at URL: http://${managerHostPort}/tarsier-manager/collect/index.html?userName=[userName] "

exit 0
