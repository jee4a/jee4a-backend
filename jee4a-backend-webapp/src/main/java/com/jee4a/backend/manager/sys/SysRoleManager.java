package com.jee4a.backend.manager.sys;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.jee4a.backend.common.enums.sys.RoleStateEnum;
import com.jee4a.backend.hub.mapper.sys.SysRoleMapper;
import com.jee4a.backend.hub.mapper.sys.SysRoleOrgMapper;
import com.jee4a.backend.hub.mapper.sys.SysRoleResourceMapper;
import com.jee4a.backend.hub.model.BaseConstant;
import com.jee4a.backend.hub.model.sys.SysRole;
import com.jee4a.backend.hub.model.sys.SysRoleOrg;
import com.jee4a.backend.hub.model.sys.SysRoleResource;
import com.jee4a.backend.hub.vo.sys.SysRoleVO;
import com.jee4a.common.BaseManager;

/**
 * @description
 */
@Component
public class SysRoleManager extends BaseManager {
	@Autowired
	private SysRoleMapper sysRoleMapper;
	@Autowired
	private SysRoleResourceMapper sysRoleResourceMapper;
	@Autowired
	private SysRoleOrgMapper sysRoleOrgMapper;
	/**
	 * @description 查询角色列表
	 * @param vo
	 * @date 2018年3月16日
	 */
	public PageInfo<SysRole> queryRoleListByPage(SysRoleVO sysRole,
			int pageNum, int pageSize) {
		PageMethod.startPage(pageNum, pageSize);
		List<SysRole> sysRoleList = sysRoleMapper.querySysRoleList(sysRole,
				pageNum, pageSize);
		return new PageInfo<>(sysRoleList);
	}

	/**
	 * @description 查询角色
	 * @param vo
	 */
	public SysRole queryRole(Integer roleId) {
		SysRole sysRole = sysRoleMapper.selectByPrimaryKey(roleId);
		// 查询角色对应的菜单
		SysRoleResource sysRoleResource = new SysRoleResource();
		sysRoleResource.setRoleId(roleId);
		List<Integer> menuIdList = new ArrayList<>();
		List<SysRoleResource> sysRoleResourceList = sysRoleResourceMapper
				.querySysRoleResourceList(sysRoleResource);
		if (!sysRoleResourceList.isEmpty()) {
			for (SysRoleResource sRR : sysRoleResourceList) {
				menuIdList.add(sRR.getResourceId());
			}
		}
		sysRole.setMenuIdList(menuIdList);
		return sysRole;
	}

