<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>模型评估</title>
 <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <!-- Bootstrap 3.3.6 -->
  <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
  <!-- daterange picker -->
  <link rel="stylesheet" href="plugins/daterangepicker/daterangepicker.css">
   <!-- Select2 -->
  <link rel="stylesheet" href="plugins/select2/select2.min.css">
  <!-- bootstrap datepicker -->
  <link rel="stylesheet" href="plugins/datepicker/datepicker3.css">
  <!-- iCheck for checkboxes and radio inputs -->
  <link rel="stylesheet" href="plugins/iCheck/all.css">
  <!-- datatables -->
  <link href="extends/datatables/media/css/jquery.dataTables.min.css" rel="stylesheet">
  <link href="extends/datatables/extensions/Buttons/css/buttons.dataTables.min.css" rel="stylesheet">
  <!-- Bootstrap Color Picker -->
  <link rel="stylesheet" href="plugins/colorpicker/bootstrap-colorpicker.min.css">
  <link rel="stylesheet" href="extends/multiple-select.css"> 
  <!-- Theme style -->
  <link rel="stylesheet" href="dist/css/AdminLTE.min.css">
  <link rel="stylesheet" href="dist/css/skins/skin-blue.min.css">
<link href="extends/datatables/extensions/Buttons/css/buttons.dataTables.min.css" rel="stylesheet">
</head>
<body  class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
  	<div class="main-wrapper">		
		<jsp:include page="header.jsp"></jsp:include>
		<jsp:include page="toolbar.jsp"></jsp:include>
		<div class="content-wrapper">
		    <section class="content-header">
		      <h1>ModelEvaluate<small>evaluate</small></h1>
		      <ol class="breadcrumb">
		        <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
		        <li class="active">ModelEvaluate</li>
		      </ol>
		    </section>
		    <section class="content">
		    	<div class="row">
		    	<div class="col-md-12">
		    		<div class="box box-danger">
		    			<div class="box-header with-border">
		    			<h3 class="box-title">条件选择</h3>
		    			</div>
		    			<div class="box-body">
		    			 <div class="row">
		    			  <div class="col-md-4">
		    				<div class="form-group">
		    					<label>测试点</label>
		    					<select id="e_place" class="form-control select2" style="width:100%;"></select>
		    				</div>
		    			  
		    			   </div>
		    			   <div class="col-md-4">	    			  
		    				<div class="form-group">
		    					<label>时间间隔</label>
		    					<select id="e_timeInterval" class="form-control select2" style="width:100%;">
		    					<option value="min" selected="selected">10分钟</option>
								<option value="hour">1小时</option>
								<option value="day">1天</option>
		    					</select>
		    				</div>
		    			   </div>
		    			   <div class="col-md-4">
		    			    <div class="form-group">
		    			    <label>预测算法</label>		    			    
		    			     <select id="e_algorithm" class="form-control select2" multiple="multiple" data-placeholder="Select an alorithm" style="width: 100%;">
									<option value="bp">神经网络</option>
									<option value="tree">决策树</option>
									<option value="smoreg">支持向量机回归</option>
							</select>
		    			   </div>
		    			 </div>		  		    			 			    		  			    			
		    		  </div> 
		    		   <div class="row">
		    			 	<div class="col-md-4">
		    		  		<div class="form-group">
		    		  			<label>开始日期</label>
						    		<div class="input-group">
				                  		<div class="input-group-addon">
				                  		 <i class="fa fa-calendar"></i>
				                    		<i class="fa fa-clock-o"></i>
				                  		</div>
				                 		 <input id="e_startDate" class="form-control" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false})"/>
               						</div>
		    		  		</div>
		    		  		</div>
		    		  		<div class="col-md-4">
		    		       <div class="form-group">
		    					<label>结束日期</label>
						    		<div class="input-group">
				                  		<div class="input-group-addon">
				                  		 <i class="fa fa-calendar"></i>
				                    		<i class="fa fa-clock-o"></i>
				                  		</div>
				                 		<input id="e_endDate" class="form-control" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false})"/>
               						</div>
                           </div>
		    			   </div>
		    			    
		    			 </div>
		    			 
		    			 </div>
		    			 <div class="box-footer">
		    				<div>
		    			 		<button id="eve_model" type="submit" class="btn btn-info pull-right" onclick="modelEvaluation()">模型评估</button> 
		    			 	</div>
		    			 		
		    			 </div>
		    			 </div>
		    		 		    
		    		 </div>	    		   
		    		  </div>
		    		  <div class="row">
		    		<div class="col-md-12">
		    			<div class="box box-default">
		    				<div class="box-header with-border">
		    				<h3 class="box-title">评估结果</h3>
		    				</div>
		    				<div class="box-body">
		    				  <div class="row">
		    				    <div class="col-md-12">
		    				    <table id="t3" class="dataTable table-striped table-bordered table-condensed" style="width:100%;height:350px">
							  		<thead >
							            <tr>
							                <th rowspan="2" >编号</th>
							                <th rowspan="2" >测量名称</th>
							                <th rowspan="2">时间</th>
							                <th rowspan="2">实际用水量</th>
							                <th colspan="2">决策树</th>
							                <th colspan="2">支持向量机回归</th>
							                <th colspan="2">神经网络</th>
							            </tr>
							            <tr>
							                <th>预测用水量</th>
							                <th>(预测用水-实际用水)/实际用水</th>
							                <th>预测用水量</th>
							                <th>(预测用水-实际用水)/实际用水</th>
							                <th>预测用水量</th>
							                <th>(预测用水-实际用水)/实际用水</th>
							            </tr>
							        </thead>
			  					</table> 
		    				    </div>			
			  				  </div>
			  				  <div class="row">
			  				    <div class="col-md-12">
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
		    			
		    		</div>
		    	</div>
		    </section>		    			
		    		</div>		    		
		<jsp:include page="footer.jsp"></jsp:include>
	</div>
</div>
 <!-- jQuery 2.2.3 -->
<script src="plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="dist/js/app.min.js"></script>
<!-- Select2 -->
<script src="plugins/select2/select2.full.min.js"></script>

<script src="extends/multiple-select.js"></script>

<script src="extends/datatables/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.flash.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/jszip.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/pdfmake.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/vfs_fonts.js"></script>

 <script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.html5.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.print.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.colVis.min.js"></script>
<script src="extends/My97DatePicker/WdatePicker.js"></script>
<!-- bootstrap color picker -->
<script src="plugins/colorpicker/bootstrap-colorpicker.min.js"></script>
<script src="extends/echarts.js"></script>
<script src="js/evaluate.js"></script>
</body>
</html>