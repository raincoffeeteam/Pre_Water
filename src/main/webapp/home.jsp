<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<style>

.tabTitle{
	font-family:微软雅黑;
	font-weight:bolder;
}
.pageDiv{
	width:100%;
	height:40px;
	line-height: 40px;
	margin:5;
	font-family:微软雅黑;
	text-align: center;
}
.selectButton{
	margin-bottom: 5px;
}
/*context-box样式  */
div.content_box {
	width: 100%;
	height: 100%;
	background-color: #fff;
	border-radius:5px;
	/* border: 1px solid #dcdcdc; */
}
.box-body{
margin-top:10px;
}
.model-title{
	width:90%;
	height:50px;
	margin-left: auto;
	margin-right: auto; 
	margin-top: 10px;
}
.model-title-context{
 	margin:1%;
	/* text-align: left;  */
	font-size:20px;
	font-family: 微软雅黑;
	
}
div.content_box {
	width: 100%;
	height: 100%;
	background-color: #fff;
	border-radius:5px;
	border: 1px solid #dcdcdc;
}
.content_box .box-header {
	height: 50px;
	line-height: 50px;
	padding: 0 20px;
	border-bottom: 1px solid #dcdcdc;
}
.content_box .box-header span {
	float: left;
	line-height: 50px;
	color: rgba(0,0,0,0.85);
	/*margin: 0;*/
}
/* .content_box .box-body {
	height: calc(100% - 50px); 
} */
</style>
<script src="extends/echarts.js"></script>
<!--multiple-select-->
<link rel="stylesheet" href="extends/multiple-select.css">
<script src="extends/multiple-select.js"></script>
<!-- datatables -->
<link href="extends/datatables/media/css/jquery.dataTables.min.css" rel="stylesheet">
<link href="extends/datatables/extensions/Buttons/css/buttons.dataTables.min.css" rel="stylesheet">
<script src="extends/datatables/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.flash.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/jszip.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/pdfmake.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/vfs_fonts.js"></script>

 <script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.html5.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.print.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.colVis.min.js"></script>
