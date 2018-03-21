/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service;

import java.io.Serializable;

/**
 * 邮件发送参数对象
 * @author Danny
 */
public class MailRequest implements Serializable {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -6712850098373824567L;
	/**
	 * 邮件接收者，多个接收者以英文;隔开
	 */
	private String to;

	/**
	 * 邮件主题编码
	 */
	private String subject;

	/**
	 * 邮件内容
	 */
	private String content;

	/**
	 * 邮件模板
	 */
	private String templateName;

	/**
	 * 内容是否HTML
	 */
	private boolean html = false;

	/**
	 * 是否发送符件
	 */
	private boolean multipart = false;

	/**
	 * 模板中预使用的参数
	 */
	private Object[] args;

	/**
	 * 发送时模板中获取的对象实例
	 */
	private Object object;

	private String[] attachmentFiles;

	/**
	 * 
	 */
	public MailRequest() {
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public boolean isHtml() {
		return html;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public boolean isMultipart() {
		return multipart;
	}

	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String[] getAttachmentFiles() {
		return attachmentFiles;
	}

	public void setAttachmentFiles(String[] attachmentFiles) {
		this.attachmentFiles = attachmentFiles;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
