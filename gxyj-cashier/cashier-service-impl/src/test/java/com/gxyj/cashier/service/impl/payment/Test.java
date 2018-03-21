/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service.impl.payment;

import com.gxyj.cashier.common.web.Processor;
/**
 * 
 * @author chu.
 *
 */
public final class Test {
	private Test() {
	}
	public static void main(String[] args) {
		Processor pro = new Processor();
		pro.setToReq("jsonValue", "122222");
		System.out.println(pro.getStringForReq("jsonValue"));
	}
}
