/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.entity.recon;

import java.io.Serializable;

/**
 * 对账通知.
 * @author zhp
 */
public class RecNoticeBean implements Serializable{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 118110888933970228L;
	
	//汇总文件名称
	private String fileNameCl;
	//文件路径
	private String filePath;
	//对账时间
	private String getFileDate;
	//业务渠道
	private String channelCd;
	//明细文件名称
	private String fileNameLt;
	
	public String getFileNameCl() {
		return fileNameCl;
	}
	public void setFileNameCl(String fileNameCl) {
		this.fileNameCl = fileNameCl;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getGetFileDate() {
		return getFileDate;
	}
	public void setGetFileDate(String getFileDate) {
		this.getFileDate = getFileDate;
	}
	public String getChannelCd() {
		return channelCd;
	}
	public void setChannelCd(String channelCd) {
		this.channelCd = channelCd;
	}
	public String getFileNameLt() {
		return fileNameLt;
	}
	public void setFileNameLt(String fileNameLt) {
		this.fileNameLt = fileNameLt;
	}
	
	@Override
	public String toString() {
		return "RecNoticeBean [fileNameCl=" + fileNameCl + ", filePath=" + filePath + ", getFileDate=" + getFileDate
				+ ", channelCd=" + channelCd + ", fileNameLt=" + fileNameLt + "]";
	}
	
	
	
	

}
