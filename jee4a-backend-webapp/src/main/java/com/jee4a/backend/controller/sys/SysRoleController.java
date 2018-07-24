package com.jee4a.backend.controller.sys;

import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jee4a.backend.hub.model.sys.SysRole;
import com.jee4a.backend.hub.vo.sys.SysRoleVO;
import com.jee4a.backend.service.sys.SysRoleService;
import com.jee4a.common.Result;
import com.jee4a.common.annotation.OperationLogAnnotation;

/**
 * @date 2018年3月13日
 */
@RestController
@RequestMapping("/sys/role")
@SuppressWarnings("rawtypes")
public class SysRoleController extends AbstractBaseController {

	@Resource
	private SysRoleService sysRoleService;

	/**
	 * @description 角色列表
	 * @return
	 * @date 2018年3月13日
	 */
	@OperationLogAnnotation("角色列表")
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = { "application/json" })
	@RequiresPermissions("sys:role:list")
	public Result<List<SysRoleVO>> queryRoleList(
			SysRoleVO sysRoleVO,
			@RequestParam(name = "pageNum", required = true, defaultValue = "0") int pageNum,
			@RequestParam(name = "pageSize", required = true, defaultValue = "10") int pageSize) {
		return sysRoleService.queryRoleListByPage(sysRoleVO, pageNum, pageSize);
	}

	/**
	 * @description 保存角色
	 * @return
	 * @date 2018年3月13日
	 */
	@OperationLogAnnotation("保存角色")
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = { "application/json" })
	@RequiresPermissions("sys:role:save")
	public Result saveRole(@RequestBody SysRole role) {
		return sysRoleService.addSysRole(role, getUserId());
	}

	/**
	 * @description 角色信息
	 * @return
	 * @date 2018年3月13日
	 */
	@OperationLogAnnotation("角色信息")
	@RequestMapping(value = "/info/{roleId}", method = RequestMethod.GET, produces = { "application/json" })
	@RequiresPermissions("sys:role:info")
	public Result roleInfo(@PathVariable(name = "roleId") Integer roleId) {
		return sysRoleService.queryRole(roleId);
	}

	/**
	 * @description 修改角色
	 * @return
	 * @date 2018年3月13日
	 */
	@OperationLogAnnotation("修改角色")
	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = { "application/json" })
	@RequiresPermissions("sys:role:update")
	public Result updateRole(@RequestBody SysRole sysRole) {
		return sysRoleService.updateSysRole(sysRole, getUserId());
	}

	/**
	 * @description 开启角色
	 * @return
	 * @date 2018年3月13日
	 */
	@OperationLogAnnotation("开启角色")
	@RequestMapping(value = "/open", method = RequestMethod.POST, produces = { "application/json" })
	@RequiresPermissions("sys:role:open")
	public Result openOrClose(@RequestBody Integer[] roleIds) {
		return sysRoleService.open(roleIds);
	}

	/**
	 * @description 关闭角色
	 * @return
	 * @date 2018年3月13日
	 */
	@OperationLogAnnotation("关闭角色")
	@RequestMapping(value = "/close", method = RequestMethod.POST, produces = { "application/json" })
	@RequiresPermissions("sys:role:close")
	public Result close(@RequestBody Integer[] roleIds) {
		return sysRoleService.close(roleIds);
	}

	@RequestMapping(value = "/select", method = RequestMethod.GET, produces = { "application/json" })
	@RequiresPermissions("sys:role:select")
	public Result queryList() {
		return sysRoleService.queryList(null);
	}
	
	@RequestMapping(value = "/queryRoleInfo/{userId}", method = RequestMethod.GET, produces = { "application/json" })
	@RequiresPermissions("sys:role:queryRoleInfo")
	public Result queryRoleInfo(@PathVariable(name = "userId") Integer userId) {
		return sysRoleService.queryRoleInfo(userId);
	}
}
