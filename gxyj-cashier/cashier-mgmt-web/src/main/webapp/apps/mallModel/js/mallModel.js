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
		this.mallVal = "";
		this.authority = false;
		this.RowData = {};
	}
	
	PageScript.prototype.init = function(){
		window['$loadData'] = this.loadData;
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		ajax.getUserInfo().done(function(user){
			page.user = user;
		});
		
		$.useModule(['datatable', 'chosen', 'datetimepicker'], function(){
			var hasAll = false;
			if(page.user.mallId == '00000000') {
				page.authority = true; 
				hasAll = true;
			}
			
			$('select').chosen({disable_search_threshold: 10});
			comm.initMallInfoSelect({ 
				selector: '#mallId', 
				success: function(){ 
					page.mallIdSelect = $("#mallId").find("option[selected='selected']").html();
					page.mallVal = $("#mallId").find("option[selected='selected']").val();
					page.loadData();
				}, change: function(e, bid){ 
					page.loadData();
				} ,
				hasAll:hasAll,
				allName:'全部',
				selectValue:page.user.mallId
			});
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
	
	
	PageScript.prototype.loadData = function(){
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'mallModel/queryList',
			data: {
				"channelId":$('#channelId').val(), 
				"mallId" :$('#mallId').val(), 
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
		var trHtmls = '', obj = {};
		if(data.size && data.list){
			page.RowData = {};
			for(var i = 0; i < data.size; i++){
				obj = data.list[i];
				page.RowData["row-" + obj.rowId] = obj;
				trHtmls += laytpl('list-tr.tpl').render({
					"index": (i < 9 ? '0' : '') + (i + 1),
					"rowId": obj.rowId,
					"channelName": obj.channelName,
					"mallName": obj.mallName,
					"webpageName": obj.webpageName,
					"modelUrl": obj.modelUrl,
					"openFlag": page.openModel[obj.openFlag],
					"payChannelName":obj.payChannelName,
					"terminal":page.stateModel[obj.terminal], 
					"buttons": (function(){
						var btnHtml = '';
						if(page.authority == true || (page.authority == false && obj.mallId != 'ALL')){
							if(obj.openFlag == 0){
								btnHtml += laytpl('list-btn.tpl').render({
									"class": "btn-open",
									"icon": "icon-play-circle",
									//"rightCode":'open',
									"title": "启用"
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
								url: 'mallModel/openFlag',
								data: {
									"rowId": obj.rowId ,
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
								url: 'mallModel/openFlag',
								data: {
									"rowId": obj.rowId,
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
				title: '新增地方平台收银模板配置',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: './add_edit_mallModel.html?type=add'
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
				iframe: utils.formatByJson('./add_edit_mallModel.html?type=edit&rowId={rowId}&mallId={mallId}&mallName={mallName}&channelId={channelId}&channelName={channelName}&webpageId={webpageId}&webpageName={webpageName}&payChannel={payChannel}&modelUrl={modelUrl}&terminal={terminal}&openFlag={openFlag}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		$('#mallId').on('change', function(){
			page.loadData();
		});
		$('#channelId').on('change', function(){
			page.loadData();
		});
		
		$('.clear-btn').on('click', function(){
			$('#openFlag').click();
			$("select").not(".pager-chosen").each(function(){
				var defaultOption = $(this).find("option").eq(0).html();
				$(this).find("option").eq(0).attr("selected", true);
				$(this).next(".chosen-container").find(".chosen-single").find("span").html(defaultOption);
				$(this).next(".chosen-container").find(".chosen-single").find("span").attr("title", defaultOption);
				$("#mallId").next(".chosen-container").find(".chosen-single").find("span").html(page.mallIdSelect);
				$("#mallId").next(".chosen-container").find(".chosen-single").find("span").attr("title", page.mallIdSelect);
				$("#mallId").val(page.mallVal);
			})
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
				url : "mallModel/queryArg",
				data: {
					rowId: rowId
				},
				checkSession: false
			}).done(function(res, rtn, state, msg) {
				var jsonVal = {
						'isPreview':'true',
						'channelCd': rtn.channelCd,
						'mallId': rtn.mallId,
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