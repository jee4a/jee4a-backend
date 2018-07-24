package com.jee4a.backend.controller.sys;

import org.junit.Test;

import com.jee4a.backend.util.MockMvcHelper;

public class SysOperationLogControllerTest extends MockMvcHelper{

	@Test
	public void testQueryLogList() {
		url = "/sys/log/list";
		type = "get";
		requestHandle();
	}

}
