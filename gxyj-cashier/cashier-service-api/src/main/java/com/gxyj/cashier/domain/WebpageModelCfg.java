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
 * 收银台页面配置.
 * @author zhup
 */
public class WebpageModelCfg extends BaseEntity {
	
	private static final long serialVersionUID = 6769606205910389401L;

    private String pageName;

    private String introduction;

    private Integer channelId;
    
    private String channelName;

    private Integer templateId;

    private String templateName;

    private String terminal;

    private String defautlWebpage;

    private Byte openFlag;

    private String pageAddress;
    
    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getDefautlWebpage() {
        return defautlWebpage;
    }

    public void setDefautlWebpage(String defautlWebpage) {
        this.defautlWebpage = defautlWebpage;
    }

	public Byte getOpenFlag() {
		return openFlag;
	}

	public void setOpenFlag(Byte openFlag) {
		this.openFlag = openFlag;
	}

	public String getPageAddress() {
		return pageAddress;
	}

	public void setPageAddress(String pageAddress) {
		this.pageAddress = pageAddress;
	}
    
}