<script src="js/home.js"></script>
<div id="main">
	<ul class="nav nav-tabs tabTitle" style="margin-bottom: 5px;width:80%;margin-left:10%;margin-right:10%;">
		<li id="tabtitle1" class="active">
			<a href="#tab1" data-toggle="tab" class="">用水预测</a>
		</li>
		<li id="tabtitle2">
			<a href="#tab2" data-toggle="tab">模型管理</a>
		</li>
		<li id="tabtitle3">
			<a href="#tab3" data-toggle="tab">模型评估</a>
		</li>
	</ul>
	<div class="tab-content">
		<div class="tab-pane active" id="tab1">
			<div class="panel panel-default" style="width:80%;margin-left:10%;margin-right:10%;">
				<div class="panel-body">
					<form id="forcast" class="form-inline" role="form">
						<div >
							<div class="col-xs-3"><label for="place" class="control-label">测试点</label>
									<select id="place" class="form-control" style="width:200px;">
									</select>
							</div>
							<div class="selectButton col-xs-3 form-group"><label for="timeInterval" class="control-label">时间间隔:</label>
								<select id="timeInterval" class="form-control" style="width:200px;">
									<option value="10" selected="selected">10分钟</option>
									<option value="60">1小时</option>
									<option value="1440">1天</option>
								</select>
							</div>
							<div class="col-xs-3"><label for="forcastDays" class="control-label">预测天数:</label>
								<select id="forcastDays" class="form-control" style="width:200px;">
									<option value="1" selected="selected">一天</option>
									<option value="7">一周</option>
									<option value="15">半月</option>
								</select>
							</div>
							<div class="col-xs-3"><label for="algorithm" class="control-label">预测算法</label>
								<select id="algorithm" multiple="multiple" style="width:200px;">
									<option value="bp" selected="selected">BP</option>
									<option value="tree">搜索树</option>
									<option value="smoreg">支持向量机回归</option>
								</select>
							</div>
							<div class="col-xs-3">
								<label class="control-label">开始日期：</label>
								<input id="startDate" class="form-control" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false})"/>
							</div>
							<div class="col-xs-3">
								<label class="control-label">结束日期：</label>
								<input id="endDate" class="form-control" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false})"/>
							</div>
							<div class="col-xs-3"><input id="dataFitting" class="btn btn-primary" type="button" value="查询" onclick="waterPrediction()"/></div>
						</div>
					</form>	
			  	</div>
			  	<!--表格和echarts展示-->
			  	<div class=""  style="height:70%;">
				  	<div class="col-xs-6">
				  		<div class="content_box">
				  		<div class="box-header">
							<span style="background"><img></img></span><span>预测表</span>
						</div>
					  	<div class="box-body" >
							<table id="table" class="dataTable table-striped table-bordered table-condensed" style="width:100%; height:400px;"></table>
						</div>	
						</div>
				  	</div>
				  	<div class="col-xs-6">
				  	<div class="content_box">
				  		<div class="box-header">
							<span style="background"><img></img></span><span>预测图</span>
						</div>
					  	<div class="box-body">
							<div id="chart" style="width:100%; height: 500px;"></div>
						</div>	
						</div>
				  	</div>
			  	</div>						
			</div>		
			
		</div>
		
		
		<div class="tab-pane" id="tab2">
			<div class="panel panel-default" style="width:80%;margin-left:10%;margin-right:10%;">
				<div class="panel-body">
					<form id="generatingModel" class="form-inline" role="form">
						<div >
							<div class="selectButton col-xs-3 form-group"><label for="timeInterval" class="control-label">时间间隔:</label>
								<select id="m_timeInterval" class="form-control" style="width:200px;">
									<option value="0.25" selected="selected">15分钟</option>
									<option value="1">1小时</option>
									<option value="24">1天</option>
								</select>
							</div>
							<div class="col-xs-3"><label for="forcastDays" class="control-label">评估天数:</label>
								<select id="m_forcastDays" class="form-control" style="width:200px;">
									<option value="1" selected="selected">一天</option>
									<option value="7">一周</option>
									<option value="15">半月</option>
								</select>
							</div>
							<div class="col-xs-3"><label for="algorithm" class="control-label">预测算法</label>
								<select id="m_algorithm" class="form-control" style="width:200px;">
									<option value="term_total" selected="selected">BP</option>
									<option value="city">搜索树</option>
									<option value="education">因子</option>
								</select>
							</div>
							<div class="col-xs-3"><label for="place" class="control-label">测试点</label>
								<select id="m_place" class="form-control" style="width:200px;">
									<option value="1" selected="selected">测试点1</option>
									<option value="2">测试点2</option>
									<option value="3">测试点3</option>
								</select>
							</div>
							<div class="col-xs-3">
								<label class="control-label">开始日期：</label>
								<input id="m_startDate" class="form-control" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false})"/>
							</div>
							<div class="col-xs-3">
								<label class="control-label">结束日期：</label>
								<input id="m_endDate" class="form-control" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false})"/>
							</div>
							<div class="col-xs-3"><input id="gene_model" class="btn btn-primary" type="button" value="生成模型"/></div>
						</div>
					</form>	
			  	</div>
			  	<!-- 模型分析和预测 -->
			  	<div class="" style="height:70%;">
			  		<div class="col-xs-4">
			  			<div class="content_box" style="height:100%">
							<div class="box-header">
								<span style="background"><img></img></span><span>模型分析</span>
							</div>
							<div class="box-body">
								<!-- <table id="pollution_table" class="stripe" cellspacing="0" style="width: 96%; margin-top:10px;"></table> -->
								<table id="taskTable" class="table table-hover">
									<thead>
										<tr>
											<th>模型时间</th>
											<th>模型名称</th>
											<th>间隔</th>
											<th>操作</th>
										</tr>
									</thead>
									<tbody></tbody>
								</table>
							</div>				
						</div>
			  		</div>
			  		<div class="col-xs-8">
			  			<div class="col-xs-6">
			  				<div class="content_box" style="height:100% ;">
								<div class="box-header">
									<span style="background"><img></img></span><span>模型参数</span>
								</div>
								<div class="box-body">
									参数
								</div>				
							</div>			  				
			  			</div>
			  			<div class="col-xs-6">
			  				<div class="content_box" style="height:100%">
								<div class="box-header">
									<span style="background"><img></img></span><span>评估结果</span>
								</div>
								<div class="box-body">
									<div>一天：</div>
									<div>一周：</div>
									<div>半月：</div>
								</div>				
							</div>	
			  			</div>
			  		</div>
				</div>
			</div>		
			
		</div>
		
		
		<div class="tab-pane" id="tab3">
			<div class="panel panel-default" style="width:80%;margin-left:10%;margin-right:10%;">
				<div class="panel-body" style="height:10%">
					<form id="generatingModel" class="form-inline" role="form">
						<div >
							<div class="col-xs-3">
								<label class="control-label">开始日期：</label>
								<input id="e_startDate" class="form-control" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false})"/>
							</div>
							<div class="col-xs-3">
								<label class="control-label">结束日期：</label>
								<input id="e_endDate" class="form-control" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false})"/>
							</div>
							<div class="selectButton col-xs-3 form-group"><label for="timeInterval" class="control-label">时间间隔:</label>
								<select id="e_timeInterval" class="form-control" style="width:200px;">
									<option value="0.25" selected="selected">10分钟</option>
									<option value="1">1小时</option>
									<option value="24">1天</option>
								</select>
							</div>	
							<div class="col-xs-3"><label for="place" class="control-label">测试点</label>
								<select id="e_place" class="form-control" style="width:200px;">
									<option value="5698" selected="selected">test_options</option>
									<option value="2">测试点2</option>
									<option value="3">测试点3</option>
								</select>
							</div>						
							<div class="col-xs-3"><label for="algorithm" class="control-label">预测算法</label>
								<select id="e_algorithm" multiple="multiple" style="width:200px;">
									<option value="bp" selected="selected">BP</option>
									<option value="tree">搜索树</option>
									<option value="smoreg">支持向量机回归</option>
								</select>
							</div>
							<div class="col-xs-3"><input id="eve_model" class="btn btn-primary" type="button" value="模型评估" onclick="modelEvaluation()"/></div>
						</div>
					</form>	
			  	</div>
			  	<!--table  -->
			  	<div style="height:30%;">
			  		<table id="t3" class="dataTable table-striped table-bordered table-condensed" style="width:100%;">
			  		<thead >
			            <tr>
			                <th rowspan="2" >编号</th>
			                <th rowspan="2" >测量名称</th>
			                <th rowspan="2">时间</th>
			                <th rowspan="2">用量</th>
			                <th colspan="2">决策树</th>
			                <th colspan="2">支持向量机回归</th>
			                <th colspan="2">神经网络</th>
			            </tr>
			            <tr>
			                <th>预测用量</th>
			                <th>差化</th>
			                <th>预测用量</th>
			                <th>差化</th>
			                <th>预测用量</th>
			                <th>差化</th>
			            </tr>
			        </thead>
			  		</table> 
			  	</div>
			  	<!-- echarts -->
			  	<div  style="height:40%;">
				  	<div class="content_box" style="">
				  		<div class="col-md-6" style="margin-top:10px;">			  			
								<div id="c1" style="width:600px; height:350px"></div>				
						</div>
						<div class="col-md-6" style="margin-top:10px;">			  			
								<div id="c2" style="width:600px; height:350px"></div>			
						</div>			  				
				  	</div>
			  	</div>

			</div>
		</div>
		
	</div>
	
	
