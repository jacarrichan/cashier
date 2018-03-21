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
				userId : param['userId'],
				userName : param['userName'],
				trueName : param['trueName'],
				//password : param['password'],
				userType : param['userType'],
				brchId : param['brchId'],
				brchName : param['brchName'],
				sex : param['sex'],
				birthday : param['birthday'],
				email : param['email'],
				telphone : param['telphone'],
				landlinePhone : param['landlinePhone'],
				address : param['address']
			};
			
			page.setFormData();
			$('#userName').data('oldValue', this.entity.userName);
			$('#userName').attr('readonly',true);
			//$('#password').attr('readonly',true);
			$('#pwdDiv').remove();
			
		}
		
		$.useModule(['chosen','datetimepicker','popover'], function(){
			page.initUI();
			page.loadSexType();
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
		    format: "yyyy-mm-dd",
			endDate: new Date()
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
	
	/**
	 * 加载用户类型数据
	 */
	/*PageScript.prototype.loadUserType = function(){
		$('#userType').append('<option>全部</option>').append('<option value="0">管理员</option>').append('<option value="1">普通用户</option>')
	};*/
	
	/**
	 * 加载用户类型数据
	 */
	PageScript.prototype.loadSexType = function(){
		$('#sex').append('<option value="">==请选择==</option>')
		.append('<option value="1">男</option>')
		.append('<option value="2">女</option>')
		.val(this.entity ? this.entity.sex : "")
	};
	
	
	PageScript.prototype.bindEvent = function(){
		
		$('.btn-submit').on('click', function(){
			var result = zuiValid('#form').validate();
			if(result){
				var $btn = $(this),
					$form = $('form'),
					data = $form.serializeJson();
				
				var uri = 'm101/f10102';
				var uriCsr = 'user/add';
				if(page.pageType == 'edit'){
					uri = 'm101/f10103';
					uriCsr = 'user/edit';
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
						var userId = {
								userId:rtn.data.userId
						}
						data = $.extend(data, userId);
						ajax.post({
							url: uriCsr,
							data: data,
							checkSession: false,
						}).done(function(res, rtn, state, msg){
							if(state){
								
							} else {
								alert(msg);
							}
						}).fail(function(e, m){
							log.error(e);
							$('.alert').addClass('alert-warning').text(m);
						}).always(function(){
							$btn.prop('disabled', false);
							zuiLoad.hide();
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