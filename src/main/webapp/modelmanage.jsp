<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>模型管理</title>
 <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <!-- Bootstrap 3.3.6 -->
  <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.5.0/css/font-awesome.min.css">
  <!-- datatables -->
  <link href="extends/datatables/media/css/jquery.dataTables.min.css" rel="stylesheet">
  <link href="extends/datatables/extensions/Buttons/css/buttons.dataTables.min.css" rel="stylesheet">
  <!-- Ionicons -->
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
  <!-- Theme style -->
  <link rel="stylesheet" href="dist/css/AdminLTE.min.css">
  <link rel="stylesheet" href="dist/css/skins/skin-blue.min.css">
  <link href="extends/datatables/media/css/jquery.dataTables.min.css" rel="stylesheet">
<link href="extends/datatables/extensions/Buttons/css/buttons.dataTables.min.css" rel="stylesheet">
</head>
<body  class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
  	<div class="main-wrapper">		
		<jsp:include page="header.jsp"></jsp:include>
		<jsp:include page="toolbar.jsp"></jsp:include>
		<div class="content-wrapper">
		    <section class="content-header">
		      <h1>ModelManage<small>manage</small></h1>
		      <ol class="breadcrumb">
		        <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
		        <li class="active">ModelManage</li>
		      </ol>
		    </section>
		    <section class="content">
		    	<div class="row">
		    	<div class="col-md-12">
		    		<div class="box box-success">
		    			<div class="box-header with-border">
		    			<h3 class="box-title">条件选择</h3>
		    			</div>
		    			<div class="box-body">
		    			 <div class="row">
		    			  <div class="col-md-4">
		    				<div class="form-group">
		    					<label>测试点</label>
		    					<select id="place" class="form-control select2" style="width:100%;"></select>
		    				</div>
		    			  
		    			   </div>
		    			   <div class="col-md-4">	    			  
		    				<div class="form-group">
		    					<label>时间间隔</label>
		    					<select id="timeInterval" class="form-control select2" style="width:100%;">
		    					<option value="10" selected="selected">10分钟</option>
								<option value="60">1小时</option>
								<option value="1440">1天</option>
		    					</select>
		    				</div>
		    			   </div>
		    			   <div class="col-md-4">
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
		    		  <div class="row">
		    			 	<div class="col-md-4">
		    		  		<div class="form-group">
		    		  			<label>开始日期</label>
						    		<div class="input-group">
				                  		<div class="input-group-addon">
				                  		 <i class="fa fa-calendar"></i>
				                    		<i class="fa fa-clock-o"></i>
				                  		</div>
				                 		 <input id="m_startDate" class="form-control" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false})"/>
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
				                 		<input id="m_endDate" class="form-control" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false})"/>
               						</div>
                           </div>
		    			   </div>
		    			    
		    			 </div>		    
		    		 </div>
		    		  <div class="box-footer">
		    			<button id="gene_model" type="submit" class="btn btn-info pull-right">生成模型</button>    
		    		  </div>		    		   
		    		  </div>		    			
		    		</div>		    		
		    	</div>
		    	  <div class="row">
			    	<div class="col-md-6">
	          			<div class="box box-warning">
	            			<div class="box-header with-border">
		              			<h3 class="box-title">模型分析</h3>		      
	            			</div>
	            			<div class="box-body" style="width:100%; height: 500px;">
							     <table id="taskTable" class="table table-hover" >
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
	       			 <div class="col-md-6">
	       			  <div class="row">
	       			   <div class="col-md-12">
	       			      <div class="box box-primary">
	            			<div class="box-header with-border">
		              			<h3 class="box-title">模型参数</h3>
	            			</div>
	           				<div class="box-body">
	              				<div id="chart" style="width:100%; height: 250px;"></div>
	            			</div>
	          			</div>
	       			   </div>
	       			    
	       			  </div>
	       			  <div class="row">
	       			   <div class="col-md-12">
	       			      <div class="box box-info">
	            			<div class="box-header with-border">
		              			<h3 class="box-title">评估结果</h3>
	            			</div>
	           				<div class="box-body">
	              				<div id="chart" style="width:100%; height: 250px;"></div>
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
</body>
</html>