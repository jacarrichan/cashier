/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'), 
	zuiValid = require('../../../lib/zuiplugin/zui.validate');
	
	function PageScript(){
		this.pageType = 'add';
		this.pageTypeX = 'add';
		this.entity = null;
		this.deafaultTmp="";
	}
	
	PageScript.prototype.init = function(){
		$("#channelId").empty();
		$("#channelId").append("<option value=\"\">请选择</option>");
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
		
		var param = utils.getUrlParam();
		page.pageType = param['type'] || 'add';
		if(page.pageType == 'edit'){
			page.pageTypeX = 'edit';
			this.entity = {
			    rowId : param['rowId'],
				pageName : param['pageName'],
				introduction : param['introduction'],
				channelId : param['channelId'],
				channelName : param['channelName'],
				terminal : param['terminal'],
				pageAddress : param['pageAddress'],
				defautlWebpage : param['defautlWebpage'],
				templateName : param['templateName'],
				templateId : param['templateId'],
				openFlag : param['openFlag'],
			};
			
			page.setFormData();
			
			//赋值
			var terminal = param['terminal'];
			var openFlags = param['openFlag'];
			var defautlWebpages = param['defautlWebpage'];
			if(openFlags == 0){
				$('#close').click();
			}
			if(openFlags == 1){
				$('#open').click();
			}
			if(defautlWebpages == 0){
			   $('#defautlYes').click();
			}
			if(defautlWebpages == 1){
				$('#defautlNo').click();
			}
			page.deafaultTmp = param['templateId'];
			var channelId = param['channelId'];
			var templateName = param['templateName'];
			var channelName = param['channelName'];
			var html_str_channel =  "<option value=\"" + channelId +"\"" +" selected=\"selected"+ "\">"+ channelName + "</option>"
			$("#channelId").append(html_str_channel);
			$('#pageName').data('oldValue', this.entity.pageName);
			//下拉该适应终端下所有页面
			$("#templateId").empty();
			ajax.post({
				url: 'payTemplate/queryListByTerminal',
				data: {"terminal": terminal}
			}).done(function(data){
				var html_str_templateId= "";
				for ( var key in data.rtn.data) {
					if(data.rtn.data[key].rowId == page.deafaultTmp){
						html_str_templateId += "<option value=\""+ page.deafaultTmp +"\""+ " selected=\"selected" +"\">"
						 + data.rtn.data[key].templateName + "</option>";
					}else{
						html_str_templateId += "<option value=\""+ data.rtn.data[key].rowId + "\">"
							 + data.rtn.data[key].templateName + "</option>";
					}
					
				}
				$("#templateId").append(html_str_templateId);
			});
			
			
			
			//不可编辑
			$('#open').attr("disabled",true);
			$('#close').attr("disabled",true);
			$('#terminal').attr("disabled",true);
			$('#channelId').attr("disabled",true);
			$('#defautlYes').attr("disabled",true);
			$('#defautlNo').attr("disabled",true);
		}
	
		zuiValid('#form').validate('init');
	  	page.bindEvent();
	};
	
	PageScript.prototype.setFormData = function(){
		utils.fillFormData('form', this.entity, '#');
	};
	
	var closeDialog = function(){
		parent.$depositDialog.close();
		delete parent.$depositDialog;
	};	
	
	$('#terminal').on('change', function(){
		$("#templateId").empty();
		$("#templateId").append("<option value=\"\">请选择</option>");
		ajax.post({
			url: 'payTemplate/queryListByTerminal',
			data: {"terminal": $('#terminal').val()}
		}).done(function(data){
			var html_str = "";
			for ( var key in data.rtn.data) {
				html_str += "<option value=\"" +  data.rtn.data[key].rowId + "\">"
						+  data.rtn.data[key].templateName + "</option>";
			}
			$("#templateId").append(html_str);
			$("#pageAddress").val("");
		});
	});
	
	
	$('#templateId').on('change', function(){
		$("#pageAddress").val("");
		var templateId = $('#templateId').val();
		ajax.post({
			url: 'payTemplate/queryTemplateUrl',
			data: {"rowId": templateId}
		}).done(function(data){
			var templateUrl = data.rtn.data.templateUrl;
			var templateName =  data.rtn.data.templateName;
			$("#pageAddress").val(templateUrl);
			$("#templateName").val(templateName);
		});
	});
	
	
	$('#channelId').on('change',function() {
		var channelName = $("#channelId").find("option:checked").text(); 
		$("#channelName").val(channelName);
	});
	
	
	
	PageScript.prototype.bindEvent = function(){
		$('.btn-submit').on('click', function(){
			var result = zuiValid('#form').validate();
			if(result){
				var $btn = $(this),
				$form = $('form'),
				data = $form.serializeJson();
				var uri = 'webpageModel/add';
				if(page.pageType == 'edit'){
					uri = 'webpageModel/update';
					data = $.extend(page.entity, data);
				}
				//同一个业务渠道和终端只能有一个默认页面
				var terminal = $('#terminal').val();
				var channelId = data.channelId;
				var defautlWebpage = $('input[name="defautlWebpage"]:checked').val();
				var rowId = data.rowId;
				ajax.post({
					url: 'webpageModel/checkBusiness',
					data: {"terminal": terminal,
						   "channelId":channelId ,
						   "defautlWebpage": defautlWebpage,
						   "rowId": rowId
					}
				}).done(function(res, rtn, state, msg){
					if(state){
						$btn.prop('disabled', true);
						var zuiLoad = new $.ZuiLoader().show('数据加载中...');
						ajax.post({
							url: uri,
							data: data					
						}).done(function(res, rtn, state, msg){
							if(state){
								if(page.pageType == 'add'){
									$('#smModal').modal({
									    keyboard : false,
									    show     : true
									});
								} else {
									parent.$Messager('保存成功');
									closeDialog();
									parent.$loadData();
								}
							} else {
								$('.alert').addClass('alert-warning').text(msg);
							}
						}).fail(function(e, m){
							log.error(e);
							$('.alert').addClass('alert-warning').text(m);
						}).always(function(){
							$btn.prop('disabled', false);
							zuiLoad.hide();
				    });
						
				}else{
				  $('.alert').addClass('alert-warning').text(msg);
				  $btn.prop('disabled', false);
				  zuiLoad.hide();
				  return false;
				}
			})
		  }
			
	   });
		
		$('.btn-cancel').on('click', function(){
			closeDialog();
			parent.$loadData();
		});
		
		
		$('#smModal').on('click', '.btn-no', function(){
			closeDialog();
			parent.$loadData();
		});
		
		$('#smModal').on('click', '.btn-yes', function(){
			//清空
			$('form')[0].reset();
			$('#smModal').modal('hide');
		});
		
		
		$('.btn-clear').on('click', function(){
			$('#parentBrchId').val('');
			$('#parentBrchName').val('');
		});
	};
	
	var page = new PageScript();
	page.init();
	return page;
});