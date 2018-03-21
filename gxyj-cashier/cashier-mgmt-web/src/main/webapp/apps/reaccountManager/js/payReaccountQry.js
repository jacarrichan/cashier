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
				"01":"对账相符",
				"02":"对账不符",
				"03":"我方少账",
				"04":"我方多账",
				"05":"笔数不符",
				"06":"金额不符",
				"07":"支付渠道有账，我方无账有订单",
				"08":"我方有账，但无订单",
		};			
	
		this.RowData = {};
	}
	
	PageScript.prototype.init = function(){
		window['$loadData'] = this.loadData;
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		$.useModule(['datatable', 'chosen', 'datetimepicker'], function(){
			page.datatimepicker();
			$('select').chosen({disable_search_threshold: 10});
			/*comm.initPayerInstiNoSelect({ 
				selector: '#payerInstiNo', 
				success: function(){ 
				}, change: function(e, bid){ 
					page.loadData();
				} ,
				hasAll:true
			});*/
			page.loadData();
		});
		
		
	  	page.bindEvent();
	};
	
	
	PageScript.prototype.loadData = function(){
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'reaccountManager/qryPayResultList',
			data: {
				"reclnDate":$('#reclnDate').val(),
				"channelCode":$('#payerInstiNo').val(),
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

		$("#reclnDate").datetimepicker({
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
		});
		
	};
  
	function getFullMD(num){//获得两位数的月和日       
		if(num.toString().length < 2){        
			num= "0" + num;       
		}        
		return num;      
	}
	
	
	PageScript.prototype.renderGrid = function(data){
		var trHtmls = '', obj = {};
		if(data.size && data.list){
			page.RowData = {};
			for(var i = 0; i < data.size; i++){
				obj = data.list[i];
				page.RowData["row-" + obj.rowId] = obj;
				trHtmls += laytpl('list-tr.tpl').render({
					"index": (i < 9 ? '0' : '') + (i + 1),
					"rowId": obj.rowId,
					"channelCode": obj.channelCode,
					"transTtlAmt": obj.transTtlAmt,
					"refundTtlAmt": obj.refundTtlAmt,	
					"transTtlCnt": obj.transTtlCnt,
					"refundTtlCnt": obj.refundTtlCnt,
					"reclnDate": obj.reclnDate,
					"procState": page.procStateModel[obj.procState],
					"tableName": (function(){
									var btnHtml = '';
									if(obj.tableName !=""&&obj.tableName != null){
										btnHtml = obj.tableName;
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
							if(obj.procState !="01"){
						 	 btnHtml += laytpl('list-btn.tpl').render({
								"class": "btn-manualQuery",
								"icon": "icon-hand-down",
								"title": "手工对账",
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
		
		$("#reclnDateParent").width($("#payerInstiNo").next(".chosen-container").width());
	};
	
	win = null;
	PageScript.prototype.bindEvent = function(){
		
		window['$Messager'] = function(text){
			alert(text);
		};
		
		$('.search-btn').on('click', function(){
			page.loadData();
		});
		
		
		//手工下载对账文件
		$('.manualfile-btn').on('click', function() {
			var reclnDate = $('#reclnDate').val();
			var channelCode = $('#payerInstiNo').val();
			if(reclnDate == null || reclnDate == ''){
				alert("请选择对账日期");
				return;
			};
			if(channelCode == null || channelCode == ''){
				alert("请选择支付渠道");
				return;
			};
			
			$.confirm({
				title : '提示',
				yesText : '发起手工对账文件下载',
				msg : '是否向支付渠道发起手工下载对账文件<br>确认要发起手工下载对账文件吗？',
				yesClick : function($modal) {
					ajax.post({
					  url: 'reaccountManager/downLoadReaccFile', 
					  data: { 
						  "reclnDate": reclnDate ,
						  "channelCode": channelCode ,
					  }
					 
					}).done(function(res, rtn, state, msg) {
						if (state) {
							layer_msg.msg("手工下载对账文件成功", 3000);
							page.loadData();
						} else {
							layer_msg.msg(msg, 3000);
						}
					}).fail(function(e, msg) {
						layer_msg.msg(msg, 3000);
					}).always(function() {
						zuiLoad.hide();
					});
					$modal.modal('hide');
				}
			});
		});
		
		
		//手工对账
		$('.manual-btn').on('click', function() {
			var reclnDate = $('#reclnDate').val();
			var channelCode = $('#payerInstiNo').val();
			if(reclnDate == null || reclnDate == ''){
				alert("请选择对账日期");
				return;
			};
			if(channelCode == null || channelCode == ''){
				alert("请选择支付渠道");
				return;
			};
			
			$.confirm({
				title : '提示',
				yesText : '发起手工对账',
				msg : '是否向支付渠道发起手工对账<br>确认要发起手工对账吗？',
				yesClick : function($modal) {
					ajax.post({
					  url: 'reaccountManager/manualReaccount', 
					  data: { 
						  "reclnDate": reclnDate ,
						  "channelCode": channelCode ,
					  }
					 
					}).done(function(res, rtn, state, msg) {
						if (state) {
							layer_msg.msg("手工对账成功", 3000);
							page.loadData();
						} else {
							layer_msg.msg(msg, 3000);
						}
					}).fail(function(e, msg) {
						layer_msg.msg(msg, 3000);
					}).always(function() {
						zuiLoad.hide();
					});
					$modal.modal('hide');
				}
			});
		});
		
		$('label.btn-radio').on('click', function(){
			pager.reset();
			setTimeout(page.loadData, 100);
		});
		
		//手工对账
		$('.result-panel').on('click', '.btn-manualQuery', function(){
			var status = "";
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			    $.confirm({
					title: '提示',
					yesText: '发起手工对账',
					msg: '是否向支付渠道发起手工对账<br>确认要发起手工对账吗？',
					yesClick: function($modal){
						if(rowId){
							var obj = page.RowData["row-" + rowId];
							if(obj){
								
								var zuiLoad = new $.ZuiLoader().show('处理中...');
								ajax.post({
									 url: 'reaccountManager/manualReaccount', 
									  data: { 
										  "reclnDate": obj.reclnDate ,
										  "channelCode": obj.channelCode ,
									  }
								}).done(function(res, rtn, state, msg){
									if(state){
										layer_msg.msg("手工对账成功", 3000);
										page.loadData();
									} else {
										layer_msg.msg(msg, 3000);
									}
								}).fail(function(e, msg){
									layer_msg.msg(msg, 3000);
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
				iframe: utils.formatByJson('./detail_payReaccount.html?type=edit&rowId={rowId}&channelCode={channelCode}&transTtlAmt={transTtlAmt}&refundTtlAmt={refundTtlAmt}&transTtlCnt={transTtlCnt}&refundTtlCnt={refundTtlCnt}&reclnDate={reclnDate}&procState={procState}&tableName={tableName}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		$('.clear-btn').on('click', function(){
			$('#reclnDate').val('');
			$("select").not(".pager-chosen").each(function(){
				var defaultOption = $(this).find("option").eq(0).html();
				$(this).find("option").eq(0).attr("selected", true);
				$(this).next(".chosen-container").find(".chosen-single").find("span").html(defaultOption);
				$(this).next(".chosen-container").find(".chosen-single").find("span").attr("title", defaultOption);
			})
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});