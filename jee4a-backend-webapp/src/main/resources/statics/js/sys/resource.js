var setting = {
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
var ztree;

var vm = new Vue({
    el:'#crmapp',
    data:{
        showList: true,
        title: null,
        resource:{
            parentName:null,
            parentId:0,
            resourceType:1,
            isShow:1,
            orderBy:0
        }
    },
    methods: {
        getResource: function(id){
            //加载菜单树
            $.get(baseURL + "sys/resource/select", function(data){
                ztree = $.fn.zTree.init($("#resourceTree"), setting, data.result);
                var node = ztree.getNodeByParam("id", vm.resource.parentId);
                ztree.selectNode(node);
                
                vm.resource.parentName = node.name;
            })
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.resource = {parentName:null,parentId:0,resourceType:1,orderBy:0,isShow:1,icon:"fa fa-circle-o"};
            vm.getResource();
        },
        update: function () {
            var resourceId = getResourceId();
            if(resourceId == null){
                return ;
            }
            $.get(baseURL + "sys/resource/info/"+resourceId, function(data){
                vm.showList = false;
                vm.title = "修改";
                vm.resource = data.result;

                vm.getResource();
            });
        },
        del: function () {
            var resourceId = getResourceId();
            if(resourceId == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/resource/delete",
                    data: "id=" + resourceId,
                    success: function(data){
                    	if(data.success){
                   		 	alert('操作成功', function(){
                                vm.reload();
                            });
                    	}else{
                           alert(data.message);
                       }
                    }
                });
            });
        },
        saveOrUpdate: function () {
            if(vm.validator()){
                return ;
            }

            var url = vm.resource.id == null ? "sys/resource/save" : "sys/resource/update";
            $.ajax({
                type: "POST",
                url:  baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.resource),
                success: function(data){
                	if(data.success){
                		 alert('操作成功', function(){
                             vm.reload();
                         });
                	}else{
                        alert(data.message);
                    }
                }
            });
        },
        resourceTree: function(){
            layer.open({
            	type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择菜单",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#resourceLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    //选择上级菜单
                    vm.resource.parentId = node[0].id;
                    vm.resource.parentName = node[0].resourceName;

                    layer.close(index);
                }
            });
        },
        reload: function () {
            vm.showList = true;
            Resource.table.refresh();
        },
        validator: function () {
            if(isBlank(vm.resource.resourceName)){
                alert("菜单名称不能为空");
                return true;
            }

            //菜单
            if(vm.resource.resourceType === 1 && isBlank(vm.resource.url)){
                alert("菜单URL不能为空");
                return true;
            }
        }
    }
});


var Resource = {
    id: "resourceTable",
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
Resource.initColumn = function () {
    var columns = [
        {field: 'selectItem', radio: true},
        {title: '菜单ID', field: 'id', visible: false, align: 'center', valign: 'middle', width: '80px'},
        {title: '菜单名称', field: 'resourceName', align: 'center', valign: 'middle', sortable: true, width: '180px'},
        {title: '上级菜单', field: 'parentName', align: 'center', valign: 'middle', sortable: true, width: '100px'},
        {title: '图标', field: 'icon', align: 'center', valign: 'middle', sortable: true, width: '80px', formatter: function(item, index){
            return item.icon == null ? '' : '<i class="'+item.icon+' fa-lg"></i>';
        }},
        {title: '类型', field: 'resourceType', align: 'center', valign: 'middle', sortable: true, width: '100px', formatter: function(item, index){
            if(item.resourceType === 0){
                return '<span class="label label-primary">目录</span>';
            }
            if(item.resourceType === 1){
                return '<span class="label label-success">菜单</span>';
            }
            if(item.resourceType === 2){
                return '<span class="label label-warning">按钮</span>';
            }
        }},
        {title: '排序号', field: 'orderBy', align: 'center', valign: 'middle', sortable: true, width: '100px'},
        {title: '是否显示', field: 'show', align: 'center', valign: 'middle', sortable: true, width: '80px', formatter: function(item, index){
            if(item.show === 0){
              	return '否';
            }
            if(item.show === 1){
            	return '是';
            }
        }},
        {title: '菜单URL', field: 'url', align: 'center', valign: 'middle', sortable: true, width: '160px'},
        {title: '授权标识', field: 'perms', align: 'center', valign: 'middle', sortable: true}]
    return columns;
};


function getResourceId () {
    var selected = $('#resourceTable').bootstrapTreeTable('getSelections');
    if (selected.length == 0) {
        alert("请选择一条记录");
        return false;
    } else {
        return selected[0].id;
    }
}


$(function () {
    var colunms = Resource.initColumn();
    var table = new TreeTable(Resource.id, baseURL + "sys/resource/list", colunms);
    table.setExpandColumn(2);
    table.setIdField("id");
    table.setCodeField("id");
    table.setParentCodeField("parentId");
    table.setExpandAll(false);
    table.init();
    Resource.table = table;
});
