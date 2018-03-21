/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.firstLoad = true;
		this.stateModel = {
				"0":"关闭",
				"1":"启用",
				"2":"维护中",
		};
		this.statePlatform = { //支付终端 状态表示channelPlatform
				"01":"PC",
				"02":"WAP",
				"03":"APP",
		};
		this.channelTypeModel = { //支付终端 状态表示channelPlatform
				"00":"其它",
				"01":"个人网银",
				"02":"企业网银",
		};
		this.RowData = {};
	}
	
	PageScript.prototype.init = function(){
		window['$loadData'] = this.loadData;
		page.user = {};
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		ajax.getUserInfo().done(function(user){
			page.user = user;
		});
		
		$.useModule(['datatable', 'chosen'], function(){
			//page.loadData();
			
			$('select').chosen({disable_search_threshold: 10});
			
			var hasAll = false;
			if(page.user.mallId == '00000000') {
				hasAll = true;
			}
			
			comm.initMallInfoSelect({ 
				selector: '#mallId', 
				success: function(){ 
					page.loadData(); 
				}, change: function(e, bid){ 
					page.loadData(); 
				} ,
				hasAll:hasAll,
				allName:'全部',
				selectValue:page.user.mallId
			});
			
			comm.initBusiChannelSelect({ 
				selector: '#busiChannelCode', 
				success: function(){ 
					page.loadData();
				}, change: function(e, bid){ 
					page.loadData();
				},
				selectValue:'',
				hasAll:true,
				allName:'全部'
			});
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
		var hasSpecialName = $('#channelName').val().trim() != null && $('#channelName').val().trim()!='' && checkCharacter($('#channelName').val().trim());
		// 判断用户的输入是否有特殊字符，有则查询失败，不发送查询条件
		if (hasSpecialName) {
			alert("查询条件支付渠道名称不能有特殊字符！");
			return ;
		}
		
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'relt/pagelist',
			data: {"channelName":$('#channelName').val().trim(),
				"mallId":$('#mallId').val(),
				"busiChannelCode":$('#busiChannelCode').val()
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
	
	//获得年月日      得到日期oTime  
    function getMyDate(str){  
        var oDate = new Date(str),  
        oYear = oDate.getFullYear(),  
        oMonth = oDate.getMonth()+1,  
        oDay = oDate.getDate(),  
        oHour = oDate.getHours(),  
        oMin = oDate.getMinutes(),  
        oSen = oDate.getSeconds(),  
        oTime = oYear +'-'+ getzf(oMonth) +'-'+ getzf(oDay);//最后拼接时间  
        return oTime;  
    };  
    //补0操作  
    function getzf(num){  
        if(parseInt(num) < 10){  
            num = '0'+num;  
        }  
        return num;  
    }  
    
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
					"busiChannelCode": utils.convertEmpty(obj.busiChannelName)==''?'全部业务渠道':utils.convertEmpty(obj.busiChannelName),
					"channelCode": obj.channelCode,
					"channelName": utils.convertEmpty(obj.channelName),
					"mallName": utils.convertEmpty(obj.mallName),
					"merchantId" : utils.convertEmpty(obj.merchantId), 
					"appId": utils.convertEmpty(obj.appId),
					"privateKey": utils.convertEmpty(obj.privateKey),
					"publicKey" : utils.convertEmpty(obj.publicKey),
					
					"buttons": (function(){
						var btnHtml = '';
							
						btnHtml += laytpl('list-btn.tpl').render({ // 修改图标
							"class": "btn-edit",
							"icon": "icon-edit",
							"title": "修改",
							"rightCode": "edit"
						}) + '&nbsp;';
						
						if(obj.busiChannelCode == 'ALL' && obj.mallId == 'ALL') {
							//不显示删除按钮
						} else {
							
							btnHtml += laytpl('list-btn.tpl').render({ //删除图标
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
		
		$("#channelNameParent").width($("#busiChannelCode").next(".chosen-container").width());
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
				title: '新增支付渠道',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: './mall_payment_add_edit.html?type=add'
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
				title: '修改支付渠道',
				backdrop: 'static',
				moveable: true,
				waittime: consts.PAGE_LOAD_TIME,
				width: 800,
				height: 600,
				iframe: utils.formatByJson('./mall_payment_add_edit.html?type=edit&rowId={rowId}&channelCode={channelCode}&channelName={channelName}&mallId={mallId}&mallName={mallName}&appId={appId}&privateKey={privateKey}&publicKey={publicKey}&merchantId={merchantId}&busiChannelName={busiChannelName}&busiChannelCode={busiChannelCode}', obj)
			});
			// 显示弹出框
			$depositDialog.show();
			// 绑定内部按钮事件
			$('.modal-dialog .btn-sure').on('click', function(){
				$depositDialog.close(); // 关闭弹出框
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
								url: 'relt/delete',
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
			
			$('#channelCode').val('');
			ajax.getUserInfo().done(function(user) {
				
				$('#mallId').val(user.mallId);
				$('#mallId').trigger('chosen:updated');
			});
			$('#busiChannelCode').val('');
			$('#busiChannelCode').trigger('chosen:updated');
			page.loadData();
			 
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