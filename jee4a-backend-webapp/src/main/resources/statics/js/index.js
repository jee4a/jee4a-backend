//生成菜单
var menuItem = Vue.extend({
    name: 'menu-item',
    props:{item:{}},
    template:[
        '<li>',
        '	<a v-if="item.resourceType === 0" href="javascript:;">',
        '		<i v-if="item.icon != null" :class="item.icon"></i>',
        '		<span>{{item.resourceName}}</span>',
        '		<i class="fa fa-angle-left pull-right"></i>',
        '	</a>',
        '	<ul v-if="item.resourceType === 0" class="treeview-menu">',
        '		<menu-item :item="item" v-for="item in item.list"></menu-item>',
        '	</ul>',

        '	<a v-if="item.resourceType === 1 && item.parentId === 0" :href="\'#\'+item.url">',
        '		<i v-if="item.icon != null" :class="item.icon"></i>',
        '		<span>{{item.resourceName}}</span>',
        '	</a>',

        '	<a v-if="item.resourceType === 1 && item.parentId != 0" :href="\'#\'+item.url"><i v-if="item.icon != null" :class="item.icon"></i><i v-else class="fa fa-circle-o"></i> {{item.resourceName}}</a>',
        '</li>'
    ].join('')
});

//iframe自适应
$(window).on('resize', function() {
	var $content = $('.content');
	$content.height($(this).height() - 120);
	$content.find('iframe').each(function(index) {
		$(this).height($content.height());
		$(this).attr("name","iframe"+Number(index+1));
	});
}).resize();

//注册菜单组件
Vue.component('menuItem',menuItem);

var vm = new Vue({
	el:'#crmapp',
	data:{
		user:{},
		menuList:{},
		main:"main.html",
		password:'',
		newPassword:'',
		trueNewPassword:'',
        navTitle:"我的工作台",
        navTitleInfo:""
	},
	methods: {
		getMenuList: function (event) {
			$.getJSON("sys/resource/nav?_"+$.now(), function(data){
				if(data.success){
					vm.menuList = data.result;
				}
			});
		},
		getUser: function(){
			$.getJSON("sys/user/info?_"+$.now(), function(data){
				if(data.success){
					vm.user = data.result;
				}
			});
		},
		updatePassword: function(){
			layer.open({
				type: 1,
				skin: 'layui-layer-molv',
				title: "修改密码",
				area: ['550px', '310px'],
				shadeClose: false,
				content: jQuery("#passwordLayer"),
				btn: ['修改','取消'],
				btn1: function (index) {
					if(vm.newPassword != vm.trueNewPassword ){
						layer.alert("两次输入密码不一致");
	            		return ;
					}
	            	var pwd =/^(?!(?:[^a-zA-Z]+|\D+|[a-zA-Z0-9]+)$).{8,}$/;
	            	if(!pwd.test(vm.newPassword)){
	            		layer.alert("密码必须包含数字、英文字母和特殊符号8位及以上");
	            		return ;
	            	}
					var data = "password="+vm.password+"&newPassword="+vm.newPassword+"&trueNewPassword="+vm.trueNewPassword;
					$.ajax({
						type: "POST",
					    url: "sys/user/password",
					    data: data,
					    dataType: "json",
					    success: function(data){
							if(data.success){
								layer.close(index);
								layer.alert('修改成功', function(index){
									window.location.href='/logout';
								});
							}else{
								layer.alert(data.message);
							}
						}
					});
	            }
			});
		},
        donate: function () {
            layer.open({
                type: 2,
                title: false,
                area: ['806px', '467px'],
                closeBtn: 1,
                shadeClose: false,
                content: ['', 'no']
            });
        }
	},
	created: function(){
		this.getMenuList();
		this.getUser();
	},
	updated: function(){
		//路由
		var router = new Router();
		routerList(router, vm.menuList);
		router.start();
	}
});



function routerList(router, menuList){
	for(var key in menuList){
		
		var menu = menuList[key];
		
		if(menu.resourceType == 0){
			
			routerList(router, menu.list);
			
		}else if(menu.resourceType == 1){
			
			router.add('#'+menu.url, function() {
				var url = window.location.hash;
				//替换iframe的url
			    vm.main = url.replace('#', '');
			    
			    //导航菜单展开
			 
			    $(".treeview-menu li").removeClass("active");
			    $("a[href='"+url+"']").parents("li").addClass("active");

			    vm.navTitleInfo=$(".active").find("a").eq(0).text();
			    vm.navTitle = $("a[href='"+url+"']").text();
			});
		}
	}
}
