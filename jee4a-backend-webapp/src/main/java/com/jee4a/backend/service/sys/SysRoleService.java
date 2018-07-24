package com.jee4a.backend.service.sys;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.jee4a.backend.common.enums.sys.SysRoleTypeEnum;
import com.jee4a.backend.common.util.DateUtils;
import com.jee4a.backend.hub.mapper.sys.SysRoleMapper;
import com.jee4a.backend.hub.mapper.sys.SysUserRoleMapper;
import com.jee4a.backend.hub.model.sys.SysRole;
import com.jee4a.backend.hub.model.sys.SysUserRole;
import com.jee4a.backend.hub.vo.sys.SysRoleVO;
import com.jee4a.backend.manager.sys.SysRoleManager;
import com.jee4a.common.BaseService;
import com.jee4a.common.Result;
import com.jee4a.common.exceptions.BusinessException;

/**
 * 角色业务接口
 * 
 */
@Service
public class SysRoleService extends BaseService {

	@Resource
	private SysRoleManager sysRoleManager;

	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;

	@Resource
	private SysRoleMapper sysRoleMapper;

	/**
	 * @description 获取角色列表
	 * @param vo
	 */
	public Result<List<SysRoleVO>> queryRoleListByPage(SysRoleVO sysRoleVO,
			int pageNum, int pageSize) {
		Result<List<SysRoleVO>> result = new Result<>();
		try {

			if (org.apache.commons.lang.StringUtils.isNotBlank(sysRoleVO
					.getQueryConditions())) {
				if (SysRoleTypeEnum.ROLEID.getK().equals(
						sysRoleVO.getQueryConditions())) {
					sysRoleVO.setId(Integer.parseInt(sysRoleVO.getName()));
				} else if (SysRoleTypeEnum.ROLENAME.getK().equals(
						sysRoleVO.getQueryConditions())) {
					sysRoleVO.setRoleName(sysRoleVO.getName());
				} else if (SysRoleTypeEnum.CREATENAME.getK().equals(
						sysRoleVO.getQueryConditions())) {
					sysRoleVO.setCreateName(sysRoleVO.getName());
				} else if (SysRoleTypeEnum.UPDATENAME.getK().equals(
						sysRoleVO.getQueryConditions())) {
					sysRoleVO.setUpdateName(sysRoleVO.getName());
				}
			}

			
			String[] paramTime = DateUtils.getParamDate(sysRoleVO.getBeginCreateTime(), sysRoleVO.getEndCreateTime());
			sysRoleVO.setBeginCreateTime(paramTime[0]);
			sysRoleVO.setEndCreateTime(paramTime[1]);
			
			String[] time = DateUtils.getParamDate(sysRoleVO.getBeginUpdateTime(), sysRoleVO.getEndUpdateTime());
			sysRoleVO.setBeginUpdateTime(time[0]);
			sysRoleVO.setEndUpdateTime(time[1]);
			
			PageInfo<SysRole> pageInfo = sysRoleManager.queryRoleListByPage(
					sysRoleVO, pageNum, pageSize);
			result.put("pageInfo", pageInfo);
			result.setSuccess();
			result.print();
		} catch (Exception e) {
			result.setDefaultError();
			logger.error("error : {}", e.getMessage());
		}
		return result;
	}

	/**
	 * @description 新增角色
	 */
	public Result addSysRole(SysRole sysRole, Integer userId) {
		Result result = new Result();
		try {
			checkParams(sysRole);
			sysRoleManager.addSysRole(sysRole, userId);
			result.setSuccess();
		} catch (BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		result.print();
		return result;
	}

	/**
	 * @description 查询角色
	 * @param vo
	 */
	public Result queryRole(Integer roleId) {
		Result result = new Result();
		try {
			SysRole sysRole = sysRoleManager.queryRole(roleId);
			result.put("role", sysRole);
			result.setSuccess();
			result.print();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}

	/**
	 * @description 修改角色
	 * @param vo
	 */
	public Result updateSysRole(SysRole sysRole, Integer userId) {
		Result result = new Result();
		try {
			checkParams(sysRole);
			sysRoleManager.updateSysRole(sysRole, userId);
			result.setSuccess();
		} catch (BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}

	/**
	 * @description 启动角色
	 * @param vo
	 */
	public Result open(Integer[] roleIds) {
		Result result = new Result();
		try {
			sysRoleManager.open(roleIds);
			result.setSuccess();
		} catch (BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}

	/**
	 * @description 关闭角色
	 * @param vo
	 */
	public Result close(Integer[] roleIds) {
		Result result = new Result();
		List<Integer> roleIdList = new ArrayList<Integer>(0);
		try {

			for (Integer roleId : roleIds) {
				List<SysUserRole> sysUserRoleList = sysUserRoleMapper
						.selectByRoleId(roleId);
				if (sysUserRoleList != null && sysUserRoleList.size() > 0) {
				} else {
					roleIdList.add(roleId);
				}
			}
			if (roleIdList.size() > 0) {
				Integer[] roleIdss = new Integer[roleIdList.size()];
				roleIdList.toArray(roleIdss);
				sysRoleManager.close(roleIdss);
				result.setSuccess();
			} else {
				throw new BusinessException(-1000, "角色正在使用中，不可禁用！");
			}

		} catch (BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}

	/**
	 * @description 入参检查
	 * @param vo
	 */
	private void checkParams(SysRole vo) {
		if (org.apache.commons.lang.StringUtils.isEmpty(vo.getRoleName())) {
			throw new BusinessException(-1000, "角色名称不能为空");
		} else {

			// 查询角色名是否已存在
			SysRole role = sysRoleMapper.selectByRoleName(vo.getRoleName());
			if (role != null && role.getId() != vo.getId()) {
				throw new BusinessException(-1000, "角色名称已存在");
			}

		}
		/*
		 * if (null == vo.getOrgId()) { throw new BusinessException(-1001,
		 * "所属部门不能为空"); } if
		 * (org.apache.commons.lang.StringUtils.isEmpty(vo.getOrgName())) {
		 * throw new BusinessException(-1000, "所属部门不能为空"); }
		 */

	}

	public Result queryList(SysRole role) {
		Result result = new Result();
		try {
			result.put(sysRoleManager.queryList(role));
			result.setSuccess();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}

	public Result queryRoleInfo(Integer userId) {
		Result result = new Result();
		try {
			result.put(sysRoleManager.queryUserRoleInfo(userId));
			result.setSuccess();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}
}
