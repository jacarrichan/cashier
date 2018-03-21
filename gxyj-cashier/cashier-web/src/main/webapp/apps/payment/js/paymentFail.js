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
		var param = utils.getUrlParam();
		var jsonValue = param['data'];
		
		var error = param['error'];
		var orderBean = utils.parseJSON(jsonValue);
		var orderData = {
				orderNum: orderBean.orderId,
				userId: orderBean.payPhone,
				orderGoods: orderBean.prodName,
				totalPay: orderBean.transAmt,
				transId: orderBean.transId
				/*error: errorMsg*/
				/*orderSpec: "17斤，2个"	*/
		};
		$("#errMsg").html(error);
		laytpl('orderInfo').render(orderData, function(html){
			$("#orderSection").append(html);
		});
		/*laytpl('userInfo').render(orderData, function(html){
			$("#userId").append(html);
		});*/
		if(orderBean.payPhone != undefined && orderBean.payPhone != ""){
			$("#userId").html("<span>" + orderBean.payPhone + "</span>");
		}
	}
	
	PageScript.prototype.bindEvent = function(){
		$("#payment-header").find("li").each(function() {
	        $(this).click(function(){
	             $(this).addClass("active");
	             $(this).siblings().removeClass("active");
	        })
	    });
		/*$("#paySecond").click(function(){
			window.location.href = consts.WEB_BASE + "";
		})*/
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