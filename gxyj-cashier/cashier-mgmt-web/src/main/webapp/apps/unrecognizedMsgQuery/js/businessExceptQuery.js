define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.firstLoad = true;
		this.RowData = {};
	}
	
	PageScript.prototype.init = function(){
		window['$loadData'] = this.loadData;
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		$.useModule(['datatable', 'chosen', 'datetimepicker'], function(){
			page.datatimepicker();
			page.loadData();
		});
		
		$("#source").empty();
		$("#source").append("<option value=\"\">全部</option>");
		ajax.post({
			url: 'business/queryBusiChannelList',
			data: {"source": ''}
		}).done(function(data){
			var html_str = "";
			for ( var key in data.rtn.data) {
				html_str += "<option value=\"" +  data.rtn.data[key].channelCode + "\">"
						+  data.rtn.data[key].channelName + "</option>";
			}
			$("#source").append(html_str);
		});
		
	  	page.bindEvent();
	};
	
	
	PageScript.prototype.loadData = function(){
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'order/businessExceptQuery',
			data: {
				"source" : $('#source').val(),
				"startDate": $('#startDate').val(),
				"endDate": $('#endDate').val(),
				"msgId": $('#msgId').val(),
			},
			pager: pager // 传入分页对象
		}).done(function(res, rtn, state, msg){
			if(state){
				page.renderGrid(rtn.data);
			} else {
				alert('数据加载失败：' + msg);
			}
		}).fail(function(){
			
		}).always(function(){
			zuiLoad.hide();
		});
		
	};
	
	
	

	//日期处理
	PageScript.prototype.datatimepicker=function(){

		$("#startDate").datetimepicker({
		    language:  "zh-CN",
		    weekStart: 1,
		    todayBtn:  1,
		    autoclose: 1,
		    todayHighlight: 1,
		    startView: 2,
		    minView: 2,
		    forceParse: 0,
		    format: "yyyy-mm-dd"
		}).on('changeDate',function(e){
			var startDate = $("#startDate").val();
			controlDate(startDate,"");
			
			var endDate=$("#endDate").val(); 
			
			if(compareDay(startDate, endDate) == -1){
				alert('开始日期大于结束日期');
				return;
			}
			
		});
		
		$("#endDate").datetimepicker({
		    language:  "zh-CN",
		    weekStart: 1,
		    todayBtn:  1,
		    autoclose: 1,
		    todayHighlight: 1,
		    startView: 2,
		    minView: 2,
		    forceParse: 0,
		    format: "yyyy-mm-dd"
		}).on('changeDate',function(e){
			var endDate = $("#endDate").val();
			controlDate("",endDate);
			
			var startSettlDate = $("#startDate").val();
			if(compareDay(startDate, endDate) == -1){
				alert('开始日期大于结束日期');
			}
		});
	};
	
	function controlDate(start,end){//控制日期不能超过三个月
		if(start != ''){
			var date = new Date(start);
			var year = date.getFullYear();
			var month = date.getMonth()+3;//加1是当前月加3是3个月
			var day = date.getDate();//当前日期
			//加判断如果月份大于12表示当前月是12月,日期应该是下一年的1月
			if(month > 12){
				month = month - 12;
				year = year + 1;
			}
			month = getFullMD(month);
			day = getFullMD(day);
			date = year + '-' + month + '-' + day;
			$('#endDate').datetimepicker("setStartDate",start);
			$('#endDate').datetimepicker("setEndDate",date);
		 }
		 if(end != ''){
			var date = new Date(end);
			var year = date.getFullYear();
			var month = parseInt(date.getMonth()-1);//加1是当前月加-1是3个月
			var day = date.getDate();//当前日期
			//加判断如果月份小于等于0,日期应该是前一年的月份
			if(month <= 0){
				month = 12 + month;
				year = year - 1;
			}
			month = getFullMD(month);
			day = getFullMD(day);
			date = year + '-' + month + '-' + day;
			$('#startDate').datetimepicker("setStartDate",date);
			$('#startDate').datetimepicker("setEndDate",end);
		 }
	}
	
	function getFullMD(num){//获得两位数的月和日       
		if(num.toString().length < 2){        
			num = "0" + num;       
		}        
		return num;      
	}
	
	function compareDay(a,b){//a,b格式为yyyy-MM-dd
		var a1 = a.split("-");
		var b1 = b.split("-");
		var d1 = new Date(a1[0],a1[1],a1[2]);
		var d2 = new Date(b1[0],b1[1],b1[2]);
		
		if(Date.parse(d1)-Date.parse(d2) > 0){//a>b 开始日期大于结束日期
			return-1;
		}
		
		if(Date.parse(d1)-Date.parse(d2) == 0){//a=b开始日期等于结束日期
			return 0;
		}
		
		if(Date.parse(d1)-Date.parse(d2) < 0){//a<b开始日期小于结束日期
			return 1;
		}
	}
	
	PageScript.prototype.renderGrid = function(data){
		var trHtmls = '',
			obj = {};
		if(data.size && data.list){
			page.RowData = {};
			for(var i = 0; i < data.size; i++){
				obj = data.list[i];
				page.RowData["row-" + obj.msgId] = obj;
				trHtmls += laytpl('list-tr.tpl').render({
					"index": (i < 9 ? '0' : '') + (i + 1),
					"msgId": obj.msgId,
					"msgCreateTime": obj.msgCreateTime,
					"source": obj.source,
					"interfaceCode":(function(){
					    var btnHtml = '';
					    if(obj.interfaceCode != "" && obj.interfaceCode !=null){
					    	btnHtml = obj.interfaceCode;
					    }
					    return btnHtml;
				     })(),
				     "rtnCode": (function(){
						    var btnHtml = '';
						    if(obj.rtnCode != "" && obj.rtnCode !=null){
						    	btnHtml = obj.rtnCode;
						    }
						    return btnHtml;
					     })(),
					"rtnMsg": (function(){
					    var btnHtml = '';
					    if(obj.errDesc != "" && obj.errDesc !=null){
					    	btnHtml = obj.rtnMsg;
					    }
					    return btnHtml;
				     })(),
					"msgData": obj.msgData,
					"buttons": (function(){
						var btnHtml = '';
							btnHtml += laytpl('list-btn.tpl').render({
								"class": "btn-detail",
								"icon": "icon-newspaper-o",
								"title": "报文详情"
							}) + '&nbsp;';
						return btnHtml;
					})()
				});
			}
		}
		
		$('#data-body').html(trHtmls);
		if(page.firstLoad){
			page.firstLoad = false;
			$('table.datatable').datatable({
				sortable: true,
				checkable: false
			})
		} else {
			$('table.datatable').datatable('load');
		}
		// 创建分页条
		pager.create('.pager-box', data);
		$('.icon-btn, .tip-icon').tooltip({html: true});
		
	};
	
	win = null;
	PageScript.prototype.bindEvent = function(){
		
		window['$Messager'] = function(text){
			alert(text);
		};
		
		$('.search-btn').on('click', function(){
			page.loadData();
		});
		
		$('#source').on('change', function(){
			page.loadData();
		});
		
		$('label.btn-radio').on('click', function(){
			pager.reset();
			setTimeout(page.loadData, 100);
		});
		
		
		$('.result-panel').on('click', '.btn-detail', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				msgId = $tr.data('id');
			var obj = page.RowData["row-" + msgId];
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '报文详情',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: utils.formatByJson('./detail_businessMsg.html?type=edit&msgId={msgId}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		$('.clear-btn').on('click', function(){
			$("#startDate").val("");
			$("#endDate").val("");
			$('#channelId').val('');
			$('#payerInstiNo').val('');
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});