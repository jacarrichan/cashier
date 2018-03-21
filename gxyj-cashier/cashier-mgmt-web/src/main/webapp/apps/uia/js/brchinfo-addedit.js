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
				brchId : param['brchId'],
				mallId : param['mallId'],
				brchName : param['brchName'],
				parentBrchId : utils.convertEmpty(param['parentBrchId']),
				parentBrchName : utils.convertEmpty(param['parentBrchName'])
			};
			
			page.setFormData();
			$('#brchName').data('oldValue', this.entity.brchName);
		}
		
		$.useModule(['chosen','datetimepicker','popover'], function(){
			$('select').chosen({disable_search_threshold: 10});
			comm.initMallInfoSelect({ 
				selector: '#mallId', 
				success: function(){ 
					
				}, change: function(e, bid){ 
					
				} ,
				hasAll:true,
				selectValue:page.entity?page.entity.mallId:'',
				allName:''
			});
			if(page.pageType == 'edit'){
				
				$("#mallId").attr("disabled","disabled");
			}
			page.initUI();
		});
		zuiValid('#form').validate('init',"bottom");
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
				
				var uri = 'm105/f10502';
				if(page.pageType == 'edit'){
					uri = 'm105/f10503';
					data = $.extend(page.entity, data);
				}
				$btn.prop('disabled', true);
				var zuiLoad = new $.ZuiLoader().show('数据加载中...');
				data = $.extend(data, { type:page.pageType});
				ajax.post({
					url: 'relt/isExist',
					data: data,
					checkSession: false,
				}).done(function(res, rtn, state, msg){
					if(state){
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
								var brchId = {
										brchId:rtn.data.brchId
								}
								data = $.extend(data, brchId);
								ajax.post({
									url: 'relt/mallEdit',
									data: data,
									checkSession: false,
								}).done(function(res, rtn, state, msg){
									if(state){
									
									} else {
										
									}
								}).fail(function(e, m){
									log.error(e);
								}).always(function(){
									$btn.prop('disabled', false);
								});
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
					} else {
						alert(msg);
					}
				}).fail(function(e, m){
					log.error(e);
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
			$('#parentBrchId').val('');
			$('#parentBrchName').val('');
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});