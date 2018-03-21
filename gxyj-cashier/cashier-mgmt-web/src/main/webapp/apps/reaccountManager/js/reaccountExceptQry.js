define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.firstLoad = true;
		this.orderTypeModel = {
				"0":"正常订单",
				"1":"退款订单",
		};
		this.reconStatusModel = {
				"01":"待处理",
				"02":"对账完成",
				"03":"对账失败",
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
			url: 'reaccountManager/qryExceptResultList',
			data: {
				"reclnDate":$('#reclnDate').val(),
				"payInstiCode":$('#payerInstiNo').val(),
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
					"payInstiCode":(function(){
						var payInstiCodeHtml = '';
						if(obj.payInstiCode!==null && obj.payInstiCode !==''){
							payInstiCodeHtml = obj.payInstiCode;
							}
							return payInstiCodeHtml;
						})(),
					"transAmt":(function(){
						var transAmtHtml = '';
						if(obj.chargeFee !== null && obj.chargeFee !==''){
							transAmtHtml = obj.chargeFee.toFixed(2);
						}
						return transAmtHtml;
					})(),
					"channelName":(function(){
							var channelNameHtml = '';
							if(obj.channelName!==null && obj.channelName !==''){
								channelNameHtml = obj.channelName;
								}
								return channelNameHtml;
					  })(),
					"transId": obj.transId,	
					"payStatus":(function(){
							if(obj.payStatus == null || obj.payStatus ==''){
							    return '';
							}else{
								return obj.payStatus;
								}
							})(),
					"orderType":  page.orderTypeModel[obj.orderType],
					"reconStatus":(function(){
						var label = '';
						if(obj.reconStatus == '03') {
							label = laytpl('label.tpl').render({
								value: page.reconStatusModel[obj.reconStatus], 
								flag: 'label-danger'
							});
						}
						if(obj.reconStatus == '02') {
							label = laytpl('label.tpl').render({
								value: page.reconStatusModel[obj.reconStatus], 
								flag: 'label-success'
							});
						}
						if(obj.reconStatus == '01') {
							label = laytpl('label.tpl').render({
								value: page.reconStatusModel[obj.reconStatus], 
								flag: 'btn-warning'
							});
						}
						return label;
					})() ,
					"reclnDate": obj.reclnDate,
					"errorInfo": obj.errorInfo,
					"buttons": (function(){
						var btnHtml = '';
							btnHtml += laytpl('list-btn.tpl').render({
								"class": "btn-detail",
								"icon": "icon-newspaper-o",
								"title": "查看明细"
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
		
		$('label.btn-radio').on('click', function(){
			pager.reset();
			setTimeout(page.loadData, 100);
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
				iframe: utils.formatByJson('./detail_reaccountExcept.html?type=edit&payInstiCode={payInstiCode}&channelName={channelName}&chargeFee={chargeFee}&transId={transId}&payStatus={payStatus}&orderType={orderType}&reconStatus={reconStatus}&reclnDate={reclnDate}&errorInfo={errorInfo}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		//异常处理
		$('.manual-btn').on('click', function(){
			var reclnDate = $('#reclnDate').val();
			if(reclnDate == null || reclnDate == ''){
				alert("请选择对账日期");
				return;
			};
			
			$.confirm({
				title : '提示',
				yesText : '发起手工异常处理',
				msg : '是否发起手工异常处理<br>确认要发起发起手工异常处理吗？',
				yesClick : function($modal) {
					ajax.post({
					  url: 'reaccountManager/manualReaccountExcept', 
					  data: { 
						  "reclnDate": reclnDate ,
					  }
					 
					}).done(function(res, rtn, state, msg) {
						if (state) {
							layer_msg.msg("手工异常处理成功！", 3000);
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