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
		
		$("#fileform").attr("action",consts.WEB_ROOT +"m800/f80008");
	  	
		var fileName = utils.getUrlParam()["fileName"];
		var orgFileName = utils.getUrlParam()["orgFileName"];
		if(orgFileName){
			parent.setHeadVal(orgFileName,fileName);
		}
		if(fileName){
			//$("#uploadFile").val(fileName.substring(14));
			//log.info(fileName.lastindexof("/")+14).substring(14));
		}
		
		page.bindEvent();
	};
	
	
	var closeDialog = function(){
		parent.$dataDialog.close();
		delete parent.$dataDialog;
	};	
	
	PageScript.prototype.loadUserInfo = function(){
		ajax.getUserInfo().done(function(user){
			if(user && user.userId){
				$('#userId').val(user.userId);
				$('#imagurl').val(consts.IMAGE_INFO_URL  +"/m900/f90001");
			} else {
				top.location.href = "./login.html";
			}
		});
	};
	
	PageScript.prototype.bindEvent = function(){
		
		
		$('#smModal').on('click', '.btn-no', function(){
			closeDialog();
		});
		
		$('#uploadFile').on('change', function(){
			if(checkPhoto()){
				page.loadUserInfo();
				$('#fileform')[0].submit();
				parent.$('.alert').addClass('alert-warning').text("上传成功！");
			}else{
				parent.$('.alert').addClass('alert-warning').text("上传图片类型错误！");
			}
			
		});
		
	};
	

	function checkPhoto() {
		var type = "";
		var val = $('#uploadFile').val();
		if (val) {
			type = val.match(/^(.*)(\.)(.{1,8})$/)[3];
			type = type.toUpperCase();
		}else{
			return false;
		}

		if (type != "JPEG" && type != "PNG" && type != "JPG" && type != "GIF") {
			return false;
		}else{
			return true;
		}
	} 
	
	
	var page = new PageScript();
	page.init();
	return page;
});