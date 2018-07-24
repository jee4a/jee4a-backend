package com.jee4a.backend.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.jee4a.backend.common.shiro.ShiroTag;

/**
 * Freemarker配置
 */
@Configuration
public class FreemarkerConfig {

	@Resource
	private ConfigValues configValues ;
	
	@Bean
	public FreeMarkerViewResolver viewResolver() {
		FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
		viewResolver.setCache(true);
		viewResolver.setPrefix("");
		viewResolver.setSuffix(".html");
		viewResolver.setContentType("text/html;charset=UTF-8");
		viewResolver.setRequestContextAttribute("request");
		viewResolver.setExposeSpringMacroHelpers(true);
		viewResolver.setExposeRequestAttributes(true);
		viewResolver.setExposeSessionAttributes(true);
		return viewResolver ;
	}
	
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer(ShiroTag shiroTag){
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:/templates");
        
        Properties settings = new Properties();
        settings.setProperty("locale", "zh_CN");
        settings.setProperty("template_update_delay", "0");
        settings.setProperty("default_encoding", "utf-8");
        settings.setProperty("number_format", "0.######");
        settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss") ;
        settings.setProperty("classic_compatible", "true") ;
        settings.setProperty("template_exception_handler", "ignore") ;
        configurer.setFreemarkerSettings(settings);
       
        Map<String, Object> variables = new HashMap<>(1);
        variables.put("shiro", shiroTag);
        variables.put("backendUrl", configValues.getBackendUrl()) ;
        configurer.setFreemarkerVariables(variables);
        
        return configurer;
    }

}
