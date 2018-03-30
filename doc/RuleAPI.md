<html lang="en">
<head>
	<meta charset="utf-8">
</head>
<body>
<pre>
一：tarsier-manager   Rule API
	API 接口出错时，均返回如下格式内容
	response:{
		success:false, // true:说明成功，提示成功即可，false：错误，message中给出错误原因
		message:"错误原因"
	}

 1:新增
 curl -XPOST 'http://localhost:8080/tarsier-manager/rule/insert' -H 'Content-Type: application/json' -d '
 {
	"name": "String",//必选，规则名称
	"projectName": "String",//必选，项目名称
	"channel": "String",//必选，数据通道
	"filter": "String",//非空，过滤表达式
	"group": "String",//可选，分组字段
	"recover": "String",//可选，告警恢复表达式
	"timewin": "int",//可选，时间窗口，单位分钟，默认 1分钟
	"trigger": "String",//必选，告警触发表达式
	"interval": "int",//可选，告警的间隔时间，单位分钟，默认30分钟
	"type": ": "int",//可选，通知类型，1：邮件，2：短信，4：微信，默认：7
	"persons": "String",//可选，通知人员邮箱地址，多人用逗号分隔，type为 邮件或微信时，必选
	"mobiles": "String",//可选，通知人员手机号，多人用逗号分隔，type短信时，必选
	"dateRange": "String",//可选，指定日期执行此规则，默认：null
	"timeRange": "String",//可选，指定具体的执行时段，单位小时。默认："0:24"
	"weekly": "String",//可选，按周重复执行。当未指定任何执行日期时(monthly，dateRange都为空)，默认值："0,1,2,3,4,5,6"
	"monthly": "String",//可选，按月重复执行。默认：null
	"disabled": "boolean",//可选，是否启用，true:不启用，false：启用。默认：false
	"userName": "String", //必选，创建人
	"template": "String" //可选，告警信息模版
 }'
 response 参考 第6个
2:更新
 curl -XPOST 'http://localhost:8080/tarsier-manager/rule/update' -H 'Content-Type: application/json' -d '
 {
 	"id":"int",
	"name": "String",//必选，规则名称
	"projectName": "String",//必选，项目名称
	"channel": "String",//必选，数据通道
	"filter": "String",//非空，过滤表达式
	"group": "String",//可选，分组字段
	"recover": "String",//可选，告警恢复表达式
	"timewin": "int",//可选，时间窗口，单位分钟，默认 1分钟
	"trigger": "String",//必选，告警触发表达式
	"interval": "int",//可选，告警的间隔时间，单位分钟，默认30分钟
	"type": ": "int",//可选，通知类型，1：邮件，2：短信，4：微信，默认：7
	"persons": "String",//可选，通知人员邮箱地址，多人用逗号分隔，type为 邮件或微信时，必选
	"mobiles": "String",//可选，通知人员手机号，多人用逗号分隔，type短信时，必选
	"dateRange": "String",//可选，指定日期执行此规则，默认：null
	"timeRange": "String",//可选，指定具体的执行时段，单位小时。默认："0:24"
	"weekly": "String",//可选，按周重复执行。当未指定任何执行日期时(monthly，dateRange都为空)，默认值："0,1,2,3,4,5,6"
	"monthly": "String",//可选，按月重复执行。默认：null
	"disabled": "boolean",//可选，是否启用，true:不启用，false：启用。默认：false
	"userName": "String", //必选，创建人
	"template": "String" //可选，告警信息模版
 }'
  response 参考 第6个
3:删除
 curl -XPOST 'http://localhost:8080/tarsier-manager/rule/delete?id={id}' -H 'Content-Type: application/json'
 response 参考 第6个

4:禁用
 curl -XPOST 'http://localhost:8080/tarsier-manager/rule/disable?id={id}' -H 'Content-Type: application/json'
 response 参考 第6个

5:启用
 curl -XPOST 'http://localhost:8080/tarsier-manager/rule/enable?id={id}' -H 'Content-Type: application/json'
 response 参考 第6个

6：根据ID获取指定配置对象
 curl -XGET 'http://localhost:8080/tarsier-manager/rule/{id}'

