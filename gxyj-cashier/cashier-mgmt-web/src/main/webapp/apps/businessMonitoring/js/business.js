/**
 * 主框架页面脚本
 */
define(function(require, exports, module){
	
	var comm = require('../../../common/js/page-common');
	pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.myChart = {};
		this.echartsLists = [];
		
	}
	
	PageScript.prototype.init = function(){	
		$.useModule(['datatable', 'echarts']);
		page.bindEvent();
		
		/* 监控数据方法 */
		function ajaxMonitoring () {
			/* 交易记录监控数据 */
			ajax.post({
				url : "order/orderMonitor",
				data: {},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				var recordsData = rtn;
				laytpl('recordsTplLists').render(recordsData, function(html){
					document.getElementById('recordsTb').innerHTML = html;
					//$("#recordsTb").append(html);
				});
				laytpl('timeTpl').render(recordsData, function(html){
					document.getElementById('timeMonitor').innerHTML = html;
					document.getElementById('channelTime').innerHTML = html;
				});
				page.myChart = echarts.init(document.getElementById('main'));
				var dd = rtn.data.list;
				var d1 = [],
					d2 = [],
					d3 = [];
				for(var i = 0; i < dd.length; i++){
					d1.push(dd[i].channelCd);
					d2.push(dd[i].sumTransAmt);
					d3.push(dd[i].sumTransCount);
				}
				 // 指定图表的配置项和数据
		        var option = {
		        	    tooltip : {
		        	        trigger: 'axis'
		        	    },
		        	    toolbox: {
		        	        show : true,
		        	        feature : {
		        	            mark : {show: true},
		        	            dataView : {show: true, readOnly: false},
		        	            magicType: {show: true, type: ['line', 'bar']},
		        	            restore : {show: true},
		        	            saveAsImage : {show: true}
		        	        }
		        	    },
		        	    calculable : true,
		        	    legend: {
		        	        data:['订单金额','订单数']
		        	    },
		        	    xAxis : [
		        	        {
		        	            type : 'category',
		        	            data : d1
		        	        }
		        	    ],
		        	    yAxis : [
		        	        {
		        	            type : 'value',
		        	            name : '订单金额',
		        	            axisLabel : {
		        	                formatter: '￥{value}'
		        	            }
		        	        },
		        	        {
		        	            type : 'value',
		        	            name : '订单数',
		        	            axisLabel : {
		        	                formatter: '{value}'
		        	            }
		        	        }
		        	    ],
		        	    series : [
		        	        {
		        	            name:'订单金额',
		        	            type:'bar',
		        	            barWidth: 20,
		        	            data:d2
		        	        },
		        	        {
		        	            name:'订单数',
		        	            type:'line',
		        	            barWidth: 30,
		        	            yAxisIndex: 1,
		        	            data:d3
		        	        }
		        	    ]
		        	};
		        // 使用刚指定的配置项和数据显示图表。
		        page.myChart.setOption(option);
		        window.onresize = function(){
		        	myChart.resize();
		        }
				
			}).fail(function() {			
				console.log("fail");
			}).always(function() {					
				//console.log("always");
			});
			
			/* 支付渠道监控数据 */
			ajax.post({
				url : "order/payOrderMonitor",
				data: {},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				var channelData = rtn.data.list;
				var optionsItems = [];
				for(var k = 0; k < channelData.length; k++){
					optionsItems.push({
						tooltip : {
		        	        trigger: 'axis'
		        	    },
		        	    toolbox: {
		        	        show : true,
		        	        feature : {
		        	            mark : {show: true},
		        	            dataView : {show: true, readOnly: false},
		        	            magicType: {show: true, type: ['line', 'bar']},
		        	            restore : {show: true},
		        	            saveAsImage : {show: true}
		        	        }
		        	    },
		        	    calculable : true,
		        	    legend: {
		        	        data:['成功订单数','失败订单数','超时订单数']
		        	    },
		        	    xAxis : [
		        	        {
		        	            type : 'category',
		        	            data : ["成功订单数","失败订单数","超时订单数"]
		        	        }
		        	    ],
		        	    yAxis : [
		        	        {
		        	            type : 'value',
		        	            name : '订单数',
		        	            axisLabel : {
		        	                formatter: '{value}'
		        	            }
		        	        }
		        	    ],
		        	    series : [
		        	        {
		        	            name:'订单数',
		        	            type:'bar',
		        	            barWidth: 15,
		        	            data:[channelData[k].sumSuccCount,channelData[k].sumFailCount,channelData[k].sumTimeoutCount]
		        	        }
		        	    ]
					});
				}
				laytpl('payChannelTpl').render(channelData, function(html) {
					document.getElementById('payChanneTb').innerHTML = html;
				});
				for(var m = 0; m < optionsItems.length; m++) {
					page.echartsLists[m] = echarts.init(document.getElementById('echarts-'+m));
					page.echartsLists[m].setOption(optionsItems[m]);
				}
				window.onresize = function(){
					for(var j=0; j< page.echartsLists.length; j++) {
						page.echartsLists[j].resize();
					}
				}
			}).fail(function() {			
				console.log("fail");
			}).always(function() {					
				//console.log("always");
			});		
		}
		/* 页面初始化加载数据 */
		ajaxMonitoring();
		
		/* 定时轮询更新 */
		var pollTime;
		var timer = null;
		ajax.post({
			url : "order/queryParamDate",
			data: {},
			checkSession: false
		}).done(function(res, rtn, state, msg) {
			pollTime = rtn.paramValue;
			//timer = window.setInterval(ajaxMonitoring, pollTime);
		}).fail(function() {			
			console.log("fail");
		}).always(function() {					
				//console.log("always");
		});
	}
	
	PageScript.prototype.bindEvent = function(){
		/* 存储信息以及根据传递的信息做跳转 */
	  	function ajaxLocalStorage(dataInfo,dataId,dataIndex,dataHref) {
	  		if(!window.localStorage){
	            alert("浏览器支持不localstorage,请更新或更换浏览器");
	            return false;
	        }else{
	            var storage=window.localStorage;
	            storage.setItem("dataInfo", dataInfo);
	            storage.setItem("dataId", dataId);
	            storage.setItem("dataIndex", dataIndex);
	            storage.setItem("dataHref", dataHref);
	        }   
			ajax.post({
				url : "base/session",
				data: {
					/*orderId: orderBean.orderId*/
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				if (state == true) {
					var userInformation = rtn;
					var userName = userInformation.data.userName;
					window.open(consts.WEB_BASE +"apps/frame/index.html?uName="+userName);
				} else {
					window.open(consts.WEB_BASE + "apps/frame/login.html");
				}
			}).fail(function() {			
				console.log("fail");
			}).always(function() {					
				//console.log("always");
			});
	  	}
	  	
	  	/* 交易记录监控 */
	  	$(document).on("click", "#main", function(){
	  		var dataInfo = $("#recordsLink").attr("data-info");
			var dataId = $("#recordsLink").attr("data-id");
			var dataIndex = $("#recordsLink").attr("data-index");
			var dataHref = $("#recordsLink").attr("data-href");
			ajaxLocalStorage(dataInfo,dataId,dataIndex,dataHref);
	  	});
	  	/* 支付渠道监控 */
		$(document).on("click", "#payChanneTb .channel-list .echarts-item", function () {
			var dataInfo = $(this).attr("data-info");
			var dataId = $(this).attr("data-id");
			var dataIndex = $(this).attr("data-index");
			var dataHref = $(this).attr("data-href");
			ajaxLocalStorage(dataInfo,dataId,dataIndex,dataHref);
		});
	}
	
	PageScript.prototype.loadData = function() {

		//	var zuiLoad = new $.ZuiLoader().show('数据加载中...');

	};
	
	var page = new PageScript();
	page.init();
	
	return page;
});