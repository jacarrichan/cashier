/**
 * 
 */
define(function(require, exports, module) {
	
	function PageScript(){
		this.pageTabDiff = 220;
	}
	
	PageScript.prototype.init = function(){
		window['INDEX_DOC'] = true; // 用于统一控制页面跳转用
		
	  	$('#rightFrame-main').attr('src', consts.WEB_BASE + 'apps/frame/home.html');
	  	$('.user-name-box').css('position', 'relative');
	  	page.showWaitAndHidden('main', true);
	  	
	  	// 初始化主题选择
	  	var zuiTheme = 'default';
	  	if(window.localStorage){
	    	zuiTheme = localStorage.getItem('theme') || zuiTheme;
	    } else if(seajs.getCookie('theme')){
	    	zuiTheme = seajs.getCookie('theme') || zuiTheme;
	    }
	  	$('.head-block.avatar .theme-box .btn').removeClass('active');
	  	$('.head-block.avatar .theme-box .btn.btn-' + zuiTheme).addClass('active');
	  	page.loginLoad();
	  	// 绑定事件
	  	page.bindEvent();
	};
	
	PageScript.prototype.loadUserInfo = function(){
		var param = utils.getUrlParam();
		var uName = param['uName'] ;
		/*ajax.post({
			data:{uName:uName}
			}).done(function(user){
			log.info(user);
			if(user && user.userId){
				$('.user-name').text(user.nickName);
				$('#user-head-avatar img').attr('src', consts.IMAGE_SERVER_URL + user.userId +'.jpg' );
				
				$('.mall-name').text(user.mallName);
				
				var unboxwid = parseInt($('.user-name-box').css('width'));
				$('.mall-name-box').css('width', 240 - unboxwid);
				
				page.loadUserMenus(user.userId);
			} else {
				top.location.href = "./login.html";
			}
		});*/
		/*ajax.post({
			url: 'm101/f10113',
			data: {uName:uName},
			uia:true,
			checkSession: false,
		}).done(function(data, status, xhr){
			if(data.success){
				ajax.sessionByName(data, status, xhr);
				var user = status.data; //
				page.loadUserMenus(status.data.userId);
				
				$('.user-name').text(user.nickName);
				$('#user-head-avatar img').attr('src', consts.IMAGE_SERVER_URL + user.userId +'.jpg' );
				
				//$('.mall-name').text(user.brchName);
				
				var unboxwid = parseInt($('.user-name-box').css('width'));
				
				$('.mall-name-box').css('width', 240 - unboxwid);
			} else {
				$('#side-menu').html(laytpl('nomenu.tpl').render({}));
			}
		}).fail(function(e){
			alert(e.message);
		});*/
		
		ajax.getUserInfo().done(function(user){
			if(user && user.userId){
				$('.user-name').text(user.userName);
				$('#user-head-avatar img').attr('src', consts.IMAGE_SERVER_URL + user.userId +'.jpg' );
				
				//$('.mall-name').text(user.mallName);
				//$('.sys-status').text(user.sysStatus);
				//$('.workDate').text(user.workDate);
				
				var unboxwid = parseInt($('.user-name-box').css('width'));
				$('.mall-name-box').css('width', 240 - unboxwid);
				
				page.loadUserMenus(user.userId);
			} else {
				//top.location.href = "./login.html";
			}
		});
	};
	
	PageScript.prototype.loginLoad = function(){
		if(parent.checkLockScreen){
			parent.checkLockScreen(function(flag){
				if(flag === false){
					page.loadUserInfo();
				} else {
					var lockTime = setInterval(function(){
						if(parent.LOCKSCREENED === false){
							clearInterval(lockTime);
							
							page.loadUserInfo();
						}
					}, 100);
				}
			});
		} else {
			page.loadUserInfo();
		}
	}
	
	PageScript.prototype.loadUserMenus = function(uid){
		//userId
		ajax.post({
			//url: consts.WEB_BASE + 'mgmt/loadUserMenu',
			url: 'm802/f80202',
			data: {userId:uid},
			uia:true,
			checkSession: false, 
		}).done(function(data){
			if(data.success){
				page.renderMenu(data.rtn.data);
				if(!window.localStorage){
		            alert("浏览器支持不localstorage，请升级或更换浏览器");
		            return false;
		        }else{
		            var storage=window.localStorage;
		            if(storage.getItem("dataInfo") != null){
		            	 var id = storage.getItem("dataId");
		            	 var src = consts.WEB_BASE + storage.getItem("dataHref")
				         var ele = $("a[data-id="+id+"]");
				         var menuName = $("a[data-id="+id+"]").text();
				         ele.addClass("active");
				         ele.parents("ul").addClass("in");
				         ele.parents("ul").parents("li").addClass("active");
				         ele.parents("ul").parents("li").find("i").attr("class","icon icon-folder-open");
				         ele.parents("ul").parents("li").find("span:last-child").attr("class","icon arrow icon-angle-down icon-folder-open");
				         if($('.page-tabs-content .J_menuTab.' + id).length == 0){ // 没有tab，添加
								$('.page-tabs-content .J_menuTab').removeClass('active');
								$('.rightframe').hide();
								$('.page-tabs-content').append(laytpl('page_tab.tpl').render({"id": id, "text": menuName}));
								$('.page-tabs-content .J_menuTab i').tooltip();
								$('.body-right').append(laytpl('iframe.tpl').render({"fid": id, "src": src}));
								page.showWaitAndHidden(id, true);
								var $avtiveMenuTab = $('.page-tabs-content .J_menuTab.' + id),
									tabswid = $('.head-block.tabs').width() - page.pageTabDiff,
									pagetabswid = $('.head-block.tabs .page-tabs-content').width();
								if(pagetabswid > tabswid){
									var $menuTabs = $('.head-block.tabs .J_menuTab'),
										len = $menuTabs.length,
										avtivewid = $avtiveMenuTab.width();
									for(var i = 0; i < len; i++){
										var $this = $($menuTabs[i]),
											thiswid = $this.width();
										if(!$this.hasClass('hidden')){
											$this.addClass('hidden');
											if(thiswid < avtivewid){
												if(!$this.next().hasClass('hidden')){
													$this.next().addClass('hidden');
												}
											}
											return;
										}
									}
								}
						} else { // 已有tab，设置焦点
								$('.page-tabs-content .J_menuTab').removeClass('active');
								$('.rightframe').hide();
								
								var $avtiveMenuTab = $('.page-tabs-content .J_menuTab.' + id);
								$avtiveMenuTab.addClass('active');
								$('#rightFrame-' + id).show();
								
								var index = $avtiveMenuTab.index(),
									$JMenuTabs = $('.page-tabs-content .J_menuTab'),
									len = $JMenuTabs.length,
									allwid = 0,
									tabswid = $('.head-block.tabs').width() - page.pageTabDiff;
								if($avtiveMenuTab.hasClass('hidden')){
									for(var i = index - 1; i < len; i++){
										var $menuTab = $($JMenuTabs[i]);
										$menuTab.removeClass('hidden');
									}
								} else {
									var end = index - 5 < 0 ? 0 : index - 5;
									for(var i = 0; i < end; i++){
										var $menuTab = $($JMenuTabs[i]);
										$menuTab.addClass('hidden');
									}
								}
						}
		            }
		           
		        }

			} else {
				$('#side-menu').html(laytpl('nomenu.tpl').render({}));
				if(!window.localStorage){
		            alert("浏览器支持不localstorage");
		            return false;
		        }else{
		            var storage=window.localStorage;
		            storage.getItem("dataInfo");
		            var ele = $("a[data-id="+storage.getItem("dataId")+"]");
		            console.log(ele.attr("data-id"));
		            console.log(storage.getItem("dataId"));
		            console.log($("#side-menu .J_menuItem"));
		        }

			}
		}).fail(function(e){
			alert(e.message);
		});
	};
	
	PageScript.prototype.renderMenu = function(menus){
		var menu = {}, menuHtml = "", menuJson = [], itemJson = {}, items = "";
		var menuNameJson = {}, nameJson = {};
		
		for(var i = 0, k = menus.length; i < k; i++){
			menu = menus[i];
			if(menu.rightLevel == '1'){
				menuJson.push({
					menuName: menu.rightName,
					menuId: menu.rightId,
					index: i
				});
			} else if(menu.rightLevel == '2'){
				if(!itemJson[menu.parentRightId]){
					itemJson[menu.parentRightId] = "";
				}
				items = itemJson[menu.parentRightId];
				items += laytpl('menu_item.tpl').render({
					menuName: menu.rightName,
					menuId: menu.rightId,
					menuUrl: menu.rightUrl,
					index: i
				});
				itemJson[menu.parentRightId] = items;
				
				if(!nameJson[menu.parentRightId]){
					nameJson[menu.parentRightId] = [];
				}
				nameJson[menu.parentRightId].push({
					id: menu.rightId,
					name: menu.rightName
				});
			}
		}
		var obj, size = menuJson.length;
		for(var j = 0; j < size; j++){
			obj = menuJson[j];
			menuHtml += laytpl('menu_line.tpl').render({
				menuName: obj.menuName,
				menuId: obj.menuId,
				items: itemJson[obj.menuId] || "",
				index: obj.index
			});
			
			var arr = nameJson[obj.menuId], twoMenu = {};
			if(arr && arr.length){
				for(var i = 0; i < arr.length; i++){
					twoMenu = arr[i];
					menuNameJson[twoMenu.id] = [obj.menuName, twoMenu.name];
				}
			}
		}
		$('#side-menu').html(menuHtml);
		
		if(window['sessionStorage']){
			sessionStorage.setItem('_MENU_NAME_JSON', utils.toJSON(menuNameJson));
		}
	};
	
	// 显示或隐藏进度等待条
	PageScript.prototype.showWaitAndHidden = function(id, flag){
		var $wait = $('.wait-panel'),
			did = $wait.data('id'),
			$frame = $('#rightFrame-'+id);
		$wait.removeClass('hidden').data('id', id).data('time', new Date().getTime());
		if(flag){
			$frame.on('load', function(){
				var wid = $wait.data('id');
				if(wid == id){
					$wait.addClass('hidden').data('id', '').data('time', new Date().getTime());
				}
			});
		}
		if(did != id){
			setTimeout(function(){
				var wid = $wait.data('id'),
				startTime = $wait.data('time'),
				endTime = new Date().getTime();
				if(wid == id && (endTime - startTime) > 10000){
					$wait.addClass('hidden').data('id', '').data('time', endTime);
				}
			}, 10000);
		}
	};
	
	PageScript.prototype.bindEvent = function(){
		
		// 切换主题
		$('.head-block.avatar .theme-box .btn').on('click', function(){
			var $this = $(this),
				theme = $this.data('theme');
			$('.head-block.avatar .theme-box .btn').removeClass('active');
			$this.addClass('active');
			if(window.localStorage){ // 支持h5本地存储
				localStorage.setItem('theme', theme);
		    	
		    	new $.zui.Messager('主题更改成功，刷新后生效。', {
		    	    type: 'info',
		    	    close: true,
		    	    actions: [{
		    	        name: 'undo',
		    	        icon: 'undo',
		    	        text: '立即刷新',
		    	        action: function() {  // 点击该操作按钮的回调函数
		    	            top.location.reload();
		    	        }
		    	    }]
		    	}).show();
		    } else if(document.cookie) { // 支持cookie
		    	var Days = 30;
		    	var exp = new Date();
		    	exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
		    	document.cookie = "theme="+ theme + ";expires=" + exp.toGMTString();
		    	
		    	new $.zui.Messager('主题更改成功，刷新后生效。', {
		    	    type: 'info',
		    	    close: true,
		    	    actions: [{
		    	        name: 'undo',
		    	        icon: 'undo',
		    	        text: '立即刷新',
		    	        action: function() {  // 点击该操作按钮的回调函数
		    	            top.location.reload();
		    	        }
		    	    }]
		    	}).show();
		    } else {
		    	alert('您的浏览器不支持主题切换，请更换浏览器');
		    }
		});
		
		// 一级菜单
		$('#side-menu').on('click', '.nav-menu', function(){
			var $this = $(this),
				$parent = $this.parent();
			if($parent.hasClass('active')){ // 已展开 -> 收缩
				$parent.removeClass('active');
				$this.next().removeClass('in');
				$this.find('.arrow').addClass('icon-angle-left').removeClass('icon-angle-down');
				$this.find('.icon').addClass('icon-folder-close').removeClass('icon-folder-open');
			} else { // 已收缩 -> 展开
				var $siblings = $parent.siblings(),
					$child = $siblings.find('ul.nav-second-level');
				$siblings.removeClass('active');
				$siblings.find('.nav-menu .arrow').addClass('icon-angle-left').removeClass('icon-angle-down');
				$siblings.find('.nav-menu .icon').addClass('icon-folder-close').removeClass('icon-folder-open');
				$child.removeClass('in');
				
				$parent.addClass('active');
				$this.next().addClass('in');
				$this.find('.arrow').removeClass('icon-angle-left').addClass('icon-angle-down');
				$this.find('.icon').addClass('icon-folder-open').removeClass('icon-folder-close');
				
			}
			
		});
		
		// 点击菜单项，新增tab或者定位tab
		$('#side-menu').on('click', '.J_menuItem', function(){
			var $this = $(this),
				id = $this.data('id'),
				src = $this.data('href'),
				menuName = $this.text();
			$('.J_menuItem').removeClass('active');
			$this.addClass('active');
			
			if($('.page-tabs-content .J_menuTab.' + id).length == 0){ // 没有tab，添加
				var index = src.indexOf("#");
				if(index != -1){
					src = src.substring(0, index) + "?_MenuRightId=" + id + src.substring(index);
				} else {
					src = src + "?_MenuRightId=" + id;
				}
				
				$('.page-tabs-content .J_menuTab').removeClass('active');
				$('.rightframe').hide();
				$('.page-tabs-content').append(laytpl('page_tab.tpl').render({"id": id, "text": menuName}));
				$('.page-tabs-content .J_menuTab i').tooltip();
				$('.body-right').append(laytpl('iframe.tpl').render({"fid": id, "src": consts.WEB_BASE + src}));
				page.showWaitAndHidden(id, true);
				var $avtiveMenuTab = $('.page-tabs-content .J_menuTab.' + id),
					tabswid = $('.head-block.tabs').width() - page.pageTabDiff,
					pagetabswid = $('.head-block.tabs .page-tabs-content').width();
				if(pagetabswid > tabswid){
					var $menuTabs = $('.head-block.tabs .J_menuTab'),
						len = $menuTabs.length,
						avtivewid = $avtiveMenuTab.width();
					for(var i = 0; i < len; i++){
						var $this = $($menuTabs[i]),
							thiswid = $this.width();
						if(!$this.hasClass('hidden')){
							$this.addClass('hidden');
							if(thiswid < avtivewid){
								if(!$this.next().hasClass('hidden')){
									$this.next().addClass('hidden');
								}
							}
							return;
						}
					}
				}
			} else { // 已有tab，设置焦点
				$('.page-tabs-content .J_menuTab').removeClass('active');
				$('.rightframe').hide();
				
				var $avtiveMenuTab = $('.page-tabs-content .J_menuTab.' + id);
				$avtiveMenuTab.addClass('active');
				$('#rightFrame-' + id).show();
				
				var index = $avtiveMenuTab.index(),
					$JMenuTabs = $('.page-tabs-content .J_menuTab'),
					len = $JMenuTabs.length,
					allwid = 0,
					tabswid = $('.head-block.tabs').width() - page.pageTabDiff;
				if($avtiveMenuTab.hasClass('hidden')){
					for(var i = index - 1; i < len; i++){
						var $menuTab = $($JMenuTabs[i]);
						$menuTab.removeClass('hidden');
					}
				} else {
					var end = index - 5 < 0 ? 0 : index - 5;
					for(var i = 0; i < end; i++){
						var $menuTab = $($JMenuTabs[i]);
						$menuTab.addClass('hidden');
					}
				}
			}
		});
		
		// 点击page tab，切换页面，并改变相应菜单状态
		$('.page-tabs-content').on('click', '.J_menuTab', function(){
			var $this = $(this),
				id = $this.data('id'),
				$siblings = $this.siblings();
			$siblings.removeClass('active');
			$this.addClass('active');
			page.changeMenuState(id);
			$('.rightframe').hide();
			$('#rightFrame-' + id).show();
			
			if($this.hasClass('main')){
				$('#side-menu .J_menuItem').removeClass('active');
			}
			
			$('.wait-panel').addClass('hidden').data('id', '').data('time', new Date().getTime());
		});
		
		// 点击page tab上的关闭图标
		$('.page-tabs-content').on('click', '.J_menuTab > i', function(e){
			var $this = $(this).parent(), // 当前点击的节点
				$prev = $this.prev(), // 上一个节点
				id = $this.data('id'), // 当前点击的节点ID
				pid = $prev.data('id'); // 上一个节点ID
			// 判断当前节点是否为选中状态，若选中则显示的到上一个节点页面，否则不变
			if($this.hasClass('active')){
				$prev.addClass('active');
				page.changeMenuState(pid);
				$('#rightFrame-' + pid).show();
			}
			$this.remove(); // 删除当前节点
			$('#rightFrame-' + id).remove(); // 删除当前页面 
			
			$('.wait-panel').addClass('hidden').data('id', '').data('time', new Date().getTime());
			
			var tabswid = $('.head-block.tabs').width() - page.pageTabDiff,
				pagetabswid = $('.head-block.tabs .page-tabs-content').width();
			if(pagetabswid < (tabswid - 250)){
				var $JMenuTabs = $('.page-tabs-content .J_menuTab.hidden'),
					len = $JMenuTabs.length;
				$($JMenuTabs[len - 1]).removeClass('hidden');
			}
			
			e.stopPropagation();
		});
		
		// 关闭全部
		$('.head-block.tabs').on('click', '.J_tabCloseAll', function(){
			$('.head-block.tabs .J_menuTab').each(function(i, e){
				var $this = $(this),
					id = $this.data('id');
				if(!$this.hasClass('main')){
					$this.remove(); // 删除当前节点
					$('#rightFrame-' + id).remove(); // 删除当前页面 
				}
			});
			$('.head-block.tabs .J_menuTab.main').addClass('active').removeClass('hidden');
			$('#rightFrame-main').show(); // 显示首页 
			page.changeMenuState(null);
		});
		
		// 关闭其他
		$('.head-block.tabs').on('click', '.J_tabCloseOther', function(){
			$('.head-block.tabs .J_menuTab').each(function(i, e){
				var $this = $(this),
					id = $this.data('id');
				if(!$this.hasClass('active') && !$this.hasClass('main')){
					$this.remove(); // 删除当前节点
					$('#rightFrame-' + id).remove(); // 删除当前页面 
				}
			});
			$('.head-block.tabs .J_menuTab.main').removeClass('hidden');
		});
		
		// 定位当前选项
		$('.head-block.tabs').on('click', '.J_tabShowActive', function(){
			var $avtiveMenuTab = $('.page-tabs-content .J_menuTab.active');
			var index = $avtiveMenuTab.index(),
				$JMenuTabs = $('.page-tabs-content .J_menuTab'),
				len = $JMenuTabs.length,
				allwid = 0,
				tabswid = $('.head-block.tabs').width() - page.pageTabDiff;
			if($avtiveMenuTab.hasClass('hidden')){
				for(var i = index - 1; i < len; i++){
					var $menuTab = $($JMenuTabs[i]);
					$menuTab.removeClass('hidden');
				}
			} else {
				for(var i = 0; i < len; i++){
					var $menuTab = $($JMenuTabs[i]),
						width = $menuTab.width();
					if(i <= index){
						allwid += width + 31;
						if(allwid > tabswid){
							$('.head-block.tabs .J_tabRight').click();
						}
					} else {
						break;
					}
				}
			}
		});
		
		// 关闭当前页面
		if(top.regShortKey){
			top.regShortKey("Alt67", function(e, code){
				var $this = $('.page-tabs-content .J_menuTab.active'), // 当前点击的节点
					$prev = $this.prev(), // 上一个节点
					id = $this.data('id'), // 当前点击的节点ID
					pid = $prev.data('id'); // 上一个节点ID
				if($this.hasClass('main')){
					return;
				}
				// 判断当前节点是否为选中状态，若选中则显示的到上一个节点页面，否则不变
				if($this.hasClass('active')){
					$prev.addClass('active');
					page.changeMenuState(pid);
					$('#rightFrame-' + pid).show();
				}
				$this.remove(); // 删除当前节点
				$('#rightFrame-' + id).remove(); // 删除当前页面 
				
				$('.wait-panel').addClass('hidden').data('id', '').data('time', new Date().getTime());
				
				var tabswid = $('.head-block.tabs').width() - page.pageTabDiff,
					pagetabswid = $('.head-block.tabs .page-tabs-content').width();
				if(pagetabswid < (tabswid - 250)){
					var $JMenuTabs = $('.page-tabs-content .J_menuTab.hidden'),
						len = $JMenuTabs.length;
					$($JMenuTabs[len - 1]).removeClass('hidden');
				}
			});
		}
		
		// 定位首页选项卡
		$('.head-block.tabs').on('click', '.J_tabHome', function(){
			$('.head-block.tabs .J_menuTab').removeClass('hidden').removeClass('active');
			$('.head-block.tabs .J_menuTab.main').addClass('active');
			$('.rightframe').hide(); // 隐藏所有页面
			$('#rightFrame-main').show(); // 显示首页
			$('#side-menu .J_menuItem').removeClass('active'); // 所有菜单去掉状态
			
			$('.wait-panel').addClass('hidden').data('id', '').data('time', new Date().getTime());
		});
		
		// 刷新当前页面
		$('.head-block.tabs').on('click', '.J_tabFlush', function(){
			$('.head-block.tabs .J_tabShowActive').click();
			var $avtiveMenuTab = $('.page-tabs-content .J_menuTab.active'),
				id = $avtiveMenuTab.data('id'),
				$frame = $('#rightFrame-' + id);
			$frame.attr('src', $frame.attr('src')); // 删除当前页面 
			page.showWaitAndHidden(id, false);			
		});		
		
		// 移动page tab -> 向左移动
		$('.head-block.tabs').on('click', '.J_tabLeft', function(){
			var $tempTab = null;
			$('.head-block.tabs .J_menuTab').each(function(i, e){
				var $this = $(this);
				if($this.hasClass('hidden')){
					$tempTab = $this;
				} else {
					if($tempTab){
						$tempTab.removeClass('hidden');
					}
					return;
				}
			});
		});
		
		// 移动page tab -> 向右移动
		$('.head-block.tabs').on('click', '.J_tabRight', function(){
			var allwid = $('.head-block.tabs').width(),
			tabswid = allwid - page.pageTabDiff,
			pagetabswid = $('.head-block.tabs .page-tabs-content').width();
			if(pagetabswid > tabswid){
				var $menuTabs = $('.head-block.tabs .J_menuTab'),
					len = $menuTabs.length;
				for(var i = 0; i < len; i++){
					var $this = $($menuTabs[i]);
					if(!$this.hasClass('hidden')){
						$this.addClass('hidden');
						return;
					}
				}
			}
		});
		
		
		$('.sidebar-switch .switch-left').on('click', function(){
			var $switch = $('.sidebar-switch'),
				$switchRight = $('.sidebar-switch .switch-right'),
				$bodyleft = $('.main-body .body-left'),
				$bodyright = $('.main-body .body-right');
			if(!$bodyleft.hasClass('hidden')){
				$switch.css('left', '-16px');
				$bodyleft.addClass('float-left');
				$switchRight.removeClass('hidden');
				$bodyright.css('width', '100%');
			}
		});
		
		$('.sidebar-switch .switch-right').on('click', function(){
			var $switch = $('.sidebar-switch'), 
				$bodyleft = $('.main-body .body-left'),
				$bodyright = $('.main-body .body-right');
			if($bodyleft.hasClass('float-left')){
				$(this).addClass('hidden');
				$switch.css('left', '244px');
				$bodyleft.removeClass('float-left');
				$bodyright.css('width', 'calc(100% - 260px)');
			}
		});
		
		$('.main-body .body-fullscreen').on('click', function(){
			var $this = $(this),
				$right = $('.main-body .body-right'),
				isfull = $this.data('isfull');
			if(isfull){
				$('.main-body .body-right').removeClass('fullscreen');
				$this.data('isfull', false);
			} else {
				$('.main-body .body-right').addClass('fullscreen');
				$this.data('isfull', true);
			}
		});
		
		if(top.regShortKey){
			// F9
			top.regShortKey("120", function(){
				$('.main-body .body-fullscreen').click();
			});
			
			// F4
			top.regShortKey("115", function(){
				var $bodyleft = $('.main-body .body-left');
				if($bodyleft.hasClass('float-left')){
					$('.sidebar-switch .switch-right').click();
				} else {
					$('.sidebar-switch .switch-left').click();
				}
			});
			
			// Alt + Q
			top.regShortKey("Alt81", function(){
				$('.J_tabExit').click();
			});
			
			// Alt + S
			top.regShortKey("Alt83", function(){
				$('.J_tabSortkey').click();
			});
		}
		
		$('.J_tabExit').on('click', function(){
			$.confirm({
				title: '提示',
				yesText: '确认退出',
				msg: '您确认要退出系统吗？',
				yesClick: function($modal){
					if(window['sessionStorage']){
		        		sessionStorage.removeItem("authen");
		        		sessionStorage.removeItem("USER-INFO");
		        	}
					$modal.modal('hide');
					
					document.location.replace("./login.html");
				}
			});
		});
		
		$('.J_tabLock').on('click', function(){
			parent.startLockScreen && parent.startLockScreen();
		});
		
		$('.J_tabSortkey').on('click', function(){
			var $backdrop = $('.modal-backdrop');
			if(!$backdrop.length){
				page.sortkey = new $.zui.ModalTrigger({
					title: "系统快捷方式",
					custom: laytpl('sortkey.html').render({
					})
				});
				page.sortkey.show();			
			}
		});
		
		$('#data-edit').on('click', function(){
			var that = $(this);
				
			// 创建iframe弹出框
			window['$dataDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '修改个人资料',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 620,
				iframe: utils.formatByJson('./user_personaldata_edit.html')
			});
			// 显示弹出框
			$dataDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$dataDialog.close(); // 关闭弹出框
			});
		});
		
		$('#pwd-edit').on('click', function(){
			var that = $(this);
				
			// 创建iframe弹出框
			window['$pwdDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '修改密码',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 550,
				height: 400,
				iframe: utils.formatByJson('./modify_pwd.html')
			});
			// 显示弹出框
			$pwdDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$pwdDialog.close(); // 关闭弹出框
			});
		});
		
		window['$Messager'] = function(text){
			new $.zui.Messager(text, {
			    type: 'success',
			    placement: 'center'
			}).show();
		};
	};
	
	/**
	 * 更改菜单状态
	 */
	PageScript.prototype.changeMenuState = function(fid){
		if(fid){
			$('#side-menu .J_menuItem').each(function(){
				var $this = $(this),
					did = $this.data('id'),
					$navMenu = $this.parent().parent().prev()
				if(did == fid){
					$('#side-menu .J_menuItem').removeClass('active');
					$this.addClass('active');
					
					$navMenu.parent().removeClass('active');
					$navMenu.click();
				}
			});
		} else {
			$('#side-menu .J_menuItem').removeClass('active');
		}
	};
	
	window['$Dialog'] = function(ops){
		ops = $.extend({
			title: '数据审核历史',
			width: 1200,
			height: 650,
			page: '',
			data: {}
		}, ops);
		window['hisAjaxData'] = ops.data;
		return new $.zui.ModalTrigger({
			name: 'hisDialogFrame',
			title: ops.title,
			backdrop: 'static',
			moveable: false,
			waittime: consts.PAGE_LOAD_TIME,
			width: ops.width,
			height: ops.height,
			iframe: consts.WEB_BASE + ops.page
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});