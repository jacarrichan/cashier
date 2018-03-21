/**
 * 主框架页面脚本
 */
define(function(require, exports, module){
	
	function PageScript(){
		this.SysSecond = 60;
		this.intervalTimer = null;
		
	}
	PageScript.prototype.init = function(){
		
	}
	
	PageScript.prototype.initData = function(jsonVal){		
		var param = utils.getUrlParam();
		var jsonValue = param['data'];
		var orderBean = utils.parseJSON(jsonValue);
		if (orderBean.isPreview != "true") {
			 /***** 立即支付 *****/
	        var timer = null;
			timer = setInterval(ajaxStatus, 5000);
			/* 支付状态查询 */
			function ajaxStatus() {
				ajax.post({
					url : "ajax/paymentStatus",
					data: {
						orderId: orderBean.orderId,
						channelCd:orderBean.channelCd,
						transId:jsonVal.transId
					},
					checkSession: false
				}).done(function(res, rtn, state, msg) {
					if(res.code == "000000"){
						clearInterval(timer);
						timer = null;
						window.location.href = consts.WEB_BASE + "order/payment/api/success?jsonValue={\"transId\":"+"\""+jsonVal.transId+"\","+"\"prodName"+"\":"+"\""+orderBean.prodName+"\"}"+"&time="+new Date().getTime();
					}else {
						//console.log("未付款");
					}
				}).fail(function() {			
					console.log("fail");
				}).always(function() {					
					//console.log("always");
				});
			}
			
			/* 页面超时 */
			var lastTime = new Date().getTime();
	        var currentTime = new Date().getTime();
	        var timeOut = 5 * 60 * 1000; //设置超时时间： 5分

	        $(function(){
	            /* 鼠标移动事件 */
	            $(document).mouseover(function(){
	                lastTime = new Date().getTime(); //更新操作时间

	            });
	        });

	        function testTime(){
	            currentTime = new Date().getTime(); //更新当前时间
	            if(currentTime - lastTime > timeOut){ //判断是否超时
	            	clearInterval(timerExpired);
	                clearInterval(timer);
	                $("#E-currency,#weChatModal").hide();
		        	$("#modalMark").show();
		        	$("#reloadPage").show();
	            }
	        }

	        /* 定时器  间隔1秒检测是否长时间未操作页面  */
	        var timerExpired = null;
	        timerExpired = window.setInterval(testTime, 1000);
	        
	        $("#refreshPage").click(function(){
		    	window.location.reload();
		    })
		    
		    // 支付遇到问题按钮 支付完成按钮   关闭弹框按钮
	        $("#payComplete").click(function() {
	        	ajax.post({
					url : "ajax/paymentStatus",
					data: {
						orderId: orderBean.orderId,
						channelCd:orderBean.channelCd,
						transId:jsonVal.transId
					},
					checkSession: false
				}).done(function(res, rtn, state, msg) {
					if(res.code == "000000"){
						clearInterval(timer);
						timer = null;
						window.location.href = consts.WEB_BASE + "order/payment/api/success?jsonValue={\"transId\":"+"\""+jsonVal.transId+"\","+"\"prodName"+"\":"+"\""+orderBean.prodName+"\"}"+"&time="+new Date().getTime();
					}else {
						$("#E-currency,#weChatModal").hide();
						$("#modalMark").hide();
					}
				}).fail(function() {			
					alert("服务器异常，请重试！");
				}).always(function() {					
					//console.log("always");
				});
	        })
	        
	         // 支付遇到问题按钮 关闭弹框按钮
	        $("#isPayment, .close").click(function() {
	        	ajax.post({
					url : "ajax/paymentStatus",
					data: {
						orderId: orderBean.orderId,
						channelCd:orderBean.channelCd,
						transId:jsonVal.transId
					},
					checkSession: false
				}).done(function(res, rtn, state, msg) {
					if(res.code == "000000"){
						clearInterval(timer);
						timer = null;
						window.location.href = consts.WEB_BASE + "order/payment/api/success?jsonValue={\"transId\":"+"\""+jsonVal.transId+"\","+"\"prodName"+"\":"+"\""+orderBean.prodName+"\"}"+"&time="+new Date().getTime();
					}else {
						ajax.post({
							url : "ajax/payProblem",
							data: {
								transId:jsonVal.transId
							},
							checkSession: false
						}).done(function(res, rtn, state, msg) {
							if(res.code == "000000"){
								$("#E-currency,#weChatModal").hide();
								$("#modalMark").hide();
							}
						}).fail(function() {			
							alert("服务器异常，请重试！");
						}).always(function() {					
							//console.log("always");
						});
					}
				}).fail(function() {			
					alert("服务器异常，请重试！");
				}).always(function() {					
					//console.log("always");
				});
	        	
	        })
		}
		
		
	}
	
	PageScript.prototype.payPromptly = function(channelInfo, orderBean, jsonVal, urlValue){
		function checkTime(i) {    
            i = (i < 10) ? ("0" + i) : i;
            return i;    
        };
        var newHtml = '<span class="time-out">二维码已过期，<a href="javascript:void(0);" id="refreshQrcode">刷新</a>页面重新获取二维码。</span>';
        /*	二维码1分钟倒计时 */
        function SetRemainTime() {
            if (page.SysSecond > 0) {
            	page.SysSecond = page.SysSecond - 1;
            	var time = checkTime(page.SysSecond);
        		$("#reduceTime").html(time);
             } else {
            	 $("#qrcode").attr("src","../images/payment/timeout.png"); 
            	 $("#qrcodeInfo").html(newHtml);
            	 window.clearInterval(page.intervalTimer);
             }
        }
        
        /* 网银支付公用方法  */
		function cyberAjax(url) {
			ajax.syncAjax({
 				url : url,
 				data: jsonVal,
 				checkSession: false
 			}).done(function(res, rtn, state, msg) {
 				window.open(res.rtn.data);
 			}).fail(function() {	
 				
 			}).always(function() {					
 				//console.log("always");
 			});
		}
        
    	if(channelInfo.payCode == null || channelInfo.payCode == undefined){
    		alert("请选择支付方式！");
    		return;
    	}
    	ajax.syncAjax({
			url : "ajax/payment/check",
			data: {
				"orderId": jsonVal.orderId,
				"orderPayAmt": jsonVal.orderPayAmt,
				"transId" : jsonVal.transId,
				"channelCd" : jsonVal.channelCd,
				"payerInstiNo": channelInfo.payCode
			},
			checkSession: false
		}).done(function(res, rtn, state, msg) {
			if(res.code == "000000"){
	        	if(channelInfo.payChannel == "01" || channelInfo.payChannel == "02" ) {// 网银支付
	        		$("#selectedBank").html(channelInfo.nowBank);
	        		$("#E-currency").show();
    				$("#modalMark").show();
					//调用银行接口
    				if (channelInfo.payCode == "007" || channelInfo.payCode == "0071") { // 光大银行本行支付
	        			window.open(consts.WEB_BASE + "cbebank/payOrder?jsonValue=" + urlValue);
             		} 
    				else if (channelInfo.payCode.indexOf("007-") > -1 || channelInfo.payCode.indexOf("0071-") > -1) { // 光大银行跨行支付
    					window.open(consts.WEB_BASE + "cbebank/interbankPay?jsonValue=" + urlValue);
    					
	        		}else if (channelInfo.payCode == "008" || channelInfo.payCode == "0081") {
	        			//农信银
	        			window.open(consts.WEB_BASE + "gxyj/rcbpay/geteway?jsonValue=" + urlValue);
	        		}
             		else {
             			//调用银行接口
    					cyberAjax(channelInfo.ajaxUrl);
    				}
    				
	        	} else {	// 微信支付
	        		if(channelInfo.payCode == "004") {
	        			$("#weChatModal").show();
        				$("#modalMark").show();
	        			if(page.SysSecond == 0) {
	        				resetQrcode();
	        			}
	        			var qrcodeUrl = "/paywechat/?jsonValue=" + urlValue;
	            		$("#qrcode").attr("src",qrcodeUrl); 
	        			window.clearInterval(page.intervalTimer);
	        			page.intervalTimer = null;
	        			page.intervalTimer = window.setInterval(SetRemainTime, 1000);
	        		} else if (channelInfo.payCode == "002") {
	        			// 翼支付
	        			$("#selectedBank").html(channelInfo.nowBank);
	        			ajax.syncAjax({
	        				url : "ebest/pay",
	        				data: jsonVal,
	        				checkSession: false
	        			}).done(function(res, rtn, state, msg) {
	        				$("#E-currency").show();
	        				$("#modalMark").show();
	        				window.open('https://webpay.bestpay.com.cn/index.html?'+res.rtn.data+'#login');
	        			}).fail(function() {	
	        				
	        			}).always(function() {					
	        				//console.log("always");
	        			});
	        		} else if (channelInfo.payCode == "005") {
	        			//支付宝
	        			$("#selectedBank").html(channelInfo.nowBank);
	        			$("#E-currency").show();
        				$("#modalMark").show();
	        			window.open(consts.WEB_BASE +"gxyj/alipay/geteway?jsonValue=" + urlValue);
	        		} else if (channelInfo.payCode == "003") {
	        			//国付宝
	        			$("#selectedBank").html(channelInfo.nowBank);
	        			$("#E-currency").show();
	        			$("#modalMark").show();
	        			window.open(consts.WEB_BASE + "gopay/?jsonValue=" + urlValue);
	        		}
	        	}
			} else {
				layer_msg.msg(res.message,2000);
			}
		}).fail(function() {			
			//console.log("fail");
		}).always(function() {					
			//console.log("always");
		});
    	
    	/* 二维码过期刷新 */
        function resetQrcode () {
        	var timeOutText = '距离二维码过期还剩<span class="red-color" id="reduceTime">60</span><span class="red-color">秒</span>，过期后请刷新页面重新获取二维码';
        	$("#qrcodeInfo").html(timeOutText);
        	var qrcodeUrl = "/paywechat/?jsonValue=" + page.urlValue;
    		$("#qrcode").attr("src",qrcodeUrl); 
			window.clearInterval(page.intervalTimer);
			page.SysSecond = 60;
			page.intervalTimer = window.setInterval(SetRemainTime, 1000);
        }
        $(document).on("click","#refreshQrcode",function(){
        	resetQrcode();
        })
        
        /* 返回选择其他支付方式 */
        $(".other-channel").click(function () {
        	/*$("#E-currency,#weChatModal").hide();
			$("#modalMark").hide();*/
        	
        	ajax.post({
				url : "ajax/paymentStatus",
				data: {
					orderId: orderBean.orderId,
					channelCd:orderBean.channelCd,
					transId:jsonVal.transId
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				if(res.code == "000000"){
					clearInterval(timer);
					timer = null;
					window.location.href = consts.WEB_BASE + "order/payment/api/success?jsonValue={\"transId\":"+"\""+jsonVal.transId+"\","+"\"prodName"+"\":"+"\""+orderBean.prodName+"\"}"+"&time="+new Date().getTime();
				}else {
					ajax.post({
						url : "ajax/payProblem",
						data: {
							transId:jsonVal.transId
						},
						checkSession: false
					}).done(function(res, rtn, state, msg) {
						if(res.code == "000000"){
							$("#E-currency,#weChatModal").hide();
							$("#modalMark").hide();
						}
					}).fail(function() {			
						console.log("fail");
						alert("服务器异常，请重试！");
					}).always(function() {					
						//console.log("always");
					});
				}
			}).fail(function() {			
				alert("服务器异常，请重试！");
			}).always(function() {					
				//console.log("always");
			});
        })
	}
	
	var page = new PageScript();
	page.init();
	
	return page;
});
