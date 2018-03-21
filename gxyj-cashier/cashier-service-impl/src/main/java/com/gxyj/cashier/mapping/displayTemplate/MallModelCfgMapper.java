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

import com.gxyj.cashier.domain.MallModelCfg;
/**
 * 
 * 地方模板.
 * @author zhup
 */
public interface MallModelCfgMapper {
	
    boolean deleteByPrimaryKey(Integer rowId);

    int deleteByMallId(String mallId);

    int insert(MallModelCfg record);

    boolean insertSelective(MallModelCfg record);

    MallModelCfg selectByPrimaryKey(Integer rowId);

    boolean updateByPrimaryKeySelective(MallModelCfg record);

    int updateByPrimaryKey(MallModelCfg record);

    boolean updateByMallIdSelective(MallModelCfg record);

	List<MallModelCfg> selectMallModelList(MallModelCfg mallModelCfg);

	List<MallModelCfg> queryMallModelList(MallModelCfg mallModelCfg);
	
	MallModelCfg queryMallModel(MallModelCfg mallModelCfg);

	List<MallModelCfg> selectMallModelPojo(MallModelCfg mallModelCfg);

	MallModelCfg selectArg(int rowId);

}
