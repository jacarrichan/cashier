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
		
		
		this.orderTypeModel = {
				"0":"正常订单",
				"1":"退款订单",
		};
		
		this.openFlagModel = {
				"0":"通道关闭",
				"1":"通道正常",
				"2":"通道关闭",
		};
		
		
		this.reconStatusModel = {
				"01":"待处理",
				"02":"对账完成",
				"03":"对账失败",
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
	
	}
	
	PageScript.prototype.init = function(){
		var param = utils.getUrlParam();
		page.pageType = param['type'] || 'add';
		if(page.pageType == 'edit'){
			page.pageTypeX = 'edit';
			this.entity = {
			     rowId : param['rowId'],
			     transId :  param['transId'],
			     transAmt:  param['chargeFee'],
			     payInstiCode: param['payInstiCode'],
				 channelName:param['channelName'],
				 payStatus: param['payStatus'],
			     orderType : page.orderTypeModel[param['orderType']],
			     reconStatus : page.reconStatusModel[param['reconStatus']], 
			     reclnDate : param['reclnDate'],
			     errorInfo : param['errorInfo'],
			};
			
			page.setFormData();
		}
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