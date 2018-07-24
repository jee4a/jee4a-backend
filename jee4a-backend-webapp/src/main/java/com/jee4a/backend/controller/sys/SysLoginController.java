package com.jee4a.backend.controller.sys;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jee4a.backend.common.config.ConfigValues;
import com.jee4a.backend.common.shiro.ShiroUtils;
import com.jee4a.backend.service.sys.SysLoginService;
import com.jee4a.common.Result;

/**
 * @date 2018年3月8日
 */
@Controller
public class SysLoginController extends AbstractBaseController {

	@Resource
	private ConfigValues configValues;

	@Resource
	private SysLoginService sysLoginService;

	@RequestMapping(value = "captcha.jpg", method = RequestMethod.GET)
	public void captcha(HttpServletResponse response) {
		sysLoginService.createCaptcha(response);
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "/login";
	}

	/**
	 * 
	 * @param userName
	 *            (用户名/手机号)
	 * @param password
	 * @param captcha
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = { "application/json" })
	@ResponseBody
	public Result doLogin(String userName, String password, String captcha,
			HttpServletRequest request, HttpServletResponse response) {
		return sysLoginService.login(captcha, userName, password,
				this.getRemoteIp(), request.getHeader("user-agent"));
	}

	@RequestMapping(value= {"/updatePassword"},method=RequestMethod.POST,produces={"application/json"})
	@ResponseBody
	public Result updatePassword(String name,String originalPassword, String newPassword, String trueNewPassword,HttpServletResponse response){
		return  sysLoginService.updatePassword(name,originalPassword,newPassword,trueNewPassword);
	}
	
	/**
	 * 退出
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout() {
		ShiroUtils.logout();
		return redirect(configValues.getLoginUrl());
	}

	@RequestMapping(value = { "/index.html", "/" }, method = RequestMethod.GET)
	public String index() {
		return "/index";
	}

	@RequestMapping(value = { "/main.html" }, method = RequestMethod.GET)
	public String main() {
		return "/main";
	}

	@RequestMapping(value = { "/unauthorized.html" }, method = RequestMethod.GET)
	public String unauthorized() {
		return "/unauthorized";
	}

	@RequestMapping("404.html")
	public String notFound() {
		return "404";
	}

	@RequestMapping(value = "page/{module}/{url}.html", method = RequestMethod.GET, produces = "text/html")
	public String module(@PathVariable("module") String module,
			@PathVariable("url") String url) {
		return "/page/" + module + "/" + url;
	}
	

}
