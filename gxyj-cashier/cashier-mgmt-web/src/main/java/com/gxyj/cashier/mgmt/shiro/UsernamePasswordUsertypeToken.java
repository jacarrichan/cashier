package com.gxyj.cashier.mgmt.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 
 * 用户登录后信息保存在shiro的会话中 
 * @author Danny
 */
public class UsernamePasswordUsertypeToken extends UsernamePasswordToken {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	private String userType;
	
	private String uiaURL;

	public UsernamePasswordUsertypeToken() {
	}

	public UsernamePasswordUsertypeToken(String username, char[] password) {
		super(username, password);
	}

	public UsernamePasswordUsertypeToken(String username, String password) {
		super(username, password);
	}

	public UsernamePasswordUsertypeToken(String username, char[] password, String host) {
		super(username, password, host);
	}

	public UsernamePasswordUsertypeToken(String username, String password, String host) {
		super(username, password, host);
	}

	public UsernamePasswordUsertypeToken(String username, char[] password, boolean rememberMe) {
		super(username, password, rememberMe);
	}

	public UsernamePasswordUsertypeToken(String username, String password, boolean rememberMe) {
		super(username, password, rememberMe);
	}

	public UsernamePasswordUsertypeToken(String username, char[] password, boolean rememberMe, String host) {
		super(username, password, rememberMe, host);
	}

	public UsernamePasswordUsertypeToken(String username, String password, boolean rememberMe, String host) {
		super(username, password, rememberMe, host);
	}
	
	public UsernamePasswordUsertypeToken(String username, String password, boolean rememberMe, String host, String userType) {
		super(username, password, rememberMe, host);
		this.userType = userType;
	}
	
	public UsernamePasswordUsertypeToken(String username, String password, boolean rememberMe, String host, String userType, String uiaURL) {
		super(username, password, rememberMe, host);
		this.userType = userType;
		this.uiaURL = uiaURL;
	}

	public String getUserType() {
		return userType;
	}

	public String getUiaURL() {
		return uiaURL;
	}

	public void setUiaURL(String uiaURL) {
		this.uiaURL = uiaURL;
	}
	
	

}
