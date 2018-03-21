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
		var jsonValue = param['jsonValue'];		
		$("#jsonValue").html(jsonValue);
	}
	
	PageScript.prototype.bindEvent = function(){
		
		$("#btnOk").click(function(){
			page.loadData();
		});
		
	}
	
	PageScript.prototype.loadData = function() {

		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		var uri ='payment/paymentStatus';
		alert('数据查询：' + uri);
		
		ajax.post({
			url : uri,
			data: {"orderId": $("#orderId").val(), "transId": $("#transId").val()},
			checkSession: false
		// 传入分页对象
		}).done(function(res, rtn, state, msg) {
			alert("bbbb"+state);
			if (state) {
				page.renderResult(rtn.data);
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
		$("#resultMsg").html(data);
	};
	
	var page = new PageScript();
	page.init();
	
	return page;
});