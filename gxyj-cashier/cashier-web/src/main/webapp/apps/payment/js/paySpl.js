/**
 * 主框架页面脚本
 */
define(function(require, exports, module){
	
	var comm = require('../../../common/js/page-common');
	pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.channelInfo = {};
	}
	
	PageScript.prototype.init = function(){	
		$.useModule(['datatable']);
		page.bindEvent();
		var data = {
				"payment": [
				    {"payType": "个人网银支付","payChannel": "01","bankLists": [{"src": '../images/payment/BOB_GX.png', "bank": '北京银行',"payCode": "001"}, {"src": '../images/payment/BOCB_GX.png', "bank": '中国银行',"payCode": "002"}, {"src": '../images/payment/ICBC_GX.png', "bank": '工商银行',"payCode": "003"}, {"src": '../images/payment/CCB_GX.png', "bank": '建设银行',"payCode": "004"},{"src": '../images/payment/CZB_GX.png', "bank": '浙商银行',"payCode": "005"},{"src": '../images/payment/NBCB_GX.png', "bank": '宁波银行',"payCode": "006"}]},
				    {"payType": "企业网银支付","payChannel": "02","bankLists": [{"src": '../images/payment/ICBC_GX.png', "bank": '工商银行',"payCode": "001"}, {"src": '../images/payment/CEB_GX.png', "bank": '光大银行',"payCode": "002"}, {"src": '../images/payment/ABC_GX.png', "bank": '农业银行',"payCode": "003"}, {"src": '../images/payment/CCB_GX.png', "bank": '建设银行',"payCode": "004"}]},
				    {"payType": "其他方式支付","payChannel": "03","bankLists": [{"src": '../images/payment/WP_GX.png', "bank": '微信支付',"payCode": "001"},{"src": '../images/payment/ZFB_GX.png', "bank": '国付宝',"payCode": "002"},{"src": '../images/payment/YZF_GX.png', "bank": '翼支付',"payCode": "003"}]}
				]
			};
		/*laytpl('payTab').render(data, function(html){
			document.getElementById('payTabList').innerHTML = html;
		});*/
		laytpl('bank-list').render(data, function(html){
			document.getElementById('bankTab').innerHTML = html;
		});
		var orderData = {
				userId: "13333333333",
				orderNum: "0200020002020",
				orderGoods: "【千品计划包邮】正宗新疆哈木瓜",
				totalPay: "44.21"
		};
		laytpl('orderNum').render(orderData, function(html){
			document.getElementById('orderNumber').innerHTML = html;
		});
		laytpl('goods').render(orderData, function(html){
			document.getElementById('orderGoods').innerHTML = html;
		});
		laytpl('orderPay').render(orderData, function(html){
			$("#totalPay").append(html);
			$("#payAmount").append(html);
		});
		laytpl('userInfo').render(orderData, function(html){
			$("#userId").append(html);
		});
	}
	PageScript.prototype.bindEvent = function(){
		var channelInfo = this.channelInfo;
		/*var param = utils.getUrlParam();
		var jsonValue = param['data'];
		var orderBean = utils.parseJSON(jsonValue);*/
		$("#buyNow").attr("href","#");
		
		/*$("#btnPay").click(function(){
			page.loadData();
		});*/
		/****   顶部导航事件      ****/
		$("#payment-header").find("li").each(function() {
	        $(this).click(function(){
	             $(this).addClass("active");
	             $(this).siblings().removeClass("active");
	        })
	    });
		/* 选取支付方式   */
		var channelCode;
		$(document).on("click", ".bank-list", function(){
			$("#buyNow").attr("href","#");
        	var payMent = $("#bank").find(".bank-list");
        	payMent.each(function(){
                $(this).find(".bank-select").hide();
                $(this).removeClass("active");
            });
        	$(this).find(".bank-select").show();
            $(this).addClass("active");
            var payChannel = $(this).find(".bank-img").attr("data-channel");
            var payCode = $(this).find(".bank-img").attr("data-code");
            channelInfo.payChannel = payChannel;
            channelInfo.payCode = payCode;
            channelCode = payCode;
        })
        // 二维码1分钟倒计时
        function checkTime(i) {    
            /*if (i < 10) {    
                i = "0" + i;    
            } */
            i < 10 ? ("0" + i) : i;
            return i;    
        };
        console.log(checkTime(5));
        var newHtml = '<span class="time-out">二维码已过期，<a href="javascript:void(0);" id="refreshQrcode">刷新</a>页面重新获取二维码。</span>';
        var SysSecond = 60;
        var InterValObj;
        var thisCode;  // 记录选取的支付渠道
        function SetRemainTime() {
            if (SysSecond > 0) {
            	SysSecond = SysSecond - 1;
            	/*console.log(SysSecond);*/
            	var time = checkTime(SysSecond);
        		$("#reduceTime").html(time);
             } else {
            	 $("#qrcode").attr("src","../images/payment/timeout.png"); 
            	 $("#qrcodeInfo").html(newHtml);
            	 window.clearInterval(InterValObj);
             }
        }
        var timer = null;
		/*timer = setInterval(ajaxstatus, 5000);*/
		function ajaxstatus() {
			ajax.post({
				url : "ajax/paymentStatus",
				data: {
					orderId: orderBean.orderId
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				if(res.code == "000000"){
					clearInterval(timer);
					timer = null;
					window.location.href = consts.WEB_BASE + "order/payment/api/suceess?jsonValue={\"orderId\":"+"\""+orderBean.orderId+"\","+"\"prodName"+"\":"+"\""+orderBean.prodName+"\"}"+"&time="+new Date().getTime();
				}else {
					console.log("未付款");
				}
				console.log("success");
			}).fail(function() {			
				console.log("fail");
			}).always(function() {					
				//console.log("always");
			});
		}
        /***** 立即支付 *****/
        $("#buyNow").click(function () {
        	var selectedBank = $("#bank").find(".bank-lists").find(".active").find(".bank-img");
        	if(selectedBank.length < 1){
        		alert("请选择支付方式！");
        		return;
        	}
        	var nowBank = selectedBank.attr("data-bank");
        	var dataChannel = selectedBank.attr("data-channel");
        	if(dataChannel == "01" || dataChannel == "02" ) {// 网银支付
        		$(this).attr("href","#E-currency");
        		$("#selectedBank").html(nowBank);
        		clearInterval(timer);
				timer = null;
				//调用银行接口
				console.log(channelInfo);	
        	} else {										 // 微信支付
        		if(channelCode == "001") {
        			$(this).attr("href","#weChatModal");
        			clearInterval(timer);
    				timer = null;
        			timer = setInterval(ajaxstatus, 5000);
        			if(SysSecond == 0) {
        				payImmediately();
        			}
        			var qrcodeUrl = "/paywechat/?jsonValue={'orderId':"+"'"+orderBean.orderId+"'"+",'transId':"+"'"+orderBean.transId+"'"+",'orderPayAmt':"+"'"+orderBean.orderPayAmt+"'"+",'clientIp':'127.0.0.1'}";
            		$("#qrcode").attr("src",qrcodeUrl); 
        			window.clearInterval(InterValObj);
            		InterValObj = window.setInterval(SetRemainTime, 1000);
        		}else {
        			/*console.log(channelInfo);*/
        		}
        	}
        });
        function payImmediately () {
        	$("#qrcodeInfo").html('距离二维码过期还剩<span class="red-color" id="reduceTime">60</span><span class="red-color">秒</span>，过期后请刷新页面重新获取二维码');
        	var qrcodeUrl = "/paywechat/?jsonValue={'orderId':"+"'"+orderBean.orderId+"'"+",'orderPayAmt':"+"'"+orderBean.orderPayAmt+"'"+",'clientIp':'127.0.0.1'}";
    		$("#qrcode").attr("src",qrcodeUrl); 
			window.clearInterval(InterValObj);
			SysSecond = 60;
    		InterValObj = window.setInterval(SetRemainTime, 1000);
        }
        $(document).on("click","#refreshQrcode",function(){
        	payImmediately();
        })
        /*支付完成按钮*/
        $("#payComplete").click(function() {
        	page.loadData();
        	location.href = "http://localhost:8090/apps/payment/paymentCompleted.html";
        })	
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