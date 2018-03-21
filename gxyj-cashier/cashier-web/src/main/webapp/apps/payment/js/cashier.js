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
		this.cyData = {};
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
				var cyberData = [];
				for(var i=0; i<data.payment.length-1;i++){
					cyberData.push(data.payment[i]);
				}
				var cyberObj= {};
				cyberObj.cyData = cyberData;  // 组装个人网银和企业网银数据
				laytpl('bank-list').render(cyberObj, function(html){
					document.getElementById('cyberPay').innerHTML = html;
				});
				var thirdData = [];
				thirdData.push(data.payment[data.payment.length-1]);
				var thirdObj = {};    // 组装第三方支付数据
				thirdObj.cyData = thirdData;
				laytpl('bank-list').render(thirdObj, function(html){
					$("#thirdPay").prepend(html);
				});
				$("#payBtn").hide();
				page.jsonVal = {
					'orderId': page.jsonData.orderId,
					'transId': page.jsonData.transId,
					'orderPayAmt': page.jsonData.orderPayAmt,
					'prodName':orderBean.prodName,
					'channelCd':orderBean.channelCd,
					'clientIp': '127.0.0.1'
				};
				page.urlValue = utils.toJSON(page.jsonVal);
				page.bindEvent();
				if(navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion .split(";")[1].replace(/[ ]/g,"")=="MSIE8.0"){ 
					if (window.PIE) { 
				        $('.max-circle,.min-circle,.personal-bank,.company-bank,.pay-now').each(function() { 
				            PIE.attach(this); 
				        });
					}
				} 
				payComm.initData(page.jsonVal);
			}).fail(function() {	
				console.log("fail");
				//alert("数据加载失败，请重试！");
			}).always(function() {					
				//console.log("always");
			});
		} else {
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
				var cyberData = [];
				for(var i=0; i<data.payment.length-1;i++){
					cyberData.push(data.payment[i]);
				}
				var cyberObj= {};
				cyberObj.cyData = cyberData;
				laytpl('bank-list').render(cyberObj, function(html){
					document.getElementById('cyberPay').innerHTML = html;
				});
				var thirdData = [];
				thirdData.push(data.payment[data.payment.length-1]);
				var thirdObj = {};
				thirdObj.cyData = thirdData;
				laytpl('bank-list').render(thirdObj, function(html){
					$("#thirdPay").prepend(html);
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
				page.jsonVal = {
						'orderId': page.jsonData.orderId,
						'transId': page.jsonData.transId,
						'orderPayAmt': page.jsonData.orderPayAmt,
						'prodName':orderBean.prodName,
						'channelCd':orderBean.channelCd,
						'clientIp': '127.0.0.1'
					};
				page.urlValue = utils.toJSON(page.jsonVal);
				
				if(navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion .split(";")[1].replace(/[ ]/g,"")=="MSIE8.0"){ 
					if (window.PIE) { 
				        $('.max-circle,.min-circle,.personal-bank,.company-bank,.pay-now').each(function() { 
				            PIE.attach(this); 
				        });
					}
				} 
				page.bindEvent();
				payComm.initData(page.jsonVal);
				
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
		$(document).on("click", ".bank-li", function(event){
			var that = $(this);
			$(".min-circle").hide();
			$(this).find(".min-circle").show();
            var payChannel = that.find(".bank-img").attr("data-channel");
            var payCode = that.find(".bank-img").attr("data-code");
            var ajaxUrl = that.find(".bank-img").attr("data-ajax");
            var nowBank = that.find(".bank-img").attr("data-bank");
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