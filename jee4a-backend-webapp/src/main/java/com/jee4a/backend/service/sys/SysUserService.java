package com.jee4a.backend.service.sys;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.jee4a.backend.common.enums.sys.SysDeletedEnum;
import com.jee4a.backend.common.enums.sys.SysUserStateEnum;
import com.jee4a.backend.common.shiro.ShiroUtils;
import com.jee4a.backend.common.util.DateUtils;
import com.jee4a.backend.common.util.StringUtil;
import com.jee4a.backend.hub.mapper.sys.SysUserMapper;
import com.jee4a.backend.hub.mapper.sys.SysUserRoleMapper;
import com.jee4a.backend.hub.model.CacheKeys;
import com.jee4a.backend.hub.model.sys.SysUser;
import com.jee4a.backend.hub.vo.sys.SysUserVO;
import com.jee4a.backend.manager.sys.SysUserManager;
import com.jee4a.common.BaseService;
import com.jee4a.common.Result;
import com.jee4a.common.exceptions.BusinessException;
import com.jee4a.common.io.cache.redis.RedisUtils;
import com.jee4a.common.lang.JsonUtils;
import com.jee4a.common.lang.StringUtils;


/**
 * <p>
 * <p>业务逻辑层</p> 
 * </p>
 * 业务操作成功，设置 result.setSuccess();
 */
@Service
@SuppressWarnings("rawtypes")
public class SysUserService extends BaseService {

	@Resource
	private SysUserMapper sysUserMapper ; 
	
	@Resource
	private SysUserManager sysUserManager ; 

	@Resource
	private SysUserRoleMapper sysUserRoleMapper;

	@Autowired
	protected RedisUtils redis;
	
	public Result password(String password, String newPassword,String trueNewPassword, SysUser loginUser) {
		Result result = new Result();
		try {

			if (StringUtils.isEmpty(password)) {
				result.setError(-50001, "请输入原密码");
				return result;
			}
			if (StringUtils.isEmpty(newPassword)) {
				result.setError(-50002, "请输入新密码");
				return result;
			}
			if(StringUtils.isEmpty(trueNewPassword)) {
				result.setError(-50002, "确认密码不能为空");
				return result;
			}
			if(!newPassword.equals(trueNewPassword)) {
				result.setError(-50002, "两次输入密码不一致");
				return result;
			}
			SysUser user = sysUserMapper.selectByPrimaryKey(loginUser.getId());

			// 原密码
			password = ShiroUtils.sha256(password, loginUser.getSalt());
			// 新密码
			newPassword = ShiroUtils.sha256(newPassword, loginUser.getSalt());

			if (!password.equals(user.getUserPwd())) {
				result.setError(-60003, "原密码错误");
				return result;
			}

			SysUser record = new SysUser();
			record.setId(user.getId());
			record.setUserPwd(newPassword);
			record.setLastUpdatePwdTime(new Date());
			sysUserMapper.updateByPrimaryKeySelective(record);
			result.setSuccess();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}

		return result;
	}
	
