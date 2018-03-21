/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.domain;

import java.util.Date;

/**
 * 
 * 支付渠道维护信息.
 * @author FangSS
 */
public class PayChnnlVind extends BaseEntity {
	
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -7740332133031834834L;

	private Integer channelId;

    private String procState;

    private Byte changeType;

    private String beginDate;

    private String endDate;

    private Date closedTime;

    private String vindExplain;
    
    private String emailsTo;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getProcState() {
        return procState;
    }

    public void setProcState(String procState) {
        this.procState = procState;
    }

    public Byte getChangeType() {
        return changeType;
    }

    public void setChangeType(Byte changeType) {
        this.changeType = changeType;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Date getClosedTime() {
        return closedTime;
    }

    public void setClosedTime(Date closedTime) {
        this.closedTime = closedTime;
    }

    public String getVindExplain() {
        return vindExplain;
    }

    public void setVindExplain(String vindExplain) {
        this.vindExplain = vindExplain;
    }

	public String getEmailsTo() {
		return emailsTo;
	}

	public void setEmailsTo(String emailsTo) {
		this.emailsTo = emailsTo;
	}
    
    
}
