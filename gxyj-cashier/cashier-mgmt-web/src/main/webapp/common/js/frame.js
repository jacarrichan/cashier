/**
 * 主框架页面脚本
 */
define(function(require, exports, module){
	
	function PageScript(){
		this.lockBox = null;
	}
	
	PageScript.prototype.init = function(){
		window['FRAME_DOC'] = true; // 用于统一控制页面跳转用
		
		ajax.session(true).always(function(res){
			var url = consts.WEB_BASE + "apps/frame/login.html";
			if(res.success&& res.obj && res.obj.userId){
				url = consts.WEB_BASE + "apps/frame/index.html";
			}
			$('#mainFrame').attr('src', url).on('load', function(){
				$('.wait-panel').animate({'opacity': 0}, function(){
					$(this).hide();
				});
			});
		});
		
		page.bindEvent();
	}
	
	PageScript.prototype.bindEvent = function(){
		
		top._keyboard_shortcuts = {};
	    
		top.regShortKey = function(keyCode, func){
			top._keyboard_shortcuts[keyCode] = func;
	    };
	    
	    top.removeShortKey = function(keyCode){
	    	top._keyboard_shortcuts[keyCode] = undefined;
			delete top._keyboard_shortcuts[keyCode];
	    };
	    
	    top.hasShortKey = function(code, hash){
	    	var has = false;
	    	has = (top._keyboard_shortcuts[code] !== undefined && top._keyboard_shortcuts[code] !== null);
	    	if(!has){
	    		has = (top._keyboard_shortcuts[hash + code] !== undefined && top._keyboard_shortcuts[hash + code] !== null);
	    	}
	    	if(!has){
	    		has = /^(Alt|Control|Shift|Meta)[0-9]+/.test(code);
	    	}
	    	return has;
	    };
	    
	    top.callShortKey = function(e, code, hash){
			var func = top._keyboard_shortcuts[code] || top._keyboard_shortcuts[hash + code];
			if(!func && /^(Alt|Control|Shift|Meta)[0-9]+/.test(code)){
				var key = code;
				if(code.indexOf('Alt') != -1){
					key = "Alt**"
				} else if(code.indexOf('Control') != -1){
					key = "Control**"
				} else if(code.indexOf('Shift') != -1){
					key = "Shift**"
				} else if(code.indexOf('Meta') != -1){
					key = "Meta**"
				}
				func = top._keyboard_shortcuts[key] || top._keyboard_shortcuts[hash + key];
			}
			if(_.isFunction(func)){
				func.call(window, e, code);
			}
	    };
	    
	    // 注册锁屏快捷键 Alt + L
	    top.regShortKey('Alt76', function(){
	    	page.startLockScreen();
	    });
	    
	    $('body').on('click', function(e){
	    	var $this = $(e ? e.target : this);
	    	if($this.hasClass('btn-lock')){
	    		page.sureLocked();
	    		
	    	} else if($this.hasClass('btn-nolock')){
	    		
	    		page.lockBox && page.lockBox.close(); // 关闭弹出框
	    		
	    	} else if($this.hasClass('btn-exit')){
	    		page.lockBox && page.lockBox.close(); // 关闭弹出框
	    		
	    		if(window['sessionStorage']){
	        		sessionStorage.removeItem("authen");
	        		sessionStorage.removeItem("USER-INFO");
	        		window['LOCKSCREENED'] = false;
	        	}
	    		$('#mainFrame').attr('src', consts.WEB_BASE + "apps/fss/login.html");
	    		
	    	} else if($this.hasClass('btn-unlock')){
	    		page.unlockScreen();
	    	}
	    });
	}
	
	PageScript.prototype.startLockScreen = function(){
		if(!window['LOCKSCREENED']){
    		page.lockScreen();
    	}
	};
	
	// 锁屏
	PageScript.prototype.lockScreen = function(){
		var $backdrop = $('.modal-backdrop');
		if(!$backdrop.length){
			page.lockBox = new $.zui.ModalTrigger({
				showHeader: false,
				loadingIcon: 'icon-lock',
				custom: laytpl('lockscreen.html').render({
				})
			});
			page.lockBox.show();
			setTimeout(function(){
				$('#lockpwd').focus().off('keyup').on('keyup', function(e){
					var code = e.keyCode || e.witch;
					if(code == 13){
						page.sureLocked();
					}
				});
			}, 200);
		}
	};
	
	PageScript.prototype.sureLocked = function(){
		var pwd = $('#lockpwd').val();
    	if(pwd){
    		ajax.post({
    			url: 'm800/f80006',
    			data: {type: 'lock', lockpwd: pwd}
    		}).done(function(res, rtn, state, msg){
    			if(state){
    				page.lockBox && page.lockBox.close(); // 关闭弹出框
    				
					setTimeout(function(){
						window['LOCKSCREENED'] = true;
						page.showLockScreen();
						page.lockScreenEvent();
					}, 50);
    			} else {
    				$('.form-text.msg').text('锁屏失败，请重试.');
    			}
    		}).fail(function(){
    			$('.form-text.msg').text('锁屏失败，请重试.');
    		});
			
    	} else {
    		$('.form-text.msg').text('请输入锁屏密码，用于解锁，并且请牢记它.');
    	}
	};
	
	PageScript.prototype.showLockScreen = function(){
		var $backdrop = $('.modal-backdrop');
		if(!$backdrop.length){
			page.lockBox = new $.zui.ModalTrigger({
				keyboard: false,
				showHeader: false,
				backdrop: 'static',
				loadingIcon: 'icon-lock',
				custom: laytpl('unlockscreen.html').render({
				})
			});
			page.lockBox.show();
			setTimeout(function(){
				$('#unlockpwd').off('keyup').on('keyup', function(e){
					var code = e.keyCode || e.witch;
					if(code == 13){
						page.unlockScreen();
					}
				});
			}, 200);
		}
	};
	
	// 解锁屏
	PageScript.prototype.unlockScreen = function(){
		var pwd = $('#unlockpwd').val();
    	if(pwd){
    		ajax.post({
    			url: 'm800/f80006',
    			data: {type: 'unlock', lockpwd: pwd}
    		}).done(function(res, rtn, state, msg){
    			if(state){
    				window['LOCKSCREENED'] = false;
    				page.lockBox && page.lockBox.close();
    			} else {
    				$('.form-text.msg').text('密码错误，解锁失败.');
    			}
    		}).fail(function(){
    			$('.form-text.msg').text('解锁失败，请重试.');
    		});
    		
    	} else {
    		$('.form-text.msg').text('请输入解锁密码.');
    	}
	};
	
	// 检查锁屏
	PageScript.prototype.checkLockScreen = function(callback){
		/*ajax.post({
			url: 'm800/f80005',
			data: {}
		}).done(function(res, rtn, state, msg){
			if(state){
				window['LOCKSCREENED'] = true;
				page.showLockScreen();
				page.lockScreenEvent();
				callback && callback.call(window, true);
			} else {
				window['LOCKSCREENED'] = false;
				page.lockBox && page.lockBox.close();
				callback && callback.call(window, false);
			}
		});*/
		callback && callback.call(window, true);
	};
	
	PageScript.prototype.lockScreenEvent = function(){
		if(window['LOCKSCREENED']){
			page.showLockScreen();
			setTimeout(function(){
				$('#unlockpwd').focus();
				page.lockScreenEvent();
			}, 100);
		}
	};
	
	window['LOCKSCREENED'] = false;
	
	var page = new PageScript();
	page.init();
	window['checkLockScreen'] = page.checkLockScreen;
	window['startLockScreen'] = page.startLockScreen;
	return page;
});