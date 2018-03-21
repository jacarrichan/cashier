define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
	}
	
	PageScript.prototype.init = function(){
		
	  	page.bindEvent();
	};

	
	PageScript.prototype.loadData = function(){

		var zuiLoad = new $.ZuiLoader().show('操作请求处理中...');
		
		ajax.post({
			url: 'schedule/maint',
			data: {"action":$('input[name="action"]:checked').val()}
		}).done(function(res, rtn, state, msg){
			alert(msg);
			
		}).fail(function(){
			
		}).always(function(){
			zuiLoad.hide();
		});
		
	  $('.icon-btn, .tip-icon').tooltip({html: true});
		
	};
	
	win = null;
	PageScript.prototype.bindEvent = function(){
		//启用
		$('.btn-start').on('click', function(){
			
			$.confirm({
				title: '提示',
				yesText: '确认启用',
				msg: '您正在进行开启任务执行操作<br>确认要启动任务吗？',
				yesClick: function($modal){
					page.loadData();
					$modal.modal('hide');
				}
			});
			
		});
		
		
		//停用
		$('.btn-stop').on('click', function(){
			$.confirm({
				title: '提示',
				yesText: '确认停止',
				msg: '您正在进行停止任务操作<br>确认要停止计划任务吗？',
				yesClick: function($modal){
					page.loadData();
					$modal.modal('hide');
				}
			});
		});
		
	}
	
	var page = new PageScript();
	page.init();
	return page;
});