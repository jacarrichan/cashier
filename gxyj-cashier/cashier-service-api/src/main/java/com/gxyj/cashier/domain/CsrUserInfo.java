/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.domain;

/**
 * 用户信息
 * @author chensj
 */
public class CsrUserInfo extends BaseEntity {
   
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 399104896159384108L;

	/**
     * 用户编号
     */
    private String userId;

    /**
     * 所属平台
     */
    private String mallId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户真实姓名
     */
    private String trueName;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String telphone;

    /**
     * 固定电话
     */
    private String landlinePhone;

    /**
     * 地址
     */
    private String address;

    /**
     * 性别
     */
    private String sex;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 数据来源：00=清结算系统，01=电商平台，02=测土配肥
     */
    private String source;

    /**
     * 登录状态  00 未登录  01 已登录  02 登录异常
     */
    private String loginStatus;

    /**
     * 用户头像
     */
    private String headImg;

    /**
     * 状态
     */
    private String status;

    
    /**
     * 用户编号
     * @return user_id 用户编号
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 用户编号
     * @param userId 用户编号
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 所属平台
     * @return mall_id 所属平台
     */
    public String getMallId() {
        return mallId;
    }

    /**
     * 所属平台
     * @param mallId 所属平台
     */
    public void setMallId(String mallId) {
        this.mallId = mallId;
    }

    /**
     * 用户名称
     * @return user_name 用户名称
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 用户名称
     * @param userName 用户名称
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 用户真实姓名
     * @return true_name 用户真实姓名
     */
    public String getTrueName() {
        return trueName;
    }

    /**
     * 用户真实姓名
     * @param trueName 用户真实姓名
     */
    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    /**
     * 登录密码
     * @return password 登录密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 登录密码
     * @param password 登录密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 用户类型
     * @return user_type 用户类型
     */
    public String getUserType() {
        return userType;
    }

    /**
     * 用户类型
     * @param userType 用户类型
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * 邮箱
     * @return email 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 邮箱
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 手机号码
     * @return telphone 手机号码
     */
    public String getTelphone() {
        return telphone;
    }

    /**
     * 手机号码
     * @param telphone 手机号码
     */
    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    /**
     * 固定电话
     * @return landline_phone 固定电话
     */
    public String getLandlinePhone() {
        return landlinePhone;
    }

    /**
     * 固定电话
     * @param landlinePhone 固定电话
     */
    public void setLandlinePhone(String landlinePhone) {
        this.landlinePhone = landlinePhone;
    }

    /**
     * 地址
     * @return address 地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 地址
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 性别
     * @return sex 性别
     */
    public String getSex() {
        return sex;
    }

    /**
     * 性别
     * @param sex 性别
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 生日
     * @return birthday 生日
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * 生日
     * @param birthday 生日
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * 数据来源：00=清结算系统，01=电商平台，02=测土配肥
     * @return source 数据来源：00=清结算系统，01=电商平台，02=测土配肥
     */
    public String getSource() {
        return source;
    }

    /**
     * 数据来源：00=清结算系统，01=电商平台，02=测土配肥
     * @param source 数据来源：00=清结算系统，01=电商平台，02=测土配肥
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 登录状态  00 未登录  01 已登录  02 登录异常
     * @return login_status 登录状态  00 未登录  01 已登录  02 登录异常
     */
    public String getLoginStatus() {
        return loginStatus;
    }

    /**
     * 登录状态  00 未登录  01 已登录  02 登录异常
     * @param loginStatus 登录状态  00 未登录  01 已登录  02 登录异常
     */
    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }

    /**
     * 用户头像
     * @return head_img 用户头像
     */
    public String getHeadImg() {
        return headImg;
    }

    /**
     * 用户头像
     * @param headImg 用户头像
     */
    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    /**
     * 状态
     * @return status 状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 状态
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
