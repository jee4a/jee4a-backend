package com.jee4a.backend.service.sys;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jee4a.backend.common.enums.sys.SysOrgStateEnum;
import com.jee4a.backend.hub.mapper.sys.SysOrgMapper;
import com.jee4a.backend.hub.model.BaseConstant;
import com.jee4a.backend.hub.model.CacheKeys;
import com.jee4a.backend.hub.model.sys.SysOrg;
import com.jee4a.backend.hub.model.sys.SysUser;
import com.jee4a.backend.hub.vo.sys.SysOrgVO;
import com.jee4a.backend.manager.sys.SysOrgManager;
import com.jee4a.common.BaseService;
import com.jee4a.common.Result;
import com.jee4a.common.exceptions.BusinessException;
import com.jee4a.common.lang.StringUtils;

@Service("sysOrgService")
@SuppressWarnings("rawtypes")
public class SysOrgService extends BaseService{
	@Resource
	private SysOrgManager sysOrgManager;
	
	@Resource
	private SysOrgMapper sysOrgMapper;
	
	/**
	 * @Description:部门添加
	 */
	@SuppressWarnings("rawtypes")
	public Result insertOrg(SysOrg org,Integer userId) {
		Result result = new Result();
		try {
			if(StringUtils.isEmpty(org.getName())) {
				throw new BusinessException(-3008, "部门名称不能为空！");
			}
			sysOrgManager.insert(org,userId);
			result.setSuccess();
		}catch(BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result.setDefaultError();
		}
		return result;
	}
	
	/**
	 * @Description:修改部门
	 */
	
	public Result updateOrg(SysOrg org) {
		Result result = new Result();
		try {
			if(org.getId() == null) {
				throw new BusinessException(-3009, "部门id不能为空！");
			}
			SysOrg sys=sysOrgMapper.selectByPrimaryKey(org.getId());
			if(sys!=null){
				//修改上级部门，用户被强制退出
				if(sys.getParentId()!=org.getParentId()){
					removeUser(sys.getId(),sys.getUpdator());
					removeChildUser(sys.getId(),sys.getUpdator());
				}
			}
			org.setUpdateTime(new Date());
			sysOrgManager.update(org);
			result.setSuccess();
		}catch(BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			result.setDefaultError();
		}
		return result;
	}
	/**
	 * 递归清理子部门下的用户信息
	 * @param id
	 * @param updator
	 */
	public void removeChildUser(Integer id,Integer updator){
		List<SysOrg> list=sysOrgMapper.queryByParentId(id);
		List<SysOrg> SysOrg=sysOrgMapper.queryByParentId(id);
		if(list!=null && list.size()>0){
			for (SysOrg sysOrg:list){
				removeUser(sysOrg.getId(),updator);
				removeChildUser(sysOrg.getId(),updator);
			}
		}
	}
	
	/**
	 * 清理部门下的用户信息
	 * @param orgId
	 * @param updator
	 */
	public void removeUser(Integer orgId,Integer updator){
		Map<String,Object> paraMap=new HashMap<String,Object>();
		  paraMap.put("orgId",orgId);
		  paraMap.put("isDeleted",0);
		  List<SysUser> userList= sysOrgManager.getUserByMap(paraMap);
		  if(userList!=null && userList.size()>0){
			  for(SysUser sysUser:userList){
				//当部门改变，当前部门下所有用户的部门角色职位清零
				  sysUser.setPositionId(0);  
				  sysUser.setOrgId(0);
				  sysUser.setUpdator(updator);
				  sysUser.setUpdateTime(new Date());
				  sysOrgManager.saveUser(sysUser);
				  String cacheKey = CacheKeys.KEY_USER_LOGIN_INFO.getKeyPrefix(sysUser.getId());
					redisUtils.del(cacheKey);
			  }
		  }
	}
	
