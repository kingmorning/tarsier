#!/bin/bash
version=16
if [ ! $COLLECT_HOME ];then
        echo pls set environment COLLECT_HOME.
        exit 1
fi
cd $COLLECT_HOME
dir=`pwd`
afile=$dir/after
bfile=$dir/before
pfile=$dir/post
ip=${1:-`/sbin/ip a|grep 'inet '|grep -v 127.0.0.1|awk '{print $2}' | awk -F '/' '{print $1}' |head -n 1`}
serverURL='http://127.0.0.1:8080/tarsier-manager/api/config'
curl -XPOST -d @post -H 'Content-type: application/json' $serverURL/status?ip=$ip\&id=$COLLECT_ID > $bfile
function getPID(){
        local tmpid=`ps -ef | grep ${1} | grep -v "grep" | awk '{if(NR==1) print $2}'`
        tmpid=${tmpid:-0}
        newId=$tmpid
        return
}
:>$afile
echo "{" > $pfile

cat $bfile | while read LINE
do
        #echo $LINE
        arr=($LINE)
        [ ${#arr[@]} -ne 6 ] && continue
        op=${arr[0]}
        id=${arr[1]}
        name=${arr[2]}
        type=${arr[3]}
        time=${arr[4]}
        pid=${arr[5]}
        configFile=$dir/config/$id.$type
        if [[ $op = *updateAgent* ]]; then
                curl -m 50 -o tmpAgent.sh $serverURL/agent
                headLine=`head -n 1 tmpAgent.sh`
                if [[ $headLine = \#* ]]; then
                        cat tmpAgent.sh > agent.sh
                        version=$(head -n 2 agent.sh | grep version= | sed 's/version=//g')
                        echo updateAgent version:$version
                fi
        fi
        if [[ $op = *check* ]]; then
                if [ `ps $pid | wc -l` -gt 1 ]; then
                        echo checked $id $name $type $time $pid >> $afile
                        echo \"$id\": >> $pfile
                        if [ -f $dir/data/$id/registry ]; then
                                cat $dir/data/$id/registry >> $pfile
                        else
                                echo '[{"source":" ","offset":0,"timestamp":"'`date +%Y-%M-%dT%H:%m:%S.%sZ`'"}]' >> $pfile
                        fi
                        echo , >> $pfile
                else
                        getPID ${configFile}
                        echo checked $id $name $type $time $newId >> $afile
                fi
        fi
        if [[ $op = *reload* ]]; then
                reloadURL=$serverURL/id/$id
                echo reloadURL:$reloadURL
                curl "$reloadURL" > $dir/tmpfile
                newTime=`head -n 1 $dir/tmpfile`
                newTime=${newTime:1}
                if [[ "$newTime" =~ ^[0-9-]+T[0-9:]+$ ]]; then
                        cp $dir/tmpfile $dir/config/$id.$type
                        echo reloaded $id $name $type $newTime $pid >> $afile
                        time=$newTime
                else
                        echo reloaded $id $name $type $time $pid >> $afile
                fi
        fi
        if [[ $op = *stop* ]] || [[ $op = *restart* ]]; then
                kill -9 $pid
                sleep 1s
                getPID ${configFile}
                echo stoped $id $name $type $time $newId >> $afile
                echo stoped $type, $pid
        fi
        if [[ $op = *start* ]]; then
                getPID ${configFile}
                if [ $newId = "0" ]; then
                        if [ $type = 'logstash'  ]; then
                                $dir/logstash/bin/logstash -r -f $configFile --path.data $dir/data/$id > $dir/log/$id.out 2>&1 &
                        elif [[ $type = *beat ]]; then
                                $dir/$type/$type -c $configFile -path.data $dir/data/$id > $dir/log/$id.out 2>&1 &
                        else
                                echo unknown type:$type,start fail
                        fi
                        getPID $configFile
                        echo started $id $name $type $time $newId >> $afile
                        echo started $type, pid:$newId
                else
                        echo "exist $id $name $type $time $newId" >> $afile
                fi
        fi
done
echo \"0\":[\"version 0 agent.sh shell 0 $version\" >> $pfile
cat $afile | sed 's/^/,\"&/g;s/$/&\"/g' >> $pfile
echo "]}" >> $pfile
exit 1