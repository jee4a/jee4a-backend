package com.jee4a.backend.common.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.jee4a.backend.controller.sys.AbstractBaseController;
import com.jee4a.backend.hub.model.CacheKeys;
import com.jee4a.common.io.cache.redis.RedisUtils;

public class LoginInterceptor extends AbstractBaseController implements HandlerInterceptor{

	@Resource
	private RedisUtils redisUtils ;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		Integer userId = getUserId();
		if(null != userId) {
			String cacheKey = CacheKeys.KEY_USER_LOGIN_INFO.getKeyPrefix(userId);
			if(StringUtils.isEmpty(redisUtils.get(cacheKey))) {
				//登出
				response.sendRedirect("/logout");
				return false;
			}
		}
		return true;
	}
}
