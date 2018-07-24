package com.jee4a.backend.controller.sys;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jee4a.backend.hub.model.sys.SysDict;
import com.jee4a.backend.service.sys.SysDictService;
import com.jee4a.common.Result;
import com.jee4a.common.annotation.OperationLogAnnotation;

@RestController
@RequestMapping("/sys/dict")
@SuppressWarnings("rawtypes")
public class SysDictController extends AbstractBaseController{

	@Resource
	private SysDictService sysDictService;
	
	@OperationLogAnnotation("字典添加")
	@RequestMapping(value = "/addDict" , method = RequestMethod.POST ,produces={"application/json"})
	@RequiresPermissions("sys:dict:add")
	public Result addDict(@RequestBody SysDict dict) {
		dict.setCreator(this.getUserId());
		return sysDictService.addDict(dict);
	}
	
	
	@OperationLogAnnotation("字典列表")
	@RequestMapping(value = "/queryPage" , method = RequestMethod.GET ,produces={"application/json"})
	@RequiresPermissions("sys:dict:list")
	public Result queryPage(@RequestParam(name = "dictName",required = false )String dictName,@RequestParam(name = "pageNum",required = true,defaultValue = "0") int pageNum,
			@RequestParam(name = "pageSize",required = true,defaultValue = "10") int pageSize) {
		SysDict dict = new SysDict();
		dict.setDicName(dictName);	
		return sysDictService.queryPage(dict, pageNum, pageSize);
	}
	
	
	@OperationLogAnnotation("字典修改")
	@RequestMapping(value = "/updateDict" , method = RequestMethod.POST ,produces={"application/json"})
	@RequiresPermissions("sys:dict:update")
	public Result updateDict(@RequestBody SysDict dict) {
		return sysDictService.updateDict(dict);
	}
	
	@OperationLogAnnotation("字典删除")
	@RequestMapping(value = "/deleteDict" , method = RequestMethod.POST ,produces={"application/json"})
	@RequiresPermissions("sys:dict:update")
	public Result deleteDict(@RequestBody String dictId) {
		return sysDictService.deleteDict(dictId,this.getUserId());
	}
	
	@OperationLogAnnotation("字典详情")
	@RequestMapping(value = "/info/{id}" , method = RequestMethod.GET ,produces={"application/json"})
	@RequiresPermissions("sys:dict:info")
	public Result getDictInfo(@PathVariable("id")Integer id) {
		return sysDictService.getDictInfo(id);
	}
}
