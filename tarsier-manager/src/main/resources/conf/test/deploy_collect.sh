#!/bin/bash
old="`crontab -l | grep '/collect;'`"
if [ -n "$old" ];then
	echo "add crontab fail, as exist crontab: $old"
	exit -1
fi
echo "start to download collect.tar.gz"
curl -o collect.tar.gz http://10.214.160.153:8080/tarsier-manager/collect/download
echo "download success. start to unzip collect.tar.gz"
tar -zxvf collect.tar.gz
echo "unzip success. start to add crontab"
(crontab -l 2>/dev/null; echo "* * * * * (export COLLECT_HOME=`pwd`/collect; \$COLLECT_HOME/agent.sh >> /tmp/crontab.out 2>&1 & ) ") | crontab -
echo "add crontab success, please add config file at URL: http://10.214.160.153:5601 "
exit 0
