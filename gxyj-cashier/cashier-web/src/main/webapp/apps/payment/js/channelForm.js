/**
 * 主框架页面脚本
 */
define(function(require, exports, module){
	
	var comm = require('../../../common/js/page-common');
	pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		
	}
	
	PageScript.prototype.init = function(){	
		$("#url").val(consts.WEB_BASE + "gxyj/interface/api");
		page.bindEvent();
		
		// textarea高度自适应
		$('#jsonValue').each(function () {
			  this.setAttribute('style', 'height:' + (this.scrollHeight) + 'px;overflow-y:hidden;');
		}).on('input', function () {
			  this.style.height = 'auto';
			  this.style.height = (this.scrollHeight) + 'px';
		});

	}
	PageScript.prototype.bindEvent = function(){
		$("#submit").click(function(){
			var url = $("#url").val();
			var jsonValue = $("#jsonValue").val();
			ajax.post({
				url : "ajax/interface/api",
				data: {
					url : url,
					jsonValue: jsonValue
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				$("#result").html(msg);//此处放返回结果
			}).fail(function() {			
				alert("数据加载失败，请重试！");
			}).always(function() {					
				//console.log("always");
			});
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