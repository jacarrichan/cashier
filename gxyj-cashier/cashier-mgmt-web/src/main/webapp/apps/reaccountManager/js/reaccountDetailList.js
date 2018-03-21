define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.firstLoad = true;
		this.orderTypeModel = {
				"01":"否",
				"02":"是",
		};
		
		this.stateModel = {
				"01":"PC",
				"02":"WAP",
				"03":"APP",
		};
		
		this.openFlagModel = {
				"0":"通道关闭",
				"1":"通道正常",
				"2":"通道关闭",
		};
		
		this.reconStatusModel = {
				"01":"对账相符",
				"02":"对账不符",
				"03":"我方少账",
				"04":"我方多账",
				"05":"笔数不符",
				"06":"金额不符",
				"07":"支付渠道有账，我方无账有订单",
				"08":"我方有账，但无订单",
		};		
		
		this.payTypeModel = {
				"B2C":"个人网银",
				"B2B":"企业网银",
				"OTH":"其他类型",
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
			comm.initPayerInstiNoSelect({ 
				selector: '#payerInstiNo', 
				success: function(){ 
				}, change: function(e, bid){ 
					page.loadData();
				} ,
				hasAll:true
			});
			page.loadData();
		});
		
	  	page.bindEvent();
	};
	
	
	PageScript.prototype.loadData = function(){
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'reaccountManager/qryReacouDetailList',
			data: {
				"checkDate":$('#checkDate').val(),
				"payerInstiCd":$('#payerInstiNo').val(),
				
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

		$("#checkDate").datetimepicker({
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
					"payerInstiNm": obj.payerInstiNm,
					"instiPayTime":  utils.formatDate(obj.instiPayTime,"yyyy-MM-dd hh:mm:ss"),
					"transId": obj.transId,	
					"orderNo": obj.orderNo,	
					"transAmt":(function(){
					    var argHtml = '';
					    if(obj.orderType =='0'){ //支付退款订单
					    	if(!obj.transAmt == null || !obj.transAmt == ''){
					    		argHtml = obj.transAmt.toFixed(2);	
					    	}
					    	
					    }
					    return argHtml;
				     })(),
					"refundAmt": (function(){
					    var argHtml = '';
					    if(obj.orderType =='1'){  //退款订单
					    	if(!obj.refundAmt == null || !obj.refundAmt == ''){
					    		argHtml = obj.refundAmt.toFixed(2);	
					    	}
					    }
					    return argHtml;
				     })(),	
					"reTransAmt": (function(){
					    var argHtml = '';
					    if(obj.orderType =='1'){  //退款订单
					    	argHtml = obj.transAmt.toFixed(2);	
					    }
					    return argHtml;
				     })(),
				     
					"instiProcCd": (function(){
						if(obj.instiProcCd == '' || obj.instiProcCd == null  ){
							return '';
						}
						else{
							return obj.instiProcCd;
						}
				})(),	
					"instiPayType":page.stateModel[obj.instiPayType],  
					"checkState":(function(){
						var label = '';
						if(obj.usingStatus == '01') {
							label = laytpl('label.tpl').render({
								value: page.reconStatusModel[obj.checkState], 
								flag: 'label-success'
							});
						}
						else {
							label = laytpl('label.tpl').render({
								value: page.reconStatusModel[obj.checkState], 
								flag: 'label-danger'
							});
						}
						return label;
					})() ,
					
					
					"buttons": (function(){
						var btnHtml = '';
							btnHtml += laytpl('list-btn.tpl').render({
								"class": "btn-detail",
								"icon": "icon-newspaper-o",
								"title": "查看订单详细信息"
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
		$("#checkDatParent").width($("#payerInstiNo").next(".chosen-container").width());
		
	};
	
	win = null;
	PageScript.prototype.bindEvent = function(){
		
		window['$Messager'] = function(text){
			alert(text);
		};
		
		$('#payerInstiCd').on('change', function(){
			page.loadData();
		});
		
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
				iframe: utils.formatByJson('./detail_reaccount.html?type=edit&rowId={rowId}&payerInstiNm={payerInstiNm}&instiPayTime={instiPayTime}&transId={transId}&orderNo={orderNo}&transAmt={transAmt}&refundAmt={refundAmt}&orderType={orderType}&instiProcCd={instiProcCd}&instiPayType={instiPayType}&checkState={checkState}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		$('.clear-btn').on('click', function(){
			$('#checkDate').val('');
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