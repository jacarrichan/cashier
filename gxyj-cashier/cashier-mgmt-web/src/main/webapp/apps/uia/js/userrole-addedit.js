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
				userId : param['userId'],
				roleName : param['roleName'],
				userName : param['userName']
			};
			$('.btn-userchoice').addClass('hidden');
			$('#roleName').data('oldValue', this.entity.roleName);
			$('.btn-rolechoice').removeClass("hidden");
			page.setFormData();
		}
		var $zv = zuiValid('#form');
		
		$zv.addMethod('zuhe', function(value, element, param){
			// 验证
			var id = element.id;
			var roleId ;
			var userId ;
			if(id == 'roleName'){
				userId = $('#userId').val();
				if(value != $(element).data('oldValue')){
					roleId = $('#roleId').val();
				}
			}
			var result = false;
			if(roleId && userId){
				ajax.syncAjax({
					url: 'm111/f11105',
					data: {roleId:roleId , userId:userId},
					tokenUrl: true,
					success: function(res){
						if(res){
							if(res.rtn.data){
								result =  false;
							}else{
								result =  true;
							}
						}else{
							result =  false;
						}
					},error: function(){
						result =  false;
					}
				});
				
			} else {
				result =  true;
			}
			
			return result;
		});
		
		$zv.validate('init',"bottom");
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
		
		$('.btn-submit').on('click', function(){
			var result = zuiValid('#form').validate();
			if(result){
				var $btn = $(this),
					$form = $('form'),
					data = $form.serializeJson();
				
				var uri = 'm111/f11102';
				if(page.pageType == 'edit'){
					uri = 'm111/f11103';
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
		
		$('.btn-userchoice').on('mousedown', function(){
			var $frame = $('#userFrame'),
				inited = $frame.data('inited');
			if(inited != 'ok'){
				$frame.data('inited', 'ok');
				$frame.attr('src', './choice_user_list.html' + right.getRightParam());
			}
		});
		
		$('.btn-rolechoice').on('mousedown', function(){
			var $frame = $('#roleFrame'),
				inited = $frame.data('inited');
			/*if(inited != 'ok'){
				var userId = $('#userId').val();
				if(userId){
					$frame.data('inited', 'ok');
					$frame.attr('src', './choice_role_list.html?userId='+ userId + right.getRightParam("&"));
				}else{
					$('.alert').addClass('alert-warning').text("请选择需要分配的用户！");
				}
			}*/
			var userId = $('#userId').val();
			$frame.data('inited', 'ok');
			$frame.attr('src', './choice_role_list.html?userId='+ userId + right.getRightParam("&"));
			
		});
		
	};
	
	var page = new PageScript();
	page.init();
	return page;
});