package com.jee4a.backend.manager.sys;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jee4a.backend.hub.mapper.sys.SysResourceMapper;
import com.jee4a.backend.hub.model.BaseConstant;
import com.jee4a.backend.hub.model.CacheKeys;
import com.jee4a.backend.hub.model.sys.SysResource;
import com.jee4a.common.BaseManager;
import com.jee4a.common.QueryHandle;
import com.jee4a.common.UpdateHandle;

/**
 * @description
 * @author 398222836@qq.com
 * @date 2018年3月12日
 */
@Component
public class SysResourceManager extends BaseManager {

	@Resource
	private SysResourceMapper sysResourceMapper;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void insertSelective(SysResource record) {
		sysResourceMapper.insertSelective(record);
	}

	/**
	 * 根据主键更新属性不为空的记录
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, value = BaseConstant.DB_CRM)
	public void updateByPrimaryKeySelective(SysResource record) {
		String cacheKey = CacheKeys.KEY_SYS_RESOURCE_ID.getKeyPrefix(record.getId());
		new UpdateHandle() {

			@Override
			public void daoUpdate() {
				sysResourceMapper.updateByPrimaryKeySelective(record);
			}

			@Override
			public void cacheUpdate() {
				redisUtils.del(cacheKey);
			}
		}.execute();

	}

	public SysResource selectByPrimaryKey(Integer id) {
		String cacheKey = CacheKeys.KEY_SYS_RESOURCE_ID.getKeyPrefix(id);
		return new QueryHandle<SysResource>() {

			@Override
			public SysResource cacheQuery() {
				return redisUtils.get(cacheKey, SysResource.class);
			}

			@Override
			public void cacheSet() {
				redisUtils.setex(cacheKey, CacheKeys.KEY_SYS_RESOURCE_ID.getExpire(),
						sysResourceMapper.selectByPrimaryKey(id));
			}

			@Override
			public SysResource daoQuery() {
				return sysResourceMapper.selectByPrimaryKey(id);
			}
		}.execute();
	}

	public List<SysResource> selectNotButtonList() {
		return sysResourceMapper.selectNotButtonList();
	}

	public List<SysResource> selectListByParentId(Integer parentId) {
		return sysResourceMapper.selectListByParentId(parentId);
	}

	public List<SysResource> selectList(SysResource record) {
		return sysResourceMapper.selectList(record);
	}

}
