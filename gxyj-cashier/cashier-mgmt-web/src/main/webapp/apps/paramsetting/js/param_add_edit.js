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
			$("#paramCode").attr("disabled","disabled");
			this.entity = {
				rowId : param['rowId'],
				paramCode : param['paramCode'],
				paramName : param['paramName'],
				paramValue : param['paramValue'], 
				paramDesc : param['paramDesc'], 
				paramType : param['paramType'], 
				valFlag : param['valFlag'], 
			};
			var pType = param['paramType'];
			if (pType == "0") {
				$("#paramType_0").click();
			}
			var pFlag = param['valFlag'];
			if (pFlag == "1") {
				$("#valFlag_1").click();
			}
			
			page.setFormData();
			
			$('#paramCode').data('oldValue', this.entity.paramCode);
			$('#paramName').data('oldValue', this.entity.paramName);
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
		/* 新增或修改 点击确定按钮 触发事件 */
		$('.btn-submit').on('click', function(){
			var result = zuiValid('#form').validate();
			
			if(result){
				var $btn = $(this),
					$form = $('form'),
					data = $form.serializeJson();
				
				var uri = 'param/addEdit';
				if(page.pageType == 'edit'){
					uri = 'param/addEdit';
					data = $.extend(page.entity, data);
				}
				$btn.prop('disabled', true);
				var zuiLoad = new $.ZuiLoader().show('数据保存中......');
				ajax.post({
					url: uri,
					data: data					
				}).done(function(res, rtn, state, msg){
					if(state){
						if(page.pageType == 'add'){
							$('#smModal').modal({
							    keyboard : false,
							    show     : true
							});
						} else {
							parent.$Messager(msg);
							closeDialog();
							parent.$loadData();
						}
					} else {
						$('.alert').addClass('alert-warning').text(msg);
					}
				}).fail(function(e, m){
					log.error(e);
					$('.alert').addClass('alert-warning').text(m);
				}).always(function(){
					$btn.prop('disabled', false);
					zuiLoad.hide();
				});
			}
		});
		
		$('.btn-cancel').on('click', function(){
			closeDialog();
			parent.$loadData();
		});
		
		
		$('#smModal').on('click', '.btn-no', function(){
			closeDialog();
			parent.$loadData();
		});
		
		$('#smModal').on('click', '.btn-yes', function(){
			
			$('form')[0].reset();
			$('#smModal').modal('hide');
		});
		
		 
		
		$('.btn-clear').on('click', function(){
			$('#parentBrchId').val('');
			$('#parentBrchName').val('');
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});