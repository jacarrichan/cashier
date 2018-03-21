/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'), 
	zuiValid = require('../../../lib/zuiplugin/zui.validate');
	
	function PageScript(){
		this.pageType = 'add';
		this.pageTypeX = 'add';
		this.entity = null;
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
		
		this.stateModel = {
				"01":"PC",
				"02":"WAP",
				"03":"APP",
		};
		
		
		this.payTypeModel = {
				"B2C":"个人网银",
				"B2B":"企业网银",
				"OTH":"其他类型",
		};		
	
	}
	
	PageScript.prototype.init = function(){
		var param = utils.getUrlParam();
		page.pageType = param['type'] || 'add';
			page.pageTypeX = 'edit';
			this.entity = {
			     rowId : param['rowId'],
			     payerInstiNm : param['payerInstiNm'],
			     instiPayTime :utils.formatDate(param['instiPayTime'],"yyyy-MM-dd hh:mm:ss"),  
			     transId : param['transId'],
			     orderNo : param['orderNo'],
			     transAmt : (function(){
				    var argHtml = '';
				    if(param['orderType'] =='0'){ //支付退款订单
				    	argHtml = param['transAmt']	
				    }
				    return argHtml;
			     })(),
				 refundAmt :(function(){
				    var argHtml = '';
				    if(param['orderType'] =='1'){  //退款订单
				    	argHtml = param['refundAmt'];	
				    }
				    return argHtml;
			     })(),	
				reTransAmt : (function(){
				    var argHtml = '';
				    if(param['orderType'] =='1'){  //退款订单
				    	argHtml = param['transAmt'];	
				    }
				    return argHtml;
			     })(),
			     instiProcCd : param['instiProcCd'],
			     instiPayType :page.stateModel[param["instiPayType"]],  
			     checkState : page.reconStatusModel[param['checkState']],
			};
			
			
			page.setFormData();
		zuiValid('#form').validate('init');
	  	page.bindEvent();
	};
	
	PageScript.prototype.setFormData = function(){
		utils.fillFormData('form', this.entity, '#');
	};
	
	var closeDialog = function(){
		parent.$depositDialog.close();
		delete parent.$depositDialog;
	};	
   
	PageScript.prototype.bindEvent = function(){
		$('.btn-cancel').on('click', function(){
			closeDialog();
			parent.$loadData();
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});