$(function () {
	
	
	
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/dict/queryPage',
        datatype: "json",
        colModel: [			
			{ label: '字典名称', name: 'dicName', index: 'dicName', width: 80 },
			{ label: '字典类型', name: 'dicType', index: 'type', width: 80 }, 			
			{ label: '字典码', name: 'dicCode', index: 'code', width: 80 }, 			
			{ label: '字典值', name: 'dicValue', index: 'value', width: 80 }, 			
			{ label: '排序', name: 'orderNum', index: 'order_num', width: 80 }, 			
			{ label: '备注', name: 'remark', index: 'remark', width: 80 }
		],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
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
	el:'#rrapp',
	data:{
        q:{
        	dicName: null
        },
		showList: true,
		title: null,
		dict: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.dict = {};
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(id)
		},
		saveOrUpdate: function (event) {
			var url = vm.dict.id == null ? "sys/dict/addDict" : "sys/dict/updateDict";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.dict),
			    success: function(result){
			    	if(result.success){
						alert('操作成功', function(index){
							vm.reload();
						});
					}else{
						alert(result.message);
					}
				}
			});
		},
		del: function (event) {
			var ids = getSelectedRows();
			if(ids == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "sys/dict/deleteDict",
                    contentType: "application/json",
				    data: JSON.stringify(ids),
				    success: function(result){
						if(result.success){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(result.message);
						}
					}
				});
			});
		},
		getInfo: function(id){
			$.get(baseURL + "sys/dict/info/"+id, function(r){
                vm.dict = r.result;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{'dictName': vm.q.dictName},
                page:page
            }).trigger("reloadGrid");
		}
	}
});