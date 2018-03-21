/**
 * 页面公共脚本
 */
define(function(require, exports, module){
	
	function PageScript(){
		
	}
	
	PageScript.prototype.init = function(){
		
		$.useModuleCaller = function(){
			var $chosens = $('[data-toggle="chosen"]');
			var len = $chosens.length;
			if(len){
				var index = 0;
				var obj = $chosens[index];
				
				startInit(obj);
				
				function startInit(t){
					var that = $(t),
						dictname = that.data("dictname"),
						selected = that.data("selected"),
						disabled = that.data("disabled"),
						deftext = that.data("deftext"),
						success = that.data("success"),
						error = that.data("error");
					if(disabled !== undefined && disabled !== null){
						disabled = disabled.split(',');
					}
					page.initDictSelect({
						select: t,
						dictname: dictname,
						selected: selected,
						deftext: deftext,
						disabled: disabled,
						success: success,
						error: error,
						finish: function(){
							index++;
							if(index <= len - 1){
								obj = $chosens[index];
								startInit(obj);
							}
						}
					});
				}
				
			}
		};
		
		page.changeBreadcrumb();
		
		page.bindEvent();
	};
	
	PageScript.prototype.changeBreadcrumb = function(){
		if(window['sessionStorage']){
			var menuJson = sessionStorage.getItem('_MENU_NAME_JSON');
			if(menuJson){
				menuJson = utils.parseJSON(menuJson);
				var params = utils.getUrlParam(),
				rightId = params._MenuRightId;
				if(!window["_right_id_"]){
					window["_right_id_"] = rightId || parent.window['_right_id_'];
				}
				var nameArr = menuJson[rightId];
				if($('ol.breadcrumb').length && !$('ol.breadcrumb').hasClass('static') && nameArr && nameArr.length == 2){
					$('ol.breadcrumb').html(utils.format('<li>首页</li><li>{0}</li><li class="active">{1}</li>', nameArr[0], nameArr[1]));
				}
				
			}
		}
	};
	
	/**
	 * 初始化字典项select下拉组件
	 * @param ops {
	 *     dictname: 字典名称，必填
	 *     select： select选择器，类选择器/ID选择器/元素选择器，例如：".sex"、"#sex"、"select"、Dom
	 *     selected： String 默认选中项值，可为空
	 *     disabled： String 默认禁用的项，可为空，多项已逗号隔开，",02,03"
	 *     deftext： String 缺省项描述，可为空
	 *     success： String 初始化成功回调函数
	 *     error： String 初始化失败回调函数
	 *     finish： String 初始化完成回调函数（成功与失败都会执行）
	 * }
	 */
	PageScript.prototype.initDictSelect = function(ops){
		if(ops.dictname && ops.select){
			function failed(msg){
				$(ops.select).html('<option value="" disabled selected>'+msg+'</option>').chosen().trigger('chosen:updated');
				ops.error && ops.error();
				ops.finish && ops.finish();
			}
			dict.getDict(ops.dictname).done(function(res, rtn, state, msg){
				if(state){
					var dictArr = rtn.data[ops.dictname];
					if(dictArr){
						var option = utils.parsetpl('<option value="" {disabled}>{desc}</option>', {
								desc: ops.deftext || " ",
								disabled: _.include(ops.disabled, "") ? "disabled" : ""
							}), 
							dict = {};
						for(var  i = 0, k = dictArr.length; i < k; i++){
							dict = dictArr[i];
							option += utils.parsetpl('<option value="{value}" data-text="{desc}" {selected} {disabled}>{desc}</option>', {
								value: dict.value,
								desc: dict.desc,
								selected: ops.selected == dict.value ? "selected" : "",
								disabled: _.include(ops.disabled, dict.value) ? "disabled" : ""
							});
						}
						$(ops.select).html(option)
						.removeAttr("data-toggle").removeAttr("data-dictname")
						.removeAttr("data-selected").removeAttr("data-deftext")
						.chosen().trigger('chosen:updated');
						
						ops.success && ops.success();
						ops.finish && ops.finish();
					} else {
						failed(ops.deftext || " ");
					}
				} else {
					failed("字典项加载失败");
				}
			}).fail(function(e){
				failed(e.statusText);
			});
		} else {
			log.error("参数不正确：", ops);
		}		
	};
	
	/**
	 * 初始化批次号选择组件
	 * @param ops
	 * 		selector 选择器 【必须】
	 * 		type 类型（01费率模块,02保证金模块）【必须】
	 * 		success 加载成功回调函数 
	 * 		change 改变事件回调函数
	 */
	PageScript.prototype.initBatchIdSelect = function(ops){
		// 检查参数
		ops = ops || {};
		if(!ops.selector){
			error("批次编号选择列表组件初始化失败：参数错误");
			log.error("批次编号选择列表组件失败，参数不符合要求，请检查：", ops);
			return;
		}
		
		_loadBatchIdList(ops);
	};
	
	PageScript.prototype.initCategorySelect = function(ops){
		// 检查参数
		ops = ops || {};
		if(!ops.firstSelector || !ops.secondSelector){
			error("类目选择列表组件初始化失败：参数错误");
			log.error("初始化类目选择列表组件失败，参数不符合要求，请检查：", ops);
			return;
		}
		
		_loadFirstCategory(ops);		
	};
	
	PageScript.prototype.initMallInfoSelect = function(ops){
		// 检查参数
		var uinfo = ajax.getReqHead().USER_INFO;
		ops = $.extend({
			hasAll: false,
			selectValue: uinfo.MALL_ID ? uinfo.MALL_ID : ''
		}, ops);
		if(!ops.selector){
			error("平台选择列表组件初始化失败：参数错误");
			log.error("初始化平台选择列表组件失败，参数不符合要求，请检查：", ops);
			return;
		}
		
		_loadMallList(ops);
	};
	
	PageScript.prototype.initStoreSelect = function(ops){
		// 检查参数
		ops = ops || {};
		if(!ops.selector){
			error("店铺选择列表组件初始化失败：参数错误");
			log.error("初始店铺台选择列表组件失败，参数不符合要求，请检查：", ops);
			return;
		}
		
		_loadStoreList(ops);
	};
	
	PageScript.prototype.initStoreCategorySelect = function(ops){
		// 检查参数
		ops = ops || {};
		if(!ops.storeSelector || !ops.firstSelector || !ops.secondSelector){
			error("店铺类目选择列表组件初始化失败：参数错误");
			log.error("初始化店铺类目选择列表组件失败，参数不符合要求，请检查：", ops);
			return;
		}
		
		_loadStoreCategoryAndList(ops);
	};
	
	/**
	 * 加载店铺列表并加载店铺下的类目
	 */
	function _loadStoreCategoryAndList(ops, sid){
		var $store = $(ops.storeSelector),
			$one = $(ops.firstSelector);
		$store.empty().append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
			code: '',
			name: '正在加载...',
			attr: 'selected'
		})).trigger('chosen:updated');
		
		ajax.post({
			url: 'm127/f12702',
			data: {}
		}).done(function(res, rtn, state, message){
			if(state){
				
				$store.empty().append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
					code: '',
					name: '全部',
					attr: 'selected'
				}));
				var attr = '', obj;
				for(var i = 0; i < rtn.data.length; i++){
					obj = rtn.data[i];
					$store.append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
						code: obj.storeId,
						name: obj.storeName,
						attr: ops.selectValue == obj.storeId ? 'selected="selected"' : ''
					}));
				}
				$store.trigger('chosen:updated');
				
				setTimeout(function(){
					ops.storeSuccess && ops.storeSuccess($store[0]);
				}, 100);
				
				$one.empty().append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
					code: '',
					name: '全部',
					attr: 'selected'
				})).trigger('chosen:updated');
				
				// 店铺Chosen绑定改变事件
				if($store.data('ac') != 'on'){
					$store.data('ac', 'on');
					$store.on('change', function(){
						var sid = $(this).val();
						ops.storeChange && ops.storeChange($store[0], sid);
						
						if(!sid){
							$one.empty().append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
								code: '',
								name: '全部',
								attr: 'selected'
							})).trigger('chosen:updated');
							return;
						}
						$store.prop('disabled', true).trigger('chosen:updated');
						
						_loadFirstCategory(ops, sid);
					});
				}
			}
		}).fail(function(e){
			console.error(e.path, e.message);
			$store.empty().append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
				code: '',
				name: '加载失败',
				attr: 'selected'
			})).trigger('chosen:updated');
		});
	};
	
	/**
	 * 加载一级类目
	 */
	function _loadFirstCategory(ops, sid){
		var $store = $(ops.storeSelector),
			$one = $(ops.firstSelector),
			$two = $(ops.secondSelector);
		$one.empty().append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
			code: '',
			name: '正在加载...',
			attr: 'selected'
		})).trigger('chosen:updated');
		var ajaxJson = {
			url: 'm103/f10302',
			data: {}
		};
		if(sid){
			ajaxJson = {
				url: 'm126/f12605',
				data: {storeId: sid}
			}
		}
		ajax.post(ajaxJson).done(function(res, rtn, state, message){
			if(state){
				$one.empty().append(laytpl('option.tpl').render({
					code: '',
					name: '全部',
					attr: 'selected'
				}));
				var attr = '';
				for(var i = 0; i < rtn.data.length; i++){
					$one.append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
						code: rtn.data[i].prodCatId,
						name: rtn.data[i].catName,
						attr: ''
					}));
				}
				$one.trigger('chosen:updated');
				setTimeout(function(){
					ops.firstSuccess && ops.firstSuccess($one[0]);
				}, 100);
				
				$two.empty().append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
					code: '',
					name: '全部',
					attr: 'selected'
				})).trigger('chosen:updated');
				ops.secondSuccess && ops.secondSuccess($two[0]);
				
				// 一级类目Chosen绑定改变事件
				if($one.data('ac') != 'on'){
					$one.data('ac', 'on');
					$one.on('change', function(){
						var pid = $(this).val();
						ops.firstChange && ops.firstChange($one[0], pid);
						
						if(!pid){
							$two.empty().append(utils.parsetpl('<option value="{code}" {attr}>{name}</option>', {
								code: '',
								name: '全部',
								attr: 'selected'
							})).trigger('chosen:updated');
							ops.secondSuccess && ops.secondSuccess($two[0]);
							
							return;
						}
						$one.prop('disabled', true).trigger('chosen:updated');
						
						_loadSecondCategory(ops, pid);
					});
				}
				
			} else {
				$one.empty().append(utils.parsetpl('<option value="{code}" {attr}>{name}</option>', {
					code: '',
					name: '加载失败',
					attr: 'selected'
				})).trigger('chosen:updated');
			}
		}).fail(function(e){
			console.error(e.path, e.message);
			$one.empty().append(utils.parsetpl('<option value="{code}" {attr}>{name}</option>', {
				code: '',
				name: '加载失败',
				attr: 'selected'
			})).trigger('chosen:updated');
			
		}).always(function(){
			if(sid){
				$store.prop('disabled', false).trigger('chosen:updated');
			}
		});
	};
	
	function _loadSecondCategory(ops, pid){
		var $one = $(ops.firstSelector),
			$two = $(ops.secondSelector);
		
		$two.empty().append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
			code: '',
			name: '正在加载...',
			attr: 'selected'
		})).trigger('chosen:updated');
		
		ajax.post({
			url: 'm103/f10303',
			data: {"firstId": pid}
		}).done(function(res, rtn, state, message){
			if(state){
				$two.data('pcode', pid);
				$two.empty().html(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
					code: '',
					name: '全部',
					attr: 'selected'
				}));
				var attr = '';
				for(var i = 0; i < rtn.data.length; i++){
					$two.append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
						code: rtn.data[i].prodCatId,
						name: rtn.data[i].catName,
						attr: ''
					}));
				}
				$two.trigger('chosen:updated');
				
				setTimeout(function(){
					ops.secondSuccess && ops.secondSuccess($one[0]);
				}, 100);
				
				// 二级类目Chosen绑定改变事件
				if($two.data('ac') != 'on'){
					$two.data('ac', 'on');
					$two.on('change', function(){
						var pid = $one.val(),
							cid = $(this).val();
						ops.secondChange && ops.secondChange($two[0], pid, cid);
					});
				}
			}
		}).fail(function(e){
			console.error(e.path, e.message);
		}).always(function(){
			$one.prop('disabled', false).trigger('chosen:updated');
		});	
	};
	
	function _loadBatchIdList(ops){
		var $batch = $(ops.selector);
		var statusClass = {
			"01": "green",
			"02": "blue",
			"03": "red",
			"04": "gray"
		};
		ajax.post({
			url: 'm143/f14306',
			data: {"batchType": ops.type}
		}).done(function(res, rtn, state, message){
			if(state){
				$batch.empty();
				var attr = '', obj;
				for(var i = 0; i < rtn.data.length; i++){
					obj = rtn.data[i];
					$batch.append(utils.parsetpl('<option class="{class}" value="{code}" data-text="{name}" data-status="{status}" {attr}>{name}({statusText})</option>', {
						code: obj.batchId,
						name: obj.batchName,
						statusText: consts.CODE.getText('批次号状态', obj.status),
						status: obj.status,
						"class": statusClass[obj.status],
						attr: ops.selectValue ? (ops.selectValue == obj.batchId ? 'selected="selected"' : '') : (obj.status == consts.CODE.BATCHID_STATUS_EFFECTED ? 'selected="selected"' : '')
					}));
				}
				$batch.trigger('chosen:updated');
				
				setTimeout(function(){
					ops.success && ops.success($batch[0]);
				}, 100);
				
				// Chosen绑定改变事件
				if($batch.data('ac') != 'on'){
					$batch.data('ac', 'on');
					$batch.on('change', function(){
						var bid = $(this).val();
						ops.change && ops.change($batch[0], bid);
					});
				}
			}
		}).fail(function(e){
			console.error(e.path, e.message);
		});
	}
	
	function _loadMallList(ops){
		var $mall = $(ops.selector);
		
		ajax.post({
			url: 'm123/f12303',
			data: {}
		}).done(function(res, rtn, state, message){
			if(state){
				$mall.empty();
				if(ops.hasAll){
					$mall.append(utils.parsetpl('<option value="" data-text="全部" {attr}>全部</option>', {
						attr: ops.selectValue ? '' : 'selected="selected"'
					}));
				}
				var attr = '', obj;
				for(var i = 0; i < rtn.data.length; i++){
					obj = rtn.data[i];
					$mall.append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
						code: obj.mallId,
						name: obj.mallName,
						attr: ops.selectValue == obj.mallId ? 'selected="selected"' : ''
					}));
				}
				$mall.trigger('chosen:updated');
				
				setTimeout(function(){
					ops.success && ops.success($mall[0]);
				}, 100);
				
				// Chosen绑定改变事件
				if($mall.data('ac') != 'on'){
					$mall.data('ac', 'on');
					$mall.on('change', function(){
						var bid = $(this).val();
						ops.change && ops.change($mall[0], bid);
					});
				}
			}
		}).fail(function(e){
			console.error(e.path, e.message);
		});
	}
	
	/**
	 * 只加载店铺列表
	 */
	function _loadStoreList(ops){
		var $store = $(ops.selector);
		
		ajax.post({
			url: 'm127/f12702',
			data: {}
		}).done(function(res, rtn, state, message){
			if(state){
				$store.empty();
				var attr = '', obj;
				for(var i = 0; i < rtn.data.length; i++){
					obj = rtn.data[i];
					$store.append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
						code: obj.storeId,
						name: obj.storeName,
						attr: ops.selectValue == obj.storeId ? 'selected="selected"' : ''
					}));
				}
				$store.trigger('chosen:updated');
				
				setTimeout(function(){
					ops.success && ops.success($store[0]);
				}, 100);
				
				// 店铺Chosen绑定改变事件
				if($store.data('ac') != 'on'){
					$store.data('ac', 'on');
					$store.on('change', function(){
						var sid = $(this).val();
						ops.change && ops.change($store[0], sid);
					});
				}
			}
		}).fail(function(e){
			console.error(e.path, e.message);
			$store.empty().append(utils.parsetpl('<option value="{code}" data-text="{name}" {attr}>{name}</option>', {
				code: '',
				name: '加载失败',
				attr: 'selected'
			})).trigger('chosen:updated');
		});
	}
		
	PageScript.prototype.bindEvent = function(){
		// 阻止搜索框按回车就自动提交查询了
		$('form.search-form').on('keydown', function(e){
			var code = e.keyCode || e.which;
			if(code == 13){
				e.preventDefault();
				e.returnValue = false;
				return false;
			}
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});