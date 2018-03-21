/**
 * ##dict模块##
 * @module core/dict
 * @author yisin
 */
define(function(require, exports, module){
	var startTime = new Date().getTime(),
		runing = false,
		RIGHT = {},
		RIGHTDICT = null,
		RIGHTJSON = null,
		
	right = {
		getRightParam: function(chars){
			var rightId = window["_right_id_"] || parent.window['_right_id_'];
			return (chars || "?") + '_MenuRightId=' + rightId;
		}
	},
	
	_RightObj = {
		checkRight: function(){
			var page = this;
			if(runing){
				return;
			}
			runing = true;
			var params = utils.getUrlParam(),
				rightId = params._MenuRightId;
			window["_right_id_"] = rightId || parent.window['_right_id_'];
			if(!RIGHTDICT && rightId){
				dict.getDict("FuncRightDict").done(function(res, rtn, state, msg){
					if(state){
						RIGHTDICT = [];
						RIGHTJSON = {};
						
						var list = rtn.data.FuncRightDict;
						if(list){
							for(var i = 0; i < list.length; i++){
								RIGHTDICT.push(list[i].value);
								RIGHTJSON[list[i].value] = false;
							}
						}
						page._checkRight(rightId);
					} else if(rightId) {
						runing = false;
						error("数据加载失败，请刷新页面重试或联系管理员");
						page._hidePage();
					} else {
						runing = false;
					}
				}).fail(function(){
					if(rightId) {
						error("数据加载失败，请刷新页面重试或联系管理员");
						page._hidePage();
					}
					runing = false;
				});
			} else if(rightId){
				for(var code in RIGHTJSON){
					RIGHTJSON[code] = false;
				}
				page._checkRight(rightId);
			} else {
				page._setRightBtn([]);
			}
		},
		
		_checkRight: function(rightId){
			var page = this;
			if(RIGHT[rightId]){
				page._setRightBtn(RIGHT[rightId]);
			} else {
				ajax.post({
					url: 'm802/f80203',
					data: {rightId: rightId }
				}).done(function(res, rtn, state, msg){
					if(state){
						RIGHT[rightId] = rtn.data;
						page._setRightBtn(RIGHT[rightId]);
					}
				}).fail(function(){
					if(rightId) {
						error("数据加载失败，请刷新页面重试或联系管理员");
						page._hidePage();
					}
					runing = false;
				});
			}
		},
		
		_setRightBtn: function(rights){
			var obj;
			for(var i = 0; i < rights.length; i++){
				obj = rights[i];
				if(_.include(RIGHTDICT, obj.rightCode)){
					RIGHTJSON[obj.rightCode] = true;
				}
			}
			// 删除没有权限的按钮
			for(var code in RIGHTJSON){
				if(!RIGHTJSON[code]){ // 无权限的按钮删除
					$('[data-right="' + code + '"]').remove();
				} else { // 有权限的按钮，显示出来
					$('[data-right="' + code + '"]').removeClass('hidden');
				}
			}
			
			runing = false;
		},
		
		_hidePage: function(){
			var text = $('.modal-body').text();
			if(!text || text.indexOf('会话已过期') == -1){
				$('body').hide();
			}
		}
	};
	
	$(function(){
		var params = utils.getUrlParam(),
			rightId = params._MenuRightId;
		if(!window["_right_id_"]){
			window["_right_id_"] = rightId || parent.window['_right_id_'];
		}
		
		$('body').on('DOMNodeInserted', function(e) { 
			_RightObj.checkRight();
		}); 
	});
	
    return right;
});