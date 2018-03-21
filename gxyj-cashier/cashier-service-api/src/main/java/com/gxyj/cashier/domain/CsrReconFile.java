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
 * 对账文件下载状态记录
 * 
 * @author Danny
 */
public class CsrReconFile  extends BaseEntity{
   
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 2800388876827136058L;

	private Integer channelId;

    private String channelCode;

    private String reconDate;

    private Integer dataSts;

    private String dataFile;

    private String srvIp;

    private String procState;

   
    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getReconDate() {
        return reconDate;
    }

    public void setReconDate(String reconDate) {
        this.reconDate = reconDate;
    }

    public Integer getDataSts() {
        return dataSts;
    }

    public void setDataSts(Integer dataSts) {
        this.dataSts = dataSts;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getSrvIp() {
        return srvIp;
    }

    public void setSrvIp(String srvIp) {
        this.srvIp = srvIp;
    }

    public String getProcState() {
        return procState;
    }

    public void setProcState(String procState) {
        this.procState = procState;
    }

}
