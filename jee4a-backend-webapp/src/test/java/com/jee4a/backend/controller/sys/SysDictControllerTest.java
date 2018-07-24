package com.jee4a.backend.controller.sys;

import java.util.Date;
import org.junit.Test;

import com.jee4a.backend.hub.model.sys.SysDict;
import com.jee4a.backend.util.MockMvcHelper;
import com.jee4a.common.lang.JsonUtils;

public class SysDictControllerTest extends MockMvcHelper{
	@Test
	public void testAddDict() {
		SysDict dic = new SysDict();
		dic.setCreateTime(new Date());
		dic.setCreator(112);
		dic.setDicCode("11");
		dic.setDicName("22");
		dic.setDicType("33");
		dic.setDicValue("444");
		dic.setRemark("单元测试");
		param = JsonUtils.toJson(dic);
		url = "/sys/dict/addDict";
		type = "post";
		requestHandleMap();
	}

	@Test
	public void testQueryPage() {
		url = "/sys/dict/queryPage";
		type = "get";
		requestHandle();
		
		param = "{\"dictName\":\"test\"}";
		requestHandle();
	}

	@Test
	public void testUpdateDict() {
		SysDict dict = new SysDict();
		dict.setId(353);
		dict.setDicCode("HX");
		param = JsonUtils.toJson(dict);
		url = "/sys/dict/updateDict";
		type = "post";
		requestHandleMap();
	}
	
	@Test
	public void testDeleteDict() {
		param = JsonUtils.toJson(353);
		url = "/sys/dict/deleteDict";
		type = "post";
		requestHandleMap();
	}

	@Test
	public void testGetDictInfo() {
		url = "/sys/dict/deleteDict/353";
		type = "get";
		requestHandle();
	}

}
