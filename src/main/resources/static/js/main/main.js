/*
 * @Author: chenty
 * @Date:   2017-04-09
 * @lastModify 2017-04-09
 */
// 全局变量
var locationError;

// layui 初始化
layui.use(['element', 'layer'], function(){
	var $ = layui.jquery
	, element = layui.element()
	, layer = layui.layer;
	
	//模拟进度条loading（1s延迟）
	$(".delay-area").fadeIn("fast");
	var n = 0, timer = setInterval(function(){
		n = n + 1;
		if(n > 100){
			n = 100;
			clearInterval(timer);
			$(".delay-area").fadeOut("fast");
			$("#map").fadeIn(2000);
		}
		element.progress('delay', n + '%');
	}, 10);
	
	// 定位失败错误提示
	locationError = function(){
		layer.open({
			type: 1
	//		,offset: ['80%', '78%']
			,id: 'location_error'
			,content: '<div style="padding: 20px 100px;z-index: 10;">无法获取您的准确位置！</div>'
			,btn: '关闭'
			,btnAlign: 'c' 
			,shade: 0 
			,yes: function(){
				layer.closeAll();
			}
		});
	}
});

/*
 * --------------------------- 图像初始化 ---------------------------
 */
var userChart1 = echarts.init(document.getElementById("userGraph1"));
var option1 = {
	    tooltip: {
	        trigger: 'item',
	        formatter: "{a} <br/>{b}: {c} ({d}%)"
	    },
	    legend: {
	        orient: 'vertical',
	        x: 'left',
	        data:['直达','营销广告','搜索引擎','邮件营销','联盟广告','视频广告','百度','谷歌','必应','其他']
	    },
	    series: [
	        {
	            name:'访问来源',
	            type:'pie',
	            selectedMode: 'single',
	            radius: [0, '30%'],

	            label: {
	                normal: {
	                    position: 'inner'
	                }
	            },
	            labelLine: {
	                normal: {
	                    show: false
	                }
	            },
	            data:[
	                {value:335, name:'直达', selected:true},
	                {value:679, name:'营销广告'},
	                {value:1548, name:'搜索引擎'}
	            ]
	        },
	        {
	            name:'访问来源',
	            type:'pie',
	            radius: ['40%', '55%'],
	            data:[
	                {value:335, name:'直达'},
	                {value:310, name:'邮件营销'},
	                {value:234, name:'联盟广告'},
	                {value:135, name:'视频广告'},
	                {value:1048, name:'百度'},
	                {value:251, name:'谷歌'},
	                {value:147, name:'必应'},
	                {value:102, name:'其他'}
	            ]
	        }
	    ]
	};
userChart1.setOption(option1);

var userChart2 = echarts.init(document.getElementById("userGraph2"));
var option2 = {
		    title: {
		        text: '堆叠区域图'
		    },
		    tooltip : {
		        trigger: 'axis',
		        axisPointer: {
		            type: 'cross',
		            label: {
		                backgroundColor: '#6a7985'
		            }
		        }
		    },
		    legend: {
		        data:['邮件营销','联盟广告','视频广告','直接访问','搜索引擎']
		    },
		    toolbox: {
		        feature: {
		            saveAsImage: {}
		        }
		    },
		    grid: {
		        left: '3%',
		        right: '4%',
		        bottom: '3%',
		        containLabel: true
		    },
		    xAxis : [
		        {
		            type : 'category',
		            boundaryGap : false,
		            data : ['周一','周二','周三','周四','周五','周六','周日']
		        }
		    ],
		    yAxis : [
		        {
		            type : 'value'
		        }
		    ],
		    series : [
		        {
		            name:'邮件营销',
		            type:'line',
		            stack: '总量',
		            areaStyle: {normal: {}},
		            data:[120, 132, 101, 134, 90, 230, 210]
		        },
		        {
		            name:'联盟广告',
		            type:'line',
		            stack: '总量',
		            areaStyle: {normal: {}},
		            data:[220, 182, 191, 234, 290, 330, 310]
		        },
		        {
		            name:'视频广告',
		            type:'line',
		            stack: '总量',
		            areaStyle: {normal: {}},
		            data:[150, 232, 201, 154, 190, 330, 410]
		        },
		        {
		            name:'直接访问',
		            type:'line',
		            stack: '总量',
		            areaStyle: {normal: {}},
		            data:[320, 332, 301, 334, 390, 330, 320]
		        },
		        {
		            name:'搜索引擎',
		            type:'line',
		            stack: '总量',
		            label: {
		                normal: {
		                    show: true,
		                    position: 'top'
		                }
		            },
		            areaStyle: {normal: {}},
		            data:[820, 932, 901, 934, 1290, 1330, 1320]
		        }
		    ]
		};
