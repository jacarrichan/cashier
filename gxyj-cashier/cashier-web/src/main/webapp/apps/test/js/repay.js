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
	}
	
	PageScript.prototype.bindEvent = function(){
		
		$("#btnPay").click(function(){
			page.loadData();
		});
		
	}
	
	PageScript.prototype.loadData = function() {

//		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		var uri ='/order/payment/api/sycn';
		//alert(uri);
		var dateStr=utils.getCurrDate('yyyyMMddhhmmss');
		if($("#orderId").val() == '' || $("#transAmt").val() == "") {
			alert("订单编号");
			return;
		}
		var msgId='BUY'+dateStr;
		var data={"terminal":$("#terminal").val(),
				 "orderId":$("#orderId").val(),
				 "transAmt":$("#transAmt").val(),
				 "transTime":$("#transTime").val(),
				 "orderType":$("#orderType").val(),
				 "channelId":$("#channelId").val(),
				 "buyerName":$("#buyerName").val(),
				 "buyerTelePhone":$("#buyerTelePhone").val(),
				 "buyerPhone":$("#buyerPhone").val(),
				 };
		var jsonValue = {
			"HEAD": {  
			"msgId": msgId + "001630328", 
			"msgCreateTime":"20170717103801",  
			"sender":"IFS", 
			"receiver":"FFS", 
			"autograph":"ewrwewer", 
			"encryptionMode":"MD5",
			"returnUrl":consts.WEB_BASE + "mq/test",
            "interfaceCode":"BUY_FSS_1001", 
            "source" : $("#channelId").val()
			},
			"BODY":[        
		        {
		            "orderId": $("#orderId").val(),
		            "terminal": $("#terminal").val(),
		            "clientIp":"127.0.0.1",
		            "orderType": $("#orderType").val(),
		            "orderPayAmt": $("#transAmt").val(),
		            "buyerName":$("#buyerName").val(),
		            "buyerTelePhone":$("#buyerTelePhone").val(),
		            "buyerPhone":$("#buyerPhone").val(),
		            "mallId":$('#mallId').val(),
		            "notifyUrl":"",
		            "prodName":"一瓶冰红茶"
		        }
	        ]
		};

		 ajax.post({
			url : 'payment/payPageTest',
			data: {
				jsonValue:jsonValue,
				getWay:uri
			},
			checkSession: false
			
		}).done(function(res, rtn, state, msg) {
			
			if (state) {
				console.log(rtn.data);
				page.renderResult(rtn.data);
//				alert('提交成功');
			} else {
				alert('数据查询失败：' + msg);
			}
		}).fail(function(rtn, state, msg) {			
			alert("数据查询失败");
		}).always(function() {					
			zuiLoad.hide();
		});
		

	};
	
	PageScript.prototype.renderResult=function(data) {
	
		location.href=data;
	};
	
	var page = new PageScript();
	page.init();
	
	return page;
});