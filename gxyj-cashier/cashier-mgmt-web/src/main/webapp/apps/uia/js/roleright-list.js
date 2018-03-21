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
		this.zTreeObj = "";
		this.oldCheckList = [] ;
		this.rId = "";
	}
	
	PageScript.prototype.init = function(){
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		
		$.useModule(['datatable','ztree' ], function(){
			page.loadData();
		});
		
	  	page.bindEvent();
	};
	
	PageScript.prototype.loadData = function(){
		
		
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'm108/f10801',
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
		var trHtmls = '';
		if(data.numberOfElements && data.content){
			var obj = {};
			page.RowData = {};
			for(var i = 0; i < data.numberOfElements; i++){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  3333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333
				obj = data.content[i][0];
				brchName = data.content[i][1];
				brchId = data.content[i][2];
				obj['brchId'] = brchId;
				obj['brchName'] = brchName;
				page.RowData["row-" + obj.rowId] = obj;
				trHtmls += laytpl('list-tr.tpl').render({
					"index": (i<9?"0":"") + (i + 1),
					"rowId": obj.rowId,
					"roleId": obj.roleId,
					"roleName": obj.roleName || "",
					"brchName": brchName || "",
					"buttons": (function(){
						var btnHtml = '';
						btnHtml += laytpl('list-btn.tpl').render({
							"class": "btn-dist",
							"title": "权限分配",
							"roleId": obj.roleId
						}) + '&nbsp;';
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
		
		// 创建分页条
		pager.create('.pager-box', data);
		var height = $('.result-panel').height();
		$('.result-panel-right').css('height', height < 600 ? 600 : height);
		$('.icon-btn').tooltip();
	};
	
	PageScript.prototype.bindEvent = function(){
		
		window['$Messager'] = function(text){
			new $.zui.Messager(text, {
			    type: 'success',
			    placement: 'center'
			}).show();
		};
		
		//权限分配
		$('.result-panel').on('click', '.btn-dist', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				rId = that.data("id");
			page.rId = rId;
			
			$('.datatable-rows .table-datatable tr').removeClass('choose');
			$tr.addClass('choose');
			
			var zuiLoad = new $.ZuiLoader().show('数据加载中...');
			ajax.post({
				url: 'm119/f11901',
				data: {roleId: page.rId},
				checkSession: false,
				uia:true,
			}).done(function(res, rtn, state, msg){
				if(state){
					page.createTree(rtn.data , rtn.checkList);
					$('.form-buttons').removeClass('hidden');
				} else {
					$Messager('数据加载失败：' + msg);
				}
			}).fail(function(){
				
			}).always(function(){
				zuiLoad.hide();
			});
			
		});
		
		//提交权限数据
		$('.btn-sure').on('click', function(){
			
			page.funRight();
			
			var zuiLoad = new $.ZuiLoader().show('数据加载中...');
			ajax.post({
				url: 'm119/f11902',
				data: {roleId: page.rId , addCode:page.addCode , delCode:page.delCode},
				checkSession: false,
				uia:true,
			}).done(function(res, rtn, state, msg){
				if(state){
					$Messager('保存成功');
				} else {
					$Messager('数据加载失败：' + msg);
				}
			}).fail(function(){
				
			}).always(function(){
				zuiLoad.hide();
			});
			
		});
		
		$('.search-btn').on('click', function(){
			page.loadData();
		});
		
		
	};
	
	PageScript.prototype.getSelectedIds = function(nodes){
		var idarr = [], ids;
		for(var i = 0 ; i< nodes.length ; i++ ){
			if(nodes[i].children && nodes[i].pId){
				idarr.push(nodes[i].pId);
				ids = page.getSelectedIds(nodes[i]);
				ids.length && idarr.push(ids);
			} else if(!nodes[i].children){
				idarr.push(nodes[i].id);
				idarr.push(nodes[i].pId);
			}
		}
		return idarr;
	};
	
	function zTreeOnCheck(event, treeId, treeNode) {
	    if("-"==treeNode.id && !treeNode.checked){
	    	alert("查询权限必须存在，如果不需要，则可以通过不选中上一级菜单来实现");
	    	page.zTreeObj.checkNode(treeNode, true, true);
	    }
	    if("-"!=treeNode.id && treeNode.level == "2" && treeNode.checked){
	    	var node = page.zTreeObj.getNodeByTId(treeNode.parentTId);
	    	if(node){
	    		var childrenNode = page.zTreeObj.getNodesByParam("id","-",node);
	    		if(childrenNode){
	    			page.zTreeObj.checkNode(childrenNode[0], true, true);
	    		}
	    	}
	    }
	};
	
	var setting = {
        check: {
            enable: true,
            chkStyle: "checkbox",
            radioType: "level"
        },
        data: {
            simpleData: {
                enable: true,
                idKey: "id",
    			pIdKey: "pId"
            }
        },
        callback: {
			onCheck: zTreeOnCheck
		}
    };
	
	PageScript.prototype.createTree = function(data, checkList){
		var zNodes = [], oneRight = {} , twoRight = {} ,threeRight = {} ;
		page.oldCheckList = [];
		baseRight = {
			id: 0,
			pId: 0,
			name: '统一收银台' ,
			open: true ,
			checked: true
		};
		zNodes.push(baseRight);
		if(data.length){
			for(var i = 0; i < data.length; i++){
				var obj = {};
				obj = data[i];
				
				var checked = false;
				
				if(checkList.length){
					for(var j = 0; j < checkList.length; j++){
						var checkobj = {};
						checkobj = checkList[j];
						
						if(obj.rightId == checkobj.rightId){
							/*if(obj.rightLevel == "3"){
								page.oldCheckList.push(checkobj.rightId);
							}*/
							page.oldCheckList.push(checkobj.rightId);
							checked = true;
							break;
						}
					}
				}
				if(obj.rightLevel == "1"){
					oneRight = {
						id: obj.rightId,
						pId: obj.parentRightId,
						name: obj.rightName ,
						open: true ,
						checked: checked
					};
					zNodes.push(oneRight);
				}else if(obj.rightLevel == "2"){
					twoRight = {
						id: obj.rightId,
						pId: obj.parentRightId,
						name: obj.rightName ,
						open: true ,
						checked: checked
					};
					zNodes.push(twoRight);
					
					queryRight = {
						id: "-",
						pId: obj.rightId ,
						name: "查询" ,
						open: true ,
						checked: (function(){
							var checked = false;
							if(checkList.length){
								for(var j = 0; j < checkList.length; j++){
									var checkobj = {};
									checkobj = checkList[j];
									if(obj.rightId == checkobj.rightId){
										checked = true;
										break;
									}
								}
							}
							return checked;
						})()
					};
					zNodes.push(queryRight);
				}else if(obj.rightLevel == "3"){
					
					threeRight = {
						id: obj.rightId,
						pId: obj.parentRightId ,
						name: obj.rightName ,
						open: true ,
						checked: checked
					};
					zNodes.push(threeRight);
				}
			}
		}
		page.zTreeObj = $.fn.zTree.init($('#treeDemo'), setting, zNodes);
		
	};
	
	
	PageScript.prototype.funRight = function(){
		var orgRightCode = page.oldCheckList ;
		var newRightCode = [];
		
		var nodes = page.zTreeObj.getCheckedNodes(true);
		
		newRightCode = page.getSelectedIds(nodes);
		var tempRid = {};
		for(var i = 0; i < newRightCode.length; i++){
			tempRid[newRightCode[i]] = "";
		}
		newRightCode = [];
		for(var id in tempRid){
			if(id != '-'){
				newRightCode.push(id);
			}
		}
		
		var addCode = [], delCode = [], id;
		for(var i = 0; i < newRightCode.length; i++){
			id = newRightCode[i];
			if(!_.include(orgRightCode, id)){
				addCode.push(id);
			}
		}
		for(var i = 0; i < orgRightCode.length; i++){
			id = orgRightCode[i];
			if(!_.include(newRightCode, id)){
				delCode.push(id);
			}
		}
		page.addCode = addCode ;
		page.delCode = delCode ;
	}
	
	
	var page = new PageScript();
	page.init();
	
	window.loadData = page.loadData;
	
	return page;
});