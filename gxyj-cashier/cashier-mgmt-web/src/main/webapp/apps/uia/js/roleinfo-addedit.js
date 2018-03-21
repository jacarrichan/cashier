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
				roleId : param['roleId'],
				roleName : param['roleName'],
				brchId : utils.convertEmpty(param['brchId'], ''),
				brchName : utils.convertEmpty(param['brchName'], '')
			};
			
			page.setFormData();
			$('#roleName').data('oldValue', this.entity.roleName);
		}
		
		$.useModule(['chosen','datetimepicker','popover'], function(){
			page.initUI();
		});
		zuiValid('#form').validate('init');
	  	page.bindEvent();
	};
	
	PageScript.prototype.initUI = function(){
		// 仅选择日期
		$(".form-date").datetimepicker(
		{
		    language:  "zh-CN",
		    weekStart: 1,
		    todayBtn:  1,
		    autoclose: 1,
		    todayHighlight: 1,
		    startView: 2,
		    minView: 2,
		    forceParse: 0,
		    format: "yyyy-mm-dd"
		});
		
		$('[data-toggle="popover"]').popover();
	};
	
	PageScript.prototype.setFormData = function(){
		
		utils.fillFormData('form', this.entity, '#');
	};
	
	var closeDialog = function(){
		parent.$depositDialog.close();
		delete parent.$depositDialog;
	};	
	
	
	PageScript.prototype.bindEvent = function(){
		
		$('.btn-submit').on('click', function(){
			var result = zuiValid('#form').validate();
			if(result){
				var $btn = $(this),
					$form = $('form'),
					data = $form.serializeJson();
				
				var uri = 'm108/f10802';
				if(page.pageType == 'edit'){
					uri = 'm108/f10803';
					data = $.extend(page.entity, data);
				}
				$btn.prop('disabled', true);
				var zuiLoad = new $.ZuiLoader().show('数据加载中...');
				ajax.post({
					url: uri,
					data: data,
					checkSession: false,
					uia:true,
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
							parent.loadData();
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
			parent.loadData();
		});
		
		
		$('#smModal').on('click', '.btn-no', function(){
			closeDialog();
			parent.loadData();
		});
		
		$('#smModal').on('click', '.btn-yes', function(){
			//清空
			$('form')[0].reset();
			$('#smModal').modal('hide');
		});
		
		$('.btn-brchchoice').on('mousedown', function(){
			var $frame = $('#brchFrame'),
				inited = $frame.data('inited');
			if(inited != 'ok'){
				$frame.data('inited', 'ok');
				$frame.attr('src', './choice_brch_list.html' + right.getRightParam());
			}
		});
		
		$('.btn-clear').on('click', function(){
			$('#brchId').val('');
			$('#brchName').val('');
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});