package com.jee4a.backend.common.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.jee4a.backend.common.interceptor.LoginInterceptor;
import com.jee4a.backend.common.interceptor.OperationLogInterceptor;
import com.jee4a.backend.manager.sys.SysOperationLogManager;
import com.jee4a.common.io.cache.redis.RedisUtils;

/**
 * @description 拦截器配置
 * @author 398222836@qq.com
 * @date 2018年3月8日
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {
	
	@Resource
	private ConfigValues configValues ;
	
	@Resource
	private RedisUtils redisUtils ;
	
	@Resource
	private SysOperationLogManager sysOperationLogManager;

	@Bean(name="operationLogInterceptor")
	public OperationLogInterceptor operationLogInterceptor() {
		OperationLogInterceptor  operationLogInterceptor = new OperationLogInterceptor() ;
		operationLogInterceptor.setSysOperationLogManager(sysOperationLogManager);
		return operationLogInterceptor ;
	}

	@Bean(name="loginOutInterceptor")
	public LoginInterceptor loginInterceptor() {
		LoginInterceptor  loginInterceptor = new LoginInterceptor() ;
		return loginInterceptor ;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(operationLogInterceptor())
		.addPathPatterns("/**")
		.excludePathPatterns("/login")
		.excludePathPatterns("/updatePassword")
		.excludePathPatterns("/login.html")
		.excludePathPatterns("/unauthorized")
		.excludePathPatterns("/unauthorized.html")
		.excludePathPatterns("/main")
		.excludePathPatterns("/main.html")
		.excludePathPatterns("/index")
		.excludePathPatterns("/index.html")
		.excludePathPatterns("/page/**")
		.excludePathPatterns("/sys/log/list")
		.excludePathPatterns("/sys/user/info")
		.excludePathPatterns("/sys/user/info.html")
		.excludePathPatterns("/sys/resource/nav")
		.excludePathPatterns("/sys/resource/nav.html")
		.excludePathPatterns("/statics/**") 
		.excludePathPatterns("/favicon.ico")
		.excludePathPatterns("/captcha.jpg");
		super.addInterceptors(registry);
		
		
		registry.addInterceptor(loginInterceptor())
		.addPathPatterns("/**")
		.excludePathPatterns("/login")
		.excludePathPatterns("/updatePassword")
		.excludePathPatterns("/logout")
		.excludePathPatterns("/login.html")
		.excludePathPatterns("/unauthorized")
		.excludePathPatterns("/unauthorized.html")
		.excludePathPatterns("/main")
		.excludePathPatterns("/main.html")
		.excludePathPatterns("/index")
		.excludePathPatterns("/index.html")
		.excludePathPatterns("/statics/**") 
		.excludePathPatterns("/favicon.ico")
		.excludePathPatterns("/captcha.jpg");
		super.addInterceptors(registry);
	}
}
