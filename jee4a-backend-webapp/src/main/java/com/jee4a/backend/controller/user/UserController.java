package com.jee4a.backend.controller.user;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jee4a.common.BaseController;
import com.jee4a.common.Result;
import com.jee4a.user.rpc.interfaces.UserRpcService;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController{

	@Reference(version = "1.0.0")
	private UserRpcService userRpcService ;
	
	@RequestMapping(value = "/info/{id}" , method = RequestMethod.GET ,produces={"application/json"})
	public Result info(@PathVariable  Integer id) {
		return userRpcService.queryById(id) ;
	}
	
}
