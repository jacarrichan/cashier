
//自动消失弹窗提醒
//** 调用方法 **
//** layer_msg.msg("提醒内容",显示时间,消失时间) 
//** 后边两个参数可选  默认： 显示时间：1500ms  消失时间：300ms

;(function(){
	var layer_msg={
		msg:function(msg,showTime,hideTime,opcity){
			
			var showTime = showTime || 1500;
			var hideTime = hideTime || 300;
			var opcity = opcity || 0.6;
			var msg_con=document.createElement("span");
			var t=document.createTextNode(msg);
			msg_con.appendChild(t);
			msg_con.style.cssText="display:block;color:#f40;position: fixed;left: 50%;min-width: 15%;max-width: 30%;padding:20px 50px;top:50%;background: #03b8cf;color: #fff !important;text-align: center;font-size: 14px;z-index: 9393939349;border-radius: 4px;";
			document.body.appendChild(msg_con);
			msg_con.style.margin= -msg_con.clientHeight/2+'px 0 0 '+ -msg_con.clientWidth/2+'px';
			setTimeout(function(){
				autohide(hideTime)
			},showTime)
			function autohide(time){
				var opacity=1;
				var change=setInterval(function(){
					opacity-=0.1;
					msg_con.style.opacity=opacity;
					if(opacity < 0){
						clearInterval(change);
						msg_con.remove(0);
					}
				},time*0.1);
			}
		}
	}
	window.layer_msg=layer_msg;
})();