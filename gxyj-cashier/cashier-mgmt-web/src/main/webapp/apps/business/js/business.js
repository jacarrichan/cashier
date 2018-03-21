/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.firstLoad = true;
		this.stateModel = {
				"0":"关闭",
				"1":"启用",
				"2":"维护",
		};
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
	  	page.bindEvent();
	};
	
	// value 值是否含有特殊字符
	function checkCharacter(value) {
		var myReg = /[`~!@#$%^&*_+<>{}\/'[\]]/im;
		var f  = myReg.test(value);
        return f;
	}
	
	PageScript.prototype.loadData = function(){
		// 判断用户的输入是否有特殊字符，有则查询失败，不发送查询条件
		var hasCodeCharacter = $('#channelCode').val().trim() != null && $('#channelCode').val().trim()!='' && checkCharacter($('#channelCode').val().trim());
		var hasNameCharacter = $('#channelName').val().trim() != null && $('#channelName').val().trim()!='' && checkCharacter($('#channelName').val().trim());
		if(hasCodeCharacter) {
			alert("查询条件业务渠道编码不能有特殊字符！");
			return ;
		}
		else if (hasNameCharacter) {
			alert("查询条件业务渠道名称不能有特殊字符！");
			return ;
		}
		
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'business/channelPage',
			data: {
				"channelCode": $('#channelCode').val().trim(),
				"channelName": $('#channelName').val().trim(),
				"startSettlDate": $('#startSettlDate').val(),
				"endSettlDate": $('#endSettlDate').val(),
				"usingStatus": $("input[name='usingStatus']:checked").val()
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
	
	//获得年月日      得到日期oTime  
    function getMyDate(str){  
        var oDate = new Date(str),  
        oYear = oDate.getFullYear(),  
        oMonth = oDate.getMonth()+1,  
        oDay = oDate.getDate(),  
        oHour = oDate.getHours(),  
        oMin = oDate.getMinutes(),  
        oSen = oDate.getSeconds(),  
        oTime = oYear +'-'+ getzf(oMonth) +'-'+ getzf(oDay);//最后拼接时间  
        return oTime;  
    };  
    //补0操作  
    function getzf(num){  
        if(parseInt(num) < 10){  
            num = '0'+num;  
        }  
        return num;  
    }  
    
	PageScript.prototype.renderGrid = function(data){
		var trHtmls = '', 
			obj = {};
		if(data.size && data.list){
			page.RowData = {};
			for(var i = 0; i < data.size; i++){
				obj = data.list[i];
				page.RowData["row-" + obj.rowId] = obj;
				trHtmls += laytpl('list-tr.tpl').render({
					/*"index": (i < 9 ? '0' : '') + (i + 1),*/
					"rowId": obj.rowId,
					"channelCode": obj.channelCode,
					"channelName": obj.channelName,
					"appId": obj.appId,
					"appKey": obj.appKey,
					
					"usingDate": (function(){
						if(obj.usingDate == null || obj.usingDate == undefined){
							return "时间数据有误";
						}
						else if(obj.usingStatus == "0"){
							return "[无]&nbsp;/&nbsp;[<label class='label label-danger'>" + utils.formatDate(obj.usingDate,"yyyy-MM-dd hh:mm:ss") +"</label>]";
						}
						else if(obj.usingStatus == "1"){
							return "[<label class='label label-success'>" + utils.formatDate(obj.usingDate,"yyyy-MM-dd hh:mm:ss") + "</label>]&nbsp;/&nbsp;[无]";
						}
						else if(obj.usingStatus == "2"){
							return "<label class='label btn-warning'>维护中......</label>";
						}
						
					})(),
					
					"usingStatus":(function(){
						var label = '';
						if(obj.usingStatus == '0') {
							label = laytpl('label.tpl').render({
								value: page.stateModel[obj.usingStatus], 
								flag: 'label-danger'
							});
						}
						if(obj.usingStatus == '1') {
							label = laytpl('label.tpl').render({
								value: page.stateModel[obj.usingStatus], 
								flag: 'label-success'
							});
						}
						if(obj.usingStatus == '2') {
							label = laytpl('label.tpl').render({
								value: page.stateModel[obj.usingStatus], 
								flag: 'btn-warning'
							});
						}
						return label;
					})() ,
					
					"buttons": (function(){
						var btnHtml = '';
						
						// 根据启用状态显示图标颜色
						var list_btn_tpl_ioc = "i";
						var list_btn_tpl_title = "";
						if(obj.usingStatus == '0') {
							list_btn_tpl_title = "启用";
							list_btn_tpl_ioc = "icon-play-circle";
						}
						if(obj.usingStatus == '1') {
							list_btn_tpl_title="关闭";
							list_btn_tpl_ioc = "icon-off";
						}
						
						btnHtml += laytpl('list-btn.tpl').render({ // 启用关闭 图标
							"class": "btn-off",
							"icon": list_btn_tpl_ioc,
							"rightCode": "close",
							"title": list_btn_tpl_title
						}) + '&nbsp;';
						
						if(obj.usingStatus == '0') { // 未启用的记录可以看到重置，修改，删除按钮
							/*btnHtml += laytpl('list-btn.tpl').render({ // 重置appid和appkey
								"class": "btn-repeat",
								"icon": "icon-repeat",
								"title": "重置appId和Key",
								"rightCode": "changeIdKey"
							}) + '&nbsp;';*/
							
							btnHtml += laytpl('list-btn.tpl').render({ // 修改图标
								"class": "btn-edit",
								"icon": "icon-edit",
								"title": "修改",
								"rightCode": "edit"
							}) + '&nbsp;';
							
							/*btnHtml += laytpl('list-btn.tpl').render({ //删除图标
								"class": "btn-del",
								"icon": "icon-trash",
								"title": "删除",
								"rightCode": "delete"
							}) + '&nbsp;';*/
						}
						
						if(obj.usingStatus == '1') { //启用的记录才会有维护功能
							btnHtml += laytpl('list-btn.tpl').render({ // 进行维护操作
								"class": "btn-cogs",
								"icon": "icon-cogs",
								"title": "进行维护",
								"rightCode": "btnCogs"
							}) + '&nbsp;';
						}
						
						if (obj.usingStatus == '2') { //处于维护状态的记录会有维护关闭图标
							btnHtml += laytpl('list-btn.tpl').render({ 
								"class": "btn-dot-circle",
								"icon": "icon-dot-circle",
								"title": "关闭维护",
								"rightCode": "btnCircle"
							}) + '&nbsp;';
						}
						
						btnHtml += laytpl('list-btn.tpl').render({ // 维护详情
							"class": "btn-list-alt",
							"icon": "icon-list-alt",
							"rightCode": "listAlt",
							"title": "维护详情"
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
	
	PageScript.prototype.datatimepicker=function(){

		$("#startSettlDate").datetimepicker({
		    language:  "zh-CN",
		    weekStart: 1,
		    todayBtn:  1,
		    autoclose: 1,
		    todayHighlight: 1,
		    startView: 2,
		    minView: 2,
		    forceParse: 0,
		    endDate: new Date(),
		    format: "yyyy-mm-dd"
		}).on('changeDate',function(e){
			var startSettlDate = $("#startSettlDate").val();
			//controlDate(startSettlDate,"");
			
			var endDate=$("#endSettlDate").val(); 
			
			if(compareDay(startSettlDate, endDate)==-1){
				alert('开始日期大于结束日期');
				$("#startSettlDate").val("");
				return;
			}
			
		});
		
		$("#endSettlDate").datetimepicker({
		    language:  "zh-CN",
		    weekStart: 1,
		    todayBtn:  1,
		    autoclose: 1,
		    todayHighlight: 1,
		    startView: 2,
		    minView: 2,
		    forceParse: 0,
		    endDate: new Date(),
		    format: "yyyy-mm-dd"
		}).on('changeDate',function(e){
			var endSettlDate = $("#endSettlDate").val();
			//controlDate("",endSettlDate);
			
			var startSettlDate = $("#startSettlDate").val();
			if(compareDay(startSettlDate, endSettlDate)==-1){
				alert('结束日期小于开始日期');
				$("#endSettlDate").val("");
				return;
			}
		});
	};
	
  function controlDate(start,end){//控制日期不能超过三个月
	  if(start!=''){
		  var date=new Date(start);
		  var year=date.getFullYear();
		  var month=date.getMonth()+3;//加1是当前月加3是3个月
		  var day=date.getDate();//当前日期
		  //加判断如果月份大于12表示当前月是12月,日期应该是下一年的1月
		  if(month>12){
			  month = month-12;
			  year = year+1;
		  }
		  month = getFullMD(month);
		  day = getFullMD(day);
		  date = year + '-' + month + '-' + day;
		  $('#endSettlDate').datetimepicker("setStartDate", start);
		  $('#endSettlDate').datetimepicker("setEndDate", date);
	  }
	  if(end!=''){
		  var date=new Date(end);
		  var year=date.getFullYear();
		  var month=parseInt(date.getMonth()-1);//加1是当前月加-1是3个月
		  var day=date.getDate();//当前日期
		  //加判断如果月份小于等于0,日期应该是前一年的月份
		  if(month <= 0){
			  month=12+month;
			  year=year-1;
		  }
		  month = getFullMD(month);
		  day = getFullMD(day);
		  date = year + '-' + month + '-' + day;
		  $('#startSettlDate').datetimepicker("setStartDate",date);
		  $('#startSettlDate').datetimepicker("setEndDate",end);
	  }
  }
	
	function getFullMD(num){//获得两位数的月和日       
		if(num.toString().length<2){        
			num="0"+num;       
		}        
		return num;      
	}
	
	function compareDay(a,b){//a,b格式为yyyy-MM-dd
		var a1=a.split("-");
		var b1=b.split("-");
		var d1=new Date(a1[0],a1[1],a1[2]);
		var d2=new Date(b1[0],b1[1],b1[2]);
		
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
	
	win = null;
	PageScript.prototype.bindEvent = function(){
		
		window['$Messager'] = function(text){
			alert(text);
		};

		/*维护详情页面*/
		$('.result-panel').on('click', '.btn-list-alt', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
		    var obj = page.RowData["row-" + rowId];
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '维护详情页',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 900,
				height: 600,
				iframe: utils.formatByJson('./business_vind.html?type=list&channelId={rowId}&channelName={channelName}', obj)					
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
			$('.modal-dialog .close').on('click', function(){
				page.loadData();
			});
		});
		
		/* 添加功能   事件触发*/
		$('.btn-add').on('click', function(){
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '新增业务渠道',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: './business_add_edit.html?type=add'
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
			$('.modal-dialog .close').on('click', function(){
				page.loadData();
			});
		});
		
		/* 维护功能 事件触发 */
		$('.result-panel').on('click', '.btn-cogs', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '添加维护任务',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: utils.formatByJson('./business_vind_add_edit.html?type=add&channelId={rowId}&channelName={channelName}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		/* 关闭维护*/
		$('.result-panel').on('click', '.btn-dot-circle', function(){
			var status = "";
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			    $.confirm({
					title: '提示',
					yesText: "确认关闭",
					msg: '您正在进行关闭维护操作<br>确认要关闭该记录的维护吗？',
					yesClick: function($modal){
						if(rowId){
							if(obj){
								var zuiLoad = new $.ZuiLoader().show('处理中...');
								ajax.post({
									url: 'businessvind/closed',
									data: {
										"channelId": rowId,
									}
								}).done(function(res, rtn, state, msg){
									if(state){
										alert(msg);
										page.loadData();
									} else {
										alert(msg);
									}
								}).fail(function(e, msg){
									alert("重发请求失败：" + msg);
								}).always(function(){
									zuiLoad.hide();
								});
							}
						}
						$modal.modal('hide');
					}
				});
		});
		
		
		/* 修改功能 事件触发 */
		$('.result-panel').on('click', '.btn-edit', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '修改业务渠道',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: utils.formatByJson('./business_add_edit.html?type=edit&rowId={rowId}&channelCode={channelCode}&channelName={channelName}&usingStatus={usingStatus}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		// 启用状态更改
		$('.result-panel').on('click', '.btn-off', function(){
			var status = "";
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			var confirm_txt = "";
			if (obj.usingStatus == "0") {
				confirm_txt = "开启";
			}
			else {
				confirm_txt = "关闭";
			}
		    $.confirm({
				title: '提示',
				yesText: "确认" + confirm_txt,
				msg: '您正在进行' + confirm_txt + '操作<br>确认要' + confirm_txt + '发布此记录吗？',
				yesClick: function($modal){
					if(rowId){
						if(obj){
							var zuiLoad = new $.ZuiLoader().show('处理中...');
							ajax.post({
								url: 'business/changeStatus',
								data: {
									"rowId": rowId,
									"usingStatus": obj.usingStatus,
								}
							}).done(function(res, rtn, state, msg){
								if(state){
									alert(msg);
									page.loadData();
								} else {
									alert(msg);
								}
							}).fail(function(e, msg){
								alert("重发请求失败：" + msg);
							}).always(function(){
								zuiLoad.hide();
							});
						}
					}
					$modal.modal('hide');
				}
			});
		});
		
		/* 删除小图标 触发事件*/
		$('.result-panel').on('click', '.btn-del', function(){
			var status = "";
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			$.confirm({
				title: '提示',
				yesText: '确认删除',
				msg: '您正在进行删除操作<br>确认要删除此记录吗？',
				yesClick: function($modal){
					if(rowId){
						if(obj){
							var zuiLoad = new $.ZuiLoader().show('处理中...');
							ajax.post({
								url: 'business/delete',
								data: {
									"rowId": rowId,
								}
							}).done(function(res, rtn, state, msg){
								if(state){
									alert("删除成功");
									page.loadData();
								} else {
									alert(msg);
								}
							}).done(function() { // 无需操作
								
							}).fail(function(e, msg){
								alert("重发请求失败：" + msg);
							}).always(function(){
								zuiLoad.hide();
							});
						}
					}
					$modal.modal('hide');
				}
			});
		});
		
		/* 重置appId和key小图标 触发事件*/
		$('.result-panel').on('click', '.btn-repeat', function(){
			var status = "";
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			$.confirm({
				title: '提示',
				yesText: '确认重置',
				msg: '您正在进行重置AppId和AppKey操作<br>确认要执行操作吗？',
				yesClick: function($modal){
					if(rowId){
						if(obj){
							var zuiLoad = new $.ZuiLoader().show('处理中...');
							ajax.post({
								url: 'business/appIdKey',
								data: {
									"rowId": rowId,
								}
							}).done(function(res, rtn, state, msg){
								if(state){
									alert(msg);
									page.loadData();
								} else {
									alert(msg);
								}
							}).done(function() { // 无需操作
								
							}).fail(function(e, msg){
								alert("重发请求失败：" + msg);
							}).always(function(){
								zuiLoad.hide();
							});
						}
					}
					$modal.modal('hide');
				}
			});
		});
		
		
		/* 点击重置按钮清空查询条件*/
		$('.clear-btn').on('click', function(){ 
			$('#channelCode').val(''); 
			$('#channelName').val('');
			$("#startSettlDate").val("");
			$("#endSettlDate").val("");
			$("#btnradio").click();
			 
		});
		
		/* 点击查询按钮触发查询操作 */
		$('.search-btn').on('click', function(){ 
			page.loadData();
		});
		
		/* 选择查询条件的 启用状态 事件 */
		$('label.btn-radio').on('change', function(){
			page.loadData();
			
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});