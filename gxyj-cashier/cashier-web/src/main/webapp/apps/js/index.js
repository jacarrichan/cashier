/**
 * 主框架页面脚本
 */
define(function(require, exports, module){
	
	function PageScript(){
	}
	
	PageScript.prototype.init = function(){
		var header = $('header').height(),
			footer = $('footer').height();
		$('.contain').css({
			height: 'calc(100% - ' + (header + footer) + 'px)'
		});
		
		$(".orderId").html("000e4343234344545454545454545");
		$(".orderCash").html("100.00元");
		
		
		
		page.bindEvent();
	}
	
	PageScript.prototype.bindEvent = function(){
		
	}
	
	var page = new PageScript();
	page.init();
	
	return page;
});