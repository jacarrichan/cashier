/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.mgmt.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

	/**
	 * ROLE_ADMIN
	 */
    public static final String ADMIN = "ROLE_ADMIN";

    /**
     *  ROLE_USER
     */
    public static final String USER = "ROLE_USER";

    /**
     * ROLE_ANONYMOUS
     */
    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {
    }
}
