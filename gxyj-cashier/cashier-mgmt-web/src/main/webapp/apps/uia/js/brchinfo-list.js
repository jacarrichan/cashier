/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.first = true;
		this.stateText = {
			"00": "已生效",
			"01": "已失效",
			"02": "待生效"
		};
		this.RowData = {};
	}
	
	PageScript.prototype.init = function(){
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		
		$.useModule(['datatable', 'chosen'], function(){
			$('select').chosen();
			
			page.loadData();
		});
		
		
		
	  	page.bindEvent();
	};
	
	PageScript.prototype.loadData = function(){
		
		
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'm105/f10501',
			checkSession: false,
			uia:true,
			data: {queryKey: $('#queryKey').val().trim()},
			pager: pager // 传入分页对象
		}).done(function(res, rtn, state, msg){
			if(state){
				page.renderGrid(rtn.data);
			} else {
				$Messager('数据加载失败：' + msg);
			}
		}).fail(function(){
			
		}).always(function(){
			zuiLoad.hide();
		});
		
	};
	
	PageScript.prototype.renderGrid = function(data){
		var trHtmls = '', obj = {};
		if(data.numberOfElements && data.content){
			for(var i = 0; i < data.numberOfElements; i++){
				obj = data.content[i][0];
				
				ajax.syncAjax({
					url: 'relt/loadMall',
					checkSession: false,
					data: {brchId: obj.brchId},
				}).done(function(res, rtn, state, msg){
					if(res.rtn.data) {
						console.log("obj.brchId=" + obj.brchId +"--mallId--"+res.rtn.data.mallId);
						obj['mallId'] = res.rtn.data.mallId;
						obj['mallName'] = res.rtn.data.mallName;
					} else {
						obj['mallId'] = '';
						obj['mallName'] = '';
					}
				}).fail(function(){
					
				}).always(function(){
				});
				parentBrchName = data.content[i][1];
				obj['parentBrchName'] = parentBrchName;
				page.RowData["row-" + obj.rowId] = obj;
				trHtmls += laytpl('list-tr.tpl').render({
					"index": (i<9?"0":"") + (i + 1),
					"rowId": obj.rowId,
					"brchId": obj.brchId,
					"brchName": obj.brchName || "",
					"parentBrchName": parentBrchName || "",
					"mallName":obj.mallName,
					"buttons": (function(){
						var btnHtml = '';
						if(obj.brchId != "1000") {
							
							btnHtml += laytpl('list-btn.tpl').render({
								"class": "btn-edit",
								"icon": "icon-edit",
								"title": "修改",
								"rightCode": "edit"
							}) + '&nbsp;';
						
							
							btnHtml += laytpl('list-btn.tpl').render({
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
		if(this.first){
			this.first = false;
			$('table.datatable').datatable({
				sortable: true,
				checkable: false
			})
		} else {
			$('table.datatable').datatable('load');
		}
		// 设定导出配置
		var expCig = {
			showExport: false,
			resType: 'array',
			resIndex: [
			{
				index: 0,
				attr: 'brchId'
			},
			{
				index: 0,
				attr: 'brchName'
			},1]
		};
		// 创建分页条
		pager.create('.pager-box', data ,expCig);
		
		$('.icon-btn').tooltip();
	};
	
	PageScript.prototype.bindEvent = function(){
		
		window['$Messager'] = function(text){
			new $.zui.Messager(text, {
			    type: 'success',
			    placement: 'center'
			}).show();
		};
		
		$('.btn-add').on('click', function(){
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '新增机构',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: './brchinfo_addedit.html?type=add'
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
		
		
		$('.result-panel').on('click', '.btn-edit', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			// 创建iframe弹出框
			window['$depositDialog'] = new $.zui.ModalTrigger({
				name: 'submitFrame',
				title: '修改机构信息',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: utils.formatByJson('./brchinfo_addedit.html?type=edit&rowId={rowId}&brchId={brchId}&brchName={brchName}&parentBrchId={parentBrchId}&parentBrchName={parentBrchName}&mallId={mallId}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		// 删除
		$('.result-panel').on('click', '.btn-del', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			page.delRowId = rowId;
			$('#smModal').modal();
		});
		
		$('#smModal').on('click', '.btn-yes', function(){
			if(page.delRowId){
				var zuiLoad = new $.ZuiLoader().show('处理中...');
				ajax.post({
					url: 'm105/f10504',
					uia:true,
					data: {rowId: page.delRowId}
				}).done(function(res, rtn, state, msg){
					if(state){
						alert('删除成功');
						page.loadData();
					} else {
						alert(msg);
					}
				}).fail(function(e, msg){
					alert("删除失败：" + msg);
				}).always(function(){
					zuiLoad.hide();
				});
				$('#smModal').modal('hide');
			}
		});
		
	};
	
	var page = new PageScript();
	page.init();
	
	window.loadData = page.loadData;
	
	return page;
});