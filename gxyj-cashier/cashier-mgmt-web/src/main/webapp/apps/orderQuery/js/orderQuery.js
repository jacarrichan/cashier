define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.firstLoad = true;
		this.terminalModel = {
				"01":"PC",
				"02":"WAP",
				"03":"APP",
				"04":"其它",
		};
		
		this.procStateModel = {
				"00":"成功",
				"01":"失败",
				"02":"未支付",
				"03":"处理中",
				"04":"订单关闭",
				"05":"订单超时",
		};
		
		this.stateModel = {
				"0":"未通知",
				"1":"通知成功",
				"2":"通知失败",
		};
	
		this.RowData = {};
		this.mallIdSelect = "";
		this.mallVal = "";
	}
	
	PageScript.prototype.init = function(){
		window['$loadData'] = this.loadData;
		
		ajax.getUserInfo().done(function(user){
			page.user = user;
		});
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		$.useModule(['datatable', 'chosen', 'datetimepicker'], function(){
			var hasAll = false;
			if(page.user.mallId == '00000000') {
				hasAll = true;
			}
			
			page.datatimepicker();
			$('select').chosen({disable_search_threshold: 10});
			comm.initMallInfoSelect({ 
				selector: '#mallId', 
				success: function(){ 
					page.mallIdSelect = $("#mallId").find("option[selected='selected']").html();
					page.mallVal = $("#mallId").find("option[selected='selected']").val();
					page.loadData();
				}, change: function(e, bid){ 
					page.loadData();
				} ,
				hasAll:hasAll,
				allName:'全部',
				selectValue:page.user.mallId
			});
			
			comm.initPayerInstiNoSelect({ 
				selector: '#payerInstiNo', 
				success: function(){ 
				}, change: function(e, bid){ 
					page.loadData();
				} ,
				hasAll: true
			});
			
			comm.initBusiChannelSelect({ 
				selector: '#channelId', 
				success: function(){ 
					page.loadData();
				}, change: function(e, bid){ 
					page.loadData();
				},
				selectValue:'',
				hasAll: true,
				allName: '全部'
			});
			
		});
		
	  	page.bindEvent();
	};
	
	
	PageScript.prototype.loadData = function(){
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		var mallId = $("#mallId").val();
		ajax.post({
			url: 'order/queryList',
			data: {
				"channelId":$('#channelId').val(), 
				"orderId" : $('#orderId').val(),
				"terminal" : $('#terminal').val(),
				"mallId" : $("#mallId").val(),
				"payerInstiNo" : $('#payerInstiNo').val(),
				"procState" : $('#procState').val(),
				"startDate":$('#startDate').val(),
				"endDate":$('#endDate').val(),
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
			
			if(compareDay(startDate, endDate)==1){
				//alert('开始日期大于结束日期');
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
			if(compareDay(startDate, endDate)==-1){
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
   if(end!=''){
		var date=new Date(end);
		var year=date.getFullYear();
		var month=parseInt(date.getMonth()-1);//加1是当前月加-1是3个月
		var day=date.getDate();//当前日期
		//加判断如果月份小于等于0,日期应该是前一年的月份
		if(month <= 0){
			month = 12+month;
			year = year-1;
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
				page.RowData["row-" + obj.rowId] = obj;
				trHtmls += laytpl('list-tr.tpl').render({
					"index": (i < 9 ? '0' : '') + (i + 1),
					"rowId": obj.rowId,
					"transId": obj.transId,
					"channelCd":  (function(){
					    var btnHtml = '';
					    if(obj.channelCd !="" && obj.channelCd !=null){
					    	btnHtml = obj.channelCd;
					    }
					    return btnHtml;
				     })(),
				    "mallId" : obj.mallId,
					"terminal": (function(){
					    var btnHtml = '';
					    if(obj.terminal =='01'||obj.terminal =='02'||obj.terminal =='03'||obj.terminal =='04'){
					    	btnHtml =	page.terminalModel[obj.terminal];	
					    }
					    return btnHtml;
				     })(),
					"orderId": obj.orderId,
					"transTime": utils.formatDate(obj.transTime,"yyyy-MM-dd hh:mm:ss"),
					"transAmt": obj.transAmt.toFixed(2),
					"payerInstiNm": (function(){
					    var btnHtml = '';
					    if(obj.payerInstiNm != "" && obj.payerInstiNm !=null){
					    	btnHtml = obj.payerInstiNm;
					    }
					    return btnHtml;
				     })(),
						
					"procState": (function(){
						    var btnHtml = '';
							if(obj.procState != "" && obj.procState != null){
								btnHtml = page.procStateModel[obj.procState];
							}
							else{
								btnHtml = "未支付";
							}
						    return btnHtml;
					 })(),
					 
					 "remark": (function(){  //回调状态
						    var btnHtml = '';
							if(obj.remark != "" && obj.remark != null){
								btnHtml = page.stateModel[obj.remark];
							}
						    return btnHtml;
					 })(),
					"buttons": (function(){
						var btnHtml = '';
							btnHtml += laytpl('list-btn.tpl').render({
								"class": "btn-detail",
								"icon": "icon-newspaper-o",
								"title": "详细"
							}) + '&nbsp;';
							if(obj.procState !="00" && obj.procState !="01" && obj.procState !="02" && obj.procState !="04"){
						 	 btnHtml += laytpl('list-btn.tpl').render({
								"class": "btn-manualQuery",
								"icon": "icon-hand-down",
								"title": "手工查询",
							}) + '&nbsp;';
							
					    }
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
		$("#orderIdParent").width($("#mallId").next(".chosen-container").width());
	};
	
	win = null;
	PageScript.prototype.bindEvent = function(){
		
		window['$Messager'] = function(text){
			alert(text);
		};
		
		
		$('#channelId').on('change', function(){
			page.loadData();
		});
		
		$('#terminal').on('change', function(){
			page.loadData();
		});
		
		$('#payerInstiNo').on('change', function(){
			page.loadData();
		});
		
		$('#procState').on('change', function(){
			page.loadData();
		});
		
		$('.search-btn').on('click', function(){
			page.loadData();
		});
		
		$('label.btn-radio').on('click', function(){
			pager.reset();
			setTimeout(page.loadData, 100);
		});
		
		//手工查询
		$('.result-panel').on('click', '.btn-manualQuery', function(){
			var status = "";
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
		    $.confirm({
				title: '提示',
				yesText: '发起查询',
				msg: '是否向支付渠道发起该订单查询<br>确认要发起查询吗？',
				yesClick: function($modal){
					if(rowId){
						var obj = page.RowData["row-" + rowId];
						if(obj){
							
							var zuiLoad = new $.ZuiLoader().show('处理中...');
							ajax.post({
								url: 'order/qryPayResult',
								data: {
									"orderId": obj.orderId ,
									"transId": obj.transId,
								}
							}).done(function(res, rtn, state, msg){
					/*			if(state){
									layer_msg.msg(msg, 2000);
									page.loadData();
								} else {*/
									layer_msg.msg(msg, 3000);
									page.loadData();
								/*}*/
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
		
		
		//详细
		$('.result-panel').on('click', '.btn-detail', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '详细信息',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: utils.formatByJson('./detail_orderQuery.html?type=edit&transId={transId}&channelCd={channelCd}&mallId={mallId}&terminal={terminal}&orderId={orderId}&transTime={transTime}&transAmt={transAmt}&payerInstiNm={payerInstiNm}&procState={procState}&remark={remark}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		$('.clear-btn').on('click', function(){
			$("#orderId").val("");
			$("#startDate").val("");
			$("#endDate").val("");
			$("select").not(".pager-chosen").each(function(){
				var defaultOption = $(this).find("option").eq(0).html();
				$(this).find("option").eq(0).attr("selected", true);
				$(this).next(".chosen-container").find(".chosen-single").find("span").html(defaultOption);
				$(this).next(".chosen-container").find(".chosen-single").find("span").attr("title", defaultOption);
				$("#mallId").next(".chosen-container").find(".chosen-single").find("span").html(page.mallIdSelect);
				$("#mallId").next(".chosen-container").find(".chosen-single").find("span").attr("title", page.mallIdSelect);
				$("#mallId").val(page.mallVal);
			})
			page.loadData();
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});