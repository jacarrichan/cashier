/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		ZV = require('../../../lib/zuiplugin/zui.validate'),
		demo1 = require("../../../lib/background/back.demo.1");
		demo2 = require("../../../lib/background/back.demo.2");
		demo3 = require("../../../lib/background/back.demo.3");
		require("../../../lib/background/tweenlite.min");
	
	function PageScript(){
		this.websocket = null;
	}
	
	PageScript.prototype.init = function(){
		$.support.cors = true;
		
		// 进入登录页面时就清理缓存token
		if(window['sessionStorage']){
			sessionStorage.removeItem("authen");
			sessionStorage.removeItem("refresh_token");
			sessionStorage.removeItem("expires_in");
		}
		
		$('.panel-login').css({"margin-top": (($(window).height() - $('.panel-login').height())/2) + "px"});
		if(window['requestAnimationFrame']){
			
			var ri = parseInt(Math.random() * 3) + 1;
			if(ri == 1){
				demo1.init();
			} else if(ri == 2){
				demo2.init();
			} else if(ri == 3){
				demo3.init();
			}
		}
		
		// websocket
		/*$.useModule(['websocket'], function(){
			page.websocket = new YSWebSocket({
				success: function(e){
					log.info("connection success");
				},
				message: function(e, d){
					log.info("message:", e, d);
				}
			});
		});*/
		
		page.$formZV = ZV('.login-form');
		
		page.$formZV.validate({
			rules: {
				uName: {
					required: true,
					length: [1, 30]
				},
				pwd: {
					required: true,
					length: [1, 30]
				}
			}, message: {
				uName: {
					required: "请输入您的用户名",
					length: "用户名长度应在{0}至{1}位之间"
				},
				pwd: {
					required: "请输入您的密码",
					length: "密码长度应在{0}至{1}位之间"
				}
			},
		},"bottom");
		
	  	page.bindEvent();
	};
	//'m802/f80201',
	PageScript.prototype.loginServer = function(){
		if(page.$formZV.validate()){
		//debugger
		var zuiLoad = new $.ZuiLoader().show('正在验证，请稍候...');
		
		var uName = $('#uName').val();
		var pwd = $('#pwd').val();
		var uri = 'm101/f10111';
		ajax.post({
			url: uri,
			uia:true,
			//noMake:true,
			checkSession: false,
			data: {uName:uName}					
		}).done(function(res, rtn, state, msg){
			if(state){
				var source = rtn.data.source;
				//如果是电商平台的用户 ，需要特殊处理
				//if("01"==source){
					//pwd = $.md5($.md5(rtn.data.userId).toUpperCase() +$.md5(pwd).toUpperCase()).toUpperCase();
					ajax.post({
						url: "m800/f80007",
						checkSession: false,
						data: {base64Str:pwd}
					}).done(function(res, rtn, state, msg){
						if(state){
							//pwd = rtn.data;
							userLogin(uName ,pwd);
						}else {
							error(msg);
						}
					}).fail(function(e){
						log.error(e);
						error("登录失败：" + e.statusText);
					});
				/*}else{
					userLogin(uName ,pwd);
				}*/
				
			} else {
				error(msg);
			}
		}).fail(function(e, m){
			log.error(e);
			error(m);
		});
		zuiLoad.hide();
		}
	};
	
	function userLogin(uName, pwd){
		
		
		ajax.syncAjax({
			url: 'base/loginAction',
			data: "username="+uName+"&password="+ pwd+"&uiaURL="+consts.TOKEN_INFO_URL+"m802/f80204",
			noMake: true,
			tokenUrl: false,
			checkSession: false,
			headers: {
    			Authorization: "Basic d2ViX2FwcDo="
    		},
			success: function(res){
				if(res.success ){
					if(window['sessionStorage']){
	            		sessionStorage.setItem("authen", "Bearer " + res.access_token);
	            		sessionStorage.setItem("refresh_token", res.refresh_token);
	            		sessionStorage.setItem("expires_in", res.expires_in);
	            		sessionStorage.setItem("opera_times", new Date().getTime());
	            	}
					//document.location.href="./index.html";
					document.location.href="./index.html?uName="+uName;
				} else {
					error("登录失败，请检查用户名或密码是否正确");
				}
				
			},
			error: function(e, msg){
				error("登录失败：" + (e.statusText == 'Bad Request' ? '密码错误' : msg));
			}
		});
	}
	
	PageScript.prototype.bindEvent = function(){
		
		$('#uName,#pwd').on('keyup', function(e){
			var code = e.keyCode || e.witch;
			if(code == 13){
				page.loginServer();
			}
		});
		
		$('.sub-btn').on('click', function(){
			page.loginServer();
		});
		
		$('.forget-pwd').on('click', function(){
			alert("请联系管理员");
		});
		
		$('.rest-btn').on('click', function(){
			$('#uName').val('');
			$('#pwd').val('');
		});
		
	};
	
	var page = new PageScript();
	page.init();
	
	
	return page;
});