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
 * 
 * 收银台支付模板配置.
 * @author zhup
 */
public class PayTemplate  extends BaseEntity {
	
	private static final long serialVersionUID = -430109574589896586L;

    private String templateName;

    private String templateUrl;

    private String terminal;

    private Byte openFlag;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public Byte getOpenFlag() {
        return openFlag;
    }

    public void setOpenFlag(Byte openFlag) {
        this.openFlag = openFlag;
    }

	@Override
	public String toString() {
		return "PayTemplate [templateName=" + templateName + ", templateUrl=" + templateUrl + ", terminal=" + terminal
				+ ", openFlag=" + openFlag + "]";
	}

    
}
