/**
 * 
 */
$(document).ready(function() {
		getAllCstId();
		$('#m_algorithm').select();
});

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
				  $("#m_place").html("<option value='请选择'>请选择...</option> "+optionstring);
			  },
			  dataType: "json",
			  contentType:"application/json"
		});
	}
function modelManage(){
		$('#gene_model').addClass('disabled').attr('disabled', 'disabled').val('模型生成中……');
		var cst_id = $("#m_place").val();
		var time = $("#m_timeInterval").val(); //时间间隔 默认一天
		var algorithm = $("#m_algorithm").val();//算法选择
		var beginDate = $("#m_startDate").val();
		var endDate = $("#m_endDate").val();
		var now = new Date()
		var now_fmt = now.getFullYear() + '-' + (now.getMonth() + 1) + '-' + now.getDate() + ' ' + now.getHours() + ':' + now.getMinutes() + ':' + now.getSeconds()
		
		$.ajax({
			  type: 'POST',
			  url: 'rest/system/modelMana/modelManage/',
			  data:JSON.stringify({
				  cst_id:cst_id,
				  time:time,//时间间隔
				  beginDate:beginDate,//"2016-07-06 00:00:00",
				  endDate:endDate,//"2016-09-07 00:00:00",
				  algorithm:algorithm//"smoreg"
				}),
				success:function(result){
					var resultList=result.data;//获取返回的结果集
					
					$('#analysis-args').append(
							'<tr><td>' + resultList.ID + '</td>' +
							'<td>' + algorithm + '</td>' + 
							'<td>' + cst_id + '</td>' +
							'<td>' + beginDate + '</td>' +
							'<td>' + endDate + '</td>' +
							'</tr>'
						);
				for(var i = 0;i<resultList.options.length-1;i++){
					
					$('#modelparam').append(
							'<tr><td>' + resultList.options[i] + '</td>'+
							'<td>' + 0 +'</td>'+
							'<td>' + 0 +'</td>'+
							'</tr>')	
				}
															
				},
				/*complete: function(){
					$('#gene_model').removeClass('disabled').removeAttr('disabled').val('生成模型');
				},*/
				dataType:"json",
				contentType:"application/json"
				
			});
	}