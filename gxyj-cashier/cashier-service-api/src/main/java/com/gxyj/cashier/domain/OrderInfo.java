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
 * @author CHU.
 *
 */
public class OrderInfo extends OrderInfoKey {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String transId; //交易流水号

    private String terminal; //支付终端:01:PC,02:WAP,03:APP,04其它

    private String orderType; //
 
    private Integer channelId; //业务渠道Id
    
    private String channelCd; //业务渠道号
    
    private String mallId;  //平台ID
    
    private String payPhone; //付款人手机号

    private String clientIp; //客户IP

    private BigDecimal transAmt; //支付金额

    private BigDecimal chargeFee; //支付手续费

    private Date transTime; //交易时间

    private String procState; //交易处理状态 00-支付成功 01-支付失败 02-未支付 03-处理中 04-订单关闭 05-订单超时

    private Integer errFlag; //异常标志 0-正常  1-异常

    private String errDesc; //异常描述

    private String reconFlag; //对账状态

    private String remark; //备注，预留字段
    
    private String prodName; //商品名称
    
    private String payerInstiNm; //订单支付渠道名称
    
    private String instiRspTime; //支付交易时间
    
    private String payerInstiNo; //订单支付渠道

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public BigDecimal getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(BigDecimal transAmt) {
        this.transAmt = transAmt;
    }

    public BigDecimal getChargeFee() {
        return chargeFee;
    }

    public void setChargeFee(BigDecimal chargeFee) {
        this.chargeFee = chargeFee;
    }

    public Date getTransTime() {
        return transTime;
    }

    public void setTransTime(Date transTime) {
        this.transTime = transTime;
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

	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}

	
	public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

   	public String getPayPhone() {
		return payPhone;
	}

	public void setPayPhone(String payPhone) {
		this.payPhone = payPhone;
	}
	
	

	public String getChannelCd() {
		return channelCd;
	}

	public void setChannelCd(String channelCd) {
		this.channelCd = channelCd;
	}

	public String getPayerInstiNm() {
		return payerInstiNm;
	}

	public void setPayerInstiNm(String payerInstiNm) {
		this.payerInstiNm = payerInstiNm;
	}

	public String getInstiRspTime() {
		return instiRspTime;
	}

	public void setInstiRspTime(String instiRspTime) {
		this.instiRspTime = instiRspTime;
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

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	@Override
	public String toString() {
		return "OrderInfo [transId=" + transId + ", terminal=" + terminal + ", orderType=" + orderType + ", channelId="
				+ channelId + ", channelCd=" + channelCd + ", mallId=" + mallId + ", payPhone=" + payPhone
				+ ", clientIp=" + clientIp + ", transAmt=" + transAmt + ", chargeFee=" + chargeFee + ", transTime="
				+ transTime + ", procState=" + procState + ", errFlag=" + errFlag + ", errDesc=" + errDesc
				+ ", reconFlag=" + reconFlag + ", remark=" + remark + ", prodName="
				+ prodName + ", payerInstiNm=" + payerInstiNm + ", instiRspTime=" + instiRspTime + ", payerInstiNo="
				+ payerInstiNo + "]";
	}

	public String getReconFlag() {
		return reconFlag;
	}

	public void setReconFlag(String reconFlag) {
		this.reconFlag = reconFlag;
	}
	
	
}
