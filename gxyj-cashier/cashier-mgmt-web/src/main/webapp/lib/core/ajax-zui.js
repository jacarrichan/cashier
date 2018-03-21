/**
 * @module core/ajax-zui
 * @see module:core/ajax-zui
 * @author yisin
 */
define(function(require, exports, module) {
    var loginData = (function(){
    		var udata = {};
	    	if(window['sessionStorage']){
	    		udata = sessionStorage.getItem("USER-INFO");
	    		if(udata){
	    			udata = $.parseJSON(udata);
	    		}
	    	}
	    	return udata || {};
    	})(),
        encKey = "",
        ajax = require("./ajax"),
        tempAjaxJson = {},
        ajaxZui = $.extend({}, ajax, {

            /**
             *
             * 使用方法：
             * ```javascript
             * ajax.ajaxRequest(param).done(function() {
             *  console.log("done");    //绑定执行成功的回调函数，支持多次调用done绑定多个回调函数
             * }).fail(function() {
             *  console.log("fail");    //绑定执行失败的回调函数，支持多次调用fail绑定多个回调函数
             * }).always(function() {
             *  console.log("complete");    //绑定执行完成的回调函数，支持多次调用always绑定多个回调函数
             * });
             * ```
             * @method ajaxRequest
             *
             * @param {object} param 请求参数
             *   @param {object|object[]} param.req  请求数据
             *  @param {string} [param.url] 服务器地址，默认是require("consts").AJAX_URL
             *  @param {string} [param.checkSession=true] 是否检查会话，配置false则不检查并且不会传递公共参数到后台
             *  @param {string} [param.noProcess=false] 是否处理返回数据
             *  @param {string} [param.type] 请求类型，post或get，默认是post
             *  @param {string} [param.reqType] 协议类型，xml或json，默认是json
             *  @param {string} [param.timeout] 超时时间
             * @return {object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
             */
            ajaxRequest: function(param) {

                if("object" !== $.type(param)) {
                    throw "请求参数不合法，请检查调用参数！";
                }

                var defObj = $.Deferred(),
                    that = this,

                    proxy = $.proxy(function() {
                        $.$$ajax(that.buildParam(param)).done(function(ansData, status, xhr) {
                        	if(that.isSuccess(ansData)) {
                            	
                        	} else if(that.isNotPermissions(ansData)){
                        		error("权限不足");
                        		log.error("权限不足，请检查代码：", param.url);
                        	}
                            if(param.noProcess) {
                                defObj.resolveWith(this, [ansData]);
                            } else {
                                that.resolveParam(ansData, defObj);
                            }
                        }).fail(function() {
                            defObj.rejectWith(this, arguments);
                        });
                    });

                param.checkSession = (undefined == param.checkSession ? true : param.checkSession);

                if(param.checkSession) {
                	
                    that.session(true).done(function(data) {
                    	
                        if(!that.isSuccess(data)) {
                            if(param.noProcess) {
                                defObj.resolveWith(this, [data]);
                            } else {
                                that.resolveParam(data, defObj);
                            }
                        } else {
                            proxy();
                        }
                    }).fail(function() {
                        defObj.rejectWith(this, arguments);
                    });
                } else {
                    proxy();
                }

                return defObj.promise();
            },
            
            /**
             *
             * **获取会话数据接口**
             *
             * @method session
             * @param {boolean} useCache 是否使用缓存
             * @return {object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
             */
            session: function(useCache) {
                var defObj = $.Deferred(),
                	that = this;
                
                if(useCache && !$.isEmptyObject(loginData)) {
                    defObj.resolveWith(this, [loginData]);
                } else {
                	$.$$ajax({
                		type: 'post',
                        url: '../../base/session', //consts.SESSION_URL,
                        dataType: 'json'
                    }).done(function(data, status, xhr) {
                        if(that.isSuccess(data) && data.rtn.data.userId) {
                            loginData = data;
                            if(window['sessionStorage']){
                	    		udata = sessionStorage.setItem("USER-INFO", utils.toJSON(loginData));
                	    	}
                        }
                        defObj.resolveWith(this, [data]);
                    }).fail(function() {
                        defObj.rejectWith(this, arguments);
                    });
                }
                return defObj.promise();
            },

            /**
             * **获取动态密钥**
             *
             * @method getEncryptKey
             * @param {boolean} useCache 是否使用缓存
             * @return {object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
             */
            getEncryptKey: function(useCache) {
                var defObj = $.Deferred();
                
                if(useCache && encKey) {
                    defObj.resolveWith(this, [encKey]);
                } else {
                	$.$$ajax({
                		type: 'post',
                        url: consts.ENCRYPT_KEY_URL,
                        dataType: 'json'
                    }).done(function(data) {
                        encKey = data.result + "";
                        defObj.resolveWith(this, [encKey]);
                    }).fail(function() {
                        defObj.rejectWith(this, arguments);
                    });
                }

                return defObj.promise();
            },

            /**
             * **组装请求参数方法**
             * @method buildParam
             * @param {object} param 请求参数
             *   @param {object|object[]} param.req  请求数据
             *  @param {string} [param.url] 服务器地址，默认是require("consts").AJAX_URL
             *  @param {string} [param.type] 请求类型，post或get，默认是post
             *  @param {string} [param.reqType] 协议类型，xml或json，默认是json
             *  @param {string} [param.timeout] 超时时间
             * @return {object} ajax调用参数
             */
            buildParam: function(param){
            	var jsonParam = {};
            	if(param.pager) {
            		param.data._pageIndex = param.pager._pageIndex;
            		param.data._pageSize = param.pager._pageSize;
                }
            	var authen = "";
            	if(window['sessionStorage']){
            		authen = sessionStorage.getItem("authen") || "Basic d2ViX2FwcDo=";
            	}
            	var uri = '';
            	var data = '';
            	if(!param.uia) {
            		uri = consts.WEB_ROOT + param.url;
            	}
            	else {
            		uri = consts.TOKEN_INFO_URL + param.url;
            		
            		//param.data = this.makeUiaJsonRequest(param.data);
            	}
            	jsonParam = {
            		headers: {
            			Authorization: authen,        
            		},
                    url: uri ? uri : consts.AJAX_URL,
                    async: true,
                    type: param.type || "POST",
                    dataType: param.dataType || consts.REQ_TYPE_JSON,
                    data: param.noMake ? param.data : this.makeJsonRequest(param.data), //utils.encrypt(, encKey),
                    processData: false,
                    timeout : param.timeout || 30000,
                    contentType: "application/x-www-form-urlencoded; charset=utf-8"
                }
            	if(param.pager){
            		
            		if(tempAjaxJson.url == jsonParam.url && tempAjaxJson.data != jsonParam.data && !param.pager_filter){
            			param.pager._pageIndex = 0;
            			param.data._pageIndex = 0;
            			jsonParam.data = param.noMake ? param.data : this.makeJsonRequest(param.data);
            		}
            		tempAjaxJson = {
            			url: jsonParam.url,
            			data: jsonParam.data
            		};
            		param.pager.data = {
        				url: param.url,
        				data: param.data,
        				uia:param.uia,
        				checkSession:param.checkSession
            		};
            	}
                return jsonParam;
            },

             /**
             * **解析服务器返回结果方法**
             * @method resolveParam
             * @param {object} param 服务器返回结果
             *  @param {array} [param.ANSWERS] 服务器返回结果
             * @param {object} defObj **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
             * @return undefined
             */
            resolveParam: function(ansData, defObj){
                 var answers = ansData ? ansData.rtn : {data: []},
                 	state = this.isSuccess(ansData),
                 	msg = ansData && ansData.message;

                 if (!ansData || !answers) {  //触发fail
                     defObj.rejectWith(this, arguments);
                     return;
                 }

                 defObj.resolveWith(this, [ansData, answers, state, msg]); //触发done
            },

            /***
             * **获取单次ajaxRequest请求调用的公共参数**
             * @method getReqHead
             * @return {object} **公共参数**
             */
            getReqHead: function() {
                return {
                    "USER_INFO": {
                    	"USER_ID": loginData.rtn && loginData.rtn.data.userId,
                    	"BRCH_ID": loginData.rtn && loginData.rtn.data.brchId,
                    	"MALL_ID": loginData.rtn && loginData.rtn.data.mallId,
                    	"RIGHT_ID": window['_right_id_'] || parent.window['_right_id_']
                    }
                };
            },

            /***
             * **生成请求参数json格式**
             * @method makeJsonRequest
             * @param {object} req 请求参数
             * @return {string} **json格式的请求串**
             */
            makeJsonRequest: function(req){
        		req = $.extend(req, this.getReqHead());
            	var data = "jsonValue=" + utils.toJSON(req);            	
            	return encodeURI(encodeURI(data));
            },
            makeUiaJsonRequest: function(req){
        		req = $.extend(req, this.getReqHead());
            	var data = utils.toJSON(req);            	
            	return encodeURI(encodeURI(data));;
            },
            
            /***
             * **生成请求参数json格式**
             * @method makeJsonRequest
             * @param {object} req 请求参数
             * @return {string} **json格式的请求串**
             */
            getUserInfo: function(){
            	var defObj = $.Deferred(),
            		that = this;
                
                this.session(true).done(function(res) {
                	var userInfo = null;
                	if(that.isSuccess(res)){
                		userInfo = res.rtn.data || res.obj || null;
        			}
                    defObj.resolveWith(this, [userInfo]);
                }).fail(function() {
                	defObj.resolveWith(this, [null]);
                });
                return defObj.promise();
            },
            getUserInfoByUserName: function(param){
            	var defObj = $.Deferred(),
            		that = this;
                
                this.sessionByName(true, param).done(function(res) {
                	var userInfo = null;
                	if(that.isSuccess(res)){
                		userInfo = res.obj || null;
        			}
                    defObj.resolveWith(this, [userInfo]);
                }).fail(function() {
                	defObj.resolveWith(this, [null]);
                });
                return defObj.promise();
            },
            sessionByName: function(data, status, xhr) {
                
                var defObj = $.Deferred(),
                	that = this;
                
            	 if(that.isSuccess(data) && status.data.userId) {
                     loginData = data;
                     if(window['sessionStorage']){
         	    		udata = sessionStorage.setItem("USER-INFO", utils.toJSON(loginData));
         	    	}
                 }
                 defObj.resolveWith(this, [data]);
             
                
                /*if(useCache && !$.isEmptyObject(loginData)) {
                    defObj.resolveWith(this, [loginData]);
                } else {
                	this.ajax
                	$.$$ajax({
                		type: 'post',
                        url: consts.TOKEN_INFO_URL + 'm101/f10113', //consts.SESSION_URL,
                        data: this.makeJsonRequest(param.data),
                        dataType: 'json'
                    }).done(function(data, status, xhr) {
                        if(that.isSuccess(data) && data.obj.userId) {
                            loginData = data;
                            if(window['sessionStorage']){
                	    		udata = sessionStorage.setItem("USER-INFO", utils.toJSON(loginData));
                	    	}
                        }
                        defObj.resolveWith(this, [data]);
                    }).fail(function() {
                        defObj.rejectWith(this, arguments);
                    });
                }*/
                return defObj.promise();
            },

            /**
             * **判断请求是否成功**
             * @method isSuccess
             * @param {object} head 请求返回头
             * @return {boolean} **业务执行成功返回true否则返回false**
             */
            isSuccess: function(head) {
                if(head && head["success"]) {
                    return true == head["success"];
                }
                return false;
            },
            /***
             * **判断是否无操作权限**
             * @method isSessionTimeout
             * @param {object} head 请求返回头
             * @return {boolean} **会话超时返回true否则返回false**
             */
            isNotPermissions: function(head) {
            	if(head && head["code"]) {
            		return "888999" == head["code"];
            	}
            	return false;
            },

            /***
             * **判断请求是否会话超时**
             * @method isSessionTimeout
             * @param {object} head 请求返回头
             * @return {boolean} **会话超时返回true否则返回false**
             */
            isSessionTimeout: function(head) {
                if(head && head["code"]) {
                    return "888888" == head["code"];
                }
                return false;
            },
            
            /***
             * **判断请求是否超时**
             * @method isTimeout
             * @param {object} head 请求返回头
             * @return {boolean} **请求超时返回true否则返回false**
             */
            isTimeout: function(head) {
                if(head && head["code"]) {
                    return "000002" == head["code"];
                }
                return false;
            }
            
        });
    return ajaxZui;
});