response:{
	success:true,
	item:{
	 	"id":"int",
		"name": "String",//规则名称
		"projectName": "String",//项目名称
		"channel": "String",//数据通道
		"filter": "String",//过滤表达式
		"group": "String",//分组字段
		"recover": "String",//告警恢复表达式
		"timewin": "int",//时间窗口，单位分钟，默认 1分钟
		"trigger": "String",//告警触发表达式
		"interval": "int",//告警的间隔时间，单位分钟，默认30分钟
		"type": ": "int",//通知类型，1：邮件，2：短信，4：微信，默认：7
		"persons": "String",//通知人员邮箱地址，多人用逗号分隔，type为 邮件或微信时，必选
		"mobiles": "String",//通知人员手机号，多人用逗号分隔，type短信时，必选
		"dateRange": "String",//指定日期执行此规则，默认：null
		"timeRange": "String",//指定具体的执行时段，单位小时。默认："0:24"
		"weekly": "String",//按周重复执行。当未指定任何执行日期时(monthly，dateRange都为空)，默认值："0,1,2,3,4,5,6"
		"monthly": "String",//按月重复执行。默认：null
		"disabled": "boolean",//是否启用，true:不启用，false：启用。默认：false
		"createTime": "date",//创建时间
		"updateTime": "date",//更新时间
		"userName": "String", //必选，创建人
		"template": "String" //可选，告警信息模版
	}
}

7：获取列表
curl -XGET 'http://localhost:8080/tarsier-manager/rule/list?userName={userName}&disabled={disabled}'
参数说明：userName必选，disabled可选，disabled值true或false
response：{
	success:true,
	items:[
		{
		 	"id":"int",
			"name": "String",//规则名称
			"projectName": "String",//项目名称
			"channel": "String",//数据通道
			"filter": "String",//过滤表达式
			"group": "String",//分组字段
			"recover": "String",//告警恢复表达式
			"timewin": "int",//时间窗口，单位分钟，默认 1分钟
			"trigger": "String",//告警触发表达式
			"interval": "int",//告警的间隔时间，单位分钟，默认30分钟
			"type": ": "int",//通知类型，1：邮件，2：短信，4：微信，默认：7
			"persons": "String",//通知人员邮箱地址，多人用逗号分隔，type为 邮件或微信时，必选
			"mobiles": "String",//通知人员手机号，多人用逗号分隔，type短信时，必选
			"dateRange": "String",//指定日期执行此规则，默认：null
			"timeRange": "String",//指定具体的执行时段，单位小时。默认："0:24"
			"weekly": "String",//按周重复执行。当未指定任何执行日期时(monthly，dateRange都为空)，默认值："0,1,2,3,4,5,6"
			"monthly": "String",//按月重复执行。默认：null
			"disabled": "boolean",//是否启用，true:不启用，false：启用。默认：false
			"createTime": "date",//创建时间
			"updateTime": "date",//更新时间
			"userName": "String", //必选，创建人
			"template": "String" //可选，告警信息模版
		}
	]
}

8: 获取人员列表
curl -XGET 'http://localhost:8080/tarsier-manager/rule/persons?id={id}&mobile={mobile}'
参数说明：id,mobile 可选. 非空时，进行模糊查询
response：{
	success:true,
	items:[ {"id":"id1","mobile":"13888888888","email":"id1@email.cn"},
			{"id":"id2","mobile":"13999999999","email":"id2@email.cn"]
	}
	
9: 获取项目列表
curl -XGET 'http://localhost:8080/tarsier-manager/rule/projects?userName={userName}'
参数说明：userName 必选.
response：{
	success:true,
	items:[ "project1",
			"project2"
	}
	
10：根据ID获取规则的运行信息
 curl -XGET 'http://localhost:8080/tarsier-manager/rule/detail?id={id}'

response:{
	success:true,
	item:{
	 	"id":"int",
		"name": "String",
		"projectName": "String",
		"channel": "String",
		"filter": "String",
		"group": "String",
		"recover": "String",
		"timewin": "int",
		"trigger": "String",
		"interval": "int",
		"type": ": "int",
		"persons": "String",
		"mobiles": "String",
		"dateRange": "String",
		"timeRange": "String",
		"weekly": "String",
		"monthly": "String",
		"disabled": "boolean",
		"createTime": "date",
		"updateTime": "date",
		"userName": "String", 
		"template": "String",
		"lastLogTime": "long",
		"lastAlarmTime": "long",
		"lastLog": "String",
		"lastAlarm": "String",
		"engineName": "String",
		"status": "String",
		"cache": "String"
	}
}

</pre>
</body>
</html>
