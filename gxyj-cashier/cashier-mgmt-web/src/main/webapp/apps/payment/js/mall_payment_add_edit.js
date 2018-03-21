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
		page.user = {};
		ajax.getUserInfo().done(function(user){
			page.user = user;
		});
		page.entity = {};
		page.entity.channelCode = '';
		page.entity.busiChannelCode = '';
		page.entity.mallId = page.user.mallId;
		page.entity.allName = '--请选择--';
		if(page.pageType == 'edit'){
			page.pageTypeX = 'edit';
			
			this.entity = {
				rowId : param['rowId'],
				channelCode : param['channelCode'],
				mallId : param['mallId'],
				busiChannelCode : param['busiChannelCode'],
				busiChannelName : param['busiChannelName'],
				merchantId: param['merchantId'],
				appId : param['appId'],
				privateKey : param['privateKey'],
				publicKey : param['publicKey'],
			};
			
			page.entity.allName = '全部';
			$('#merchantId').data('oldValue', this.entity.merchantId);
			$('#appId').data('oldValue', this.entity.appId);
			$('#privateKey').data('oldValue', this.entity.privateKey);
		
			
		}
		
		$.useModule([ 'chosen'], function(){
			
			$('select').chosen({disable_search_threshold: 10});
			
			var hasAll = false;
			if(page.user.mallId == '00000000') {
				hasAll = true;
			}
			
			comm.initPaymentSelect({ 
				selector: '#channelCode', 
				success: function(){ 
						
				}, change: function(e, bid){ 
					
				},
				selectValue:page.entity.channelCode,
				hasAll:true,
				allName:page.entity.allName
			});
			
			comm.initMallInfoSelect({ 
				selector: '#mallId', 
				success: function(){ 
					
				}, change: function(e, bid){ 
				},
				selectValue:page.entity.mallId,
				hasAll:hasAll,
				allName:page.entity.allName
			});
			
			
			comm.initBusiChannelSelect({ 
				selector: '#busiChannelCode', 
				success: function(){ 
					
				}, change: function(e, bid){ 
				},
				selectValue:page.entity.busiChannelCode,
				hasAll:true,
				allName:page.entity.allName
			});
		});
		page.setFormData();
		if(page.pageType == 'edit'){
			
			
			$("#channelCode").attr("disabled","disabled");
			$("#mallId").attr("disabled","disabled");
			$("#busiChannelCode").attr("disabled","disabled");
		}
		zuiValid('#form').validate('init',"right");
		
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
			/*if($('#channelCode').val() == null || $('#channelCode').val() == '') {
				alert('支付渠道不允许为空');
				return;
			}*/
			
			if(result){
				var $btn = $(this),
					$form = $('form'),
					data = $form.serializeJson();
				
				var uri = 'relt/save';
				if(page.pageType == 'add'){
					page.entity = {
							mallName: $('#mallId').find("option:selected").text()
					};
					if ($('#mallId').val() == '') {
						page.entity.mallName = '';
					}
				}
				if(page.pageType == 'edit'){
					uri = 'relt/update';
				}
				
				data = $.extend(page.entity, data);
				
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
			/*$('#channelCode').val('');
			$('#mallId').val('');
			$("#mallId").find("option[value='']").attr("selected",true);
			$("#busiChannelCode").find("option[value='']").attr("selected",true);*/
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});