	/**
	 * @Description:添加用户   
	 */
	public Result insertUser(SysUserVO user,Integer createrId) {
		Result result = new Result();
		//添加用户
		try {
			checkNoEmptyFields(user,user.getUserPwd());
			SysUser oldUser = sysUserMapper.queryByName(user.getUserName());
			if(oldUser != null && SysUserStateEnum.NORMAL.getK()==oldUser.getState()
					&&SysDeletedEnum.NORMAL.getK() == oldUser.getIsDeleted()) {
				result.setError(-20002, "用户名被占用");
				return result;
			}
			if(oldUser != null && SysUserStateEnum.DISABLED.getK()==oldUser.getState()
					&&SysDeletedEnum.NORMAL.getK() == oldUser.getIsDeleted()) {
				result.setError(-20003, "已有该用户名被禁用，请联系管理员");
				return result;
			}
			
			Map<String,Object>map =new HashMap<String,Object>();
			map.put("mobile", user.getMobile());
			List<SysUser> list=sysUserMapper.findByMap(map);
			if(list != null && list.size() > 0){
				result.setError(-20004, "手机号已被占用");
				return result;
			}
			if(user.getRoleId() == null) {
				new BusinessException(-2005,"角色不能为空");
			}
			user.setCreator(createrId);
			String salt = RandomStringUtils.randomAlphanumeric(20);
			user.setSalt(salt);
			user.setUserPwd(ShiroUtils.sha256(user.getUserPwd(), user.getSalt()));
			if(oldUser !=null && SysDeletedEnum.DELETED.getK() == oldUser.getIsDeleted()) {
				user.setIsDeleted(SysDeletedEnum.NORMAL.getK());
				user.setId(oldUser.getId());
				sysUserManager.updateUserInfo(user);
				result.setSuccess();
				return result;
			}
			user.setLastUpdatePwdTime(new Date());
			user.setState(SysUserStateEnum.NORMAL.getK());
			sysUserManager.insertUser(user);
			result.setSuccess();
		} catch(BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}
	
	/**
	 * @Description:校验必传参数 
	 */
	public void checkNoEmptyFields(SysUserVO user) {
		//用户名
		if(StringUtils.isEmpty(user.getUserName())) {
			throw new BusinessException(-2005, "用户名不能为空");
		}
		
		
		if(user.getOrgId() == null) {
			throw new BusinessException(-2006, "所属部门不能为空");
		}
		//手机号
		if(StringUtils.isEmpty(user.getMobile())) {
			throw new BusinessException(-2009, "手机号不能为空");
		}
		
		if(!StringUtil.checkMobilePhone(user.getMobile())){
			throw new BusinessException(-2011, "手机号格式不正确");
		}
		if(user.getPositionId() == null){
			throw new BusinessException(-2012, "职位不能为空");
		}
		//角色
		Integer roleId =user.getRoleId();
		if(roleId == null ){
			throw new BusinessException(-2010, "角色不能为空");
		}
	}
	
	public void checkNoEmptyFields(SysUserVO user,String password) {
		this.checkNoEmptyFields(user);
		if(StringUtils.isEmpty(password)) {
			throw new BusinessException(-2007, "初始密码不能为空");
		}
	}
	
	/**
	 * @Description:  用户列表分页查询
	 */
	public Result<PageInfo<SysUserVO>> queryPage(SysUserVO sysUserVO ,int pageNum, int pageSize) {
		Result<PageInfo<SysUserVO> > result = new Result<>();
		try {
			String[] paramTime = DateUtils.getParamDate(sysUserVO.getBeginCreateTime(), sysUserVO.getEndCreateTime());
			sysUserVO.setBeginCreateTime(paramTime[0]);
			sysUserVO.setEndCreateTime(paramTime[1]);
			
			String[] time = DateUtils.getParamDate(sysUserVO.getBeginUpdateTime(), sysUserVO.getEndUpdateTime());
			sysUserVO.setBeginUpdateTime(time[0]);
			sysUserVO.setEndUpdateTime(time[1]);
			
			PageInfo<SysUserVO>  pageInfo = sysUserManager.queryPage(sysUserVO ,pageNum, pageSize) ;
			result.put(pageInfo);
			result.setSuccess();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}
	
	/**
	 * @Description: 获取用户详细信息
	 */
	@SuppressWarnings("unchecked")
	public Result getUserInfo(Integer userId) {
		Result result = new Result();
		try {
			if(userId == null) {
				throw new BusinessException(-3009, "用户id不能为空！");
			}
			SysUserVO user = sysUserManager.getUserInfo(userId);
			user.setRoleId(sysUserRoleMapper.queryUserRoleId(userId));
			result.put(user);
			result.setSuccess();
		}catch(BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}
	
	/**
	 * @Description:修改用户信息
	 */
	public Result updateUserInfo(SysUserVO userVo) {
		Result result = new Result();
		try {
			this.checkNoEmptyFields(userVo);
			
			SysUser user = sysUserMapper.selectByPrimaryKey(userVo.getId());
			
			Map<String,Object>map =new HashMap<String,Object>();
			map.put("mobile", userVo.getMobile());
			
			List<SysUser> list=sysUserMapper.findByMap(map);
			if(!user.getMobile().equals(userVo.getMobile()) && CollectionUtils.isNotEmpty(list)){
				result.setError(-20004, "手机号已被占用");
				return result;
			}
			
			Integer oldOrgId = user.getOrgId();
			Integer nowOrgId = userVo.getOrgId();
			if(oldOrgId != null && !oldOrgId.equals(nowOrgId)) {
				//解除用户与部门角色权限关系
				userVo.setPositionId(0);
				userVo.setResetRole(false);
				String cacheKey = CacheKeys.KEY_USER_LOGIN_INFO.getKeyPrefix(user.getId());
				redisUtils.del(cacheKey);
			}
			// 新密码
			if(!StringUtils.isEmpty(userVo.getUserPwd())) {
				String newPassword = ShiroUtils.sha256(userVo.getUserPwd(), user.getSalt());
				userVo.setUserPwd(newPassword);
			}
			sysUserManager.updateUserInfo(userVo);
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
	 * @Description: 删除用户信息
	 */
	@SuppressWarnings("unchecked")
	public Result deleteUser(String userIdStr, Integer updateId) {
		Result result = new Result();
		List<String> userIdList = JsonUtils.json2List(userIdStr);
		try {
			if(org.apache.commons.collections.CollectionUtils.isEmpty(userIdList)) {
				throw new BusinessException(-2009, "用户id为空，删除失败");
			}
			sysUserManager.deleteUser(userIdList,updateId);
			result.setSuccess();
		}catch(BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		String salt = RandomStringUtils.randomAlphanumeric(20);
		System.out.println(ShiroUtils.sha256("123456",salt)+","+salt);
	}
}
