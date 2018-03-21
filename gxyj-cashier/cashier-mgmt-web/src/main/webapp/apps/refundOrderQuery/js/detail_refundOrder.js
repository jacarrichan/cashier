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
		this.terminalModel = {
				"01":"PC",
				"02":"WAP",
				"03":"APP",
				"04":"其它",
		};
		
		this.stateModel = {
				"0":"未通知",
				"1":"通知成功",
				"2":"通知失败",
		};
		
		this.procStateModel = {
				"00":"成功",
				"01":"失败",
				"02":"未支付",
				"03":"处理中",
				"04":"订单关闭",
		};
	
	}
	
	PageScript.prototype.init = function(){
		var param = utils.getUrlParam();
		page.pageType = param['type'] || 'add';
		if(page.pageType == 'edit'){
			page.pageTypeX = 'edit';
			this.entity = {
			     rowId : param['transId'],
				transId : param['transId'],
				channelCd : param['channelCd'],
				mallId : param['mallId'],
				refundId : param['refundId'],
				orgnOrderId : param['orgnOrderId'],
				refundDate : utils.formatDate(param['refundDate'],"yyyy-MM-dd hh:mm:ss"),
				refundAmt :  parseFloat(param['refundAmt']).toFixed(2), 
				payerInstiNm : param['payerInstiNm'],
				procState : page.procStateModel[param['procState']],
				remark : page.stateModel[param['remark']],
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