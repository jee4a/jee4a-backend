package com.jee4a.backend.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 公共配置类
 * 
 * @author tpeng 2018年1月24日
 * @email 398222836@qq.com
 */
@Configuration
public class ConfigValues {

	@Value("${spring.application.name}")
	private String appName;

	@Value("${domain.jee4a.backend.url}")
	private String backendUrl;

	@Value("${passport.login.url}")
	private String loginUrl;

	@Value("${passport.success.url}")
	private String successUrl;

	@Value("${passport.unauthorized.url}")
	private String unauthorizedUrl;

	@Value("${passport.captcha.open}")
	private boolean captchaIsOpen;
	
	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}
 
	/**
	 * @return the backendUrl
	 */
	public String getBackendUrl() {
		return backendUrl;
	}
	/**
	 * @return the loginUrl
	 */
	public String getLoginUrl() {
		return loginUrl;
	}

	/**
	 * @return the successUrl
	 */
	public String getSuccessUrl() {
		return successUrl;
	}

	/**
	 * @return the unauthorizedUrl
	 */
	public String getUnauthorizedUrl() {
		return unauthorizedUrl;
	}

	/**
	 * @return the captchaIsOpen
	 */
	public boolean isCaptchaIsOpen() {
		return captchaIsOpen;
	}

}
