package com.jee4a.backend.manager.sys;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jee4a.backend.common.enums.sys.SysOrgStateEnum;
import com.jee4a.backend.common.enums.sys.SysUserStateEnum;
import com.jee4a.backend.hub.mapper.sys.SysOrgMapper;
import com.jee4a.backend.hub.mapper.sys.SysUserMapper;
import com.jee4a.backend.hub.mapper.sys.SysUserRoleMapper;
import com.jee4a.backend.hub.model.BaseConstant;
import com.jee4a.backend.hub.model.sys.SysUser;
import com.jee4a.backend.hub.model.sys.SysUserRole;
import com.jee4a.backend.hub.vo.sys.SysUserVO;
import com.jee4a.common.BaseManager;
import com.jee4a.common.exceptions.BusinessException;
@Component
public class SysUserManager extends BaseManager{
	
	
	@Resource
	private SysUserMapper sysUserMapper;
	
	@Resource
	private SysOrgMapper sysOrgMapper;
	
	@Resource
	private SysUserRoleMapper sysUserRoleMapper;
	
	public PageInfo<SysUserVO> queryPage(SysUserVO sysUserVO,int pageNum, int pageSize) {
    	PageHelper.startPage(pageNum,pageSize);  
    	List<SysUserVO> list = sysUserMapper.queryPage(sysUserVO) ;
    	for (SysUserVO user : list) {
    		String roleNames=sysUserRoleMapper.querUserListName(user.getId());
    		user.setRoleName(roleNames);
		}
    	return new PageInfo<>(list)  ;
    }
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void insertUser(SysUserVO userVo) {
		SysUser user = SysUserVO.toModel(userVo);
		user.setCreateTime(new Date());
		user.setIsDeleted(SysUserStateEnum.NORMAL.getK());
		sysUserMapper.insert(user);
		
		userVo.setRoleIdList(Arrays.asList(userVo.getRoleId()));
		if(!userVo.getRoleIdList().isEmpty()) {
			this.inserUserAndRole(userVo.getRoleIdList(), user.getId());
		}
	}
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public SysUser selectByPrimaryKey(Integer id) {
		return sysUserMapper.selectByPrimaryKey(id);
	}
	
	public SysUserVO getUserInfo(Integer userId) {
		return sysUserMapper.selectUserDeailts(userId);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void updateUserInfo(SysUserVO userVO) {
		SysUser user = SysUserVO.toModel(userVO);
		Integer userId = userVO.getId();
		if(userId == null) {
			return;
		}
		user.setUpdateTime(new Date());
		sysUserMapper.updateByPrimaryKeySelective(user);
		Integer roleIdOld= sysUserRoleMapper.queryUserRoleId(user.getId());
		
		if(!userVO.isResetRole() || !userVO.getRoleId().equals(roleIdOld)){
			SysUserRole userRole = new SysUserRole();
			userRole.setUserId(user.getId());
			userRole.setIsDeleted(SysOrgStateEnum.DELETED.getK());
			userRole.setUpdateTime(new Date());
			userRole.setUpdator(userVO.getUpdator());
			sysUserRoleMapper.updateByPrimaryKeySelective(userRole);
			if(userVO.isResetRole()){
				this.inserUserAndRole(userVO.getRoleId(),userId);
			}
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void inserUserAndRole(Integer roleId,Integer userId) {
		if(roleId == null) {
			throw new BusinessException(-3001, "修改失败，角色列表为空");
		}
			SysUserRole userRole = new SysUserRole();
			userRole.setUserId(userId);
			userRole.setCreateTime(new Date());
			userRole.setRoleId(roleId);
			userRole.setIsDeleted(SysOrgStateEnum.NORMAL.getK());
			sysUserRoleMapper.insert(userRole);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void inserUserAndRole(List<Integer> roleList,Integer userId) {
		if(!roleList.isEmpty()) {
			throw new BusinessException(-3001, "修改失败，角色列表为空");
		}
		for (Integer roleId : roleList) {
			SysUserRole userRole = new SysUserRole();
			userRole.setUserId(userId);
			userRole.setCreateTime(new Date());
			userRole.setRoleId(roleId);
			userRole.setIsDeleted(SysOrgStateEnum.NORMAL.getK());
			sysUserRoleMapper.insert(userRole);
		}
	}
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void deleteUser(List<String> userIdList,Integer updateId) {
		//删除用户
		for (String userId : userIdList) {
			SysUser user = new SysUser();
			user.setUpdator(updateId);
			user.setId(Integer.valueOf(userId));
			user.setIsDeleted(SysOrgStateEnum.DELETED.getK());
			user.setUpdateTime(new Date());
			sysUserMapper.updateByPrimaryKeySelective(user);
			
			SysUserRole userRole = new SysUserRole();
			userRole.setUserId(Integer.valueOf(userId));
			userRole.setIsDeleted(SysOrgStateEnum.DELETED.getK());
			userRole.setUpdateTime(new Date());
			userRole.setUpdator(user.getUpdator());
			sysUserRoleMapper.updateByPrimaryKeySelective(userRole);
		}
	}
}
