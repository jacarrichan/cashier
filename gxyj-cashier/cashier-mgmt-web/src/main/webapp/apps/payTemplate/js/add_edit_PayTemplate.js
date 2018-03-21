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
				templateUrl : param['templateName'],
				terminal : param['terminal'],
				rowId : param['rowId'],
				openFlag : param['openFlag'],
			};
			
			page.setFormData();
			$('#terminal').val(param['terminal']);
			$('#templateUrl').val(param['templateName']);
			var openFlags = param['openFlag'];
			if(openFlags == 0){
				$('#close').click();
			}if(openFlags == 1){
				$('#open').click();
			}
			
			//不可编辑
			$('#open').attr("disabled",true);
			$('#close').attr("disabled",true);
			$('#terminal').attr("disabled",true);
			$('#templateUrl').attr("disabled",true);
			
			
		}
		
		zuiValid('#form').validate('init','right');
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

		$('#terminal').on('change',function() {
			$.useModule(['datatable', 'chosen'], function(){
				$('select').chosen({disable_search_threshold: 10});
				comm.initDictSelect({
					select: '#templateUrl', 
					dictname: 'PayTemplateUrl_'+ $('#terminal').val(),
					deftext:'--请选择--'
				});
				
			});
		});
		
		
		
		$('.btn-submit').on('click', function(){
			var result = zuiValid('#form').validate();
			if(result){
				var $btn = $(this),
					$form = $('form'),
					data = $form.serializeJson();
				
				var uri = 'payTemplate/add';
				
				var val = $("#templateUrl").val();
				var channelCodeValue = $("#templateUrl option[value='"+val+"']").attr("data-text");
				//console.log(channelCodeValue);
				var urlName = {
					templateName :channelCodeValue
				}
				data = $.extend(data, urlName);

				/*if(page.pageType == 'edit'){
					uri = 'payTemplate/update';
					data = $.extend(page.entity, data);
				}*/
			
				
				
				ajax.post({
					url: 'payTemplate/checkName',
					data: {
						  "terminal": $('#terminal').val(),
						  "templateName":channelCodeValue ,
					}
				}).done(function(res, rtn, state, msg){
					if(state){
						$btn.prop('disabled', true);
						var zuiLoad = new $.ZuiLoader().show('数据加载中...');
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
									parent.$Messager('保存成功');
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
						
				}else{
				  $('.alert').addClass('alert-warning').text(msg);
				  $btn.prop('disabled', false);
				  zuiLoad.hide();
				  return false;
				}
			})
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
			//清空
			$('form')[0].reset();
			$('#smModal').modal('hide');
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});