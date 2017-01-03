<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
	<script>
		//菜单选择器
		function checkMenu(menuName){
			$("#menu li").each(function(){
			    $(this).attr("class","");
			});
			$("#"+menuName).parent().attr("class","liActive");
		}
	</script>
		<aside class="main-sidebar">
    		<section class="sidebar">
      		<!-- search form -->
      		<form action="#" method="get" class="sidebar-form">
		        <div class="input-group">
		          <input type="text" name="q" class="form-control" placeholder="Search...">
		              <span class="input-group-btn">
		                <button type="submit" name="search" id="search-btn" class="btn btn-flat"><i class="fa fa-search"></i></button>
		              </span>
		        </div>
		    </form>      
		      <!-- Sidebar Menu -->
		      <ul class="sidebar-menu" id="menu">
		        <li class="header">HEADER</li>
		        <li><a id="waterpredict" href="waterpredict.jsp"><i class="fa fa-link"></i> <span>用水预测</span></a></li>
		        <li><a id="modelmanage" href="modelmanage.jsp"><i class="fa fa-link"></i> <span>模型管理</span></a></li>
		        <li><a id="modelevaluate" href="modelevaluate.jsp"><i class="fa fa-link"></i> <span>模型评估</span></a></li>
		      </ul>
    		</section>
  		</aside>