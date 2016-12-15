function initPredictEcharts(elem,xData,yData){
	var myChart = echarts.init(document.getElementById(elem));
	option = {
		    title : {
		        text: '预测值',
		    },
		    tooltip : {
		        trigger: 'axis'
		    },
		    toolbox: {
		        show : true,
		        feature : {
		            mark : {show: true},
		            dataView : {show: true, readOnly: false},
		            magicType : {show: true, type: ['line', 'bar']},
		            restore : {show: true},
		            saveAsImage : {show: true}
		        }
		    },
		    calculable : true,
		    xAxis : [
		        {
		            type : 'category',
		            boundaryGap : false,
		            data : xData
		        }
		    ],
		    yAxis : [
		        {
		            type : 'value',
		            axisLabel : {
		                formatter: '{value} t'
		            }
		        }
		    ],
		    series : yData
		};
	myChart.setOption(option);                    
}

function initEcharts(elem,xData,yData){
	var myChart = echarts.init(document.getElementById(elem));
	option = {
		    title : {
		        text: '每日用水量',
		    },
		    tooltip : {
		        trigger: 'axis'
		    },
		    toolbox: {
		        show : true,
		        feature : {
		            mark : {show: true},
		            dataView : {show: true, readOnly: false},
		            magicType : {show: true, type: ['line', 'bar']},
		            restore : {show: true},
		            saveAsImage : {show: true}
		        }
		    },
		    calculable : true,
		    xAxis : [
		        {
		            type : 'category',
		            boundaryGap : false,
		            data : xData
		        }
		    ],
		    yAxis : [
		        {
		            type : 'value',
		            axisLabel : {
		                formatter: '{value} t'
		            }
		        }
		    ],
		    series : yData
		};
	myChart.setOption(option);                    
}

/**
 * code:"SUCCESS"
data
actualValue:143.4
deviation:"-25.369429%"
id:"5698"
name:"5698"
predictValue:107.02023844858329
time:"2016-09-02 00:00:00"
 * @param elem
 * @returns
 */
function initTable2(elem,tableDatas){
	$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) { $($.fn.dataTable.tables(true)).DataTable().columns.adjust(); });
	var tableDatas=tableDatas;
	var lengthMenu;
	if(elem=='table1'){
		lengthMenu=[4]
		buttons=[]
	}else{
		lengthMenu=[10];
        buttons= [
                  'copy', 'csv', 'excel', 'pdf'
              ];
	}
	
	
	table=$("#"+elem).DataTable({
		dom: 'Bfrt<"pageclass"p><"infoclass"i>',
		buttons:buttons,
        sScrollX: "100%",   //表格的宽度
		scrollX:true,//设置滚动
		scollY:true,//设置滚动
		destroy: true,
		retrieve:true,
		ordering: true,
		searching: true,//开启搜索
        info: false,//是否显示左下角信息
        bLengthChange: true, //改变每页显示数据数量
        autoWidth: true,//自动宽度
		pagingType: "full",
		orderFixed:true,//高亮排序的列
		"lengthMenu": lengthMenu,
		stripeClasses: [ 'strip1', 'strip2' ],
		columns: [
			{ title:"编号","data": "id" , "className": "dt-center"},
			{ title:"预测点","data": "cst_id" , "className": "dt-center" },
			{ title:"预测时间","data": "cDate" , "className": "dt-center" },
			{ title:"预测用量","data": "predictValue" , "className": "dt-center" }
		],
		data: tableDatas,
		language: {
			info: "显示第 _PAGE_ 页，一共 _PAGES_ 页",
			emptyTable: "无数据",
			zeroRecords:    "无匹配结果",
			paginate: {
				first:      "首页",
				previous:   "上一页",
				next:       "下一页",
				last:       "尾页"
			}
		}		
    });
}
