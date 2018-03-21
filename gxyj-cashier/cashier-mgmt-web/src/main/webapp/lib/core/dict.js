/**
 * ##dict模块##
 * @module core/dict
 * @author yisin
 */
define(function(require, exports, module){
    top.fssDataDict = top.fssDataDict || {};

    return {
        /**
         * **根据数据字典key 获取对应的数据字典项**
         *
         * 使用方法：
         * ```javascript
         *  dict.getDict("INPUT_TYPE", ...).done(function() {
         *  console.log("done");    //绑定执行成功的回调函数，支持多次调用done绑定多个回调函数
         * }).fail(function() {
         *  console.log("fail");    //绑定执行失败的回调函数，支持多次调用fail绑定多个回调函数
         * }).always(function() {
         *  console.log("complete");    //绑定执行完成的回调函数，支持多次调用always绑定多个回调函数
         * });
         * ```
         * @method getDict
         * @param {string} keys 数据字典的key，多个时以数组传递，['', '']
         * @return {Object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
         */
        getDict: function(){
            var dtd = $.Deferred(),//在函数内部，新建一个Deferred对象
            	names = [],
            	dictJson = {}; 
            
            if(arguments.length){
            	var dataName = "";
            	for(var i = 0, k = arguments.length; i < k; i++){
            		dataName = arguments[i];
            		if(dataName){
            			if(top.fssDataDict[dataName]){
            				dictJson[dataName] = top.fssDataDict[dataName];
            			} else {
            				names.push(dataName);
            			}
            		}
            	}
            }
            if(names.length){
                ajax.post({
                    url: "m800/f80003",
                    data: {
                    	"dataNames": names,
                    	"cacheType": "dictCache"
                    }
                }).done(function(res, rtn, state, msg){
                    if(state){
                    	dictJson = $.extend(dictJson, rtn.data);
                    	top.fssDataDict = $.extend(top.fssDataDict, rtn.data);
                    	
                        dtd.resolveWith(this, [res, {data: dictJson}, true, msg]); //改变Deferred对象的执行状态
                    } else {
                        dtd.rejectWith(this, [res, msg]);//改变Deferred对象的执行状态
                    }
                }).fail(function() {
                    dtd.rejectWith(this, arguments);
                });
            } else {
            	dtd.resolveWith(this, [{
					code: consts.CODE.MSG_CODE_000000,
					message: "成功",
					rtn: {data: dictJson}
				}, {data: dictJson}, true, "成功"]); //改变Deferred对象的执行状态
            }
            return dtd.promise(); // 返回promise对象
        },
        
        /**
         * **获取单个字典项**
         *
         * 使用方法：
         * ```javascript
         *  getDictByCode("channel", "channelWeChat").done(function() {
         *  console.log("done");    //绑定执行成功的回调函数，支持多次调用done绑定多个回调函数
         * }).fail(function() {
         *  console.log("fail");    //绑定执行失败的回调函数，支持多次调用fail绑定多个回调函数
         * }).always(function() {
         *  console.log("complete");    //绑定执行完成的回调函数，支持多次调用always绑定多个回调函数
         * });
         * ```
         * @method getDictByCode
         * @param {string} keys 数据字典的key，多个时以数组传递，['', '']
         * @return {Object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
         */
        getDictByCode: function(dataName, dataCode, cache){
        	var dtd = $.Deferred(),//在函数内部，新建一个Deferred对象
	        	names = [],
	        	dictObj = null; 
	        
	        if(dataName && dataCode){
	        	cache = cache === undefined ? true : cache;
	        	if(cache){
	        		if(top.fssDataDict[dataName]){
	    				var dictArr = top.fssDataDict[dataName];
	    				if(dictArr && dictArr.length){
	    					for(var i = 0; i < dictArr.length; i++){
	    						if(dictArr[i].code == dataCode){
	    							dictObj = dictArr[i];
	    							break;
	    						}
	    					}
	    				}
	    			}
	        	}
		        if(!dictObj){
		            ajax.post({
		                url: "m800/f80002",
		                data: {
		                	"dataName": dataName,
		                	"dataCode": dataCode,
		                	"cacheType": "dictCache"
		                }
		            }).done(function(res, rtn, state, msg){
		                if(state){
		                	dictObj = rtn.data;
		                	// 更新缓存
		                	var dictArr = top.fssDataDict[dataName];
		    				if(dictArr){
		    					for(var i = 0; i < dictArr.length; i++){
		    						if(dictArr[i].code == dataCode){
		    							top.fssDataDict[dataName][i] = dictObj;
		    							break;
		    						}
		    					}
		    				} else {
		    					top.fssDataDict[dataName] = [dictObj];
		    				}
		                    dtd.resolveWith(this, [res, {data: dictObj}, true, msg]); //改变Deferred对象的执行状态
		                } else {
		                    dtd.rejectWith(this, [res, msg]);//改变Deferred对象的执行状态
		                }
		            }).fail(function() {
		                dtd.rejectWith(this, arguments);
		            });
		        } else {
		        	dtd.resolveWith(this, [{
						code: consts.CODE.MSG_CODE_000000,
						message: "成功",
						rtn: {data: dictObj}
					}, {data: dictObj}, true, "成功"]); //改变Deferred对象的执行状态
		        }
	        } else {
	        	dtd.rejectWith(this, [{message: "Parameter is Null."}, "参数为空"]);
	        }
	        return dtd.promise(); // 返回promise对象
        },
        
        /**
         * **翻译字典项（将字典项值显示为中文描述）**
         *
         * 使用方法：
         * ```javascript
         *  transDict("channel", "01").done(function() {
         *  console.log("done");    //绑定执行成功的回调函数，支持多次调用done绑定多个回调函数
         * }).fail(function() {
         *  console.log("fail");    //绑定执行失败的回调函数，支持多次调用fail绑定多个回调函数
         * }).always(function() {
         *  console.log("complete");    //绑定执行完成的回调函数，支持多次调用always绑定多个回调函数
         * });
         * ```
         * @method transDict
         * @param {string} dataName 数据字典名称
         * @param {string} dataValue 数据字典值
         * @return {Object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
         */
        transDict: function(dataName, dataValue){
        	var dataDesc = "";
        	var dictArr = null;
        	// 先从缓存中取
        	if(top.fssDataDict[dataName]){
				dictArr = top.fssDataDict[dataName];
			}
        	// 缓存中未取到再去服务器上取
        	if(!dictArr || !dictArr.length){
        		dataDesc = dataValue;
				ajax.syncAjax({
	                url: "m800/f80003",
                    data: {
                    	"dataNames": [dataName],
                    	"cacheType": "dictCache"
                    },
	                success: function(data){
	                	if(data.code == "000000"){
	                		var rtndata = data.rtn.data;
	                		if(rtndata){
	                			top.fssDataDict = $.extend(top.fssDataDict, rtndata);
	                			dictArr = rtndata[dataName];
	                		}
	                	}
	                },
	                error: function(e){
	                	log.error("数据字典项加载失败：" + (e && e.statusText), dataName);
	                }
	            });
			}
        	// 翻译成中文
        	if(dictArr && dictArr.length){
				for(var i = 0, k = dictArr.length; i < k; i++){
					if(dictArr[i].value == dataValue){
						dataDesc = dictArr[i].desc;
						break
					}
				}
			}
        	return dataDesc;
        },

        /**
         * **ajax同步调用，获取到字典数据**
         *
         * @method requestDict
         * @param {string} dictCode 数据字典的key
         * @return {Array} 字典数据
         */
        requestDict : function(){
            var names = [],
        		dictJson = {}; 
	        if(arguments.length){
	        	var dataName = "";
	        	for(var i = 0, k = arguments.length; i < k; i++){
	        		dataName = arguments[i];
	        		if(dataName){
	        			if(top.fssDataDict[dataName]){
	        				dictJson[dataName] = top.fssDataDict[dataName];
	        			} else {
	        				names.push(dataName);
	        			}
	        		}
	        	}
	        }
	        if(names.length){
	            ajax.ajax({
	                async: false,
	                url: "m800/f80001",
	                data: {
	                	"dataNames": names,
	                	"cacheType": "dictCache"
	                },
	                noProcess: true,
	                success: function(data){
	                	if(data.code == "000000"){
	                		var rtndata = data.rtn.data;
	                		dictJson = $.extend(dictJson, rtndata);
	                    	top.fssDataDict = $.extend(top.fssDataDict, rtndata);
	                	}
	                }
	            });
	        }
            return dictJson;
        }
    };
});