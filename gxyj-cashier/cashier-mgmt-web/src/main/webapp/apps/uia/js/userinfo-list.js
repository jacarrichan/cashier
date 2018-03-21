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
		
		this.sexText = {
			"1": "男",
			"2": "女"
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
			url: 'm101/f10101',
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
		var trHtmls = '' ;
		if(data.numberOfElements && data.content){
			var obj = {}, brchName, brchId;
			page.RowData = {};
			for(var i = 0; i < data.numberOfElements; i++){
				obj = data.content[i][0];
				brchName = data.content[i][1];
				brchId = data.content[i][2];
				obj['brchId'] = brchId;
				obj['brchName'] = brchName;
				page.RowData["row-" + obj.rowId] = obj;
				trHtmls += laytpl('list-tr.tpl').render({
					"index": (i<9?"0":"") + (i + 1),
					"rowId": obj.rowId,
					"userId": obj.userId,
					"userName": obj.userName || "",
					"trueName": obj.trueName || "",
					"brchName": brchName || "",
					"email": obj.email || "",
					"telphone": obj.telphone || "",
					"landlinePhone": obj.landlinePhone || "",
					"buttons": (function(){
						var btnHtml = '';
						btnHtml += laytpl('list-btn.tpl').render({
							"class": "btn-edit",
							"icon": "icon-edit",
							"title": "修改",
							"rightCode": "edit"
						}) + '&nbsp;';
						
						if(obj.userName != "admin") {
							
							btnHtml += laytpl('list-btn.tpl').render({
								"class": "btn-del",
								"icon": "icon-trash",
								"title": "删除",
								"rightCode": "delete"
							}) + '&nbsp;';
						}
						
						/*btnHtml += laytpl('list-btn.tpl').render({
							"class": "btn-edit-pwd",
							"icon": "icon-repeat",
							"title": "重置密码",
							"rightCode": "edit"
						}) + '&nbsp;';*/
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
			resIndex: [{
				index: 0,
				attr: 'userId'
			}, {
				index: 0,
				attr: 'userName'
			}, {
				index: 0,
				attr: 'trueName'
			}, 1 ,
			{
				index: 0,
				attr: 'email'
			},{
				index: 0,
				attr: 'telphone'
			},{
				index: 0,
				attr: 'landlinePhone'
			}]
		};
		// 创建分页条
		pager.create('.pager-box', data, expCig);
		
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
				title: '新增用户',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 630,
				iframe: './userinfo_addedit.html?type=add'
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
				title: '修改用户信息',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 630,
				iframe: utils.formatByJson('./userinfo_addedit.html?type=edit&rowId={rowId}&userId={userId}&userName={userName}&trueName={trueName}&userType={userType}&brchId={brchId}&brchName={brchName}&sex={sex}&birthday={birthday}&email={email}&telphone={telphone}&landlinePhone={landlinePhone}&address={address}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
			});
		});
		
		$('.result-panel').on('click', '.btn-edit-pwd', function(){
			
			var that = $(this),
			$tr = that.parent().parent(),
			rowId = $tr.data('id');
			//var obj = page.RowData["row-" + rowId];
			
			$.confirm({
				title: '提示',
				yesText: '确认重置',
				msg: '您正在进行重置密码操作<br>确认要重置此记录吗？',
				yesClick: function($modal){
					if(rowId){
						var obj = page.RowData["row-" + rowId];
						if(obj){
							
							var zuiLoad = new $.ZuiLoader().show('数据加载中...');
							ajax.post({
								url: 'm101/f10116',
								checkSession: false,
								uia:true,
								data: {userId: obj.userId}					
							}).done(function(res, rtn, state, msg){
								if(state){
									alert('重置成功');
								} else {
									
								}
							}).fail(function(e, m){
								log.error(e);
								
							}).always(function(){
								
								zuiLoad.hide();
							});
						}
					}
					$modal.modal('hide');
				}
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
					url: 'm101/f10104',
					checkSession: false,
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