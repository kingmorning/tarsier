input=Jun 12 17:23:00 cpvl-zabx-a02 root: [euid=root]:root pts/1 2017-06-12 17:00 (10.15.190.111):[/var/log]root 2017-06-12 17:23:00 echo 33
searchPattern=^(\w+ \d+ [\d:]+) (\S+) \S+ \[\w+=\w+\]:(\S+) \S+ ([\d- :]+)\(([\d.]+)\):\[(.*)\](\w+) ([\d-]+) ([\d:]+) (.*)$
replaceString=timestamp=$8T$9 projectName=command loginIP=$5 loginTime=$4loginUser=$3 hostName=$2 exePath=$6 exeUser=$7 exeTime=$8 $9 exeCmd=$10
output=timestamp=2017-06-12T17:23:00 projectName=command loginIP=10.15.190.111 loginTime=2017-06-12 17:00 loginUser=root hostName=cpvl-zabx-a02 exePath=/var/log exeUser=root exeTime=2017-06-12 17:23:00 exeCmd=echo 33
