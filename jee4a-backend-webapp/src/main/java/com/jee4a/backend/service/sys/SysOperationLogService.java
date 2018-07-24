package com.jee4a.backend.service.sys;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.jee4a.backend.hub.model.sys.SysOperationLog;
import com.jee4a.backend.manager.sys.SysOperationLogManager;
import com.jee4a.common.BaseService;
import com.jee4a.common.Result;

/**
 * @description 日志
 * @date 2018年3月14日
 */
@Service
public class SysOperationLogService extends BaseService {

	@Resource
	private SysOperationLogManager sysOperationLogManager ;
	
	public Result<PageInfo<SysOperationLog>> queryPage(String operation ,int pageNum, int pageSize) {
		Result<PageInfo<SysOperationLog> > result = new Result<>();
		try {
			SysOperationLog params = new SysOperationLog();
			params.setOperation(operation);
			PageInfo<SysOperationLog>  pageInfo = sysOperationLogManager.queryPage(params ,pageNum, pageSize) ;
			result.put(pageInfo);
			result.setSuccess();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		return result;
	}
}
