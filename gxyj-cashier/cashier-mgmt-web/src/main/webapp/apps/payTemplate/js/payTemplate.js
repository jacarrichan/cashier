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
		this.RowData = {};
	}
	
	PageScript.prototype.init = function(){
		window['$loadData'] = this.loadData;
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		$.useModule(['datatable', 'chosen', 'datetimepicker'], function(){
			page.loadData();
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
		if($('#templateName').val().trim() != null && $('#templateName').val().trim()!='' && checkCharacter($('#templateName').val().trim())) {
			alert("查询条件模板名称不能有特殊字符！");
			return ;
		}
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'payTemplate/queryList',
			data: {"templateName":$('#templateName').val().trim(), 
				"terminal" : $('#terminal').val(),
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
					"templateName": obj.templateName,
					"terminal": page.stateModel[obj.terminal],
					"templateUrl": obj.templateUrl,
					"openFlag": page.openModel[obj.openFlag],
					"buttons": (function(){
						var btnHtml = '';
						if(obj.openFlag == 0){
							btnHtml += laytpl('list-btn.tpl').render({
								"class": "btn-open",
								"icon": "icon-play-circle",
								"rightCode":'open',
								"title": "启用"
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
								url: 'payTemplate/openFlag',
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
								url: 'payTemplate/openFlag',
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
		
		
		$('.btn-add').on('click', function(){
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '新增收银台基础模板',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: './add_edit_PayTemplate.html?type=add'
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
		
		$('.search-btn').on('click', function(){
			page.loadData();
		});
		
		$('label.btn-radio').on('click', function(){
			pager.reset();
			setTimeout(page.loadData, 100);
		});
		
		$('#terminal').on('change', function(){
			page.loadData();
		});
		
		$('#openFlag').on('click', function(){
			page.loadData();
		});
		
		$('.clear-btn').on('click', function(){
			$('#templateName').val('');
			$("#terminal").find("option[value='']").attr("selected",true);
			$('#openFlag').click();
		});
	};

	/* 预览 */
	$(document).on('click',".btn-preview", function(){
		var that = $(this),
		$tr = that.parent().parent(),
		rowId = $tr.data('id');
		var obj = page.RowData["row-" + rowId];
		var jsonVal = {
				'isPreview':'true',
				'channelCd': '',
				'mallId': '',
				'terminal': obj.terminal
		};
		if(obj.terminal == '01') {
			window.open( consts.WEB_BASE_URL + obj.templateUrl+"?data=" + encodeURI(utils.toJSON(jsonVal)), "_blank" ,"height=760,width=1300,scrollbars=no,location=no" );
		}
		else if(obj.terminal == '02') { 
			window.open(consts.WEB_BASE_URL + obj.templateUrl+"?data=" + encodeURI(utils.toJSON(jsonVal)), "_blank" ,"height=667,width=365,scrollbars=no,location=no" );
		}
		else if(obj.terminal == '03') { 
			window.open(consts.WEB_BASE_URL + obj.templateUrl+"?data=" + encodeURI(utils.toJSON(jsonVal)), "_blank" ,"height=667,width=365,scrollbars=no,location=no" );
		}
	});
	
	var page = new PageScript();
	page.init();
	return page;
});