/**
 * 
 */

function modelManage(){
		$('#gene_model').addClass('disabled').attr('disabled', 'disabled').val('模型生成中……');
		var cst_id = $("#m_place").val();
		var timeInterval = $("#m_timeInterval").val(); //时间间隔 默认一天
		var forcastDays = $("#m_forcastDays").val(); //预测天数
		var algorithm = $("#m_algorithm").val();//算法选择
		var algorithm_text = $("#m_algorithm").find(':selected').text()
		var startDate = $("#m_startDate").val();
		var endDate = $("#m_endDate").val();
		var now = new Date()
		var now_fmt = now.getFullYear() + '-' + (now.getMonth() + 1) + '-' + now.getDate() + ' ' + now.getHours() + ':' + now.getMinutes() + ':' + now.getSeconds()
		
		$('#analysis-args').append(
			'<tr><td>' + now_fmt + '</td>' +
			'<td>' + algorithm_text + '</td>' +
			'<td>' + timeInterval + '</td>' +
			'<td>生成模型</td></tr>'
		);
		$.ajax({
			  type: 'POST',
			  url: 'rest/system/modelManage/modelManage/',
			  data:JSON3.stringify({
				  cst_id:cst_id,
				  timeInterval:timeInterval,
				  forcastDays:forcastDays,
				  beginDate:startDate,//"2016-07-06 00:00:00",
				  endDate:endDate,//"2016-09-07 00:00:00",
				  algorithm:algorithm//"smoreg"
				}),
				success:function(result){
					var resultList=result.data;//获取返回的结果集
					
					$('#param').append('<tr><td>' + algorithm_text + '</td><td>' + resultList[0].modelparam + '</td></tr>')
					
	
					
				},
				complete: function(){
					$('#gene_model').removeClass('disabled').removeAttr('disabled').val('生成模型');
				},
				dataType:"json",
				contentType:"application/json"
				
			});
	}