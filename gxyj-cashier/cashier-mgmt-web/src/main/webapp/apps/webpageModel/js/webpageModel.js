define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.firstLoad = true;
		this.stateModel = {
				"01":"PC",
				"02":"WAP",
				"03":"APP(微支付)",
		};
		this.openModel = {
				"0":"停用",
				"1":"启用",
		};
		this.defautlWebpageModel = {
				"0":"是",
				"1":"否",
		};
		this.RowData = {};
	}
	
	PageScript.prototype.init = function(){
		window['$loadData'] = this.loadData;
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		$.useModule(['datatable', 'chosen', 'datetimepicker'], function(){
			page.loadData();
		});
		
		$("#channelId").empty();
		$("#channelId").append("<option value=\"\">全部</option>");
		ajax.post({
			url: 'business/queryBusiChannelList',
			data: {"channelId": ''}
		}).done(function(data){
			var html_str = "";
			for ( var key in data.rtn.data) {
				html_str += "<option value=\"" +  data.rtn.data[key].rowId + "\">"
						+  data.rtn.data[key].channelName + "</option>";
			}
			$("#channelId").append(html_str);
		});
		
		
	  	page.bindEvent();
	};
	
	
	// value 值是否含有特殊字符
	function checkCharacter(value) {
		var myReg = /[`~!@#$%^&*_+<>{}\/'[\]]/im;
		var f  = myReg.test(value);
        return f;
	}
	
	PageScript.prototype.loadData = function(){
		if($('#pageName').val().trim() != null && $('#pageName').val().trim()!='' && checkCharacter($('#pageName').val().trim())) {
			alert("查询条件页面名称不能有特殊字符！");
			return ;
		}
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'webpageModel/queryList',
			data: {"pageName":$('#pageName').val().trim(), 
				"terminal" : $('#terminal').val(),
				"channelId" : $('#channelId').val(), 
				"openFlag" : $('input[name="openFlag"]:checked').val()
				},
			pager: pager // 传入分页对象
		}).done(function(res, rtn, state, msg){
			if(state){
				page.renderGrid(rtn.data);
			} else {
				alert('数据加载失败：' + msg);
			}
		}).fail(function(){
			
		}).always(function(){
			zuiLoad.hide();
		});
		
	};
	
	PageScript.prototype.renderGrid = function(data){
		var trHtmls = '',
			obj = {};
		if(data.size && data.list){
			page.RowData = {};
			for(var i = 0; i < data.size; i++){
				obj = data.list[i];
				page.RowData["row-" + obj.rowId] = obj;
				trHtmls += laytpl('list-tr.tpl').render({
					"index": (i < 9 ? '0' : '') + (i + 1),
					"rowId": obj.rowId,
					"pageName": obj.pageName,
					"introduction": obj.introduction,
					"channelName": obj.channelName,
					"terminal": page.stateModel[obj.terminal],
					"pageAddress": obj.pageAddress,
					"defautlWebpage": page.defautlWebpageModel[obj.defautlWebpage],
					"templateName": obj.templateName,
					"templateId": obj.templateId,
					"openFlag": page.openModel[obj.openFlag],
					"buttons": (function(){
						var btnHtml = '';
						if(obj.defautlWebpage ==1){
							if(obj.openFlag == 0){
								btnHtml += laytpl('list-btn.tpl').render({
									"class": "btn-open",
									"icon": "icon-play-circle",
									"title": "启用",
									"rightCode":'open'
								}) + '&nbsp;';
							
								btnHtml += laytpl('list-btn.tpl').render({
									"class": "btn-edit",
									"icon": "icon-edit",
									"title": "修改",
									"rightCode":'edit'
								}) + '&nbsp;';
							}
							
							if(obj.openFlag == 1){
								btnHtml += laytpl('list-btn.tpl').render({
									"class": "btn-off",
									"icon": "icon-off",
									"title": "停用",
									"rightCode":'close'
								}) + '&nbsp;';
							}
						}else{
							if(obj.openFlag == 0){
								btnHtml += laytpl('list-btn.tpl').render({
									"class": "btn-open",
									"icon": "icon-play-circle",
									"title": "启用",
									"rightCode":'open'
								}) + '&nbsp;';
							}
							btnHtml += laytpl('list-btn.tpl').render({
								"class": "btn-edit",
								"icon": "icon-edit",
								"title": "修改",
								"rightCode":'edit'
							}) + '&nbsp;';
							
						}
						btnHtml += laytpl('list-btn.tpl').render({
							"class": "btn-preview",
							"icon": "icon-desktop",
							"title": "预览",
						}) + '&nbsp;';
						return btnHtml;
					})()
				});
			}
		}
		
		$('#data-body').html(trHtmls);
		if(page.firstLoad){
			page.firstLoad = false;
			$('table.datatable').datatable({
				sortable: true,
				checkable: false
			})
		} else {
			$('table.datatable').datatable('load');
		}
		// 创建分页条
		pager.create('.pager-box', data);
		$('.icon-btn, .tip-icon').tooltip({html: true});
		
	};
	
	win = null;
	PageScript.prototype.bindEvent = function(){
		
		window['$Messager'] = function(text){
			alert(text);
		};

		//启用
		$('.result-panel').on('click', '.btn-open', function(){
			var status = "";
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
		    $.confirm({
				title: '提示',
				yesText: '确认启用',
				msg: '您正在进行启用操作<br>确认要启用此记录吗？',
				yesClick: function($modal){
					if(rowId){
						var obj = page.RowData["row-" + rowId];
						if(obj){
							
							var zuiLoad = new $.ZuiLoader().show('处理中...');
							ajax.post({
								url: 'webpageModel/openFlag',
								data: {
									"rowId": rowId,
									"openFlag": "1", //0：停用  1:启用
								}
							}).done(function(res, rtn, state, msg){
								if(state){
									alert("启用成功");
									page.loadData();
								} else {
									alert(msg);
								}
							}).fail(function(e, msg){
								alert("重发请求失败：" + msg);
							}).always(function(){
								zuiLoad.hide();
							});
						}
					}
					$modal.modal('hide');
				}
			});
		});
		
		//停用
		$('.result-panel').on('click', '.btn-off', function(){
			var status = "";
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			   
		    $.confirm({
				title: '提示',
				yesText: '确认停用',
				msg: '您正在进行停用操作<br>确认要停用此记录吗？',
				yesClick: function($modal){
					if(rowId){
						var obj = page.RowData["row-" + rowId];
						if(obj){
							var zuiLoad = new $.ZuiLoader().show('处理中...');
							ajax.post({
								url: 'webpageModel/openFlag',
								data: {
									"rowId": rowId,
									"openFlag": "0", //0：停用  1:启用
								}
							}).done(function(res, rtn, state, msg){
								if(state){
									alert("停用成功");
									page.loadData();
								} else {
									alert(msg);
								}
							}).fail(function(e, msg){
								alert("重发请求失败：" + msg);
							}).always(function(){
								zuiLoad.hide();
							});
						}
					}
					$modal.modal('hide');
				}
			});
		});
		
		$('.result-panel').on('click', '.btn-del', function(){
			var status = "";
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			$.confirm({
				title: '提示',
				yesText: '确认删除',
				msg: '您正在进行删除操作<br>确认要删除此记录吗？',
				yesClick: function($modal){
					if(rowId){
						//var obj = page.RowData["row-" + rowId];
						if(obj){
							var zuiLoad = new $.ZuiLoader().show('处理中...');
							ajax.post({
								url: 'webpageModel/delete',
								data: {
									"rowId":obj.rowId
								}
							}).done(function(res, rtn, state, msg){
								if(state){
									alert("删除成功");
									page.loadData();
								} else {
									alert(msg);
								}
							}).done(function() { 
								//showCarouselEntry(obj);
							}).fail(function(e, msg){
								alert("重发请求失败：" + msg);
							}).always(function(){
								zuiLoad.hide();
							});
						}
					}
					$modal.modal('hide');
				}
			});
		});
		
		$('.search-btn').on('click', function(){
			page.loadData();
		});
		
		$('label.btn-radio').on('click', function(){
			pager.reset();
			setTimeout(page.loadData, 100);
		});
		
		$('.btn-add').on('click', function(){
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '新增收银台业务渠道模板',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: './add_edit_WebpageModel.html?type=add'
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
			$('.modal-dialog .close').on('click', function(){
				page.loadData();
			});
		});
		
		$('.result-panel').on('click', '.btn-edit', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '修改信息',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: utils.formatByJson('./add_edit_WebpageModel.html?type=edit&rowId={rowId}&pageName={pageName}&introduction={introduction}&channelName={channelName}&terminal={terminal}&pageAddress={pageAddress}&defautlWebpage={defautlWebpage}&templateId={templateId}&templateName={templateName}&openFlag={openFlag}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		$('#terminal').on('change', function(){
			page.loadData();
		});
		$('#openFlag').on('click', function(){
			page.loadData();
		});
		$('#channelId').on('change', function(){
			page.loadData();
		});
		$('.clear-btn').on('click', function(){
			$("#terminal").find("option[value='']").attr("selected",true);
			$('#pageName').val('');
			$('#openFlag').click();
			$('#channelId').find("option[value='']").attr("selected",true);
		});
		
		
		// 判断是否为ie浏览器
	    function isIEBoswer() { //ie?  
	    	var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
	    	var isIE = !!window.ActiveXObject || "ActiveXObject" in window; //判断是否IE浏览器
	    	var isEdge = userAgent.indexOf("Edge") > -1 && !isIE; //判断是否IE的Edge浏览器
	        if (isIE || isEdge){
	        	  return true;  
	        } else {
	        	 return false;  
	        } 
	    }  
	    var isIe = isIEBoswer();
		/* 预览 */
		$(document).on('click',".btn-preview", function(){
			var that = $(this),
			$tr = that.parent().parent(),
			rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			if(!isIe && obj.terminal == '01'){
				var newWindow = window.open("","_blank","height=760,width=1300,scrollbars=no,location=no");
			}else if(!isIe && obj.terminal != '01') {
				var newWindow = window.open("","_blank","height=667,width=365,scrollbars=no,location=no");
			}
			ajax.post({
				url : "webpageModel/queryArg",
				data: {
					rowId: rowId
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				var jsonVal = {
						'isPreview':'true',
						'channelCd': rtn.channelCd,
						'mallId': '',
						'terminal': rtn.terminal
				};
				if(obj.terminal == '01') { //PC
					if(!isIe){
						newWindow.location = consts.WEB_BASE_URL + rtn.pageAddress+"?data=" + encodeURI(utils.toJSON(jsonVal))
					}else{
						window.open( consts.WEB_BASE_URL + rtn.pageAddress+"?data=" + encodeURI(utils.toJSON(jsonVal)), "_blank" ,"height=760,width=1300,scrollbars=no,location=no" );
					}
				}
				else if(obj.terminal == '02') { // WAP
					if(!isIe){
						newWindow.location = consts.WEB_BASE_URL + rtn.pageAddress+"?data=" + encodeURI(utils.toJSON(jsonVal))
					}else{
						window.open(consts.WEB_BASE_URL + rtn.pageAddress+"?data=" + encodeURI(utils.toJSON(jsonVal)), "_blank" ,"height=667,width=365,scrollbars=no,location=no" );
					}
				}
				else if(obj.terminal == '03') { // APP
					if(!isIe){
						newWindow.location = consts.WEB_BASE_URL + rtn.pageAddress+"?data=" + encodeURI(utils.toJSON(jsonVal))
					}else{
						window.open(consts.WEB_BASE_URL + rtn.pageAddress+"?data=" + encodeURI(utils.toJSON(jsonVal)), "_blank" ,"height=667,width=365,scrollbars=no,location=no" );
					}
				}
			}).fail(function() {	
				if(isIe){
					newWindow.document.body.innerHTML = "";
    				newWindow.document.write('服务器处理异常,请重试！');
				}
			}).always(function() {					
				//console.log("always");
			});
		})
	};
	
	var page = new PageScript();
	page.init();
	return page;
});