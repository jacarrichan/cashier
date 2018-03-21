/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 对账结果数据
 * 
 * @author Danny
 */
public class ReconDataResult implements Serializable{

	private ReconSummryData summryData;
	
	private List<ReconDataDetail> dataDetails; 
	
	private String resultStatus;
	
	private String checkDate;
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -2793507236594690979L;

	/**
	 * 
	 */
	public ReconDataResult() {
	}
	
	

	public ReconDataResult(String checkDate) {
		this();
		this.checkDate = checkDate;
	}



	public ReconSummryData getSummryData() {
		return summryData;
	}

	public void setSummryData(ReconSummryData summryData) {
		this.summryData = summryData;
	}

	public List<ReconDataDetail> getDataDetails() {
		return dataDetails;
	}

	public void setDataDetails(List<ReconDataDetail> dataDetails) {
		this.dataDetails = dataDetails;
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}
	
	
	
	
}
