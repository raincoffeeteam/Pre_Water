/**
 * 
 */
$(".select2").select2();
$(document).ready(function() {
		getAllCstId();
		$('#e_algorithm').multipleSelect();
	});
	//var tableDatas=[];
	var table;
	//initEcharts('chart');
function getAllCstId(){
		$.ajax({
			  type: 'POST',
			  url: 'rest/system/interval/getAllCstId/',
			  success: function(result){
				  list =result.data;
				  var optionstring = "";
				  for(var i=0;i<list.length;i++){
					  optionstring += "<option value=\"" + list[i].CST_ID + "\" >" + list[i].CST_NAME + "</option>"; 
				  }
				  $("#e_place").html("<option value='请选择'>请选择...</option> "+optionstring); 
			  },
			  dataType: "json",
			  contentType:"application/json"
		});
	}

function modelEvaluation(){
		var cst_id=$("#e_place").val();
		var beginDate=$("#e_startDate").val();
		var endDate=$("#e_endDate").val();
		var timeInterval=$("#e_timeInterval").val();
		var algorithm=$("#e_algorithm").val();
		$.ajax({
			  type: 'POST',
			  url: 'rest/system/modelEval/modelEvalution/',
			  data:JSON.stringify({
				  cst_id:cst_id,
				  timeInterval:timeInterval,
				  beginDate:beginDate,//"2016-07-06 00:00:00",
				  endDate:endDate,//"2016-09-07 00:00:00",
				  algorithm:algorithm//"smoreg"
				}),
			  success: function(result){
				 // alert("sss");
				  var resultList=result.data;
				  //加载table
				  var tableDatas=[];
				  var xData=[];
				  var yData=[];
				  var yData2=[];
				  var actual=[];
				  var treeData=[];
				  var smoregData=[];
				  var bpData=[];
				  var treeDeviationData=[];
				  var smoregDeviationData=[];
				  var bpDeviationData=[];
				  for(var i=0;i<resultList.length;i++){
						tableDatas[i]={
								'id':resultList[i].id,
								'name':resultList[i].name,
								'time':resultList[i].time,
								'actualValue':resultList[i].actualValue,
								'tree_predictValue':isNull(resultList[i].tree_predictValue),
								'tree_deviation':isNull(resultList[i].tree_deviation),
								'smoreg_predictValue':isNull(resultList[i].smoreg_predictValue),
								'smoreg_deviation':isNull(resultList[i].smoreg_deviation),
								'bp_predictValue':isNull(resultList[i].bp_predictValue),
								'bp_deviation':isNull(resultList[i].bp_deviation),
						};
						//
						xData[i]=resultList[i].time;
						actual[i]=resultList[i].actualValue;
						treeData[i]=resultList[i].tree_predictValue;
						smoregData[i]=resultList[i].smoreg_predictValue;
						bpData[i]=resultList[i].bp_predictValue;
						//
						treeDeviationData[i]=toPoint(resultList[i].tree_deviation);
						smoregDeviationData[i]=toPoint(resultList[i].smoreg_deviation);
						bpDeviationData[i]=toPoint(resultList[i].bp_deviation);
					}
				  initTable('t3',tableDatas,'tree');
				  yData=[
					  {name:'actual',type:'line',data:actual},
					  {name:'treeData',type:'line',data:treeData},
					  {name:'smoregData',type:'line',data:smoregData},
					  {name:'bpData',type:'line',data:bpData}
				  ];
				  yData2=[
					  {name:'treeData',type:'line',data:treeDeviationData},
					  {name:'smoregData',type:'line',data:smoregDeviationData},
					  {name:'bpData',type:'line',data:bpDeviationData}
				  ];
				  //加载chart1,chart2
				  initEcharts('c1',xData,yData);
				  initEcharts('c2',xData,yData2);
			  },
			  dataType: "json",
			  contentType:"application/json"
		});
	}

function isNull(num){
	if(num==null){
		return 0;
	}else{
		return num;
	}
}
function toPoint(percent){
	if(percent=='0'||percent==0||percent==null){ return 0;}else{
	    var str=percent.replace("%","");
	    str= str/100;
	    return str;
	}		
}

function initTable(elem,tableDatas,method){
	$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) { $($.fn.dataTable.tables(true)).DataTable().columns.adjust(); });
	var tableDatas=tableDatas;
	var lengthMenu;
	if(elem=='t3'){
		lengthMenu=[10]
		buttons=[]
	}else{
		lengthMenu=[10];
        buttons= [ 'copy', 'csv', 'excel', 'pdf'];
	}
	table=$("#"+elem).DataTable({
		/*dom: 'Bfrt<"pageclass"p><"infoclass"i>',*/
		'dom': '<"float_left"f>r<"float_right"l>tip',
		buttons:buttons,
        sScrollX: "100%",   //表格的宽度
		scrollX:true,//设置滚动
		scollY:true,//设置滚动
		destroy: true,
		retrieve:true,
		ordering: true,
		searching: true,//开启搜索
        info: true,//是否显示左下角信息
        bLengthChange: true, //改变每页显示数据数量
        autoWidth: true,//自动宽度
		pagingType: "full",
		orderFixed:true,//高亮排序的列
		"lengthMenu": lengthMenu,
		stripeClasses: [ 'strip1', 'strip2' ],
		columns: [
			{ title:"编号","data": "id" , "className": "dt-center"},
			{ title:"测量名称","data": "name" , "className": "dt-center" },
			{ title:"时间","data": "time" , "className": "dt-center" },
			{ title:"用量","data": "actualValue" , "className": "dt-center" },
			{ title:"预测用量","data": "tree_predictValue" , "className": "dt-center" },
			{ title:"插化","data": "tree_deviation" , "className": "dt-center" },
			{ title:"预测用量","data": "smoreg_predictValue" , "className": "dt-center" },
			{ title:"插化","data": "smoreg_deviation" , "className": "dt-center" },
			{ title:"预测用量","data": "bp_predictValue" , "className": "dt-center" },
			{ title:"插化","data": "bp_deviation" , "className": "dt-center" },
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