</div>
<script>
	$(document).ready(function() {
		getAllCstId();
		$('#algorithm').multipleSelect();
		$('#e_algorithm').multipleSelect();
	});
	//var tableDatas=[];
	var table;
	//initEcharts('chart');
	initTable2('table');
	
	
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
				  $("#place").html("<option value='请选择'>请选择...</option> "+optionstring);
				  $("#e_place").html("<option value='请选择'>请选择...</option> "+optionstring); 
			  },
			  dataType: "json",
			  contentType:"application/json"
		});
	}
	/*
	function forcast(){
		var cst_id=$("#place").val();
		var timeInterval= $("#timeInterval").val();
		$.ajax({
			  type: 'POST',
			  url: 'rest/system/interval/getAllById/',
			  data:JSON3.stringify({
				  cst_id:cst_id,
				  timeInterval:timeInterval
				}),
			  success: function(result){
				  list =result.data;
			  },
			  dataType: "json",
			  contentType:"application/json"
		});
	}*/
	//水预测
	function waterPrediction(){
		var cst_id = $("#place").val();
		var timeInterval = $("#timeInterval").val();
		var forcastDays = $("#forcastDays").val();
		var algorithm = $("#algorithm").val();
		var startDate = $("#startDate").val();
		var endDate = $("#endDate").val();
		
		$.ajax({
			type: 'POST',
			url: 'rest/system/waterPredictAction/predictWater/',
			data:JSON3.stringify({
				cst_id:cst_id,
				timeInterval:timeInterval,
				forcastDays:forcastDays,
				algorithm:algorithm,
				startDate:startDate,
				endDate:endDate
			}),
			success:function(result){
				var resultList=result.data;//获取返回的结果集
				var xData=[];//存储echart x轴数据
				var yData=[];//存储echart y轴数据
				var predictValue=[];//实际预测值
				//加载表格，加载echart
				//读取返回的x轴数据
				for(var i = 0;i<resultList.length; i++){
					xData[i]=resultList[i].cDate;
					predictValue[i]=resultList[i].predictValue;
						
				}
				 yData=[
					 {name:'predictValue',type:'line',data:predictValue}
				  ];
				  //加载chart
				  initPredictEcharts('chart',xData,yData);
				
			},
			dataType:"json",
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
			  url: 'rest/system/modelEvaluation/modelEvalution/',
			  data:JSON3.stringify({
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
			lengthMenu=[4]
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
	
</script>