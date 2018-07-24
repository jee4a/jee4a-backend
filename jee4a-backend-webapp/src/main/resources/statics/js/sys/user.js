$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/user/list',
        datatype: "json",
        colModel: [			
			{ label: '用户ID', name: 'id', index: "id", width: 45, key: true },
			{ label: '用户名', name: 'userName', width: 75 },
			{ label: '真实姓名', name: 'actualName', width: 75 },
			{ label: '手机号', name: 'mobile', width: 100 },
			{ label: '坐席号', name: 'staffNo', width: 100 },
            { label: '职位', name: 'positionName', width: 75 },
            { label: '角色', name: 'roleName', width: 75 },
            { label: '所属部门', name: 'orgName', sortable: false, width: 75 },
			{ label: '状态', name: 'state', width: 60, formatter: function(value, options, row){
				return value == 0 ? 
					'<span class="label label-success">正常</span>' : 
					'<span class="label label-danger">禁用</span>';
			}},
			{ label: '最新修改人', name: 'updateName', width: 75 },
			{ label: '最新修改时间', name: 'updateTime', width: 85 },
			{ label: '创建人', name: 'createName', width: 75 },
			{ label: '创建时间', name: 'createTime', index: "create_time", width: 85},
			{ label: '操作',  width: 85, formatter: function(value, options, row){
				return "<button type='button' class='btn btn-primary btn-xs margin' onclick='vm.searchUserInfo(this)'>查看</button>";
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
    vm.getorg1();
});
var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "parentId",
            rootPId: 0
        },
        key: {
            url:"nourl"
        }
    }
};
var ztree;

