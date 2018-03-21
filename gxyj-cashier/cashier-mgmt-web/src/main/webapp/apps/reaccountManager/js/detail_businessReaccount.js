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
	}
	
	PageScript.prototype.init = function(){
		var param = utils.getUrlParam();
		page.pageType = param['type'] || 'add';
		page.pageTypeX = 'edit';
		this.entity = {
		     rowId : param['rowId'],
		     channelName : param['channelName'],
		     paySumAmt : param['paySumAmt'],
		     refundSumAmt : param['refundSumAmt'],
		     payTotalCnt : param['payTotalCnt'],
		     refundTotalCnt : param['refundTotalCnt'],
		     beginChkDate : param['beginChkDate'],
		     procState : page.reconStatusModel[param['procState']],
		     tableName : param['tableName'],
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