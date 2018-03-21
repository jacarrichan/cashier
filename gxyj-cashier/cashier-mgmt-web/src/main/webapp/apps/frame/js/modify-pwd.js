/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'), 
	zuiValid = require('../../../lib/zuiplugin/zui.validate');
	
	function PageScript(){
		this.entity = null;
		
	}
	
	PageScript.prototype.init = function(){
		
		ajax.getUserInfo().done(function(user){
			if(user){
				$('#userId').val(user.userId);
			} else {
				document.location.href.replace("./login.html");
			}
		});
		zuiValid('#form').validate('init');
		
	  	page.bindEvent();
	};
	
	PageScript.prototype.initUI = function(){
		
		$('[data-toggle="popover"]').popover();
	};
	
	var closeDialog = function(){
		parent.$pwdDialog.close();
		delete parent.$pwdDialog;
	};	
	
	
	PageScript.prototype.bindEvent = function(){
		
		$('.btn-submit').on('click', function(){
			var result = zuiValid('#form').validate();
			if(result){
				var $btn = $(this),
					$form = $('form'),
					data = $form.serializeJson();
				
				var uri = 'm101/f10109';
				var zuiLoad = new $.ZuiLoader().show('数据加载中...');
				ajax.post({
					url: uri,
					data: data,
					uia:true,
					checkSession:false
				}).done(function(res, rtn, state, msg){
					if(state){
						parent.$Messager('修改成功');
						closeDialog();
					} else {
						$('.alert').addClass('alert-warning').text(msg);
					}
				}).fail(function(e, m){
					log.error(e);
					$('.alert').addClass('alert-warning').text(m);
				}).always(function(){
					zuiLoad.hide();
				});
			}
		});
		
		$('.btn-cancel').on('click', function(){
			closeDialog();
		});
		
		
		$('#smModal').on('click', '.btn-no', function(){
			closeDialog();
		});
		
	};
	
	
	var page = new PageScript();
	page.init();
	return page;
});