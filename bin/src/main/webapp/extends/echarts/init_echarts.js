/**
 * 构建动态图表
 * @param url   获取后台数据地址
 * @param elem  加载容器
 */
function EconfigAPI(url, countField, elem) {
	$.ajaxSettings.async = false; //同步才能获取数据
	$.post(url, {countField:countField}, function(response) {
		categories = response[0].categories;
		series = response[0].series;
	}, "json");

	//--- 折柱 ---
	var myChart = echarts.init(document.getElementById(elem));
	var option = {
		title : {
			text : '合同金额统计',
			subtext : '测试数据'
		},
		tooltip : {
			trigger : 'axis'
		},
		legend : {
			data : [ '记录数' ]
		},
		toolbox : {
			show : true,
			feature : {
				mark : {
					show : true
				},
				dataView : {
					show : true,
					readOnly : false
				},
				magicType : {
					show : true,
					type : [ 'line', 'bar' ]
				},
				restore : {
					show : true
				},
				saveAsImage : {
					show : true
				}
			}
		},
		calculable : true,
		xAxis : [ {
			type : 'category',
			data : categories
		} ],
		yAxis : [ {
			type : 'value'
		} ],
		series : [
				{
					name : '记录数',
					type : 'bar',
					data : series,
					markPoint : {
						data : [ {
							type : 'max',
							name : '最大值'
						}, {
							type : 'min',
							name : '最小值'
						} ]
					},
					markLine : {
						data : [ {
							type : 'average',
							name : '平均值'
						} ]
					}
				}
				]
	};
	myChart.setOption(option);
}