	/**
	 * @Description:添加部门信息
	 */
	public Result queryList(SysOrg sysOrg,Integer userId) {
		Result result = new Result();
		try {
			List<SysOrg> orgList = sysOrgMapper.queryList(sysOrg);
			if(org.springframework.util.CollectionUtils.isEmpty(orgList)) {
				result.setSuccess(false);
				return result;
			}
			//添加一级部门
			if(userId == BaseConstant.SUPER_ADMIN){
				SysOrg root = new SysOrg();
				root.setId(0);
				root.setName("一级部门");
				root.setParentId(-1);
				root.setIsDeleted(SysOrgStateEnum.NORMAL.getK());
				orgList.add(root);
			}
			result.setResult(orgList);
			result.setSuccess();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result.setDefaultError();
		}
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public Result getOrgInfo(Integer userId) {
		Result result = new Result();
		Integer orgId = 0;
		if(userId != BaseConstant.SUPER_ADMIN){
			List<SysOrg> orgList = sysOrgMapper.queryList(null);
			Integer parentId = null;
			for(SysOrg org : orgList){
				if(parentId == null){
					parentId = org.getParentId();
					continue;
				}

				if(parentId > org.getParentId()){
					parentId = org.getParentId();
				}
			}
			orgId = parentId;
		}
		result.put(orgId);
		result.setSuccess();
		return result;
	}
	
	/**
	 * @Description:获取部门信息列表  
	 */
	public Result<List<SysOrgVO>> getOrgList(String name) {
		Result<List<SysOrgVO>> result = new Result<>();
		List<SysOrgVO> resultList =new ArrayList<>(); 
		try {
			SysOrg sys=new SysOrg();
			if(name!=null){
				boolean isInt = Pattern.compile("^-?[1-9]\\d*$").matcher(name.replace(" ","")).find();
				if(isInt){
					sys.setId(Integer.valueOf(name));
				}else{
					sys.setName(name);
				}
			}
			List<SysOrg> orgList = sysOrgMapper.queryList(sys);
			if(org.springframework.util.CollectionUtils.isEmpty(orgList)) {
				throw new BusinessException(-3099, "获取数据为空");
			}
			for (SysOrg sysOrg : orgList) {
				resultList.add(this.getOrgVo(sysOrg));
			}
			result.put(resultList);
			result.setSuccess();
		}catch(BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result.setDefaultError();
		}
		return result;
	}
	
	/**
	 * @Description: 根据部门id获取一个部门信息
	 */
	@SuppressWarnings("unchecked")
	public Result getOrgById(Integer orgId) {
		Result result = new Result();
		try {
			if(orgId == null) {
				throw new BusinessException(-7008, "查询id不能为空");
			}
			SysOrg sysOrg = sysOrgMapper.selectByPrimaryKey(orgId);
			if(sysOrg !=null) {
				result.put(this.getOrgVo(sysOrg));
				result.setSuccess();
			}
		}catch(BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result.setDefaultError();
		}
		return result;
	}
	
	/**
	 * @Description: 删除部门，做的逻辑删除
	 */
	public Result updateById(Integer orgId) {
		Result result = new Result();
		try {
			if(orgId ==null ) {
				result.setMessage("请选择要删除部门");
				return result;
			}
			SysOrg sysOrg = new SysOrg();
			sysOrg.setParentId(orgId);
			List<SysOrg> orgList = sysOrgMapper.queryList(sysOrg);
			if(org.apache.commons.collections.CollectionUtils.isNotEmpty(orgList)) {
				result.setMessage("请删除子部门");
				return result;
			}
			sysOrg.setId(orgId);
			sysOrgManager.delete(sysOrg);
			result.setSuccess();
		} catch (Exception e) {
			logger.info("org delete fail");
			result.setDefaultError();
		}
		return result;
	}
	
	/**
	 * @Description:通过sysorg 获取一个包含上级部门名称的VO   
	 */
	public SysOrgVO getOrgVo(SysOrg sysOrg) throws Exception{
		SysOrgVO sysorgVo =SysOrgVO.toVO(sysOrg);
		SysUser user=sysOrgManager.getUser(sysOrg.getCreator());
		if(user!=null){
			sysorgVo.setCreateName(user.getUserName());
		}
		sysOrg = sysOrgMapper.selectByPrimaryKey(sysOrg.getParentId());
		if(sysOrg != null) {
			sysorgVo.setParentName(sysOrg.getName());
		}else {
			sysorgVo.setParentName("一级部门");
		}
		return sysorgVo;
	}
	
	//查询所有部门列表
	public Result queryOrgList(SysOrg sysOrg) {
		Result result = new Result();
		try {
			List<SysOrg> orgList = sysOrgManager.queryList(sysOrg);
			result.put(orgList);
			result.setSuccess();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}
}
