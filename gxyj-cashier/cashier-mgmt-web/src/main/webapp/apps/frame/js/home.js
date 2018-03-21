/**
 * 
 */
define(function(require, exports, module) {
	
	function PageScript(){
		
	}
	
	PageScript.prototype.init = function(){
		/*$.useModule(['chart'], function(){
			
			var data = {
			    // labels 数据包含依次在X轴上显示的文本标签
			    labels: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
			    datasets: [{
			        label: "绿队",
			        color: "green",
			        fillColor: "rgba(10, 160, 10, 1)",
			        strokeColor: "rgba(220,220,220,1)",
			        pointColor: "rgba(220,220,220,1)",
			        pointStrokeColor: "#fff",
			        pointHighlightFill: "#fff",
			        pointHighlightStroke: "rgba(10, 160, 10, 1)",
			        data: [2565, 2759, 3680, 2781, 4786, 2827, 3990, 4560, 5630, 4444, 29850, 3366]
			    }, {
			        label: "红队",
			        color: "red",
			        fillColor: "rgba(250, 10, 10, 1)",
			        strokeColor: "rgba(220, 220, 220, 1)",
			        pointColor: "rgba(220, 220, 220, 1)",
			        pointStrokeColor: "#fff",
			        pointHighlightFill: "#fff",
			        pointHighlightStroke: "rgba(250, 10, 10, 1)",
			        // 数据集
			        data: [65, 59, 80, 81, 156, 55, 40, 44, 255, 70, 830, 140]
			    }]
			};

			var options = {}; // 图表配置项，可以留空来使用默认的配置

			var myLineChart = $("#flot-dashboard-chart canvas").barChart(data, options);
			
		});*/
	  	
	  	page.bindEvent();
	};
	
	PageScript.prototype.bindEvent = function(){
		
	};
	
	var page = new PageScript();
	page.init();
	return page;
});