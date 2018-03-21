/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.firstLoad = true;
		this.stateModel = {
				"00":"维护中",
				"01":"维护完成",
		};
		this.RowData = {};
		this.pageType = 'list';
		this.channelId = null;
	}
	
	PageScript.prototype.init = function(){
		window['$loadData'] = this.loadData;
		
		var param = utils.getUrlParam();
		page.pageType = param['type'] || "add";
		if(page.pageType == 'list'){
			this.channelId = param['channelId'];
			$("#channelName").html(param['channelName']);
		}
		
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		$.useModule(['datatable', 'chosen'], function(){
			page.loadData();
		});
	  	page.bindEvent();
	};
	
	
	PageScript.prototype.loadData = function(){
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'businessvind/channelPage',
			data: {
				"channelId": page.channelId,
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
					"procState":(function(){
						var label = '';
						if(obj.procState == '00') {
							label = laytpl('label.tpl').render({
								value: page.stateModel[obj.procState], 
								flag: 'btn-warning'
							});
						}
						if(obj.procState == '01') {
							label = laytpl('label.tpl').render({
								value: page.stateModel[obj.procState], 
								flag: 'label-success'
							});
						}
						return label;
					})() ,
					
					"beDate": (function(){
						var endStr = utils.formatDate(obj.endDate, "yyyy-MM-dd");
						if(obj.endDate == null || obj.endDate == "") {
							endStr = "--";
						}
						return utils.formatDate(obj.beginDate,"yyyy-MM-dd") + "&nbsp;至&nbsp;" + endStr;
					})(),
					
					"vindExplain": obj.vindExplain ,
					
					"closedTime": utils.formatDate(obj.closedTime, "yyyy-MM-dd hh:mm:ss") ,
					
					"buttons": (function(){
						var btnHtml = '';
						
						// 根据启用状态显示图标颜色
						var list_btn_tpl_ioc="i";
						var list_btn_tpl_title="";
						if(obj.usingStatus == '0') {
							list_btn_tpl_title="启用";
							list_btn_tpl_ioc="icon-play-circle";
						}
						if(obj.usingStatus == '1') {
							list_btn_tpl_title="关闭";
							list_btn_tpl_ioc = "icon-off";
						}
						
						btnHtml += laytpl('list-btn.tpl').render({ // 启用关闭 图标
							"class": "btn-off",
							"icon": list_btn_tpl_ioc,
							"rightCode": "close",
							"title": list_btn_tpl_title
						}) + '&nbsp;';
						
						if(obj.usingStatus == '0') { // 未启用的记录可以看到重置，修改，删除按钮
							btnHtml += laytpl('list-btn.tpl').render({ // 重置appid和appkey
								"class": "btn-repeat",
								"icon": "icon-repeat",
								"title": "重置appId和Key",
								"rightCode": "changeIdKey"
							}) + '&nbsp;';
							
							btnHtml += laytpl('list-btn.tpl').render({ // 修改图标
								"class": "btn-edit",
								"icon": "icon-edit",
								"title": "修改",
								"rightCode": "edit"
							}) + '&nbsp;';
							
							btnHtml += laytpl('list-btn.tpl').render({ //删除图标
								"class": "btn-del",
								"icon": "icon-trash",
								"title": "删除",
								"rightCode": "delete"
							}) + '&nbsp;';
						}
						
						if(obj.usingStatus == '1') { //启用的记录才会有维护功能
							btnHtml += laytpl('list-btn.tpl').render({ // 进行维护操作
								"class": "btn-cogs",
								"icon": "icon-cogs",
								"title": "进行维护",
								"rightCode": "btnCogs"
							}) + '&nbsp;';
						}
						
						if (obj.usingStatus == '2') { //处于维护状态的记录会有维护关闭图标
							btnHtml += laytpl('list-btn.tpl').render({ 
								"class": "btn-dot-circle",
								"icon": "icon-dot-circle",
								"title": "关闭维护",
								"rightCode": "btnCircle"
							}) + '&nbsp;';
						}
						
						btnHtml += laytpl('list-btn.tpl').render({ // 启用关闭 图标
							"class": "btn-list-alt",
							"icon": "icon-list-alt",
							"rightCode": "close",
							"title": "维护详情"
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
	
	
 
	
	PageScript.prototype.bindEvent = function(){
		   
	};
	
	var page = new PageScript();
	page.init();
	return page;
});