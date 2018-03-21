/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.displayTemplate;

import java.util.List;

import com.gxyj.cashier.domain.WebpageModelCfg;
/**
 * 
 * 收银台页面配置.
 * @author zhup
 */
public interface WebpageModelCfgMapper {
    boolean deleteByPrimaryKey(Integer rowId);

    int insert(WebpageModelCfg record);

    boolean insertSelective(WebpageModelCfg record);

    WebpageModelCfg selectByPrimaryKey(Integer rowId);

    boolean updateByPrimaryKeySelective(WebpageModelCfg record);

    int updateByPrimaryKey(WebpageModelCfg record);

	List<WebpageModelCfg> selectWebpageModelList(WebpageModelCfg webpageModelCfg);

	List<WebpageModelCfg> checkName(WebpageModelCfg webpageModelCfg);

	List<WebpageModelCfg> queryWebpageModelList(WebpageModelCfg webpageModel);

	List<WebpageModelCfg> selectByPojo(WebpageModelCfg qDate);

	WebpageModelCfg selectArg(int rowId);

}
