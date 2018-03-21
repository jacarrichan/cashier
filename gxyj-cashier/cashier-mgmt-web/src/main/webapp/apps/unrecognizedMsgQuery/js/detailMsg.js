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
	}
	
	PageScript.prototype.init = function(){
		var param = utils.getUrlParam();
		page.pageType = param['type'] || 'add';
		if(page.pageType == 'edit'){
			page.pageTypeX = 'edit';
			this.entity = {
			     rowId : param['rowId'],
			};
			
			page.setFormData();
			var rowId = param['rowId'];
			//页面赋值
			ajax.post({
				url: 'order/queryPayMsgData',
				data: {"rowId": rowId}
			}).done(function(data){
				$("#msgData").val(data.rtn.data.msgData);
			});
		
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