/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mapping.recon;

import java.util.List;

import com.gxyj.cashier.domain.CsrReconFile;

/**
 * 
 * 对账文件下载状态
 * 
 * @author Danny
 */
public interface CsrReconFileMapper {
	
	int deleteByPrimaryKey(Integer rowId);

	int insert(CsrReconFile record);

	int insertSelective(CsrReconFile record);

	CsrReconFile selectByPrimaryKey(Integer rowId);

	int updateByPrimaryKeySelective(CsrReconFile record);

	int updateByPrimaryKey(CsrReconFile record);
	
	List<CsrReconFile> selectByCriteira(CsrReconFile criteria);
	
	int queryFileExists(CsrReconFile criteria);
}
