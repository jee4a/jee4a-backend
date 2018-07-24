$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/role/list',
        datatype: "json",
        colModel: [
            { label: '角色ID', name: 'id', index: "id", width: 45, key: true },
            { label: '角色名称', name: 'roleName', index: "role_name", width: 75 },
            { label: '创建时间', name: 'createTime', index: "create_time", width: 80},
            { label: '创建人', name:'creatorName',width:'45'},
            { label: '最新修改时间', name: 'updateTime', index: "updateTime", width: 80},
            { label: '最新修改人', name:'updatorName',width:'45'},
            { label: '操作', name: '', width: 40,formatter: function(value, options, row){
				return  '<button type="button" class="btn btn-primary btn-xs margin" onclick="vm.detail('+row.id+')">查看</button>';
				}}
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
            root: "result.pageInfo.list",
            page: "result.pageInfo.pageNum",
            total: "result.pageInfo.pages",
            records: "result.pageInfo.total"
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

//菜单树
var menu_ztree;
var menu_setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    },
    check:{
        enable:true,
        nocheckInherit:true
    }
};

//部门结构树
var dept_ztree;
var dept_setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    }
};

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
        	beginCreateTime: null,
        	endCreateTime: null,
        	beginUpdateTime:null,
			endUpdateTime:null,
			queryConditions:null,
            name: null
        },
        showList: true,
        showDetail: false,
        showAdd : false,
        title:null,
        searchType: [
       		      { text: '角色ID', value: "1" },  
       		      { text: '角色名称', value: "2" },
       		      { text: '创建人', value: "3" } ,
       		      { text: '最新修改人', value: "4" }
       		    ] ,
        role:{
            orgId:null,
            orgName:null
        },
        roleInfo:{}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.showDetail= false;
            vm.showAdd= true;
            vm.title = "新增";
            vm.role = {orgName:null, orgId:null};
            vm.getMenuTree(null,'');

            vm.getDept();

          /*  vm.getDataTree();*/
        },
        update: function () {
            var roleId = getSelectedRow();
            if(roleId == null){
                return ;
            }

            vm.showList = false;
            vm.showDetail= false;
            vm.showAdd= true;
            vm.title = "修改";
            vm.getMenuTree(roleId,'');
//            vm.getMenuTree(roleId,'Detail');
            vm.getDept();
        },
        del: function (action) {
            var roleIds = getSelectedRows();
            var url ;
            if(roleIds == null){
                return ;
            }
            if(action=='open'){
            	url = baseURL + "sys/role/open"
            }else if(action=='close'){
            	url = baseURL + "sys/role/close"
            }else{
            	return;
            }
            confirm('确定要修改选中的记录的状态？', function(){
                $.ajax({
                    type: "POST",
                    url: url,
                    contentType: "application/json",
                    data: JSON.stringify(roleIds,action),
                    success: function(r){
                        if(r.success === true){
                            alert('操作成功', function(){
                                vm.reload();
                            });
                        }else{
                            alert(r.message);
                        }
                    }
                });
            });
        },
        detail : function(roleId){
        	vm.showList = false ;
			vm.showDetail=true;
			vm.showAdd= false;
 			$.get(baseURL + "sys/role/info/"+roleId, function(data){
 				if(data.success){
 					vm.roleInfo = data.result.role ;
 					vm.getMenuTree(roleId,'Detail');
 					
 				}else{
 					alert(data.message);
 				}
             })
		},
		getRole : function(roleId, str) {
			var treeObj = $.fn.zTree.getZTreeObj("menuTree"+str);
			var nodes = treeObj.transformToArray(treeObj.getNodes());
			$.get(baseURL + "sys/role/info/" + roleId, function(r) {
				vm.role = r.result.role;
				var menuIds = vm.role.menuIdList;
				if (str == "Detail") {
					// 勾选角色所拥有的菜单
					for (var i = 0; i < nodes.length; i++) {
						for (var j = 0; j < menuIds.length; j++) {
							if (nodes[i].id == menuIds[j]) {
								var node = menu_ztree.getNodeByParam("id",menuIds[j]);
								menu_ztree.checkNode(node, true, false);
							}
						}
						nodes[i].chkDisabled = true;
					}
				} else {
					for (var i = 0; i < menuIds.length; i++) {
						var node = menu_ztree.getNodeByParam("id", menuIds[i]);
						if(node != null){
							menu_ztree.checkNode(node, true, false);
						}
					}
				}
				vm.getDept();
			});
		},
        saveOrUpdate: function () {
            //获取选择的菜单
            var nodes = menu_ztree.getCheckedNodes(true);
            var menuIdList = new Array();
            for(var i=0; i<nodes.length; i++) {
                menuIdList.push(nodes[i].id);
            }
            vm.role.menuIdList = menuIdList;
            //获取选择的数据
            var reg=new RegExp("^(?![0-9]+$)[0-9\u4E00-\u9FA5]{1,40}$");
			if(!reg.test(vm.role.roleName)){
				alert("请以汉字 或者 汉字+数字的格式输入");
			}else{
               var url = vm.role.id == null ? "sys/role/save" : "sys/role/update";
	              $.ajax({
	                type: "POST",
	                url: baseURL + url,
	                contentType: "application/json",
	                data: JSON.stringify(vm.role),
	                success: function(r){
	                    if(r.success === true){
	                        alert('操作成功', function(){
	                            vm.reload();
	                        });
	                    }else{
	                        alert(r.message);
	                    }
	                }
	              });
			}
        },
        getMenuTree: function(roleId,str) {
            //加载菜单树
            $.get(baseURL + "sys/resource/list", function(r){
                menu_ztree = $.fn.zTree.init($("#menuTree"+str), menu_setting, r);
                //展开所有节点
                menu_ztree.expandAll(true);
                
                if(roleId != null){
                    vm.getRole(roleId,str);
                }
                
            });
        },
        getDept: function(){
            //加载部门树
            $.get(baseURL + "sys/org/list", function(r){
                dept_ztree = $.fn.zTree.init($("#deptTree"), dept_setting, r);
                var node = dept_ztree.getNodeByParam("id", vm.role.orgId);
                if(node != null){
                    dept_ztree.selectNode(node);

                    vm.role.orgName = node.name;
                }
            })
        },
        deptTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择部门",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = dept_ztree.getSelectedNodes();
                    //选择上级部门
                    vm.role.orgId = node[0].id;
                    vm.role.orgName = node[0].name;

                    layer.close(index);
                }
            });
        },
        reload: function () {
            vm.showList = true;
            vm.showDetail= false;
            vm.showAdd= false;
            layer.closeAll();
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{
                	'queryConditions': vm.q.queryConditions,
					'beginCreateTime': vm.q.beginCreateTime,
					'endCreateTime': vm.q.endCreateTime,
					'beginUpdateTime': vm.q.beginUpdateTime,
					'endUpdateTime': vm.q.endUpdateTime,
                	'name': vm.q.name},
                page:page
            }).trigger("reloadGrid");
        },
    	queryConditionsChange:function(obj){
			vm.q.queryConditions = $(obj).val();
		}
    }
});
function isDeletedformatter(cellValue, options, rowdata, action){
	if(cellValue=='0'){
		return '<span class="label label-success">已开启</span>';
	}else if(cellValue == '1'){
		return '<span class="label label-danger">已关闭</span>';
	}else{
		return '/';
	}
};
