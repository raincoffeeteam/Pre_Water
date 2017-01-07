<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link rel="stylesheet" href="extends/bootstrap-3.3.5/css/bootstrap.min.css">
<!-- 可选的Bootstrap主题文件（一般不用引入） -->
<link rel="stylesheet" href="extends/bootstrap-3.3.5/css/bootstrap-theme.min.css">
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="extends/jquery-1.12.0.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="extends/bootstrap-3.3.5/js/bootstrap.min.js"></script>
<script src="extends/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="extends/json3.min.js"></script>
<title>工业用水预测系统</title>
</head>
<body>
	<div class="main-wrapper">		
		<jsp:include page="headerMenu.jsp"></jsp:include>
		<div id="main_content" style="background: #ffffff;">
			<jsp:include page="home.jsp"></jsp:include>
		</div>
		<jsp:include page="footer.jsp"></jsp:include>
	</div>
</body>
</html>
	<script type="text/javascript">
		$(document).ready(function() {
			//s();
		});	
		function s(){
			$.ajax({
				  type: 'POST',
				  url: 'rest/system/city/getCityByCountry/',
				  data:JSON3.stringify({
					  countryCode:"AFG"
					}),
				  success: function(result){
					  debugger;
					  list =result.data;
					  $("#test").html(JSON3.stringify(result.data))
					  alert(result)
				  },
				  dataType: "json",
				  contentType:"application/json"
			});
		}
	</script>
