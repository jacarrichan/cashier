/**
 * 主框架页面脚本
 */
define(function(require, exports, module){
	
	var comm = require('../../../common/js/page-common');
	pager = require('../../../lib/zuiplugin/zui.pager');
	var payComm = require('./pay-common');
	
	function PageScript(){
		this.channelInfo = {};
		this.jsonData = {};
		this.jsonVal = {};
		this.urlValue;
	}
	
	PageScript.prototype.init = function(){		
		
		var param = utils.getUrlParam();
		var jsonValue = param['data'];
		var orderBean = utils.parseJSON(jsonValue);
		
		if (orderBean.isPreview == "true") {
			ajax.get({
				url : "ajax/perviewTemp",
				data: {
					channelCd: orderBean.channelCd,
					terminal: orderBean.terminal,
					mallId: orderBean.mallId
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				var data = utils.parseJSON(res.message);
				laytpl('bank-list').render(data, function(html){
					document.getElementById('bankTab').innerHTML = html;
				});
				page.jsonData = utils.parseJSON(rtn.data);
				if($("#payTab").length > 0){
					laytpl('payTab').render(data, function(html){
						$("#payTabList").append(html);
					});
				}
				$("#buyNow").hide();
				page.bindEvent();
				payComm.initData(page.jsonData);
			}).fail(function() {	
				console.log("fail");
				//alert("数据加载失败，请重试！");
			}).always(function() {					
				//console.log("always");
			});
		}
		else {
			//支付渠道数据
			ajax.get({
				url : "ajax/orderUpload",
				data: {
					orderId: orderBean.orderId,
					channelCd: orderBean.channelCd
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				var data = utils.parseJSON(res.message);
				laytpl('bank-list').render(data, function(html){
					document.getElementById('bankTab').innerHTML = html;
				});
				page.jsonData = utils.parseJSON(rtn.data);
				laytpl('orderNum').render(page.jsonData, function(html){
					document.getElementById('orderNumber').innerHTML = html;
				});
				laytpl('orderPay').render(page.jsonData, function(html){
					$("#totalPay").append(html);
					$("#payAmount").append(html);
				});
				if(page.jsonData.buyerPhone != undefined && page.jsonData.buyerPhone != ""){
					$("#userId").html("<span>" + page.jsonData.buyerPhone + "</span>");
				}
				laytpl('transId').render(page.jsonData, function(html){
					$("#transNum").append(html);
				});
				if($("#payTab").length > 0){
					laytpl('payTab').render(data, function(html){
						$("#payTabList").append(html);
					});
				}
				page.jsonVal = {
					'orderId': page.jsonData.orderId,
					'transId': page.jsonData.transId,
					'orderPayAmt': page.jsonData.orderPayAmt,
					'prodName':orderBean.prodName,
					'channelCode':'',
					'channelCd':orderBean.channelCd,
					'clientIp': '127.0.0.1'
				};
				page.urlValue = utils.toJSON(page.jsonVal);
				page.bindEvent();
				payComm.initData(page.jsonData);
			}).fail(function() {			
				alert("数据加载失败，请重试！");
			}).always(function() {					
				//console.log("always");
			});
		}
	}
	
	PageScript.prototype.bindEvent = function(){
		var param = utils.getUrlParam();
		var jsonValue = param['data'];
		var orderBean = utils.parseJSON(jsonValue);
		
		/* 导航切换事件 */
		$("#payment-header").find("li").each(function() {
	        $(this).click(function(){
	             $(this).addClass("active");
	             $(this).siblings().removeClass("active");
	        })
	    });
		
		/* 选取支付方式 */  
		$(document).on("click", ".bank-item", function(){
        	var payMent = $("#bank").find(".bank-item");
        	payMent.each(function(){
                $(this).find(".bank-select").hide();
                $(this).removeClass("active");
            });
        	$(this).find(".bank-select").show();
            $(this).addClass("active");
            var payChannel = $(this).find(".bank-img").attr("data-channel");
            var payCode = $(this).find(".bank-img").attr("data-code");
            var ajaxUrl = $(this).find(".bank-img").attr("data-ajax");
            var nowBank = $(this).find(".bank-img").attr("data-bank");
            page.channelInfo.payChannel = payChannel;
            page.channelInfo.payCode = payCode;
            page.channelInfo.nowBank = nowBank;
            page.channelInfo.ajaxUrl = ajaxUrl;
            page.jsonVal.channelCode = payCode;
            page.urlValue = utils.toJSON(page.jsonVal);
        })
		
		// 立即支付
        $("#buyNow").click(function () {
        	payComm.payPromptly(page.channelInfo,orderBean,page.jsonVal,page.urlValue);
        	
        });
	}
	
	PageScript.prototype.loadData = function() {

//		var zuiLoad = new $.ZuiLoader().show('数据加载中...');

	};
	
	var page = new PageScript();
	page.init();
	
	return page;
});