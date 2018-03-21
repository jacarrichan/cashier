/**
 * WebSocket的二次封装
 */
(function(win){
	
	var DEFAILT_OPTION = {
			success: null,
			error: null,
			close: null,
			message: null,
			debug: false
		},
		options = {},
		jsws,
		JSWebSocket = window.WebSocket || window.MozWebSocket,
		/** 事件绑定及处理 */
		bindEvent = function(ws){
			jsws.onopen = function(e){
				options.success && options.success.call(ws, e);
			};
			
			jsws.onclose = function(e){
				options.close && options.close.call(ws, e);
			};
			
			jsws.onerror = function(e){
				options.error && options.error.call(ws, e);
			};
			
			jsws.onmessage = function(e){
				if(e && e.data){
					options.message && options.message.call(ws, e.data);
				}
			};
		};
	
	win['YSWebSocket'] = function(ops) {
		var _self = this;
			options = $.extend(DEFAILT_OPTION, ops),
			httpUrl = consts.WEB_BASE,
			wsurl = httpUrl.replace('http', 'ws') + "websocket";
			jsws = new JSWebSocket(wsurl);
		
		jsws && bindEvent(_self);
			
		// 发送数据
		_self.send = function(){
			if(arguments.length){
				var req = arguments.length > 1 ? arguments[0] : "msg";
				var data = arguments.length > 1 ? arguments[1] : arguments[0];
				var msg = {
					"req": req,
					"data": data
				};
				if(JSON && JSON.stringify){
					msg = JSON.stringify(msg);
					jsws.send(msg);
				}
			}
		};
		
		_self.close = function(){
			jsws.close();
		};
	};
	
})(window);
