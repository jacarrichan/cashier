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
		this.mallIdSelect ="";
	}
	
	PageScript.prototype.init = function(){
		var param = utils.getUrlParam();
		
		page.user = {};
		ajax.getUserInfo().done(function(user){
			page.user = user;
		});
		var param = utils.getUrlParam();
		page.entity = {};
		page.entity.mallId = page.user.mallId;
		page.entity.allName = '全部平台';
		
		
		page.pageType = param['type'] || 'add';
		if(page.pageType == 'edit'){
			page.entity.allName = '全部';
		} else{
			
			var hasAll = false;
			if(page.user.mallId == '00000000') {
				hasAll = true;
			}
			
			$.useModule(['datatable', 'chosen', 'datetimepicker'], function(){
				$('select').chosen({disable_search_threshold: 10});
				comm.initMallInfoSelect({ 
					selector: '#mallId', 
					success: function(){ 
						this.mallIdSelect  = $("#mallId").find("option[selected='selected']").html();
					}, change: function(e, bid){ 
					} ,
					selectValue:page.entity.mallId,
					hasAll: hasAll,
					allName:page.entity.allName
					
				});
			});
		}
		
		
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
		var terminal = param['terminal'];
		//$("#channelDiv").html("");
		$("#personalType,#companyType,#thirdType").html("");
		ajax.post({
			url: 'payment/queryList',
			data: {"usingStatus": '1',"channelPlatform": terminal}
		  
		}).done(function(data){
			var labelObj = {};
			labelObj.personalPay = [];
			labelObj.companyPay = [];
			labelObj.thirdPay = [];
			var lableData = data.rtn.data;
			for(var i = 0; i< lableData.length; i++){
				if(lableData[i].channelType == "01"){
					labelObj.personalPay.push(lableData[i]);
				}else if (lableData[i].channelType == "02"){
					labelObj.companyPay.push(lableData[i]);
				}else{
					labelObj.thirdPay.push(lableData[i]);
				}
			}
			laytpl('input.tpl').render(labelObj.personalPay, function(html){
				$("#personalType").append(html);
			});
			laytpl('input.tpl').render(labelObj.companyPay, function(html){
				$("#companyType").append(html);
			});
			laytpl('input.tpl').render(labelObj.thirdPay, function(html){
				$("#thirdType").append(html);
			});
		}).always(function(){
			if(page.pageType == 'edit'){
				var checkBox = param['payChannel'];
		        var checkBoxArray = checkBox.split(",");  
		        for(var i=0;i<checkBoxArray.length;i++){  
		            $("input[name='payChannel']").each(
		            function(){  
		                if($(this).val()==checkBoxArray[i]){  
		                    $(this).attr("checked","checked");  
		                }  
		            })  
		        }  
			}
			
		});
		
		
		
		if(page.pageType == 'edit'){
			page.pageTypeX = 'edit';
			this.entity = {
			    rowId : param['rowId'],
			    channelId : param['channelId'],
			    channelName : param['channelName'],
			    mallId : param['mallId'],
			    mallName : param['mallName'],
			    webpageId : param['webpageId'],
			    webpageName : param['webpageName'],
			    modelUrl : param['modelUrl'],
				openFlag : param['openFlag'],
				payChannel : param['payChannel'],
				terminal : param['terminal'],
			};
			var rowId = param['rowId'];
			$("#rowId").val(rowId); 
			var channelId = param['channelId'];
			var channelName = param['channelName'];
			$("#channelId").empty();
			var html_str =  "<option value=\"" + channelId + "\">"+ channelName + "</option>"
			$("#channelId").append(html_str);
			var openFlag = param['openFlag'];
			if(openFlag == 0){
				$('#close').click();
			}if(openFlag == 1){
				$('#open').click();
			}
			$('#open').attr("disabled",true);
			$('#close').attr("disabled",true);
			var mallId = param['mallId'];
			var mallName = param['mallName'];
			$("#mallId").empty();
			var html_strss =  "<option value=\"" + mallId + "\">"+ mallName + "</option>"
			$("#mallId").append(html_strss);
			
			var webpageId = param['webpageId'];
			var webpageName = param['webpageName'];
			$("#webpageId").empty();
			//下啦该渠道下所有页面
			ajax.post({
				url : 'webpageModel/queryListByChannelId',
				data : {
				"channelId" : $('#channelId').val()
			    }
			}).done(function(data) {
					var html_str_page = "";
					for ( var key in data.rtn.data) {
						if(data.rtn.data[key].rowId == webpageId){
							html_str_page += "<option value=\""+ webpageId +"\""+ " selected=\"selected" +"\">"
							 + data.rtn.data[key].pageName + "</option>";
						}else{
						html_str_page += "<option value=\""+ data.rtn.data[key].rowId + "\">"
								 + data.rtn.data[key].pageName + "</option>";
						      }
						}
					$("#webpageId").append(html_str_page);
				  
				});
			page.setFormData();
			//不可编辑
			$('#open').attr("disabled",true);
			$('#close').attr("disabled",true);
			$('#mallId').attr("disabled",true);
			$('#channelId').attr("disabled",true);
			
			
			
			
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
	
	$('#channelId').on('change',function() {
		$("#webpageId").empty();
		$("#webpageId").append("<option value=\"\">请选择</option>");
		ajax.post({
			url : 'webpageModel/queryListByChannelId',
			data : {
			"channelId" : $('#channelId').val()
		    }
		}).done(function(data) {
			var html_str = "";
			for ( var key in data.rtn.data) {
			    html_str += "<option value=\""+ data.rtn.data[key].rowId + "\">"
						 + data.rtn.data[key].pageName + "</option>";
				}
			$("#webpageId").append(html_str);
			$("#modelUrl").val("");
			$("#terminal").val("");
		});
	});
	
	$('#channelId').on('change',function() {
		var channelName = $("#channelId").find("option:checked").text(); 
		$("#channelName").val(channelName);
		
	});
	
	$('#mallId').on('change',function() {
		var mallName = $("#mallId").find("option:checked").text(); 
		$("#mallName").val(mallName);
	});
	
	$('#webpageId').on('change',function() {
		var webpageName = $("#webpageId").find("option:checked").text(); 
		$("#webpageName").val(webpageName);
	});
	
	
    $('#webpageId').on('change', function(){
		$("#modelUrl").val('');
		$("#terminal").val('');
		var rowId = $('#webpageId').val();
		ajax.post({
			url: 'webpageModel/queryDetail',
			data: {"rowId": rowId}
		}).done(function(data){
			var pageUrl = data.rtn.data.pageAddress;
			var terminal = data.rtn.data.terminal;
			$("#modelUrl").val(pageUrl);
			$('#terminal').val(terminal);
			
			//根据页面动态更新
			$("#personalType,#companyType,#thirdType").html("");
			ajax.post({
				url: 'payment/queryList',
				data: {"usingStatus": '1',"channelPlatform": terminal}
			  
			}).done(function(data){
				var labelObj = {};
				labelObj.personalPay = [];
				labelObj.companyPay = [];
				labelObj.thirdPay = [];
				var lableData = data.rtn.data;
				for(var i = 0; i< lableData.length; i++){
					if(lableData[i].channelType == "01"){
						labelObj.personalPay.push(lableData[i]);
					}else if (lableData[i].channelType == "02"){
						labelObj.companyPay.push(lableData[i]);
					}else{
						labelObj.thirdPay.push(lableData[i]);
					}
				}
				
				laytpl('input.tpl').render(labelObj.personalPay, function(html){
					$("#personalType").html(html);
				});
				laytpl('input.tpl').render(labelObj.companyPay, function(html){
					$("#companyType").html(html);
				});
				laytpl('input.tpl').render(labelObj.thirdPay, function(html){
					$("#thirdType").html(html);
				});
			})
			
		});
	});
	
    //全选 
    $("#btn1").click(function(){ 
       $("input[name='payChannel']").attr("checked","true"); 
    });
    //取消选择 
    $("#btn2").click(function(){ 
       $("input[name='payChannel']").removeAttr("checked"); 
       $("#payChannel").val("");
    });
    
    //反选 
   $("#btn3").click(function(){ 
	   $("input[name='payChannel']").each(function(){ 
		   if($(this).attr("checked")){ 
			   $(this).removeAttr("checked"); 
		   } else { 
			   $(this).attr("checked","true"); 
		   } 
	   }); 
    }); 
   
	PageScript.prototype.bindEvent = function(){
		$('.btn-submit').on('click', function(){
			var result = zuiValid('#form').validate();
			if(result){
				var $btn = $(this),
					$form = $('form'),
					data = $form.serializeJson();
				 var payChannel = data.payChannel;
				 if (payChannel == null || payChannel == "") {
					   $('.alert').addClass('alert-warning').text("支付渠道不能为空");
					   $btn.prop('disabled', false);
					   zuiLoad.hide();
					   return false;
				 }
				var uri = 'mallModel/add';
				if(page.pageType == 'edit'){
					uri = 'mallModel/update';
					data = $.extend(page.entity, data);
				}
			   
				var mallName = $("#mallId").find("option:selected").text();
			    var mallId = $("#mallId").val();
				if(mallName == '全部平台' && mallId == ''){
					mallId ='ALL';
					var mallIdData = {mallId: mallId};
					data = $.extend(data, mallIdData);
				}
				
				var mallNameData = {mallName:mallName};
				data = $.extend(data, mallNameData);
				var terminal = $('#terminal').val();
				var channelId = $('#channelId').val();
				var rowId = $('#rowId').val();
				ajax.post({
					url: 'mallModel/checkName',
					data: {
						"mallId": mallId,
						"channelId": channelId,
						"terminal": terminal,
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