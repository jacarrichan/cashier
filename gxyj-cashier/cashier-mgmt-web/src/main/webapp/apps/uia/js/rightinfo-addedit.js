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
		this.codeIndex = 1;
	}
	
	PageScript.prototype.init = function(){
		var param = utils.getUrlParam();
		page.pageType = param['type'] || 'add';
		
		if(page.pageType == 'edit'){
			page.pageTypeX = 'edit';
			
			this.entity = {
				rowId : param['rowId'],
				rightId : param['rightId'],
				rightName : param['rightName'] || "",
				rightUrl : param['rightUrl'] || "",
				rightNum : param['rightNum'] || "",
				parentRightName : utils.convertEmpty(param['parentRightName'], ""),
				parentRightId : utils.convertEmpty(param['parentRightId'], "") 
			};
			var pId = this.entity.rightId;
			if(this.entity.parentRightId){
				$('#funDiv').removeClass('hidden');
				
				page.loadRightDict(pId);
			}
			page.setFormData();
			$('#rightName').data('oldValue', this.entity.rightName);
		} else {
			page.loadRightDict();
		}
		
		$.useModule(['popover'], function(){
			page.initUI();
		});
		zuiValid('#form').validate('init');
	  	page.bindEvent();
	};
	
	PageScript.prototype.loadRightDict = function(pId){
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		dict.getDict("FuncRightDict").done(function(res, rtn, state, msg){
			if(state){
				var list = rtn.data.FuncRightDict;
				if(list){
					var $box = $('.functions-box');
					for(var i = 0; i < list.length; i++){
						if(!$('[data-code="' + list[i].value + '"]').length){
							$box.append(laytpl('function.tpl').render({
								name: list[i].desc,
								code: list[i].code,
								value: list[i].value,
								checked: ''
							}));
						}
					}
				}
				
				if(page.pageType == 'edit' && pId){
					// 加载权限数据
					ajax.post({
						url: 'm112/f11207',
						data: {parentRightId: pId }	,
						checkSession: false,
						uia:true,
					}).done(function(res, rtn, state, msg){
						if(state){
							page.entity.orgRightCode = rtn.data ;
							for(var i = 0; i < rtn.data.length; i++){
								$('#right-' + rtn.data[i]).prop('checked', true);
							}
							if($('[data-more="true"]:checked').length){
								$('#cbox-more').prop('checked', true);
								$('.functions-box').removeClass('hidden');
							}
						}
					}).fail(function(e, m){
						log.error(e);
						$('.alert').addClass('alert-warning').text(m);
					}).always(function(){
						zuiLoad.hide();
					});
				} else {
					zuiLoad.hide();
				}
			} else {
				zuiLoad.hide();
				$('.alert').addClass('alert-warning').text('数据加载失败，请刷新页面重试或联系管理员');
			}
		/*if(page.pageType == 'edit' && pId){
			// 加载权限数据
			ajax.post({
				url: 'm112/f11207',
				data: {parentRightId: pId }	,
				checkSession: false,
				uia:true,
			}).done(function(res, rtn, state, msg){
				if(state){
					page.entity.orgRightCode = rtn.data ;
					for(var i = 0; i < rtn.data.length; i++){
						$('#right-' + rtn.data[i]).prop('checked', true);
					}
					if($('[data-more="true"]:checked').length){
						$('#cbox-more').prop('checked', true);
						$('.functions-box').removeClass('hidden');
					}
				}
			}).fail(function(e, m){
				log.error(e);
				$('.alert').addClass('alert-warning').text(m);
			}).always(function(){
				zuiLoad.hide();
				});
			} else {
				zuiLoad.hide();
			}*/
		});
			
	};
	
	PageScript.prototype.initUI = function(){
		
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
				
				var uri = 'm112/f11202';
				if(page.pageType == 'edit'){
					uri = 'm112/f11203';
					
					page.funRight();
					
					data = $.extend(page.entity, data, {
						addCode: page.addCode,
						delCode: page.delCode
					});
				}
				
				if(data.functions && !_.isArray(data.functions)){
					data.functions = [data.functions];
				} else if(!data.functions){
					data.functions = [];
				}
				// 顶级菜单的菜单URL设置
				if(!data.rightUrl || !data.rightUrl.trim()){
					data.rightUrl = "javascript:;";
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
		
		$('#cbox-more').on('click', function(){
			var ischeck = $(this).is(':checked');
			if(ischeck){
				$('.functions-box').removeClass('hidden');
			} else {
				$('.functions-box').addClass('hidden');
				$('.functions-box input[name="functions"]').prop('checked', false);
			}
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
		
		$('.btn-rightchoice').on('mousedown', function(){
			var $frame = $('#rightFrame'),
				inited = $frame.data('inited');
			if(inited != 'ok'){
				$frame.data('inited', 'ok');
				$frame.attr('src', './choice_right_list.html' + right.getRightParam());
			}
		});
		
		$('.btn-clear').on('click', function(){
			$('#parentRightId').val('');
			$('#parentRightName').val('');
		});
	};
	
	
	PageScript.prototype.funRight = function(){
		var orgRightCode = page.entity.orgRightCode ;
		var newRightCode = [];
		$('input[name="functions"]:checked').each(function(){
			$(this).val() && newRightCode.push($(this).val());
		});
		var addCode = [], delCode = [], id;
		if(newRightCode){
			for(var i = 0; i < newRightCode.length; i++){
				id = newRightCode[i];
				if(id && !_.include(orgRightCode, id)){
					addCode.push(id);
				}
			}
		}
		if(orgRightCode){
			for(var i = 0; i < orgRightCode.length; i++){
				id = orgRightCode[i];
				if(id && !_.include(newRightCode, id)){
					delCode.push(id);
				}
			}
		}
		page.addCode = addCode ;
		page.delCode = delCode ;
	}
	
	var page = new PageScript();
	page.init();
	return page;
});