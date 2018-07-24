package com.jee4a.backend.util;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import com.jee4a.backend.common.shiro.ShiroUtils;
import com.jee4a.common.lang.JsonUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(value = "dev")
@AutoConfigureMockMvc 
public class MockMvcHelper {

	@Autowired
	private WebApplicationContext context;
	@Resource
	private org.apache.shiro.mgt.SecurityManager securityManager;
	
	public MockMvc mockMvc;  
	private Subject subject;
	private MockHttpServletRequestBuilder requestBuilder = null;
	protected MockHttpSession mockSeesion;
	protected MockHttpServletRequest request;
	protected MockHttpServletResponse response;
	
	//请求地址
	public String url;
	//请求参数
	public String param;
	//请求类型
	public String type;
	
	@Before
	public void setupMockMvc() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest(context.getServletContext());
		mockSeesion = new MockHttpSession(context.getServletContext());
		request.setSession(mockSeesion);
		SecurityUtils.setSecurityManager(securityManager);
		subject = ShiroUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken("admin", "123456");
		token.setHost("127.0.0.1");
		subject.login(token);
	}

	//单个参数
	@SuppressWarnings("unused")
	public void requestHandle() {
		try {
			MvcResult resutl = mockMvc.perform(buildsRequestHandle()).andDo(MockMvcResultHandlers.print()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//执行文件上传  
	public void upload(MockMultipartFile firstFile) {
		try {
			mockMvc.perform(MockMvcRequestBuilders.fileUpload(url).file(firstFile)).andDo(MockMvcResultHandlers.print()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	//map / bean 参数
	@SuppressWarnings("unused")
	public void requestHandleMap() {
		try {
			MvcResult resutl = mockMvc.perform(buildsRequestHandleMap()).andDo(MockMvcResultHandlers.print()).andReturn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public MockHttpServletRequestBuilder buildsRequestHandle() {
		getHandlerType();
		getHandlerParam();
		return requestBuilder;
	}
	public MockHttpServletRequestBuilder buildsRequestHandleMap() {
		getHandlerType();
		requestBuilder.content(param);
		return requestBuilder;
	}

	public void getHandlerType() {
		switch (type) {
		case "post":
			requestBuilder = MockMvcRequestBuilders.post(url);
			
			break;

		case "get":
			requestBuilder = MockMvcRequestBuilders.get(url);
			break;

		case "put":
			requestBuilder = MockMvcRequestBuilders.put(url);
			break;

		default:
			requestBuilder = MockMvcRequestBuilders.get(url);
			break;
		}
		requestBuilder.session(mockSeesion);
		requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8)
		.accept( MediaType.APPLICATION_JSON);
	}

	@SuppressWarnings("unchecked")
	public void getHandlerParam() {
		if(!StringUtils.isEmpty(param)) {
			Map<String ,Object>  map = JsonUtils.json2Map(param);
			for (Map.Entry<String ,Object> obj : map.entrySet()) {
				requestBuilder.param(obj.getKey()+"",obj.getValue()+"");
			}
		}
	}

}
