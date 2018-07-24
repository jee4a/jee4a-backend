package com.jee4a.backend.controller.sys;

import org.apache.shiro.SecurityUtils;

import com.jee4a.backend.hub.model.sys.SysUser;
import com.jee4a.common.BaseController;

/**
 * @description 公共控制类
 * @author 398222836@qq.com
 * @date 2018年3月12日
 */
public abstract class AbstractBaseController extends BaseController {

	protected SysUser getUser() {
		Object sysUser = SecurityUtils.getSubject().getPrincipal();
		return sysUser == null ? null : (SysUser) sysUser;
	}

	protected Integer getUserId() {
		return getUser() == null ? null : getUser().getId();
	}

	protected String getUserName() {
		return getUser() == null ? null : getUser().getUserName();
	}

	protected Integer getOrgId() {
		return getUser() == null ? null : getUser().getOrgId();
	}
}
