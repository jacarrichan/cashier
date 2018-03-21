/**
 * ##模块加载器##
 * board 看板
 * calendar 日历
 * chart 图标
 * chosen 下拉选择框
 * datatable 数据表格
 * datetimepicker 日期选择器
 * droppable 拖动
 * hotkey 热键
 * imgcutter 图片剪切器
 * kindeditor 富文本编辑器
 * migrate 图片放大
 * markdown 
 * array 数组扩展
 * bootbox 弹出框组件
 * clipboard 粘贴板
 * colorpicker 颜色选择器
 * colorset 颜色设置
 * dashboard 
 * imgready 图片加载
 * selectable 数据表格滑动多选组件
 * sortable 数据表格排序组件
 * ueditor 富文本编辑器
 * validate 验证框架
 * ztree 
 * sockjs 
 * websocket 
 *
 * 使用方法：
 * ```javascript
 *
 *  //使用datatable模块
 *  $.useModule(['datatable']);
 * 
 *  //使用datatable 与 calendar 模块
 *  $.useModule(['datatable', 'calendar']);
 * 
 *  //加载模块后执行回调函数
 *  $.useModule(['datatable', 'calendar'], function(){
 *  
 *  });
 *
 * ```
 * @module core
 */
define(function(require, exports, module) {
	var head = document.head || document.getElementsByTagName("head")[0] || document.documentElement;
	$.useModule = function(){
		
		var mods = arguments[0];
		var callback = arguments[1];
		
		if(mods && _.isArray(mods) && mods.length > 0){
			var fileArr = [],
				jsfile, cssfile;
				try{
				
				$.each(mods, function(i, name) {
					
					switch(name){
						
						case 'board': 
							cssfile = getCss('lib/board/zui.board.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/board/zui.board.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'calendar': 
							cssfile = getCss('lib/calendar/zui.calendar.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/calendar/zui.calendar.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'chart': 
							jsfile = getScript('lib/chart/zui.chart.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'chosen': 
							cssfile = getCss('lib/chosen/chosen.css');
							cssfile && fileArr.push(cssfile);
							cssfile = getCss('lib/chosenicons/zui.chosenicons.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/chosen/chosen.js');
							jsfile && fileArr.push(jsfile);
							jsfile = getScript('lib/chosenicons/zui.chosenicons.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'datatable': 
							cssfile = getCss('lib/datatable/zui.datatable.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/datatable/zui.datatable.min.js');
							jsfile && fileArr.push(jsfile);
							break;	
						case 'datetimepicker': 
							cssfile = getCss('lib/datetimepicker/datetimepicker.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/datetimepicker/datetimepicker.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'droppable': 
							jsfile = getScript('lib/droppable/droppable.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'hotkey': 
							jsfile = getScript('lib/hotkey/hotkey.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'imgcutter': 
							cssfile = getCss('lib/imgcutter/zui.imgcutter.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/imgcutter/zui.imgcutter.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'kindeditor': 
							cssfile = getCss('lib/kindeditor/kindeditor.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/kindeditor/kindeditor.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'migrate': 
							jsfile = getScript('lib/migrate/migrate1.2.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'array': 
							jsfile = getScript('lib/array/zui.array.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'bootbox': 
							cssfile = getCss('lib/bootbox/bootbox.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/bootbox/bootbox.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'chosenicons': 
							cssfile = getCss('lib/chosenicons/zui.chosenicons.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/chosenicons/zui.chosenicons.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'clipboard': 
							jsfile = getScript('lib/clipboard/clipboard.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'colorpicker': 
							cssfile = getCss('lib/colorpicker/zui.chosenicons.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/colorpicker/zui.colorpicker.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'colorset': 
							jsfile = getScript('lib/colorset/colorset.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'dashboard': 
							cssfile = getCss('lib/dashboard/zui.dashboard.min.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/dashboard/zui.dashboard.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'imgready': 
							jsfile = getScript('lib/imgready/imgready.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'selectable': 
							jsfile = getScript('lib/selectable/zui.selectable.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'sortable': 
							jsfile = getScript('lib/selectable/zui.sortable.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'ueditor': 
							cssfile = getCss('lib/ueditor/ueditor.min.css');
							cssfile && fileArr.push(cssfile);
							break;
						case 'yswebsocket': 
							jsfile = getScript('lib/websocket/yswebsocket-1.0.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'markdown': 
							jsfile = getScript('lib/markdown/remarkable.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'validate': 
							jsfile = getScript('lib/jquery/jquery.validate.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'ztree': 
							cssfile = getCss('lib/ztree/style/ztree.css');
							cssfile && fileArr.push(cssfile);
							
							jsfile = getScript('lib/ztree/ztree.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'sockjs': 
							jsfile = getScript('lib/websocket/sockjs/sockjs.min.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'websocket': 
							jsfile = getScript('lib/websocket/websocket.js');
							jsfile && fileArr.push(jsfile);
							break;
						case 'echarts': 
							jsfile = getScript('lib/echarts/echarts.min.js');
							jsfile && fileArr.push(jsfile);
							break;
					}
					
				});
				
			}catch(e){
				console.error("useModule 1：", e);
			}
			$.loadScriptCss(fileArr, callback);
			
		}
		
	};
	
	function getScript(path){
		var result = null;
		$('script').each(function(i, scr){
			var url = scr.src.substring(scr.src.indexOf("://") + 3);
			if(url.indexOf(path) == -1){
				result = path;
				return result;
			}
		});
		return result;
	}
	
	function getCss(path){
		var result = null;
		$('link').each(function(i, scr){
			var url = scr.href.substring(scr.href.indexOf("://") + 3);
			if(url.indexOf(path) == -1){
				result = path;
				return result;
			}
		});
		return result;
	}
	
	function resole(path){
		return consts.WEB_BASE + path + "#";
	}
	
	function addOnload(node, callback) {
        var supportOnload = "onload" in node;

        if (supportOnload) {
            node.onload = onload;
            node.onerror = function() {
                onload();
            }
        }
        else {
            node.onreadystatechange = function() {
                if (/loaded|complete/.test(node.readyState)) {
                    onload();
                }
            }
        }

        function onload() {
            // Ensure only run once and handle memory leak in IE
            node.onload = node.onerror = node.onreadystatechange = null;
            // Remove the script to reduce memory leak
            head.removeChild(node);
            // Dereference the node
            node = null;
            callback();
        }
    }
	
	$.loadScriptCss = function(){
		if(arguments.length > 0){
			var fileArr = arguments[0];
			var callback = arguments[1];
			var all = 0, node = null, loadCount = 0;
			for(var i = 0, k = fileArr.length; i < k; i++){
				if(fileArr[i].substring(fileArr[i].length - 4) == '.css'){
					$("<link>").attr({ rel: "stylesheet", href: resole(fileArr[i]) }).appendTo("head");
				} else if(fileArr[i].substring(fileArr[i].length - 3) == '.js'){
					//$("<script>").attr({src: resole(fileArr[i]) }).appendTo("head");
					node = document.createElement("script");
					node.async = true;
			        node.src = resole(fileArr[i]);
			        addOnload(node, function(){
			        	loadCount++;
			        });
			        head.appendChild(node);
			        all++;
				}
			}
			var inter = setInterval(function(){
				if(all == loadCount && callback && _.isFunction(callback)){
					clearInterval(inter);
					callback.call(window);
					$.useModuleCaller && $.useModuleCaller.call(window);
				}
			}, 10);
		}
	};
	
	// 禁用IE的兼容模式
	if(!$('meta[http-equiv="X-UA-Compatible"]').length){
		var meta = document.createElement("meta");
		meta.setAttribute("http-equiv", "X-UA-Compatible");
		meta.setAttribute("content", "IE=edge");
		head.appendChild(meta);
	}
	
});