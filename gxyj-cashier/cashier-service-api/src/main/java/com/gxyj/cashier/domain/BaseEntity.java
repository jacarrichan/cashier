/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 实体类基础类
 * 
 * @author Danny
 */
public class BaseEntity implements Serializable {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -6950321401347175118L;

	/**
     * 主键ID
     */
	@Id
	@Column(name = "row_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rowId;
	
	@Transient
	private Integer page = 1;

	@Transient
	private Integer rows = 10;

	 /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private Date createdDate;

    /**
     * 修改人
     */
    private String lastUpdtBy;

    /**
     * 最后一次修改时间
     */
    private Date lastUpdtDate;

    /**
     * 版本号
     */
    private Integer version;

	/**
     * 主键ID
     * @return row_id 主键ID
     */
    public Integer getRowId() {
        return rowId;
    }

    /**
     * 主键ID
     * @param rowId 主键ID
     */
    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	/**
     * 创建人
     * @return created_by 创建人
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 创建人
     * @param createdBy 创建人
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 创建时间
     * @return created_date 创建时间
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * 创建时间
     * @param createdDate 创建时间
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * 修改人
     * @return last_updt_by 修改人
     */
    public String getLastUpdtBy() {
        return lastUpdtBy;
    }

    /**
     * 修改人
     * @param lastUpdtBy 修改人
     */
    public void setLastUpdtBy(String lastUpdtBy) {
        this.lastUpdtBy = lastUpdtBy;
    }

    /**
     * 最后一次修改时间
     * @return last_updt_date 最后一次修改时间
     */
    public Date getLastUpdtDate() {
        return lastUpdtDate;
    }

    /**
     * 最后一次修改时间
     * @param lastUpdtDate 最后一次修改时间
     */
    public void setLastUpdtDate(Date lastUpdtDate) {
        this.lastUpdtDate = lastUpdtDate;
    }

    /**
     * 版本号
     * @return version 版本号
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * 版本号
     * @param version 版本号
     */
    public void setVersion(Integer version) {
        this.version = version;
    }
	
	
}