	/**
	 * @description 新增角色
	 * @param vo
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void addSysRole(SysRole sysRole, Integer userId) {
		// 保存角色信息
		sysRole.setCreator(userId);
		sysRole.setUpdator(userId);
		sysRole.setCreateTime(new Date());
		sysRole.setUpdateTime(new Date());
		sysRole.setIsDeleted(RoleStateEnum.START.getKey());
		sysRoleMapper.insert(sysRole);
		// 保存角色与菜单关系
		SysRoleResource sysRoleResource = null;
		for (Integer sysResource : sysRole.getMenuIdList()) {
			sysRoleResource = new SysRoleResource();
			sysRoleResource.setCreator(userId);
			sysRoleResource.setUpdator(userId);
			sysRoleResource.setCreateTime(new Date());
			sysRoleResource.setUpdateTime(new Date());
			sysRoleResource.setRoleId(sysRole.getId());
			sysRoleResource.setResourceId(sysResource);
			sysRoleResource.setIsDeleted(RoleStateEnum.START.getKey());
			sysRoleResourceMapper.insert(sysRoleResource);
		}
		// 保存角色与部门关系
		SysRoleOrg sysRoleOrg = new SysRoleOrg();
		sysRoleOrg.setCreator(userId);
		sysRoleOrg.setUpdator(userId);
		sysRoleOrg.setCreateTime(new Date());
		sysRoleOrg.setUpdateTime(new Date());
		sysRoleOrg.setRoleId(sysRole.getId());
		sysRoleOrg.setOrgId(sysRole.getOrgId());
		sysRoleOrg.setIsDeleted(RoleStateEnum.START.getKey());
		sysRoleOrgMapper.insert(sysRoleOrg);
		// 更新角色部门信息
		sysRole.setOrgId(sysRole.getOrgId());
		sysRoleMapper.updateByPrimaryKeySelective(sysRole);
	}

	/**
	 * @description 修改角色
	 * @param vo
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void updateSysRole(SysRole sysRole, Integer userId) {
		// 修改角色与部门关系
		SysRoleOrg paramaSysRoleOrg = new SysRoleOrg();
		paramaSysRoleOrg.setRoleId(sysRole.getId());
		SysRoleOrg sysRoleOrg = sysRoleOrgMapper
				.querySysRoleOrg(paramaSysRoleOrg);

		if (sysRoleOrg != null) {
			sysRoleOrg.setIsDeleted(RoleStateEnum.CLOSE.getKey());
			sysRoleOrg.setUpdateTime(new Date());
			sysRoleOrgMapper.updateByPrimaryKey(sysRoleOrg);
			sysRoleOrg.setIsDeleted(RoleStateEnum.START.getKey());
			sysRoleOrg.setId(null);
			sysRoleOrgMapper.insert(sysRoleOrg);
		} else {
			sysRoleOrg = new SysRoleOrg();
			sysRoleOrg.setCreator(userId);
			sysRoleOrg.setUpdator(userId);
			sysRoleOrg.setCreateTime(new Date());
			sysRoleOrg.setUpdateTime(new Date());
			sysRoleOrg.setRoleId(sysRole.getId());
			sysRoleOrg.setIsDeleted(RoleStateEnum.START.getKey());
			sysRoleOrg.setOrgId(sysRole.getOrgId());
			sysRoleOrgMapper.insert(sysRoleOrg);
		}

		sysRole.setUpdator(userId);
		sysRole.setUpdateTime(new Date());
		sysRoleMapper.updateByPrimaryKeySelective(sysRole);
		List<Integer> menuIdList = sysRole.getMenuIdList();
		if (CollectionUtils.isEmpty(menuIdList)) {
			return;
		}
		SysRoleResource paramaSysRoleResource = new SysRoleResource();
		paramaSysRoleResource.setRoleId(sysRole.getId());
		List<SysRoleResource> sysRoleResourceList = sysRoleResourceMapper
				.querySysRoleResourceList(paramaSysRoleResource);
		if (!sysRoleResourceList.isEmpty()) {
			for (SysRoleResource srr : sysRoleResourceList) {
				srr.setIsDeleted(RoleStateEnum.CLOSE.getKey());
				srr.setUpdateTime(new Date());
				sysRoleResourceMapper.updateByPrimaryKey(srr);
			}
		}
		// 保存角色与菜单关系
		SysRoleResource sysRoleResource = null;
		for (Integer sysResource : menuIdList) {
			sysRoleResource = new SysRoleResource();
			sysRoleResource.setRoleId(sysRole.getId());
			sysRoleResource.setResourceId(sysResource);
			SysRoleResource oldSysRoleResource = sysRoleResourceMapper
					.querySysRoleResource(sysRoleResource);
			if (oldSysRoleResource == null) {
				sysRoleResource.setCreator(userId);
				sysRoleResource.setUpdator(userId);
				sysRoleResource.setCreateTime(new Date());
				sysRoleResource.setUpdateTime(new Date());
				sysRoleResource.setRoleId(sysRole.getId());
				sysRoleResource.setResourceId(sysResource);
				sysRoleResource.setIsDeleted(RoleStateEnum.START.getKey());
				sysRoleResourceMapper.insert(sysRoleResource);
			} else {
				oldSysRoleResource.setIsDeleted(RoleStateEnum.START.getKey());
				sysRoleResource.setUpdateTime(new Date());
				sysRoleResourceMapper
						.updateByPrimaryKeySelective(oldSysRoleResource);
			}
		}

	}

	/**
	 * @description 开启角色
	 * @param vo
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void open(Integer[] roleIds) {
		SysRole sysRole = new SysRole();
		for (Integer roleId : roleIds) {
			sysRole.setId(roleId);
			sysRole.setIsDeleted(RoleStateEnum.START.getKey());
			sysRoleMapper.updateByPrimaryKeySelective(sysRole);
		}
	}

	/**
	 * @description 关闭角色
	 * @param vo
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void close(Integer[] roleIds) {
		SysRole sysRole = new SysRole();
		for (Integer roleId : roleIds) {
			sysRole.setId(roleId);
			sysRole.setIsDeleted(RoleStateEnum.CLOSE.getKey());
			sysRoleMapper.updateByPrimaryKeySelective(sysRole);
		}
	}

	public List<SysRole> queryList(SysRole role) {
		return sysRoleMapper.queryList(role);
	}

	public SysRole queryUserRoleInfo(Integer userId) {
		return sysRoleMapper.queryUserRoleInfo(userId);
	}
}
