<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>用水预测</title>
 <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <!-- Bootstrap 3.3.6 -->
  <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
  <!-- Theme style -->
  <link rel="stylesheet" href="dist/css/AdminLTE.min.css">
  <link rel="stylesheet" href="dist/css/skins/skin-blue.min.css">
  <link href="extends/datatables/media/css/jquery.dataTables.min.css" rel="stylesheet">
<link href="extends/datatables/extensions/Buttons/css/buttons.dataTables.min.css" rel="stylesheet">

</head>

<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
  	<div class="main-wrapper">		
		<jsp:include page="header.jsp"></jsp:include>
		<jsp:include page="toolbar.jsp"></jsp:include>
		<div class="content-wrapper">
		    <section class="content-header">
		      <h1>WaterPredict<small>predict</small></h1>
		      <ol class="breadcrumb">
		        <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
		        <li class="active">WaterPredict</li>
		      </ol>
		    </section>
		    <section class="content">
		    	<div class="row">
		    	<div class="col-md-12">
		    		<div class="box box-info">
		    			<div class="box-header with-border">
		    			<h3 class="box-title">条件选择</h3>
		    			</div>
		    			<div class="box-body">
		    			 <div class="row">
		    			  <div class="col-md-3">
		    				<div class="form-group">
		    					<label>测试点</label>
		    					<select id="place" class="form-control select2" style="width:100%;"></select>
		    				</div>
		    			  
		    			   </div>
		    			   <div class="col-md-3">	    			  
		    				<div class="form-group">
		    					<label>时间间隔</label>
		    					<select id="timeInterval" class="form-control select2" style="width:100%;">
		    					<option value="minute" selected="selected">10分钟</option>
								<option value="hour">1小时</option>
								<option value="day">1天</option>
		    					</select>
		    				</div>
		    			   </div>
		    			   <div class="col-md-3">
		    			    <div class="form-group">
		    			     <label>预测天数</label>
		    			      <select id="forcastDays" class="form-control select2" style="width:100%;">
		    			        <option value="1" selected="selected">一天</option>
								<option value="7">一周</option>
								<option value="15">半月</option>
		    			      </select>
		    			    </div>
		    			   </div>
		    			   <div class="col-md-3">
		    			    <div class="form-group">
		    			    <label>预测算法</label>
		    			      <select id="algorithm" class="form-control select2" style="width:100%;">
		    			        <option value="bp" selected="selected">BP</option>
								<option value="tree">搜索树</option>
								<option value="smoreg">支持向量机回归</option>
		    			      </select>
		    			   </div>
		    			 </div>
		    			
		    		  </div>
		    			
		    		</div>
		    		<div class="box-footer">
		    		   <button type="submit" class="btn btn-info pull-right" onclick="waterPrediction()">查询</button>    
		    		</div>
		    	</div>
				</div>
				
			</div>
		    	<div class="row">
			    	<div class="col-xs-6">
	          			<div class="box box-danger">
	            			<div class="box-header with-border">
		              			<h3 class="box-title">预测表</h3>		      
	            			</div>
	            			<div class="box-body">
							     <table id="table1" class="dataTable table-striped table-bordered table-condensed" style="width:100%; height:500px;">
							     </table>
						    </div>	
	          			</div>
	       			 </div>	
	       			 <div class="col-xs-6">
	          			<div class="box box-primary">
	            			<div class="box-header with-border">
		              			<h3 class="box-title">预测图</h3>
	            			</div>
	           				<div class="box-body">
	              				<div id="chart" style="width:100%; height: 500px;"></div>
	            			</div>
	          			</div>
	       			 </div>			    	
		    	</div>
		    </section>
		</div>
		<jsp:include page="footer.jsp"></jsp:include>
	</div>
</div>
<script src="plugins/jQuery/jquery-2.2.3.min.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="dist/js/app.min.js"></script>
<script src="plugins/echarts/echarts.js"></script>
<script src="extends/datatables/media/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/dataTables.buttons.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.flash.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/jszip.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/pdfmake.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/vfs_fonts.js"></script>

 <script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.html5.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.print.min.js"></script>
<script type="text/javascript" charset="utf8" src="extends/datatables/extensions/Buttons/js/buttons.colVis.min.js"></script>
<script src="js/predict.js"></script>	
</body>
</html>

