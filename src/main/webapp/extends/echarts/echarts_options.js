var prepaymentOption = {
    title: {
        text: '提前还款率'
    },
    tooltip: {},
    legend: {
        data:['还款率']
    },
    grid: {
        left: '3%',
        top: '12%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis: {
        data: ["1月","2月","3月","4月","5月","6月"]
    },
    yAxis: {
    	type: 'value',
    	axisLabel: {
            formatter: '{value} %'
        }
    },
    series: [{
        type: 'line',
        data: [5, 20, 36, 10, 10, 20]
    }]
};
var returnRateOption = {
		title: {
			text: '收益率'
		},
		tooltip: {
			 formatter: '{a}: ({c})'
		},
		legend: {
			y: 'bottom',
			data:['aaa', 'aap', 'aa','aam']
		},
		xAxis: {
			type: 'value',
			name: '月',
			min:0,
			max: 40,
			splitLine:{
				show:false
			},
			data:[]
		},
		yAxis: {
			type: 'value',
			name: '收益率',
			axisLabel: {
				formatter: '{value} %'
			}
		},
		series: [{
            name: 'aaa',
            type: 'scatter',
            data:[]
        },
        {
            name: 'aap',
            type: 'scatter',
            data:[]
        },
        {
            name: 'aa',
            type: 'scatter',
            data:[]
        },
        {
        	name: 'aam',
            type: 'scatter',
            data:[]	
        }]
};
var productOption = {
		title: {
			text: '产品设计方案'
		},
		tooltip: {},
		legend: {
			data:['占比']
		},
		grid: {
	        left: '3%',
	        top: '12%',
	        right: '4%',
	        bottom: '3%',
	        containLabel: true
	    },
		xAxis: {
			type:'value',
			axisLabel: {
	            formatter: '{value} %'
	        }
		},
		yAxis: {
			type:'category',
			data: ["劣后","中间","优先"]
		},
		series: [{
			type: 'bar',
			data: [10,70, 20]
		}]
};
var cashflowOption = {
	    title: {
	    	left: 'center',
	        text: '现金流'
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    grid: {
	        left: '3%',
	        top: '12%',
	        right: '4%',
	        bottom: '3%',
	        containLabel: true
	    },
	    xAxis: {
	    	type: 'value'
	    },
	    yAxis: { 
	    	type: 'category',
	    	data:['0'],
	    	boundaryGap:false
	    },
	    series: [
	        {
	            type:'line',
	            data:[0,0]
	        }
	    ]
};
var cashFlowTypeOption = {
	    tooltip : {
	        trigger: 'item',
	        formatter: "{a} <br/>{b} : {c} ({d}%)"
	    },
	    series : [
	        {
	            name: '金额分类',
	            type: 'pie',
	            center: ['50%', '60%'],
	            data:[
	                {value:1, name:'正常回款'},
	                {value:1, name:'提前回款'},
	                {value:1, name:'其他（保证金）'}
	            ],
	            itemStyle: {
	                emphasis: {
	                    shadowBlur: 10,
	                    shadowOffsetX: 0,
	                    shadowColor: 'rgba(0, 0, 0, 0.5)'
	                }
	            }
	        }
	    ]
};
var cashFlowCompareOption = {
	    tooltip : {
	        trigger: 'axis'
	    },
	    legend: {
	        data:['基础现金流','理论现金流','实际现金流']
	    },
	    toolbox: {
	        show : true,
	        feature : {
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
	            data : []
	        }
	    ],
	    yAxis : [
	        {
	            type : 'value'
	        }
	    ],
	    series : [
	        {
	            name:'基础现金流',
	            type:'bar',
	            data:[],
	        },
	        {
	            name:'理论现金流',
	            type:'bar',
	            data:[],
	        },
			{
				name:'实际现金流',
				type:'bar',
				data:[],
			}
	    ]
};