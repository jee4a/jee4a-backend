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
        showList: true,
        chanShowList:false,
        upShowList:false,
        title: null,
        org:{
            parentName:null,
            parentId:0,
            orderNum:0
        },
        q:{
        	name:null,
        	parentName:null
        
        }
    },
    methods: {
        getorg: function(){
            //加载部门树
            $.get(baseURL + "sys/org/select", function(r){
            	if(r.result !=null && r.result !=''){
            		ztree = $.fn.zTree.init($("#orgTree"), setting, r.result);
                    var node = ztree.getNodeByParam("id", vm.org.parentId);
                    ztree.selectNode(node);
                    vm.org.parentName = node.name;
            	}else{
            		aletr("获取部门数据为空");
            	}
                
            })
        },
        add: function(){
            vm.showList = false;
            vm.upShowList=true;
            vm.title = "新增";
            vm.org = {parentName:null,parentId:0,orderNum:0};
            $("#colseId").hide();
            vm.getorg();
        },
        update: function () {
            var id = getId();
            if(id == false){
                return ;
            }
            $("#colseId").show();
            $.get(baseURL + "sys/org/info/"+id, function(r){
                vm.showList = false;
                vm.upShowList=true;
                vm.title = "修改";
                if(r.result != null && r.result !=''){
                	 vm.org = r.result;
                     vm.getorg();
                }else{
                	alert("获取部门信息为空！");
                }
               
            });
        },
        updateTZ:function(obj){
        	var tr=$(obj).parent().parent();
        	var id=$(tr).find(":radio").val();
        	$.get(baseURL + "sys/org/info/"+id, function(r){
        		vm.showList = false;
                vm.chanShowList=true;
                vm.title = "调整";
                if(r.result != null && r.result !=''){
                	 vm.org = r.result;
                	 vm.q.parentName = r.result.parentName;
                     vm.getorg();
                }else{
                	alert("获取部门信息为空！");
                }
               
            });
        },
        del: function () {
            var id = getId();
            if(id == false){
                return ;
            }
            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/org/delete",
                    data: "id=" + id,
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
        saveOrUpdate: function (event) {
            var url = vm.org.id == null ? "sys/org/add" : "sys/org/update";
            //var test ='^[\u4e00-\u9fa5][0-9]*,{0,}$'
            var reg = new RegExp("^(?![0-9]+$)[0-9\u4E00-\u9FA5]{1,40}$");
			if(!reg.test(vm.org.name)){
				alert("请以汉字 或者 汉字+数字的格式输入");
			}else{
             $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.org),
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
		   }
        },
        chang:function(event){
        	var url = vm.org.id == null ? "sys/org/add" : "sys/org/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.org),
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
                    vm.org.parentId = node[0].id;
                    vm.org.parentName = node[0].name;
                    layer.close(index);
                }
            });
        },
        reload: function () {
        	layer.closeAll();
            vm.showList = true;
            vm.chanShowList=false;
            vm.upShowList=false;
            var opt = {
                    query:{
                        name:vm.q.name
                    }
                };
            org.table.refresh(opt);
        }
    }
});

var org = {
    id: "orgTable",
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
org.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true},
        {title: '部门ID', field: 'id', visible: false, align: 'center', valign: 'middle', width: '80px'},
        {title: '部门名称', field: 'name', align: 'center', valign: 'middle', sortable: true, width: '180px'},
        {title: '上级部门', field: 'parentName', align: 'center', valign: 'middle', sortable: true, width: '100px'},
        {title: '创建时间', field: 'createTime', align: 'center', valign: 'middle', sortable: true, width: '100px'},
        {title: '创建人', field: 'createName', align: 'center', valign: 'middle', sortable: true, width: '100px'}
        ]
    return columns;
};


function getId () {
    var selected = $('#orgTable').bootstrapTreeTable('getSelections');
    if (selected.length == 0) {
        alert("请选择一条记录");
        return false;
    } else {
        return selected[0].id;
    }
}


$(function () {
    $.get(baseURL + "sys/org/info", function(r){
        var colunms = org.initColumn();
        var table = new TreeTable(org.id, baseURL + "sys/org/list", colunms);
        table.setRootCodeValue(r.result);
        table.setExpandColumn(2);
        table.setIdField("id");
        table.setCodeField("id");
        table.setParentCodeField("parentId");
        table.setExpandAll(false);
        table.init();
        org.table = table;
    });
});
