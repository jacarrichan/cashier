/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'), 
	zuiValid = require('../../../lib/zuiplugin/zui.validate');
	
	function PageScript(){
		this.entity = null;
		
	}
	
	PageScript.prototype.loadUserInfo = function(){
		ajax.getUserInfo().done(function(user){
			if(user && user.userId){
				$('#userId').val(user.userId);
				$('.user-headimg').attr('src', consts.IMAGE_SERVER_URL + user.userId +'.jpg' );
			} else {
				//top.location.href = "./login.html";
			}
		});
		
		/*ajax.post({
			//url: consts.WEB_BASE + 'mgmt/loadUserMenu',
			url: 'm101/f10113',
			data: {uName:'admin'},
			uia:true,
			checkSession: false,
		}).done(function(data, status, xhr){
			if(data.success){
				$('#userId').val(status.data.userId);
				$('.user-headimg').attr('src', consts.IMAGE_SERVER_URL + status.data.userId +'.jpg' );
			} else {
				top.location.href = "./login.html";
			}
		}).fail(function(e){
			alert(e.message);
		});*/
	};
	
	PageScript.prototype.init = function(){
		page.loadUserInfo();
		var uri = 'm101/f10107';
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: uri,
			data: {},
			uia:true,
			checkSession:false
		}).done(function(res, rtn, state, msg){
			if(state){
				page.entity = rtn.data;
				var img = page.entity.headImg ;
				if(img){
					$("#headImgDis").val(img.substring(img.lastIndexOf("/")+16)) ;
				}
				page.setFormData();
			} else {
				$('.alert').addClass('alert-warning').text(msg);
			}
		}).fail(function(e, m){
			log.error(e);
			$('.alert').addClass('alert-warning').text(m);
		}).always(function(){
			zuiLoad.hide();
		});
		
		
		
		$.useModule(['chosen','datetimepicker','popover'], function(){
			page.initUI();
			page.loadSexType();
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
		parent.$dataDialog.close();
		delete parent.$dataDialog;
	};	
	
	
	/**
	 * 加载性别
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
				
				var uri = 'm101/f10108';
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
		
		$('#btn-changeImg').on('click', function(){
			$('#uploadDiv').removeClass('hidden');
		});
	};
	
	window.setHeadVal = function (valDis , val){
		ajax.getUserInfo().done(function(user){
			if(user && user.userId){
				$('.headimg-preview').removeClass('hidden');
				var $img = utils.format('<img class="user-headimg" src="{0}"/>', consts.IMAGE_SERVER_URL + user.userId +'.jpg?r=' + Math.random());
				$('.user-headimg-box').html($img);
				var $img2 = $('<img>').attr('src', consts.IMAGE_SERVER_URL + user.userId +'.jpg?r=' + Math.random() );
				$('#user-head-avatar', parent.document).html($img2);
				$('#headImgDis').val(valDis);
				$('#headImg').val(val);
			} else {
				top.location.href = "./login.html";
			}
			/*var user = {}; 
			user.userId = "1000";
			$('.headimg-preview').removeClass('hidden');
			var $img = utils.format('<img class="user-headimg" src="{0}"/>', consts.IMAGE_SERVER_URL + user.userId +'.jpg?r=' + Math.random());
			$('.user-headimg-box').html($img);
			var $img2 = $('<img>').attr('src', consts.IMAGE_SERVER_URL + user.userId +'.jpg?r=' + Math.random() );
			$('#user-head-avatar', parent.document).html($img2);
			$('#headImgDis').val(valDis);
			$('#headImg').val(val);*/
		});
		
	}
	
	
	var page = new PageScript();
	page.init();
	return page;
});