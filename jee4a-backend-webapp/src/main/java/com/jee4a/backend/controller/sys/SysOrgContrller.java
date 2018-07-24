package com.jee4a.backend.controller.sys;


import java.util.List;

import javax.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jee4a.backend.hub.model.sys.SysOrg;
import com.jee4a.backend.hub.vo.sys.SysOrgVO;
import com.jee4a.backend.service.sys.SysOrgService;
import com.jee4a.common.Result;
import com.jee4a.common.annotation.OperationLogAnnotation;

@RequestMapping("/sys/org")
@RestController
@SuppressWarnings("rawtypes")
public class SysOrgContrller extends AbstractBaseController{

	@Resource
	private SysOrgService  sysOrgService;
	
	/**
	 * @Description: 添加部门
	 */

	@OperationLogAnnotation("添加部门信息")
	@RequiresPermissions("sys:org:add")
	@RequestMapping(value = "/add",method=RequestMethod.POST,produces={"application/json"})
	public Result addSysOrg(@RequestBody SysOrg org) {
		return sysOrgService.insertOrg(org,this.getUserId());
	}
	
	/**
	 * @Description: 查找部门列表
	 */
	@OperationLogAnnotation("查询部门信息")
	@RequiresPermissions("sys:org:select")
	@RequestMapping(value = "/select",method=RequestMethod.GET,produces={"application/json"})
	public Result queryList(SysOrg org) {
		return sysOrgService.queryList(org, this.getUserId());
	}
	
	/**
	 * @Description:查询部门上级菜单
	 */
	@OperationLogAnnotation("查询部门上级菜单")
	@RequestMapping(value = "/info",method=RequestMethod.GET,produces={"application/json"})
	@RequiresPermissions("sys:org:list")
	public Result info(){
		return sysOrgService.getOrgInfo(this.getUserId());
	}
	
	/**
	 * @Description: 获取部门列表
	 */
	@OperationLogAnnotation("部门列表")
	@RequestMapping(value = "/list",method=RequestMethod.GET,produces={"application/json"})
	@RequiresPermissions("sys:org:list")
	public List<SysOrgVO> getList(String name){
		return sysOrgService.getOrgList(name).get();
	}
	
	
	/**
	 * @Description: 获取部门详情  
	 */
	@RequestMapping(value = "/info/{orgId}",method=RequestMethod.GET,produces={"application/json"})
	@RequiresPermissions("sys:org:info")
	public Result info(@PathVariable("orgId") Integer orgId){
		return sysOrgService.getOrgById(orgId);
	}
	
	/**
	 * @Description:修改部门信息  
	 */
	@OperationLogAnnotation("修改部门信息")
	@RequestMapping(value = "/update",method=RequestMethod.POST,produces={"application/json"})
	@RequiresPermissions("sys:org:update")
	public Result update(@RequestBody SysOrg org){
		org.setUpdator(getUserId());
		return sysOrgService.updateOrg(org);
	}
	
	/**
	 * @Description: 删除部门   
	 */
	@OperationLogAnnotation("删除部门")
	@RequestMapping(value = "/delete",method=RequestMethod.POST,produces={"application/json"})
	@RequiresPermissions("sys:org:delete")
	public Result delete(Integer id){
		return sysOrgService.updateById(id);
	}
	
	@OperationLogAnnotation("查询所有部门")
	@RequiresPermissions("sys:org:queryOrgList")
	@RequestMapping(value = "/queryOrgList",method=RequestMethod.GET,produces={"application/json"})
	public Result queryOrgList(SysOrg org){
		return sysOrgService.queryOrgList(null);
	}
}
