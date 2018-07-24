package com.jee4a.backend.controller.sys;

import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.jee4a.backend.hub.vo.sys.SysResourceVO;
import com.jee4a.backend.service.sys.SysResourceService;
import com.jee4a.common.Result;
import com.jee4a.common.annotation.OperationLogAnnotation;

/**
 * @author 398222836@qq.com
 * @date 2018年3月8日
 */
@RestController
@RequestMapping("/sys/resource")
@SuppressWarnings("rawtypes")
public class SysResourceController extends AbstractBaseController {
	
	@Resource
	private SysResourceService  sysResourceService ;
	
	
	/**
	 * 导航菜单
	 */
	@OperationLogAnnotation("导航菜单 ")
	@RequestMapping(value = "/nav" , method = RequestMethod .GET ,produces={"application/json"})
	public Result<List<SysResourceVO>> nav(){
		return sysResourceService.queryUserResourceList(getUserId()) ;
	}
	
	/**
	 * 
	 * @description 查询菜单 
	 * @param id
	 * @return
	 * @date 2018年3月13日
	 */
	@OperationLogAnnotation("菜单详情 ")
	@RequestMapping(value = "/info/{id}" , method = RequestMethod .GET ,produces={"application/json"})
	@RequiresPermissions("sys:resource:info")
	public Result<SysResourceVO> queryResourceById(@PathVariable Integer id) {
		return sysResourceService.queryById(id) ;
	}
	
	/**
	 * @description 所有菜单列表 
	 * @return
	 * @date 2018年3月13日
	 */
	@OperationLogAnnotation("菜单列表 ")
	@RequestMapping(value = "/list" , method = RequestMethod .GET ,produces={"application/json"})
	@RequiresPermissions("sys:resource:list")
	public List<SysResourceVO> queryResourceList() {
		return  sysResourceService.queryResourceList().get() ;
	}
	
	/**
	 * 
	 * @return
	 * @date 2018年3月13日
	 */
	@OperationLogAnnotation("选择菜单")
	@RequestMapping(value = "/select" , method = RequestMethod .GET ,produces={"application/json"})
	@RequiresPermissions("sys:resource:select")
	public Result<List<SysResourceVO>>  selectResourceList() {
		return  sysResourceService.selectNotButtonTreeList()  ;
	}
	
	/**
	 * 都
	 * @description 菜单新增 
	 * @param vo
	 * @return
	 * @date 2018年3月13日
	 */
	@OperationLogAnnotation("新增菜单 ")
	@RequestMapping(value = "/save" , method = RequestMethod.POST ,produces={"application/json"})
	@RequiresPermissions("sys:resource:save")
	public Result  saveResource(@RequestBody  SysResourceVO vo) {
		return  sysResourceService.saveReource(vo,getUserId()) ;
	}
	
	@OperationLogAnnotation("更新菜单 ")
	@RequestMapping(value = "/update" , method = RequestMethod.POST ,produces={"application/json"})
	@RequiresPermissions("sys:resource:update")
	public Result  updateResource(@RequestBody  SysResourceVO vo) {
		return  sysResourceService.updateReource(vo, getUserId()) ;
	}
	
	@OperationLogAnnotation("删除菜单 ")
	@RequestMapping(value = "/delete" , method = RequestMethod.POST ,produces={"application/json"})
	@RequiresPermissions("sys:resource:delete")
	public Result  deleteResource(Integer id) {
		return  sysResourceService.deleteReource(id,getUserId()) ;
	}
}
