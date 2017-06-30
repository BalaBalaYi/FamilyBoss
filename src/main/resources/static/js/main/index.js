layui.use(['element', 'util', 'layer', 'layim'], function(){
	var $ = layui.jquery
	, element = layui.element()
	, util = layui.util
	, layer = layui.layer
	, layim = layui.layim;
	
	// 触发事件
	var active = {
		tabAdd: function(){
			// 判断tab项是否存在
			var layId = $(this).attr("lay-id");
			if($("li[lay-id='" + layId + "']").val() != null){
				element.tabChange("tab-sub-menu", layId);
			} else {
				//新增一个Tab项
				var src = $(this).attr("rel");
				element.tabAdd('tab-sub-menu', {
					title: $(this).html()
					,content: '<iframe src="' + src + '" frameborder="0" width="100%" height="100%" align="middle"/>'
					,id: $(this).attr("lay-id")
				})
			}
		}
		,calendar: function(){
			layer.open({
				type: 1
				,title :'日历'
				,id: 'Calendar'
				,content: '<div id="calendar"></div>'
				,area: ['500px', '542px']
				,skin: 'layui-layer-molv'
				,shade: 0 
				,success: function(layero, index){
					var myCalendar = new SimpleCalendar('#calendar');
				}
				,cancel: function(index, layero){ 
					$("#calendar_dd").attr("class", "");
				}
			});
		}
		,planet: function(){
			layer.open({
				type: 2
				,title :'行星引力'
				,id: 'Planet'
				,content: '/extra/showPlanet.do'
				,area: ['1000px', '800px']
				,skin: 'layui-layer-molv'
				,shade: 0 
				,cancel: function(index, layero){ 
					$("#planet_dd").attr("class", "");
				}
			});
		}
		,calculator: function(){
			layer.open({
				type: 2
				,title :'计算器'
				,id: 'Calculator'
				,content: '/extra/showCalculator.do'
				,area: ['570px', '545px']
				,skin: 'layui-layer-molv'
				,shade: 0 
				,cancel: function(index, layero){ 
					$("#calculator_dd").attr("class", "");
				}
			});
		}
	};
	
	//固定块
	util.fixbar({
		bar1: '&#xe607;'
		,bgcolor: '#393D49'
		,css: {bottom: 60}
		,click: function(type){
			if(type === 'bar1'){
				layer.msg('自制家庭后台管理系统，欢迎使用和交流！<br/>如有问题，请通过页面下方的"GitHub"提交你的issue，或是通过邮件：robert37@sina.com。', {time: 8000})
			}
		}
	});
	
	$('.site-active').on('click', function(){
		var othis = $(this), type = othis.data('type');
		active[type] ? active[type].call(this, othis) : '';
		var layid = $(this).attr("lay-id");
		element.tabChange('tab-sub-menu', layid);
	});
	$('.other-active').on('click', function(){
		var othis = $(this), type = othis.data('type');
		active[type] ? active[type].call(this, othis) : '';
	});
	
	//Hash地址的定位
	var layid = location.hash.replace(/^#tab-menu=/, '');
	element.tabChange('tab-sub-menu', layid);

	element.on('tab(tab-sub-menu)', function(elem){
		location.hash = 'tab-sub-menu='+ $(this).attr('lay-id');
	});
	
	// 修改密码点击监听
	$("#toChangePwd").click(function(){
		// 判断tab项是否存在
		if($("li[lay-id='11']").val() != null){
			element.tabChange("tab-sub-menu", 11);
			$("#toChangePwd_dd").attr("class", "");
			return false;
		} else {
			// 跳转至人员管理tab，页面至修改密码页面
			var src = $(this).attr("rel");
			element.tabAdd('tab-sub-menu', {
				title: "人员管理"
				,content: '<iframe src="' + src + '" frameborder="0" width="100%" height="100%" align="middle"/>'
				,id: "11"
			})
			element.tabChange('tab-sub-menu', "11");
		}
	});
	
	// 登出点击监听
	$("#logout").click(function(){
		location.href = "/login/doLogout.do";
	});
	
	$(".navbar-toggler").click(function(){
		var widthNow = $(".index-left-bar").width();
		if(widthNow > 0) {
			$(".index-left-bar").animate({width:'0px'});
			$(".index-body, .index-foot").animate({left: '0px'});
		} else {
			$(".index-left-bar").animate({width:'200px'});
			$(".index-body, .index-foot").animate({left: '200px'});
		}
	});
	
	
	// ==================== IM ====================
	//基础配置
	layim.config({
		
		//获取主面板列表信息
		init: {
			url: '/im/init.do' //接口地址（返回的数据格式见下文）
			,type: 'get' //默认get，一般可不填
			,data: {} //额外参数
		}
		
		//获取群员接口
		,members: {
			url: '/im/getMembers.do' //接口地址（返回的数据格式见下文）
			,type: 'get' //默认get，一般可不填
			,data: {} //额外参数
		}
	
		//上传图片接口（返回的数据格式见下文）
		,uploadImage: {
			url: '/file/fileUpload.do' //接口地址（返回的数据格式见下文）
			,type: 'post' //默认post
		}
	
		//上传文件接口（返回的数据格式见下文）
		,uploadFile: {
			url: '/file/fileUpload.do' //接口地址（返回的数据格式见下文）
			,type: 'post' //默认post
		}
	
		//增加皮肤选择，如果不想增加，可以剔除该项
//		,skin: [ 
//			'http://xxx.com/skin.jpg'
//		]
	
		,brief: false //是否简约模式（默认false，如果只用到在线客服，且不想显示主面板，可以设置 true）
		,title: 'FamilyBoss-IM' //主面板最小化后显示的名称
		,min: true //用于设定主面板是否在页面打开时，始终最小化展现。默认false，即记录上次展开状态。
		,minRight: null //【默认不开启】用户控制聊天面板最小化时、及新消息提示层的相对right的px坐标，如：minRight: '200px'
		,maxLength: 3000 //最长发送的字符长度，默认3000
		,isfriend: true //是否开启好友（默认true，即开启）
		,isgroup: true //是否开启群组（默认true，即开启）
		,right: '0px' //默认0px，用于设定主面板右偏移量。该参数可避免遮盖你页面右下角已经的bar。
		,chatLog: '/chat/log/' //聊天记录地址（如果未填则不显示）
//		,find: '/find/all/' //查找好友/群的地址（如果未填则不显示）
		,copyright: false //是否授权，如果通过官网捐赠获得LayIM，此处可填true
		
	});
	
	// =================== 建立WebSocket通讯 ===================
	var socket;
	if (!window.WebSocket){
		alert("你的浏览器不支持websocket，请升级到IE10以上浏览器，或者使用谷歌、火狐、360浏览器。");
	} else {
		socket = new WebSocket('ws://localhost:8080/websocket/' + $("#userId").val());
	}

	// socket错误处理
	socket.onerror = function(event) {
		alert("websockt连接发生错误，请刷新页面重试!")
	};
	
	// socket连接成功处理
	socket.onopen = function(){
		var msg = {
				type: 'sys'
				,key: $("#userId").val()
				,data: 'FamilyBoss-IM 连接成功！用户id：' + $("#userId").val()
		}		
		socket.send(JSON.stringify(msg)); 
	};
	
	// 监听收到的消息
	socket.onmessage = function(response){
		var data = response.data;
		var dataJson = JSON.parse(data);
		layim.getMessage({
			username: dataJson.username
			,avatar: dataJson.avatar
			,id: dataJson.id
			,type: dataJson.type
			,content: dataJson.content
			,mine: dataJson.mine
			,timestamp: dataJson.timestamp
		});
	};
	
	// 监听发送的消息
	layim.on('sendMessage', function(data){
		
		/* 
		 * 发送消息用户及发送内容信息
		 * 
		 * 例：
		 * {
		 * 	  avatar: "avatar.jpg" //我的头像
		 * 	  ,content: "你好吗" //消息内容
		 * 	  ,id: "100000" //我的id
		 * 	  ,mine: true //是否我发送的消息
		 * 	  ,username: "纸飞机" //我的昵称
		 * 	}
		 * 
		 */
		
		// 发送
		this.waitForConnection(function () {// 连接建立才能发送消息
			socket.send(JSON.stringify({
							type: 'chat'
							,data: data
						}
			));
		}, 500);
	});
	
	// 判断连接是否建立
	waitForConnection = function (callback, interval) {
		if (socket.readyState === 1) {
			console.log("1");
			callback();
		} else {
			console.log("2");
			var that = this;
			setTimeout(function () {
				that.waitForConnection(callback, interval);
			}, interval);
		}
	}

});


// 天气插件
(function(T, h, i, n, k, P, a, g, e) {
	g = function() {
		P = h.createElement(i);
		a = h.getElementsByTagName(i)[0];
		P.src = k;
		P.charset = "utf-8";
		P.async = 1;
		a.parentNode.insertBefore(P, a)
	};
	T["ThinkPageWeatherWidgetObject"] = n;
	T[n] || (T[n] = function() {
		(T[n].q = T[n].q || []).push(arguments)
	});
	T[n].l = +new Date();
	if(T.attachEvent) {
		T.attachEvent("onload", g)
	} else {
		T.addEventListener("load", g, false)
	}
}(window, document, "script", "tpwidget", "//widget.thinkpage.cn/widget/chameleon.js"))

tpwidget("init", {
			"flavor": "slim",
			"location": "WX4FBXXFKE4F",
			"geolocation": "disabled",
			"language": "zh-chs",
			"unit": "c",
			"theme": "chameleon",
			"container": "tp-weather-widget",
			"bubble": "enabled",
			"alarmType": "badge",
			"color": "#F47837",
			"uid": "U605DCADA4",
			"hash": "78f46a1198d54dafa0cda717efa717a9"
		});
tpwidget("show");
		
