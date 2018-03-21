/**
 * 主框架页面脚本
 */
define(function(require, exports, module){
	
	function PageScript(){
	}
	
	PageScript.prototype.init = function(){
		
		var url = consts.WEB_BASE + "apps/index.html";
		
		$('#mainFrame').attr('src', url).on('load', function(){
			/*$('.wait-panel').animate({'opacity': 0}, function(){
				$(this).hide();
			});*/
		});
		
		
		page.bindEvent();
	}
	
	PageScript.prototype.bindEvent = function(){
		
	}
	
	var page = new PageScript();
	page.init();
	
	return page;
});