userChart2.setOption(option2);

var itemChart1 = echarts.init(document.getElementById("itemGraph1"));
itemChart1.setOption(option1);
var itemChart2 = echarts.init(document.getElementById("itemGraph2"));
itemChart2.setOption(option2);

var incomeChart1 = echarts.init(document.getElementById("incomeGraph1"));
incomeChart1.setOption(option1);
var incomeChart2 = echarts.init(document.getElementById("incomeGraph2"));
incomeChart2.setOption(option2);

var payChart1 = echarts.init(document.getElementById("payGraph1"));
payChart1.setOption(option1);
var payChart2 = echarts.init(document.getElementById("payGraph2"));
payChart2.setOption(option2);


/*
 * --------------------------- 地图初始化 ---------------------------
 */
var map = new BMap.Map("map"); // 创建地图实例  
var point = new BMap.Point(116.403959, 39.915122); // 默认点坐标（北京中心）
map.centerAndZoom(point, 15); // 初始化地图，设置中心点坐标和地图级别 
map.addControl(new BMap.NavigationControl()); // 地图平移缩放控件（左上）
map.addControl(new BMap.ScaleControl()); // 比例尺控件（左下）
map.addControl(new BMap.OverviewMapControl()); // 缩略地图控件（右下）
map.addControl(new BMap.MapTypeControl()); // 地图类型控件（右上）
var geolocationControl = new BMap.GeolocationControl(); // 定位控件设置
geolocationControl.addEventListener("locationSuccess", function(e){
	// 定位成功事件
	var address = '';
	address += e.addressComponent.province;
	address += e.addressComponent.city;
	address += e.addressComponent.district;
	address += e.addressComponent.street;
	address += e.addressComponent.streetNumber;
	if(address == ""){
		locationError();
		map.centerAndZoom(point, 15);
	}
});
geolocationControl.addEventListener("locationError",function(e){
	// 定位失败事件
	locationError();
	map.centerAndZoom(point, 15);
});
map.addControl(geolocationControl); // 定位控件（左下）
map.addControl(new BMap.CityListControl({ // 城市列表控件
	anchor: BMAP_ANCHOR_TOP_LEFT,
	offset: new BMap.Size(50, 20)
}));

// 根据浏览器定位
var geolocation = new BMap.Geolocation();
geolocation.getCurrentPosition(function(r){
	if(this.getStatus() == BMAP_STATUS_SUCCESS){
		if(r.point.lng != 0 && r.point.lat != 0 ){
			var mk = new BMap.Marker(r.point);
			map.addOverlay(mk);
			map.panTo(r.point);
		} else {
			locationError();
			var mk = new BMap.Marker(point);
			map.addOverlay(mk);
			map.panTo(point);
		}
	} else {
		locationError();
		var mk = new BMap.Marker(point);
		map.addOverlay(mk);
		map.panTo(point);
	}
},{enableHighAccuracy: true})

// 搜索框查询地址及定位
var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
		{"input" : "suggestId"
		,"location" : map
	});

ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
var str = "";
	var _value = e.fromitem.value;
	var value = "";
	if (e.fromitem.index > -1) {
		value = _value.province + _value.city + _value.district + _value.street + _value.business;
	}
	str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;
	
	value = "";
	if (e.toitem.index > -1) {
		_value = e.toitem.value;
		value = _value.province + _value.city + _value.district + _value.street + _value.business;
	}
	str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
	$("#searchResultPanel")[0].innerHTML = str;
});

var myValue;
ac.addEventListener("onconfirm", function(e) {    //鼠标点击下拉列表后的事件
var _value = e.item.value;
	myValue = _value.province + _value.city + _value.district + _value.street + _value.business;
	$("#searchResultPanel")[0].innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
	
	setPlace();
});

function setPlace(){
	map.clearOverlays();    //清除地图上所有覆盖物
	function myFun(){
		var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
		map.centerAndZoom(pp, 18);
		map.addOverlay(new BMap.Marker(pp));    //添加标注
	}
	var local = new BMap.LocalSearch(map, { //智能搜索
		onSearchComplete: myFun
	});
	local.search(myValue);
}
