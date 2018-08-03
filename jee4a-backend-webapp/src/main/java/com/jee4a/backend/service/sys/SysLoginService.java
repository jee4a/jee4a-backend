package com.jee4a.backend.service.sys;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.jee4a.backend.common.config.ConfigValues;
import com.jee4a.backend.common.shiro.ShiroUtils;
import com.jee4a.backend.common.utils.DateUtils;
import com.jee4a.backend.hub.mapper.sys.SysUserLoginLogMapper;
import com.jee4a.backend.hub.mapper.sys.SysUserMapper;
import com.jee4a.backend.hub.model.CacheKeys;
import com.jee4a.backend.hub.model.sys.SysUser;
import com.jee4a.backend.hub.model.sys.SysUserLoginLog;
import com.jee4a.common.BaseService;
import com.jee4a.common.Result;
import com.jee4a.common.exceptions.BusinessException;

/**
 * @date 2018年3月8日
 */
@Service
public class SysLoginService extends BaseService {
	@Resource
	private Producer producer;
	@Resource
	private ConfigValues configValues;
	@Resource
	private SysUserMapper sysUserMapper;
	@Resource
	private SysUserLoginLogMapper sysUserLoginLogMapper;

	public void createCaptcha(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");

		// 生成文字验证码
		String text = producer.createText();
		// 生成图片验证码
		BufferedImage image = producer.createImage(text);
		// 保存到shiro session
		ShiroUtils.setSessionAttribute(Constants.KAPTCHA_SESSION_KEY, text);

		try {
			ServletOutputStream out = response.getOutputStream();
			ImageIO.write(image, "jpg", out);
		} catch (IOException e) {
			logger.error("生成验证码错误：" + e.getMessage(), e);
		}

	}

	public Result login(String captcha, String userName, String password,
			String ip, String userAgent) {
		Result result = new Result();
		Integer userId = null;
		try {
			SysUser user = null;
			// 按用户名查询用户
			SysUser user1 = sysUserMapper.selectByUserName(userName);
			if (user1 != null) {
				user = user1;
			} else {
				// 按手机号查询用户
				SysUser user2 = sysUserMapper.selectByMobile(userName);
				if (user2 != null) {
					user = user2;
					userName = user2.getUserName();
				}

			}
			if (user == null) {
				throw new BusinessException(-1005, "用户名不存在");
			}
			Date lastUpdatePwd = null;
			if (user != null) {
				lastUpdatePwd = user.getLastUpdatePwdTime();
			} else if (user1 != null) {
				lastUpdatePwd = user1.getLastUpdatePwdTime();

			}
			if (org.springframework.util.StringUtils.isEmpty(lastUpdatePwd)) {
				lastUpdatePwd = new Date();
			}
			boolean isUpdatePwd = DateUtils.daysBetween(new Timestamp(
					lastUpdatePwd.getTime()),
					new Timestamp(new Date().getTime())) >= 90;

			if (isUpdatePwd) {
				throw new BusinessException(-1009, "距离上次修改密码超过90天，请修改密码再登录");
			}
			if (configValues.isCaptchaIsOpen()) {
				if (org.apache.commons.lang.StringUtils.isEmpty(captcha)) {
					throw new BusinessException(-1000, "验证码不能为空");
				}
				String kaptcha = ShiroUtils
						.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
				if (!captcha.equalsIgnoreCase(kaptcha)) {
					throw new BusinessException(-1002, "验证码不正确");
				}
			}

			if (org.apache.commons.lang.StringUtils.isEmpty(userName)) {
				throw new BusinessException(-1003, "用户名不能为空");
			}
			if (org.apache.commons.lang.StringUtils.isEmpty(password)) {
				throw new BusinessException(-1004, "密码不能为空");
			}

			Subject subject = ShiroUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(userName,
					password);
			subject.login(token);
			
			String cacheKey = CacheKeys.KEY_USER_LOGIN_INFO.getKeyPrefix(user.getId());
			redisUtils.setex(cacheKey, DateUtils.getIntervalsEndTime(),user.getId());

		} catch (UnknownAccountException e) {
			result.setError(-1005, e.getMessage());
		} catch (IncorrectCredentialsException e) {
			result.setError(-1006, "账号或密码不正确");
		} catch (LockedAccountException e) {
			result.setError(-1007, "账号已被锁定,请联系管理员");
		} catch (AuthenticationException e) {
			result.setError(-1008, "账户验证失败");
		} catch (BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}
		
		if (result.canNext()) {
			userId = ShiroUtils.getUserId();
			result.setSuccess();
		}
		logLogin(userName, ip, userAgent, result.getCode(),
				userId == null ? null : userId);
		result.print();
		return result;
	}

	private void logLogin(String userName, String ip, String userAgent,
			Integer loginStatus, Integer userId) {
		try {
			SysUserLoginLog log = new SysUserLoginLog();
			log.setUserName(userName);
			log.setLoginStatus(loginStatus);
			log.setLoginTime(new Date());
			log.setIp(ip);
			log.setClientInfo(userAgent);
			if (null != userId) {
				log.setUserId(userId);
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					sysUserLoginLogMapper.insertSelective(log);
				}
			}).start();
		} catch (Exception e) {
		}
	}
	
	public Result updatePassword(String name, String originalPassword,String newPassword, String trueNewPassword) {
		Result result = new Result();
		try {

			if (StringUtils.isEmpty(originalPassword)) {
				result.setError(-50001, "请输入原密码");
				return result;
			}
			if (StringUtils.isEmpty(newPassword)) {
				result.setError(-50002, "请输入新密码");
				return result;
			}
			if(StringUtils.isEmpty(trueNewPassword)) {
				result.setError(-50002, "确认密码不能为空");
				return result;
			}
			if(!newPassword.equals(trueNewPassword)) {
				result.setError(-50002, "两次输入密码不一致");
				return result;
			}
			SysUser user = sysUserMapper.queryByName(name);
			if(user == null){
				result.setError(-50002, "未查询到用户信息");
				return result;	
			}
			
			// 原密码 
			originalPassword = ShiroUtils.sha256(originalPassword, user.getSalt());
			// 新密码
			newPassword = ShiroUtils.sha256(newPassword, user.getSalt());

			if (!originalPassword.equals(user.getUserPwd())) {
				result.setError(-60003, "原始密码错误");
				return result;
			}

			SysUser record = new SysUser();
			record.setId(user.getId());
			record.setUserPwd(newPassword);
			record.setLastUpdatePwdTime(new Date());
			sysUserMapper.updateByPrimaryKeySelective(record);
			result.setSuccess();
		}catch(BusinessException e) {
			result.setError(e.getCode(), e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setDefaultError();
		}

		return result;
		
	}


}
