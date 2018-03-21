/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.firstLoad = true;
		this.paramTypeModel = {
				"0":"系统参数",
				"1":"业务参数",
		};
		this.paramFlagModel = {
				"0":"无效",
				"1":"有效",
		};
		this.RowData = {};
	}
	
	PageScript.prototype.init = function(){
		window['$loadData'] = this.loadData;
		
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		
		$.useModule(['datatable', 'chosen'], function(){
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
		var hasSpecialCode = $('#paramCode').val().trim() != null && $('#paramCode').val().trim()!='' && checkCharacter($('#paramCode').val().trim());
		var hasSpecialName = $('#paramName').val().trim() != null && $('#paramName').val().trim()!='' && checkCharacter($('#paramName').val().trim());
		var hasSpecialVal = $('#paramValue').val().trim() != null && $('#paramValue').val().trim()!='' && checkCharacter($('#paramValue').val().trim());
		// 判断用户的输入是否有特殊字符，有则查询失败，不发送查询条件
		if(hasSpecialCode) {
			alert("查询条件参数代码不能有特殊字符！");
			return ;
		}
		else if (hasSpecialName) {
			alert("查询条件参数名称不能有特殊字符！");
			return ;
		}
		else if (hasSpecialVal) {
			alert("查询条件参数值不能有特殊字符！");
			return ;
		}
		
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'param/pageInfo',
			data: {
				"paramCode": $('#paramCode').val().trim(),
				"paramName": $('#paramName').val().trim(),
				"paramValue": $('#paramValue').val().trim(),
				"paramType": $("input[name='paramType']:checked").val(),
				"valFlag": $("input[name='valFlag']:checked").val(),
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
					/*"index": (i < 9 ? '0' : '') + (i + 1),*/
					"rowId": obj.rowId,
					"paramCode": obj.paramCode,
					"paramName": obj.paramName,
					"paramValue": obj.paramValue,
					"paramDesc": obj.paramDesc,
					
					"paramType": page.paramTypeModel[obj.paramType],
					
					"valFlag":(function(){
						var label = '';
						if(obj.valFlag == '0') {
							label = laytpl('label.tpl').render({
								value: page.paramFlagModel[obj.valFlag], 
								flag: 'label-danger'
							});
						}
						if(obj.valFlag == '1') {
							label = laytpl('label.tpl').render({
								value: page.paramFlagModel[obj.valFlag], 
								flag: 'label-success'
							});
						}
						return label;
					})() ,
					
					"buttons": (function(){
						var btnHtml = '';
						
						// 根据启用状态显示图标颜色
						var list_btn_tpl_ioc="i";
						var list_btn_tpl_title="";
						if(obj.valFlag == '0') {
							list_btn_tpl_title="启用";
							list_btn_tpl_ioc="icon-play-circle"
						}
						if(obj.valFlag == '1') {
							list_btn_tpl_title="关闭";
							list_btn_tpl_ioc = "icon-off"
						}
						
						btnHtml += laytpl('list-btn.tpl').render({ // 启用关闭 图标
							"class": "btn-off",
							"icon": list_btn_tpl_ioc,
							"rightCode": "close",
							"title": list_btn_tpl_title
						}) + '&nbsp;';
						
						if(obj.valFlag == '0') { // 未启用的记录可以看到的图片
							btnHtml += laytpl('list-btn.tpl').render({ // 修改
								"class": "btn-edit",
								"icon": "icon-edit",
								"title": "修改",
								"rightCode": "edit"
							}) + '&nbsp;';
							
							btnHtml += laytpl('list-btn.tpl').render({ //删除
								"class": "btn-del",
								"icon": "icon-trash",
								"title": "删除",
								"rightCode": "delete"
							}) + '&nbsp;';
						}
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
	
	PageScript.prototype.bindEvent = function(){
		
		window['$Messager'] = function(text){
			alert(text);
		};

		 
		/* 添加功能   事件触发*/
		$('.btn-add').on('click', function(){
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '新增系统参数',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: './param_add_edit.html?type=add'
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
		
		/* 修改功能 事件触发 */
		$('.result-panel').on('click', '.btn-edit', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '修改系统参数',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: utils.formatByJson('./param_add_edit.html?type=edit&rowId={rowId}&paramCode={paramCode}&paramName={paramName}&paramValue={paramValue}&paramDesc={paramDesc}&paramType={paramType}&valFlag={valFlag}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		// 启用状态更改
		$('.result-panel').on('click', '.btn-off', function(){
			var status = "";
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			var confirm_txt = "";
			confirm_txt = (obj.valFlag == "0") ? "开启" : "关闭";
		    $.confirm({
				title: '提示',
				yesText: "确认" + confirm_txt,
				msg: '您正在进行' + confirm_txt + '操作<br>确认要' + confirm_txt + '发布此记录吗？',
				yesClick: function($modal){
					if(rowId){
						if(obj){
							var zuiLoad = new $.ZuiLoader().show('处理中...');
							ajax.post({
								url: 'param/changeStatus',
								data: {
									"rowId": rowId,
									"valFlag": obj.valFlag,
								}
							}).done(function(res, rtn, state, msg){
								if(state){
									alert(msg);
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
		
		/* 删除小图标 触发事件*/
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
						if(obj){
							var zuiLoad = new $.ZuiLoader().show('处理中...');
							ajax.post({
								url: 'param/delete',
								data: {
									"rowId": rowId,
								}
							}).done(function(res, rtn, state, msg){
								if(state){
									alert("删除成功");
									page.loadData();
								} else {
									alert(msg);
								}
							}).done(function() { // 无需操作
								
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
		
		/* 点击重置按钮清空查询条件*/
		$('.clear-btn').on('click', function(){ 
			$('#paramCode').val(''); 
			$('#paramName').val('');
			$("#paramValue").val("");
			$("#btnradio").click();
			$("#btnradio2").click();
			
		});
		
		/* 点击查询按钮触发查询操作 */
		$('.search-btn').on('click', function(){ 
			page.loadData();
		});
		
		/* 选择查询条件的 启用状态 事件 */
		$('label.btn-radio').on('change', function(){
			page.loadData();
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});