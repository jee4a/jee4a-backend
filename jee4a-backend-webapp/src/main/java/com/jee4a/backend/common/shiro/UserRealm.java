package com.jee4a.backend.common.shiro;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jee4a.backend.common.enums.sys.SysDeletedEnum;
import com.jee4a.backend.common.enums.sys.SysUserStateEnum;
import com.jee4a.backend.hub.mapper.sys.SysResourceMapper;
import com.jee4a.backend.hub.mapper.sys.SysUserMapper;
import com.jee4a.backend.hub.model.BaseConstant;
import com.jee4a.backend.hub.model.sys.SysResource;
import com.jee4a.backend.hub.model.sys.SysUser;
import com.jee4a.common.lang.JsonUtils;


/**
 * 认证
 */
@Component
public class UserRealm extends AuthorizingRealm {
	
	public static  final Logger logger = LoggerFactory.getLogger(UserRealm.class);
	
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysResourceMapper sysResourceMapper;
    
    /**
     * 授权(验证权限时调用)
     */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SysUser user = (SysUser)principals.getPrimaryPrincipal();
		Integer userId = user.getId();
		
		List<String> permsList;
		
		//系统管理员，拥有最高权限
		if(userId == BaseConstant.SUPER_ADMIN){
			List<SysResource> menuList = sysResourceMapper.selectList(null);
			permsList = new ArrayList<>(menuList.size());
			for(SysResource menu : menuList){
				permsList.add(menu.getPerms());
			}
		}else{
			permsList = sysUserMapper.queryAllPerms(userId);
		}

		//用户权限列表
		Set<String> permsSet = new HashSet<>();
		for(String perms : permsList){
			if(StringUtils.isBlank(perms)){
				continue;
			}
			permsSet.addAll(Arrays.asList(perms.trim().split(",")));
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("perms: "+ JsonUtils.toJson(permsSet));
		}
		
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setStringPermissions(permsSet);
		return info;
	}

	/**
	 * 认证(登录时调用)
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken)authcToken;

        //查询用户信息
		SysUser user = new SysUser();
		user.setUserName(token.getUsername());
		user = sysUserMapper.selectByUserName(token.getUsername()) ;
        
        //账号不存在
        if(user == null) {
            throw new UnknownAccountException("账号或密码不正确");
        }
        if(user.getState().intValue()==SysUserStateEnum.DISABLED.getK()) {
        	 throw new LockedAccountException("账号已禁用，请联系管理员") ;
        }
        if(user.getIsDeleted().intValue()==SysDeletedEnum.DELETED.getK()) {
       	 throw new LockedAccountException("账号已删除，请联系管理员") ;
       }
       return new SimpleAuthenticationInfo(user, user.getUserPwd(), ByteSource.Util.bytes(user.getSalt()), getName());
	}

	@Override
	public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
		HashedCredentialsMatcher shaCredentialsMatcher = new HashedCredentialsMatcher();
		shaCredentialsMatcher.setHashAlgorithmName(ShiroUtils.HASH_ALGORITHM_NAME);
		shaCredentialsMatcher.setHashIterations(ShiroUtils.HASH_ITERATIONS);
		super.setCredentialsMatcher(shaCredentialsMatcher);
	}
}
