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
		$("#payment-header").find("li").each(function() {
	        $(this).click(function(){
	             $(this).addClass("active");
	             $(this).siblings().removeClass("active");
	        })
	    });
		// 点击折叠图标
		$(".chevron-icon").each(function () {
			$(this).click(function () {
				if($(this).attr("class") == "icon icon-chevron-down chevron-icon"){
					$(this).attr("class","icon icon-chevron-up chevron-icon");
					$(this).parents(".panel-section").siblings(".panel-section").find(".chevron-icon").attr("class","icon icon-chevron-down chevron-icon");
					$(this).parents(".panel-section").find(".panel-collapse").removeClass("collapse").addClass("in");
					$(this).parents(".panel-section").siblings(".panel-section").find(".panel-collapse").removeClass("in").addClass("collapse");
				} else {
					$(this).attr("class","icon icon-chevron-down chevron-icon");
					$(this).parents(".panel-section").find(".panel-collapse").removeClass("in").addClass("collapse");
				}
			})
		});
		var payMent = $("#bank").find(".bank-list");
        payMent.each(function() {
            $(this).click(function(){
                payMent.each(function(){
                    $(this).find(".bank-select").hide();
                    $(this).removeClass("active");
                });
                $(this).find(".bank-select").show();
                $(this).addClass("active");

            })
        });
        // 点击折叠栏
        $(".panel-section").find("h3").each(function (){
        	$(this).click(function () {
        		collapseTab($(this));
        	})
        })
        function collapseTab (_this) {
        	if(_this.next("i").attr("class") == "icon icon-chevron-down chevron-icon"){
				_this.next("i").attr("class","icon icon-chevron-up chevron-icon");
				_this.parents(".panel-section").siblings(".panel-section").find(".chevron-icon").attr("class","icon icon-chevron-down chevron-icon");
				_this.parents(".panel-section").find(".panel-collapse").removeClass("collapse").addClass("in");
				_this.parents(".panel-section").siblings(".panel-section").find(".panel-collapse").removeClass("in").addClass("collapse");
			} else {
				_this.next("i").attr("class","icon icon-chevron-down chevron-icon");
				_this.parents(".panel-section").find(".panel-collapse").removeClass("in").addClass("collapse");
			}
        };
        var payMent = $("#bank").find(".bank-list");
        payMent.each(function() {
            $(this).click(function(){
                payMent.each(function(){
                    $(this).find(".bank-select").hide();
                    $(this).removeClass("active");
                });
                $(this).find(".bank-select").show();
                $(this).addClass("active");

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