var vm = new Vue({
    el:'#crmapp',
    data:{
        q:{
            userName: null,
            state: null,
            orgId: null,
            orgName:null,
            beginCreateTime: null,
        	endCreateTime: null,
        	beginUpdateTime:null,
			endUpdateTime:null,
        },
        showList: true,
        showSaveOrUpdate:false,
        showUserInfo:false,
        title:null,
        editOrRedonly:false,
        roleList:{},
        user:{
            state:1,
            orgId:null,
            orgName:null,
            roleIdList:[],
            positionId:null,
            staffNo:null
        },
        positionList:{},
        orgList:{},
        optionsstate: [
         		      { text: '启用', value: 0 },  
         		      { text: '禁用', value: 1 }  
         		    ]  
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.editOrRedonly=false;
            vm.showSaveOrUpdate = true;
            vm.showUserInfo = false;
            vm.title = "新增";
            vm.roleList = {};
            vm.user = {orgName:null, orgId:null, state:0, roleIdList:[]};
        	this.$refs.pwd.type="password";
        	this.$refs.pwd.disabled="";
            //获取角色信息
            this.getRoleList();	
            vm.getorg();
            //获取职位
            this.getPosition();
        },
        getPosition:function(){
        	 $.get(baseURL + "sys/user/queryPositionList", function(result){
             	if(result.success){
                 	vm.positionList  = result.result;
                 }else{
                     alert(result.message);
                 }
             });
        },
        getorg: function(){
             //加载部门树
            $.get(baseURL + "sys/org/select", function(r){
            	if(r.result != null && r.result != ''){
            		 ztree = $.fn.zTree.init($("#orgTree"), setting, r.result);
                     var node = ztree.getNodeByParam("id", vm.user.orgId);
                     if(node != null){
                         ztree.selectNode(node);
                         vm.user.orgName = node.name;
                     }
            	}else{
            		aletr("获取部门数据为空");
            	}
            })
        },
        getorg1: function(){
        	//加载部门树
        	$.get(baseURL + "sys/org/select", function(r){
        		if(r.result != null && r.result != ''){
        			ztree = $.fn.zTree.init($("#orgTree1"), setting, r.result);
        			var node = ztree.getNodeByParam("id", vm.q.orgId);
        			if(node != null){
        				ztree.selectNode(node);
        				vm.q.orgName = node.name;
        			}
        		}else{
        			aletr("获取部门数据为空");
        		}
        	})
        },
        update: function () {
            var userId = getSelectedRow();
            if(userId == null){
                return ;
            }
            vm.showList = false;
            vm.showSaveOrUpdate = true;
            vm.showUserInfo = false;
            vm.editOrRedonly=true;
            vm.title = "修改";
            this.getRoleList();
            this.getPosition();
            vm.getorg();
            vm.getUser(userId);
            vm.user.userPwd=null;
           // this.$refs.pwd.type="password";
           // this.$refs.pwd.disabled="disabled";
            //获取角色信息
        },
        del: function () {
            var userIds = getSelectedRows();
            if(userIds == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/user/delete",
                    contentType: "application/json",
                    data: JSON.stringify(userIds),
                    success: function(r){
                        if(r.success){
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
        saveOrUpdate: function () {
            var url = vm.user.id == null ? "sys/user/save" : "sys/user/update";
            if(vm.user.id == null || vm.user.id == ''){
            	var userName =/^(?![0-9]+$)[0-9A-Za-z]+$/ ;
            	if(!userName.test(vm.user.userName) ){
            		alert("用户名只能是字母或字母加数字组合");
            		return ;
            	}
            	var p = /[^\uFF00-\uFFFF]/i;
            	if(!p.test(vm.user.userPwd)){
            		alert("密码不能是全角字符");
            		return;
            	}
            }
            if(vm.user.userPwd != null && vm.user.userPwd != ''){
            	var pwd =/^(?!(?:[^a-zA-Z]+|\D+|[a-zA-Z0-9]+)$).{8,}$/;
             	if(!pwd.test(vm.user.userPwd)){
             		alert("密码必须包含数字、英文字母和特殊符号8位及以上");
             		return ;
             	}
             	var pattern = new RegExp("[！￥·（）——|{}【】‘；：”“'。，、？《》]")  
                 if(pattern.test(vm.user.userPwd)){
                 	alert("密码只能包含数字、英文字母和特殊符号8位及以上，不能包含中文特殊字符");
                 	return;
                 }
            }
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.user),
                success: function(r){
                    if(r.success){
                        alert('操作成功', function(){
                            vm.reload();
                        });
                    }else{
                        alert(r.message);
                    }
                }
            });
        },
        getUser: function(userId){
            $.get(baseURL + "sys/user/getUserInfo/"+userId, function(r){
            	if(r.result != null && r.result != ''){
            		 vm.user = r.result;
            		 vm.user.userPwd="";
            	}else{
            		alert("获取用户数据为空！");
            	}
            });
        },
        getRoleList: function(){
            $.get(baseURL + "sys/role/select", function(r){
            	if(r.result != null && r.result != ''){
            		vm.roleList = r.result;
            	}else{
            		alert("获取角色数据为空！");
            	}
                
            });
        }, 
        orgTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择部门",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#orgLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    //选择上级部门
                    vm.user.orgId = node[0].id;
                    vm.user.orgName = node[0].name;
                    layer.close(index);
                }
            });
        },
        orgTree1: function(){
        	layer.open({
        		type: 1,
        		offset: '50px',
        		skin: 'layui-layer-molv',
        		title: "选择部门",
        		area: ['300px', '450px'],
        		shade: 0,
        		shadeClose: false,
        		content: jQuery("#orgLayer1"),
        		btn: ['确定', '取消'],
        		btn1: function (index) {
        			var node = ztree.getSelectedNodes();
        			//选择上级部门
        			vm.q.orgId = node[0].id;
        			vm.q.orgName = node[0].name;
        			layer.close(index);
        		}
        	});
        },
        reload: function () {
            vm.showList = true;
            vm.showSaveOrUpdate =false;
            vm.showUserInfo = false;
            vm.getorg1();
            layer.closeAll();
        	this.$refs.pwd.type="password";
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{
                	'userName': vm.q.userName,
                	'state':vm.q.state,
                	'orgId':vm.q.orgId,
                	'beginCreateTime': vm.q.beginCreateTime,
					'endCreateTime': vm.q.endCreateTime,
					'beginUpdateTime': vm.q.beginUpdateTime,
					'endUpdateTime': vm.q.endUpdateTime
                	},
                page:page
            }).trigger("reloadGrid");
        },
        selectChange:function (obj){
        	vm.user.positionId = $(obj).val();
        },
        selectChangeState:function(obj){
			vm.q.state = $(obj).val();
		},
		selectChangeOrg:function(obj){
			vm.q.orgId = $(obj).val();
		},
		selectChangeRole:function(obj){
			vm.user.roleId = $(obj).val();
		},
		changePwd:function(obj){
			if(this.$refs.pwd.type =='password'){
				this.$refs.pwd.type="text";
				vm.user.userPwd=null;
			}
			
		},
        getOrglist:function(){
       	 $.get(baseURL + "sys/org/queryOrgList", function(result){
            	if(result.success){
                	vm.orgList  = result.result;
                }else{
                    alert(result.message);
                }
            });
       },searchUserInfo:function(obj){
    	   vm.showList = false;
           vm.showSaveOrUpdate =false;
           vm.showUserInfo = true;
    	   var userId = $(obj).parent().parent().attr("id");
    	   if(userId == null || userId == ''){
    		   alert("请选择要查看的用户！");
    		   return ;
    	   }
    	   $.get(baseURL + "sys/user/getUserInfo/"+userId, function(r){
           	if(r.result != null && r.result != ''){
           		 vm.user = r.result;
                 vm.user.userPwd = "********";
           	}else{
           		alert("获取用户数据为空！");
           	}
           });
    	   
    	   
       }
    }
});
