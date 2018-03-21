/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * 
 * 读取gxyjCashier.properties并封装成对象
 * @author Danny
 */
@Service
@ConfigurationProperties(prefix = "cashier")
// @PropertySource("classpath:gxyjCashier.properties")
public class CashierConfig {

	@Value("${cashier.http.read.timeout}")
	int readTimeout;

	@Value("${cashier.http.connect.timeout}")
	int connectTimeout;

	@Value("${cashier.http.max.total}")
	int maxTotal;

	@Value("${cashier.http.max.per.route}")
	int maxPerRoute;

	@Value("${cashier.ftp.ip}")
	String ftpServer;
	@Value("${cashier.ftp.port}")
	int ftpPort;
	@Value("${cashier.ftp.user}")
	String ftpUser;
	@Value("${cashier.ftp.password}")
	String ftpPwd;
	@Value("${cashier.ftp.timeout}")
	int ftpTimeout;

	@Value("${cashier.recon.default.filepath}")
	String defaultFilePath;

	@Value("${cashier.recln.ftp.ip}")
	String reclnSrvIp;

	@Value("${cashier.recln.ftp.port}")
	int reclnFtpPort;
	
	@Value("${cashier.recln.ftp.user}")
	String reclnFtpFtpUser;
	
	@Value("${cashier.recln.ftp.password}")
	String reclnFtpPwd;
	
	@Value("${cashier.recln.ftp.timeout}")
	int reclnFtpTimeout;

	@Value("${cashier.recln.bestpay.filepath}")
	String bestpayRcnlPath;
	
	
	@Value("${cashier.recln.ceb.filePath}")
	String cebRcnlPath;

	@Value("${cashier.recon.local.filepath}")
	String reconLocalPath;

	/**
	 * 
	 */
	public CashierConfig() {
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxPerRoute() {
		return maxPerRoute;
	}

	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}

	public String getFtpServer() {
		return ftpServer;
	}

	public void setFtpServer(String ftpServer) {
		this.ftpServer = ftpServer;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPwd() {
		return ftpPwd;
	}

	public void setFtpPwd(String ftpPwd) {
		this.ftpPwd = ftpPwd;
	}

	public int getFtpTimeout() {
		return ftpTimeout;
	}

	public void setFtpTimeout(int ftpTimeout) {
		this.ftpTimeout = ftpTimeout;
	}

	

	public String getDefaultFilePath() {
		return defaultFilePath;
	}

	public void setDefaultFilePath(String defaultFilePath) {
		this.defaultFilePath = defaultFilePath;
	}

	public String getReclnSrvIp() {
		return reclnSrvIp;
	}

	public void setReclnSrvIp(String reclnSrvIp) {
		this.reclnSrvIp = reclnSrvIp;
	}

	public int getReclnFtpPort() {
		return reclnFtpPort;
	}

	public void setReclnFtpPort(int reclnFtpPort) {
		this.reclnFtpPort = reclnFtpPort;
	}

	public String getReclnFtpFtpUser() {
		return reclnFtpFtpUser;
	}

	public void setReclnFtpFtpUser(String reclnFtpFtpUser) {
		this.reclnFtpFtpUser = reclnFtpFtpUser;
	}

	public String getReclnFtpPwd() {
		return reclnFtpPwd;
	}

	public void setReclnFtpPwd(String reclnFtpPwd) {
		this.reclnFtpPwd = reclnFtpPwd;
	}

	public int getReclnFtpTimeout() {
		return reclnFtpTimeout;
	}

	public void setReclnFtpTimeout(int reclnFtpTimeout) {
		this.reclnFtpTimeout = reclnFtpTimeout;
	}

	public String getBestpayRcnlPath() {
		return bestpayRcnlPath;
	}

	public void setBestpayRcnlPath(String bestpayRcnlPath) {
		this.bestpayRcnlPath = bestpayRcnlPath;
	}

	public String getCebRcnlPath() {
		return cebRcnlPath;
	}

	public void setCebRcnlPath(String cebRcnlPath) {
		this.cebRcnlPath = cebRcnlPath;
	}

	public String getReconLocalPath() {
		return reconLocalPath;
	}

	public void setReconLocalPath(String reconLocalPath) {
		this.reconLocalPath = reconLocalPath;
	}
	
	
	
	

}
