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

		var url = consts.WEB_BASE + "gxyj/interface/api";
		var dateStr=utils.getCurrDate('yyyyMMddhhmmss');
		if($("#origOrderId").val() == '' || $("#refundAmt").val() == "") {
			alert("订单编号");
			return;
		}
		var msgId='BUY'+dateStr;
		var data={"terminal":$("#terminal").val(),
				 "origOrderId":$("#origOrderId").val(),
				 "refundAmt":$("refundAmt").val(),
				 "channelType":$("channelType").val(),
				 "refundId":$("#refundId").val(),
				 "channelId":$("#channelId").val()
				 };
		var jsonValue = {
			"HEAD": {  
			"msgId": msgId + "001630328", 
			"msgCreateTime":"20170717103801",  
			"sender":"IFS", 
			"receiver":"FFS", 
			"autograph":"ewrwewer", 
			"encryptionMode":"MD5",
            "interfaceCode":"BUY_CSR_1002", 
            "returnUrl":consts.WEB_BASE + "mq/test",
            "source" : $("#channelId").val()
			},
			"BODY":[        
		        {
		        	"refundId": $("#refundId").val(),
		        	"origOrderId": $("#origOrderId").val(),
		        	"refundAmt": $("#refundAmt").val(),
		            "terminal": $("#terminal").val(),
		            "channelType":$("#channelType").val(),
		            "channelId": $("#channelId").val(),
		            "mallId":$('#mallId').val()
		            
		        }
	        ]
		};
		
		 ajax.post({
			url : 'ajax/interface/api',
			data: {
				url:url,
				jsonValue:jsonValue
			},//utils.toJSON(jsonValue),
			checkSession: false
			
		}).done(function(res, rtn, state, msg) {
			
			if (state) {
				$("#result").html(msg);//此处放返回结果
				//page.renderResult(rtn.data);
				
			} else {
				alert('退款失败：' + msg);
			}
		}).fail(function(rtn, state, msg) {			
			alert("退款失败");
		}).always(function() {					
			zuiLoad.hide();
		});
		

	};
	
	PageScript.prototype.renderResult=function(data) {
	
		location.href=consts.WEB_BASE+'/order/payment/api/commit?jsonValue='+data;
	};
	
	var page = new PageScript();
	page.init();
	
	return page;
});