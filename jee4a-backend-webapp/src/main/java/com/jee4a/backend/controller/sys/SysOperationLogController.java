package com.jee4a.backend.controller.sys;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.jee4a.backend.hub.model.sys.SysOperationLog;
import com.jee4a.backend.service.sys.SysOperationLogService;
import com.jee4a.common.Result;

/**
 * @author 398222836@qq.com
 * @date 2018年3月14日
 */
@RestController
@RequestMapping("/sys/log")
public class SysOperationLogController extends AbstractBaseController {
	
	@Resource
	private SysOperationLogService  sysOperationLogService ;
	
	/**
	 * @description 日志列表
	 * @return
	 * @date 2018年3月14日
	 */
	@RequestMapping(value = "/list" , method = RequestMethod .GET ,produces={"application/json"})
	@RequiresPermissions("sys:log:list")
	public Result<PageInfo<SysOperationLog>> queryLogList(@RequestParam(name = "key",required = false )  String key , @RequestParam(name = "pageNum",required = true,defaultValue = "0") int pageNum,
			@RequestParam(name = "pageSize",required = true,defaultValue = "10") int pageSize) {
		return  sysOperationLogService.queryPage(key, pageNum, pageSize) ;
	}
}
