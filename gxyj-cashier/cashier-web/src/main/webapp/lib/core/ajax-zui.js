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
                        //that.getEncryptKey(true).done(function() {
                            $.$$ajax(that.buildParam(param)).done(function(ansData, status, xhr) {
                            	if(that.isSuccess(ansData)) {
	                            	var au = xhr.getResponseHeader("Authorization");
	                            	if(au && au.length > 10 && window['sessionStorage']){
	                            		sessionStorage.setItem("authen", au);
	                            	}
                            	} else if(that.isNotPermissions(ansData)){
                            		error("权限不足");
                            		log.error("权限不足，请检查代码：", param.url);
                            	}
                                if(param.noProcess) {
                                    defObj.resolveWith(this, [ansData]);
                                } else {
                                    that.resolveParam(ansData, defObj);
                                }
                                that.flushAuthen();
                            }).fail(function() {
                            	var arg0 = arguments[0],
                            		res = arg0.responseText ? utils.parseJSON(arg0.responseText) : arg0;
                            	if(arg0 && arg0.status == 401 && arg0.statusText == "Unauthorized"){
                            		res.message = "会话超时";
                            		res.path = param.url;
                            		// 会话超时，需要统一跳转到登录页面
                            		// 这里分两种情况，一种是rightFrame下的页面，一种是弹窗口页面
                            		var level = 0,
                            			getIndexDoc = function(win){
                            			if(win && level < 5){
                            				level++;
                            				if(win['INDEX_DOC']){
                            					return win;
                            				} else {
                            					return getIndexDoc(win.parent);
                            				}
                            			}
                            			return null;
                            		}
                            		var FRAMEWINDOW = window['FRAME_DOC'] && window,
                            			INDEXWINDOW = getIndexDoc(window);
                            		$.confirm({
                            			title: "系统提示",
                            			msg: "因长时间未操作，会话已过期，请重新登录",
                            			showCancel: false,
                            			yesText: '立即登录',
                            			yesClick: function(){
                                    		if(FRAMEWINDOW){
                                    			FRAMEWINDOW.document.location.href = consts.WEB_BASE + "apps/fss/login.html";
                                    		} else {
                                    			if(INDEXWINDOW){
                                    				INDEXWINDOW.document.location.href = consts.WEB_BASE + "apps/fss/login.html";
                                    			}
                                    		}
                            			}
                            		});
                            	}
                                defObj.rejectWith(this, [res]);
                            });
                        /*}).fail(function() {
                            defObj.rejectWith(this, arguments);
                        });*/
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
                    	var arg0 = arguments[0],
                	 	res = arg0.responseText ? utils.parseJSON(arg0.responseText) : arg0;
                        defObj.rejectWith(this, [res]);
                    });
                } else {
                    proxy();
                }

                return defObj.promise();
            },
            
            flushAuthen: function(){
            	var oldTime = sessionStorage.getItem("opera_times"),
            		expires_in = sessionStorage.getItem("expires_in"),
            		refresh_token = sessionStorage.getItem("refresh_token"),
            		newTime = new Date().getTime();
            	//log.info((newTime - oldTime)/1000, expires_in);
            	if((newTime - oldTime)/1000 > (expires_in - 5)){
            		sessionStorage.setItem("opera_times", newTime);
            		/*ajax.syncAjax({
            			url: 'oauth/token',
            			data: "username="+uName+"&password="+ pwd+"&grant_type=password",
            			noMake: true,
            			tokenUrl: true,
            			checkSession: false,
            			headers: {
                			Authorization: refresh_token
                		},
            			success: function(res){
            				if(res && res.access_token){
            					if(window['sessionStorage']){
            	            		sessionStorage.setItem("authen", "Bearer " + res.access_token);
            	            		sessionStorage.setItem("refresh_token", res.refresh_token);
            	            		sessionStorage.setItem("expires_in", res.expires_in);
            	            		sessionStorage.setItem("opera_times", new Date().getTime());
            	            	}
            					document.location.href="./index.html";
            				} else {
            					error("登录失败，请检查用户名或密码是否正确");
            				}
            				
            			},
            			error: function(e, msg){
            				error("登录失败：" + (e.statusText == 'Bad Request' ? '密码错误' : msg));
            			}
            		});*/
            	}
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
                	var authen = "";
                	if(window['sessionStorage']){
                		authen = sessionStorage.getItem("authen") || "Basic d2ViX2FwcDo=";
                	}
                	$.$$ajax({
                		type: 'post',
                        url: consts.SESSION_URL,
                        dataType: 'json',
                        headers: {
                			Authorization: authen,                			
                		},
                    }).done(function(data, status, xhr) {
                        if(that.isSuccess(data) && data.rtn.data.userId) {
                            loginData = data || {
                            	"USER_ID": ""
                            };
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
            	jsonParam = {
            		headers: {
            			Authorization: authen,        
            		},
                    url: param.url ? consts.WEB_ROOT + param.url : consts.AJAX_URL,
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
        				data: param.data
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
                		userInfo = res.rtn.data || null;
        			}
                    defObj.resolveWith(this, [userInfo]);
                }).fail(function() {
                	defObj.resolveWith(this, [null]);
                });
                return defObj.promise();
            },

            /***
             * **生成请求参数xml格式**
             * @method makeXmlRequest
             * @param {object|object[]} req 请求参数
             * @return {string} **xml格式的请求串**
             */
            makeXmlRequest: function(req) {
                return "<?xml version='1.0' encoding='UTF-8'?><requests><![CDATA[" +
                    utils.toJSON({req: req}) +
                "]]></requests>";
            },

            /**
             * **判断请求是否成功**
             * @method isSuccess
             * @param {object} head 请求返回头
             * @return {boolean} **业务执行成功返回true否则返回false**
             */
            isSuccess: function(head) {
                if(head && head["code"]) {
                    return "000000" == head["code"];
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