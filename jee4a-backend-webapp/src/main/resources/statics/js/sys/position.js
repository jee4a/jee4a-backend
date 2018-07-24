$(function () {
	vm.getUserAuthor("sys:position:update",1);
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/position/queryPage',
        datatype: "json",
        colModel: [			
			{ label: '职位ID', name: 'id', index: 'id', width: 80 },
			{ label: '职位名称', name: 'positionName', index: 'name', width: 80 }, 			
			{ label: '创建时间', name: 'createTime', index: 'createTime', width: 80 }, 			
			{ label: '创建人', name: 'createName', index: 'createName', width: 80 }, 			
			{ label: '状态', name: 'state', index: 'state', width: 80 ,formatter: function(value, options, row){
				return value === 0 ? 
						'<span class="label label-success">启用</span>' : 
						'<span class="label label-danger">禁用</span>';
				}}, 			
			{ label: '操作', name: 'state', index: 'state', width: 80,formatter: function(value, options, row){
				if (vm.enableDisable) {
					if(value == 0){
						return "<button class='label label-default' style title='启用' disabled='disabled' onclick='vm.updatestate(this,0)'>启用</button>"+
						"<button class='label label-danger' style title='禁用' onclick='vm.updatestate(this,1)'>禁用</button>";
						
					}
					if(value == 1){
						return "<button class='label label-success' style title='启用' onclick='vm.updatestate(this,0)'>启用</button>"+
						"<button class='label label-default' disabled='disabled' style title='禁用' onclick='vm.updatestate(this,1)'>禁用</button>";
					}
				}else{
					return '';
				}
				
				} }
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
        	positionName: null,
            state:null
        },
		showList: true,
		title: null,
		position: {},
		optionsstate: [
		      { text: '启用', value: 0 },  
		      { text: '禁用', value: 1 }  
		    ] ,
		enableDisable:false
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.position = {};
		},
		 update: function () {
	            var id = getSelectedRow();;
	            if(id == null){
	                return ;
	            }
	            $.get(baseURL + "sys/position/info/"+id, function(r){
	                vm.showList = false;
	                vm.title = "修改";
	                if(r.result != null && r.result !=''){
	                	 vm.position = r.result;
	                }else{
	                	alert("获取职位信息为空！");
	                }
	               
	            });
	        },
		saveOrUpdate: function (event) {
			var url = vm.position.id == null ? "sys/position/addPosition" : "sys/position/updatePosition";
			var reg = new RegExp("^(?![0-9]+$)[0-9\u4E00-\u9FA5]{1,40}$");
			if(!reg.test(vm.position.positionName)){
				alert("请以汉字 或者 汉字+数字的格式输入");
			}else{
			 $.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.position),
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
		  }
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{
                postData:{'name': vm.q.positionName,'state':vm.q.state},
                page:page
            }).trigger("reloadGrid");
		},
		selectChange:function(obj){
			vm.q.state = $(obj).val();
		},
		updatestate:function(obj,state){
			var url = "sys/position/updatePosition";
			var state = state;
			var tr = $(obj).parent().parent();
			var id =  $(tr).attr("id");
			$.ajax({
				type: "post",
			    url: baseURL + url,
                dataType: "json",
			    data:{"state":state,"id":id},
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
        getUserAuthor:function(obj,flag){
			$.ajax({
		        type: "get",
		        url: baseURL + "sys/user/getUserPerms",
		        contentType: "application/json",
		        data: {"userPerms":obj},
		        success: function(data){
		            if(data.success){
		            	var r = data.result.userPerms;
		            	if(r){
		            		if(flag == 1){vm.enableDisable = true;}
		            	}
		            }else{
		                alert(data.message);
		            }
		        }
		    });
		}
	}
});