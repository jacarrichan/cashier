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
	}
	
	PageScript.prototype.init = function(){
		var param = utils.getUrlParam();
		page.pageType = param['type'] || 'add';
		if(page.pageType == 'edit'){
			page.pageTypeX = 'edit';
			
			this.entity = {
				channelId : param['channelId'],
				channelName : param['channelName'],
			};
			
			page.setFormData();
		}
		$("#channelName").html(param['channelName']);
		$("#channelId").val(param['channelId']);
		$.useModule(['datatable', 'chosen', 'datetimepicker'], function(){
			page.datatimepicker();
		});
		
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
	
	
	/* 时间控件初始化*/
	PageScript.prototype.datatimepicker=function(){

		$("#beginDate").datetimepicker({
		    language:  "zh-CN",
		    weekStart: 1,
		    todayBtn:  1,
		    autoclose: 1,
		    todayHighlight: 1,
		    startView: 2,
		    minView: 2,
		    forceParse: 0,
		    format: "yyyy-mm-dd"
		}).on('changeDate',function(e){
			var beginDate = $("#beginDate").val();
			controlDate(beginDate,"");
			
			var endDate=$("#endDate").val(); 
			
			if(compareDay(beginDate, endDate) == -1){
				alert('开始日期大于结束日期');
				$("#beginDate").val("");
				return;
			}
			
		});
		
		$("#endDate").datetimepicker({
		    language:  "zh-CN",
		    weekStart: 1,
		    todayBtn:  1,
		    autoclose: 1,
		    todayHighlight: 1,
		    startView: 2,
		    minView: 2,
		    forceParse: 0,
		    format: "yyyy-mm-dd"
		}).on('changeDate',function(e){
			var endDate = $("#endDate").val();
			controlDate("",endDate);
			
			var beginDate = $("#beginDate").val();
			if(compareDay(beginDate, endDate)==-1){
				alert('结束日期小于开始日期');
				$("#endDate").val("");
				return;
			}
		});
	};
	
  function controlDate(start,end){//控制日期不能超过三个月
	if(start!=''){
		var date=new Date(start);
		var year = date.getFullYear();
		var month = date.getMonth()+3;//加1是当前月加3是3个月
		var day = date.getDate();//当前日期
	//加判断如果月份大于12表示当前月是12月,日期应该是下一年的1月
		if(month > 12){
			month = month-12;
			year = year+1;
		}
		month = getFullMD(month);
		day = getFullMD(day);
		date = year + '-' + month + '-' + day;
		$('#endDate').datetimepicker("setStartDate",start);
		$('#endDate').datetimepicker("setEndDate",date);
	}
	if(end!=''){
		var date = new Date(end);
		var year = date.getFullYear();
		var month = parseInt(date.getMonth()-1);//加1是当前月加-1是3个月
		var day = date.getDate();//当前日期
		//加判断如果月份小于等于0,日期应该是前一年的月份
		if(month<=0){
			month=12+month;
			year=year-1;
		}
		month = getFullMD(month);
		day = getFullMD(day);
		date = year + '-' + month + '-' + day;
		$('#beginDate').datetimepicker("setStartDate",date);
		$('#beginDate').datetimepicker("setEndDate",end);
	}
  }
	
	function getFullMD(num){//获得两位数的月和日       
		if(num.toString().length < 2){        
			num = "0" + num;       
		}        
		return num;      
	}
	
	function compareDay(a,b){//a,b格式为yyyy-MM-dd
		var a1 = a.split("-");
		var b1 = b.split("-");
		var d1 = new Date(a1[0],a1[1],a1[2]);
		var d2 = new Date(b1[0],b1[1],b1[2]);
		
		if(Date.parse(d1)-Date.parse(d2) > 0){//a>b 开始日期大于结束日期
			return-1;
		}
		
		if(Date.parse(d1)-Date.parse(d2) == 0){//a=b开始日期等于结束日期
			return 0;
		}
		
		if(Date.parse(d1)-Date.parse(d2) < 0){//a<b开始日期小于结束日期
			return 1;
		}
	}
	
	//验证 邮箱合法性
	function authEmailS(vals, eindex) {
		if (vals == "" || vals == null){
			alert("没有输入邮箱无法添加!");
			return false;
		}
		var reg = /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/;
		if (!reg.test(vals)) {
			alert("输入邮箱格式不正确!");
			return false;
		}
		
		//检验是否重复
		var array=$("input[dis_ids='dishiddend']");
		for(var i=0; i < array.length; i++){
			var valuess = array[i].getAttribute("value");
			if (eindex !=null && eindex != "") {
				var idx = array[i].parentNode.getAttribute("e_index");
				if (idx == eindex) {
					continue;
				}
			}
			if($.trim(vals)  == $.trim(valuess)){
				alert("邮箱已存在!");
				return false;
			}
		}
		return true;
	}
	
	
	PageScript.prototype.bindEvent = function(){
		/* 新增或修改 点击确定按钮 触发事件 */
		$('.btn-submit').on('click', function(){
			var array = $("div[id^='div_email_']"); //添加的邮箱
			if (array.length < 1) {
				alert("通知人员邮箱不可为空");
				return;
			}
			
			var result = zuiValid('#form').validate();
			
			if(result){
				var $btn = $(this),
					$form = $('form'),
					data = $form.serializeJson();
				
				var uri = 'businessvind/addEdit';
				if(page.pageType == 'edit'){
					uri = 'businessvind/addEdit';
					data = $.extend(page.entity, data);
				}
				$btn.prop('disabled', true);
				var zuiLoad = new $.ZuiLoader().show('数据保存中......');
				ajax.post({
					url: uri,
					data: data					
				}).done(function(res, rtn, state, msg){
					if(state){
							parent.$Messager(msg);
							closeDialog();
							parent.$loadData();
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
			$('form')[0].reset();
			$('#smModal').modal('hide');
		});
		
		 
		
		$('.btn-clear').on('click', function(){
			$('#parentBrchId').val('');
			$('#parentBrchName').val('');
		});
	
	
		var add_email_count = 0;
		// 通知人员输入框后面的加号按钮
		$('#add_email').on('click', function(){
			var vals = $("#emailsTo").val();
			if(!authEmailS(vals, null)){
				return ;
			}
			var html = "<div class=\"form-group col-sm-12\" id=div_email_" + add_email_count + " e_index=\"" + add_email_count + "\">";
			    html += "<label class=\"col-sm-5\"></label>";
			    html += "<input class=\"form-control\" dis_ids=\"dishiddend\" name=\"emailsTo\" value=\"" + vals + "\" /><button  name=\"del_emial\" class=\"btn \" del_value=\""+add_email_count+"\" type=\"button\"><i class=\"icon icon-times\"></i></button>";
			    html += "</div>";
			$(html).insertAfter($(this).parent());
			add_email_count ++;
			$("#emailsTo").val(""); // 清空缓存
			
		});
		
		// 点击邮件的input保存旧数据
		$(document).on('focus', "input[dis_ids='dishiddend']", function(){
			var val = $(this).val();
			$(this).attr("oldVal",val);
		});
		// 失去焦点验证邮件地址
		$(document).on('blur', "input[dis_ids='dishiddend']", function(){
			var val = $(this).val();
			var eindex = $(this).parent().attr("e_index");
			if(!authEmailS(val, eindex)){
				var oldval = $(this).attr("oldVal");
				$(this).val(oldval);
				return ;
			}
		});
		
		
		// 删除添加好的邮箱地址
		$(document).on('click', "button[name='del_emial']", function(){
			var index = $(this).attr("del_value");
			$("#div_email_" + index).remove();
		});
	};
	var page = new PageScript();
	page.init();
	return page;
});