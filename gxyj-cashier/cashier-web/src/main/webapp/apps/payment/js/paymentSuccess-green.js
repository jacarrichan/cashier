/**
 * 主框架页面脚本
 */
define(function(require, exports, module){
	
	var comm = require('../../../common/js/page-common');
	pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
	}
	
	PageScript.prototype.init = function(){		
		page.bindEvent();
		var orderData = {
				userId: "13232555555",
				orderNum: "0200020002020",
				orderGoods: "【千品计划包邮】正宗新疆哈木瓜",
				totalPay: "44.21",
				/*orderSpec: "17斤，2个"	*/
		};
		laytpl('orderInfo').render(orderData, function(html){
			document.getElementById('orderSection').innerHTML = html;
		});
		laytpl('userInfo').render(orderData, function(html){
			$("#userId").append(html);
		});
	}
	
	PageScript.prototype.bindEvent = function(){
		$("#payment-header").find("li").each(function() {
	        $(this).click(function(){
	             $(this).addClass("active");
	             $(this).siblings().removeClass("active");
	        })
	    });
	}
	
	PageScript.prototype.loadData = function() {

//		var zuiLoad = new $.ZuiLoader().show('数据加载中...');

	};
	
	PageScript.prototype.renderResult=function(data) {
		//alert(data);
	};
	
	var page = new PageScript();
	page.init();
	
	return page;
});