<html lang="en">
<head>
	<meta charset="utf-8">
</head>
<body>
<pre>
一：tarsier-manager   api
 1:新增
 curl -XPOST 'http://localhost:8080/tarsier-manager/ls/save' -H 'Content-Type: application/json' -d '
 {
	"userName": "root",
	"group": "groupNameXXXX",//可选，默认值null
	"host": "hostNameXXXX",//可选，默认值null
	"ip": "127.0.0.1",
	"disabled": false, //可选，默认值false
	"config": "config..."//可选，默认值null
 }'
 response 参考 第6个
2:更新
 curl -XPOST 'http://localhost:8080/tarsier-manager/ls/save' -H 'Content-Type: application/json' -d '
 {
 	"id":123,
	"userName": "root",
	"group": "groupNameXXXX",//可选，默认值null
	"host": "hostNameXXXX",//可选，默认值null
	"ip": "127.0.0.1",
	"disabled": false, //可选，默认值false
	"config": "config..."//可选，默认值null
 }'
  response 参考 第6个
3:删除
 curl -XPOST 'http://localhost:8080/tarsier-manager/ls/delete?id={id}' -H 'Content-Type: application/json'
 response 参考 第6个

4:禁用
 curl -XPOST 'http://localhost:8080/tarsier-manager/ls/disable?id={id}' -H 'Content-Type: application/json'
 response 参考 第6个

5:启用
 curl -XPOST 'http://localhost:8080/tarsier-manager/ls/enable?id={id}' -H 'Content-Type: application/json'
 response 参考 第6个

6：根据ID获取指定配置对象
 curl -XGET 'http://localhost:8080/tarsier-manager/ls/{id}'

response:{
	success:true,
	item:{
			"id":123,
			"userName": "root",
			"group": "groupNameXXXX",
			"host": "hostNameXXXX",
			"ip": "127.0.0.1",
			"disabled": false,
			"createTime":"2017-03-21 21:22:33",
			"updateTime":"2017-03-21 21:22:33",
			"config":"config...",
			"checkTime":"2017-03-21 21:22:33",
			"status":"online/offline/disabled",
			"desc": "description..."
	}
}

7：获取列表
curl -XGET 'http://localhost:8080/tarsier-manager/ls/list?userName=root&group=xx&host=xx&ip=xx&disabled=false'
参数说明：userName必选，group、host、ip、disabled可选，disabled值为true或者false
response：{
	success:true,
	items:[
		{
		 	"id":123,
			"userName": "root",
			"group": "groupNameXXXX",
			"host": "hostNameXXXX",
			"ip": "127.0.0.1",
			"disabled": false,
			"createTime":"2017-03-21 21:22:33",
			"updateTime":"2017-03-21 21:22:33",
			"config":"config...",
			"checkTime":"2017-03-21 21:22:33",
			"status":"online/offline/disabled",
			"desc": "description..."
		}
	]
}

8: 根据IP获取配置
 curl -XGET 'http://localhost:8080/tarsier-manager/ls/conf?ip=xxx'
 response:{
	success:true,
	item:{
			"id":123,
			"userName": "root",
			"group": "groupNameXXXX",
			"host": "hostNameXXXX",
			"ip": "127.0.0.1",
			"disabled": false,
			"createTime":1234567890,
			"updateTime":1234567890,
			"config":"config..."
	}
}

9:获取群组
curl -XGET 'http://localhost:8080/tarsier-manager/ls/group?userName=root'
参数说明：userName必选
response：{
	success:true,
	items:[
		"xxxxx",
		"yyyy"
	]
}

10:配置校验
 curl -XPOST 'http://localhost:8080/tarsier-manager/ls/check' -H 'Content-Type: application/json' -d '
 {
 	"id":123,
	"config": "config..."
 }'
该接口耗时 数秒

response:{
	success:false, // true:说明成功，提示成功即可，false：错误，message中给出错误原因
	message:"错误原因"
}
</pre>
</body>
</html>
