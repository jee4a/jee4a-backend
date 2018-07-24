$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/log/list',
        datatype: "json",
        colModel: [			
			{ label: 'id', name: 'id', width: 30, key: true },
			{ label: '用户名', name: 'userName', width: 50 }, 			
			{ label: '用户操作', name: 'operation', width: 70 }, 			
			{ label: '请求方法', name: 'method', width: 150 }, 			
			{ label: '请求参数', name: 'params', width: 80 },
            { label: '执行时长(毫秒)', name: 'exeTime', width: 80 },
			{ label: 'IP地址', name: 'ip', width: 70 }, 			
			{ label: '创建时间', name: 'createTime', width: 90 }			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,
        pager: "#jqGridPager",
        jsonReader : {
            root: "result.list",
            page: "result.pageNum",
            total: "result.pages",
            records: "result.total"
        },
        prmNames : {
            page:"pageNum", 
            rows:"pageSize"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#crmapp',
	data:{
		q:{
			key: null
		},
	},
	methods: {
		query: function () {
			vm.reload();
		},
		reload: function (event) {
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
				postData:{'key': vm.q.key},
                page:page
            }).trigger("reloadGrid");
		}
	}
});