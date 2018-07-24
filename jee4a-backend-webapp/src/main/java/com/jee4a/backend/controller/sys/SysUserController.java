package com.jee4a.backend.controller.sys;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.jee4a.backend.common.shiro.ShiroTag;
import com.jee4a.backend.hub.vo.sys.SysUserVO;
import com.jee4a.backend.service.sys.SysPositionService;
import com.jee4a.backend.service.sys.SysUserService;
import com.jee4a.common.Result;
import com.jee4a.common.annotation.OperationLogAnnotation;



/**
 * <p></p> 
 */
@RestController
@RequestMapping("/sys/user")
@SuppressWarnings("rawtypes")
public class SysUserController  extends AbstractBaseController{
	@Resource
	private SysUserService sysUserService  ;

	@Resource
	private ShiroTag tag;
	@Resource
	private SysPositionService sysPositionService;
	@OperationLogAnnotation("修改密码 ")
	@RequestMapping(value= {"/password"},method=RequestMethod.POST,produces={"application/json"})
	public Result password(String password, String newPassword, String trueNewPassword,HttpServletResponse response){
		return sysUserService.password(password, newPassword,trueNewPassword,getUser()) ;
	}
	
	@RequestMapping(value= {"info"},method=RequestMethod.GET,produces={"application/json"})
	public Result info(){
		return sysUserService.getUserInfo(this.getUserId());
	}
	
	@OperationLogAnnotation("用户列表列表")
	@RequestMapping(value = "/list" , method = RequestMethod .GET ,produces={"application/json"})
	@RequiresPermissions("sys:user:list")
	public Result<PageInfo<SysUserVO>> queryLogList(SysUserVO sysUserVO, @RequestParam(name = "pageNum",required = true,defaultValue = "0") int pageNum,
			@RequestParam(name = "pageSize",required = true,defaultValue = "10") int pageSize) {
		return  sysUserService.queryPage(sysUserVO, pageNum, pageSize);
	}
	
	/**
	 * 保存用户
	 */
	@OperationLogAnnotation("添加用户")
	@RequestMapping(value = "/save" ,method = RequestMethod .POST ,produces={"application/json"})
	@RequiresPermissions("sys:user:save")
	public Result save(@RequestBody SysUserVO user){
		return sysUserService.insertUser(user, this.getUserId());
	}
	
	
	/**
	 * 修改用户
	 */
	@OperationLogAnnotation("修改用户信息")
	@RequestMapping(value="/update",method = RequestMethod.POST ,produces={"application/json"})
	@RequiresPermissions("sys:user:update")
	public Result update(@RequestBody SysUserVO user){
		user.setUpdator(this.getUserId());
		return sysUserService.updateUserInfo(user);
	}
	
	@OperationLogAnnotation("获取用户详情")
	@RequestMapping(value = "/getUserInfo/{userId}",method = RequestMethod .GET ,produces={"application/json"})
	@RequiresPermissions("sys:user:info")
	public Result getUserInfo(@PathVariable("userId")Integer userId) {
		return sysUserService.getUserInfo(userId);
	}
	
	
	@OperationLogAnnotation("删除用户")
	@RequestMapping(value = "/delete",method = RequestMethod.POST ,produces={"application/json"})
	@RequiresPermissions("sys:user:delete")
	public Result delete(@RequestBody String userIds) {
		return sysUserService.deleteUser(userIds,this.getUserId());
	}
	

	@RequestMapping(value = "/queryPositionList" , method = RequestMethod.GET ,produces={"application/json"})
	@RequiresPermissions("sys:user:getUserPositionInfo")
	public Result queryPositionList() {
		return sysPositionService.queryList(null);
	}
	
	
	@RequestMapping(value = "/getUserPerms" , method = RequestMethod.GET ,produces={"application/json"})
	public Result getUserPerms(String userPerms) {
		Result result = new Result();
		result.put("userPerms",tag.hasPermission(userPerms)?true:false);
		result.setSuccess();
		return result;
	}
}
