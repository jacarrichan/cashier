/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * 退款交易表
 * 
 * @author Danny
 */
public class RefundOrderInfo extends BaseEntity{
	
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -1677598273482052149L;

	private String transId;

    private String refundId;

    private String orgnOrderId;
    
    private Integer channelId;

    private String channelCd;

    private String clientIp;

    private BigDecimal refundAmt;

    private BigDecimal orgnTransAmt;

    private Date refundDate;

    private String procState;

    private Integer errFlag;

    private String reconFlag;

    private String remark;
    
    private String mallId;
    
    private String payerInstiNo;
    
    private String payerInstiNm;
    
    private String instiRespCd;	//支付渠道处理码
    
    private String instiRspDes;	 //支付渠道处理描述
    
    private String instiRspTime; //支付响应时间
    
    private String dealTime;	//退款完成时间

    
    public String getInstiRespCd() {
		return instiRespCd;
	}

	public void setInstiRespCd(String instiRespCd) {
		this.instiRespCd = instiRespCd;
	}

	public String getInstiRspDes() {
		return instiRspDes;
	}

	public void setInstiRspDes(String instiRspDes) {
		this.instiRspDes = instiRspDes;
	}

	public String getDealTime() {
		return dealTime;
	}

	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}

	public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getOrgnOrderId() {
        return orgnOrderId;
    }

    public void setOrgnOrderId(String orgnOrderId) {
        this.orgnOrderId = orgnOrderId;
    }

    public String getChannelCd() {
        return channelCd;
    }

    public void setChannelCd(String channelCd) {
        this.channelCd = channelCd;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public BigDecimal getRefundAmt() {
        return refundAmt;
    }

    public void setRefundAmt(BigDecimal refundAmt) {
        this.refundAmt = refundAmt;
    }

    public BigDecimal getOrgnTransAmt() {
        return orgnTransAmt;
    }

    public void setOrgnTransAmt(BigDecimal orgnTransAmt) {
        this.orgnTransAmt = orgnTransAmt;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public String getProcState() {
        return procState;
    }

    public void setProcState(String procState) {
        this.procState = procState;
    }

   

    public Integer getErrFlag() {
		return errFlag;
	}

	public void setErrFlag(Integer errFlag) {
		this.errFlag = errFlag;
	}


	public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public String getMallId() {
		return mallId;
	}

	public void setMallId(String mallId) {
		this.mallId = mallId;
	}
	
	public String getPayerInstiNo() {
		return payerInstiNo;
	}

	public void setPayerInstiNo(String payerInstiNo) {
		this.payerInstiNo = payerInstiNo;
	}

	public String getPayerInstiNm() {
		return payerInstiNm;
	}

	public void setPayerInstiNm(String payerInstiNm) {
		this.payerInstiNm = payerInstiNm;
	}

	@Override
	public String toString() {
		return "RefundOrderInfo [transId=" + transId + ", refundId=" + refundId + ", orgnOrderId=" + orgnOrderId
				+ ", channelId=" + channelId + ", channelCd=" + channelCd + ", clientIp=" + clientIp + ", refundAmt="
				+ refundAmt + ", orgnTransAmt=" + orgnTransAmt + ", refundDate=" + refundDate + ", procState="
				+ procState + ", errFlag=" + errFlag + ", reconFlag=" + reconFlag + ", remark=" + remark + "]";
	}

	public String getReconFlag() {
		return reconFlag;
	}

	public void setReconFlag(String reconFlag) {
		this.reconFlag = reconFlag;
	}

	public String getInstiRspTime() {
		return instiRspTime;
	}

	public void setInstiRspTime(String instiRspTime) {
		this.instiRspTime = instiRspTime;
	}
    
    

}
