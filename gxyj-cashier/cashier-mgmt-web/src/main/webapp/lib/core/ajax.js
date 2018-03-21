/**
 * ##ajax模块，包含一些抽象的与后台交互的方法##
 * @module core/ajax
 * @author yisin
 */
define(function(require, exports, module) {
	
	$.$$ajax = $.ajax;
    
    var ajax = {        
        /**
         * **标准ajax接口，用以获取后台数据**
         *
         * 使用方法：
         * ```javascript
         * ajax.get(param).done(function() {
         *  console.log("done");    //绑定执行成功的回调函数，支持多次调用done绑定多个回调函数
         * }).fail(function() {
         *  console.log("fail");    //绑定执行失败的回调函数，支持多次调用fail绑定多个回调函数
         * }).always(function() {
         *  console.log("complete");    //绑定执行完成的回调函数，支持多次调用always绑定多个回调函数
         * });
         * ```
         * @method get
         *
         * @param {object} param 请求参数
         *   @param {object|object[]} param.req  请求数据
         *  @param {string} [param.url] 服务器地址，默认是require("consts").AJAX_URL
         *  @param {string} [param.reqType] 协议类型，xml或json，默认是json
         *  @param {string} [param.timeout] 超时时间
         * @return {object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
         */
        get: function(param) {
            param.type = "get";
            return this.ajaxRequest(param);
        },

        /**
         * **标准ajax接口，用以获取后台数据**
         *
         * 使用方法：
         * ```javascript
         * ajax.post(param).done(function() {
         *  console.log("done");    //绑定执行成功的回调函数，支持多次调用done绑定多个回调函数
         * }).fail(function() {
         *  console.log("fail");    //绑定执行失败的回调函数，支持多次调用fail绑定多个回调函数
         * }).always(function() {
         *  console.log("complete");    //绑定执行完成的回调函数，支持多次调用always绑定多个回调函数
         * });
         * ```
         * @method post
         *
         * @param {object} param 请求参数
         *   @param {object|object[]} param.req  请求数据
         *  @param {string} [param.url] 服务器地址，默认是require("consts").AJAX_URL
         *  @param {string} [param.type] 请求类型，post或get，默认是post
         *  @param {string} [param.reqType] 协议类型，xml或json，默认是json
         *  @param {string} [param.timeout] 超时时间
         * @return {object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
         */
        post: function(param) {
            param.type = "post";
            return this.ajaxRequest(param);
        },

        /**
         * **标准ajax接口，用以删除后台数据**
         *
         * 使用方法：
         * ```javascript
         * ajax.delete(param).done(function() {
         *  console.log("done");    //绑定执行成功的回调函数，支持多次调用done绑定多个回调函数
         * }).fail(function() {
         *  console.log("fail");    //绑定执行失败的回调函数，支持多次调用fail绑定多个回调函数
         * }).always(function() {
         *  console.log("complete");    //绑定执行完成的回调函数，支持多次调用always绑定多个回调函数
         * });
         * ```
         * @method del
         *
         * @param {object} param 请求参数
         *   @param {object|object[]} param.req  请求数据
         *  @param {string} [param.url] 服务器地址，默认是require("consts").AJAX_URL
         *  @param {string} [param.type] 请求类型，post或get，默认是post
         *  @param {string} [param.reqType] 协议类型，xml或json，默认是json
         *  @param {string} [param.timeout] 超时时间
         * @return {object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
         */
        del: function(param) {
            param.type = "delete";
            return this.ajaxRequest(param);
        },

        /**
         * **标准ajax接口，用以修改后台数据**
         *
         * 使用方法：
         * ```javascript
         * ajax.put(param).done(function() {
         *  console.log("done");    //绑定执行成功的回调函数，支持多次调用done绑定多个回调函数
         * }).fail(function() {
         *  console.log("fail");    //绑定执行失败的回调函数，支持多次调用fail绑定多个回调函数
         * }).always(function() {
         *  console.log("complete");    //绑定执行完成的回调函数，支持多次调用always绑定多个回调函数
         * });
         * ```
         * @method put
         *
         * @param {object} param 请求参数
         *   @param {object|object[]} param.req  请求数据
         *  @param {string} [param.url] 服务器地址，默认是require("consts").AJAX_URL
         *  @param {string} [param.type] 请求类型，post或get，默认是post
         *  @param {string} [param.reqType] 协议类型，xml或json，默认是json
         *  @param {string} [param.timeout] 超时时间
         * @return {object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
         */
        put: function(param) {
            param.type = "put";
            return this.ajaxRequest(param);
        },

        /**
         * **跨域调用接口**
         *
         * 使用方法：
         * ```javascript
         * ajax.jsonp(param).done(function() {
         *  console.log("done");    //绑定执行成功的回调函数，支持多次调用done绑定多个回调函数
         * }).fail(function() {
         *  console.log("fail");    //绑定执行失败的回调函数，支持多次调用fail绑定多个回调函数
         * }).always(function() {
         *  console.log("complete");    //绑定执行完成的回调函数，支持多次调用always绑定多个回调函数
         * });
         * ```
         * @method jsonp
         * @param  {object} param 请求参数
         * @return {object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
         */
        jsonp: function(param) {
            return $.$$ajax($.extend({
                url: param.url,
                dataType: "jsonp",
                jsonp: "jsonpcallback"
            }, param));
        },

        /**
         * **jquery原生的ajax方法**
         *
         * 使用方法：
         * ```javascript
         * ajax.ajax(param).done(function() {
         *  console.log("done");    //绑定执行成功的回调函数，支持多次调用done绑定多个回调函数
         * }).fail(function() {
         *  console.log("fail");    //绑定执行失败的回调函数，支持多次调用fail绑定多个回调函数
         * }).always(function() {
         *  console.log("complete");    //绑定执行完成的回调函数，支持多次调用always绑定多个回调函数
         * });
         * ```
         * @method ajax
         * @param  {object} param 请求参数
         * @return {object} **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
         */
        ajax: function(param) {
            return this.ajaxRequest(param);
        },
        
        syncAjax: function(param){
        	param.url = param.url ? (param.tokenUrl ? consts.TOKEN_INFO_URL : consts.WEB_ROOT) + param.url : consts.AJAX_URL;
        	param.async = false;
        	param.timeout = param.timeout || 30000;
        	param.contentType = "application/x-www-form-urlencoded; charset=utf-8";
        	param.processData = false;
        	param.type = param.type || "POST";
        	param.data = param.noMake ? param.data : this.makeJsonRequest(param.data);
        	var authen = "";
        	if(!param.noMake && window['sessionStorage']){
        		authen = sessionStorage.getItem("authen") || "";
        		if(authen){
        			param.headers = {
            			Authorization: authen
            		}
        		}
        	}
        	return $.$$ajax(param);
        },

        /**
         * **通用组装请求参数方法**
         * @method buildParam
         * @param {object} param 请求参数
         * @return {object} ajax调用参数
         */
        buildParam: function(param) {
            return param;
        },

        /**
         * **通用解析服务器返回结果方法**
         * @method resolveParam
         * @param {object} param 请求参数
         * @param {object} defObj **[Deferred Object](http://api.jquery.com/category/deferred-object/)**
         * @return undefined
         */
        resolveParam: function(param, defObj) {
            defObj.resolveWith(this, param);
        }
    };
    return ajax;
});