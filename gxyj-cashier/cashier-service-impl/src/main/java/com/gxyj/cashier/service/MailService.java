/*
 * Copyright (c) 2015-2017 China CO-OP ELECTRONIC COMMERCE CO. LTD. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * China CO-OP ELECTRONIC COMMERCE CO. LTD. ("Confidential Information").
 * It may not be copied or reproduced in any manner without the express
 * written permission of China CO-OP ELECTRONIC COMMERCE CO. LTD.
 */

package com.gxyj.cashier.service;

import java.io.File;
import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.springframework.core.io.FileSystemResource;

import com.gxyj.cashier.config.EmailConfig;

/**
 * 邮件服务
 * @author chensj
 */
@Component
public class MailService {

	private static final String SPLIT_SEMICOLON = ";";

	private final Logger log = LoggerFactory.getLogger(MailService.class);

	private static final String KEY_OBJ = "obj";

	private static final String BASE_URL = "baseUrl";

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private MessageSource messageSource;

	private SpringTemplateEngine templateEngine;

	@Autowired
	private EmailConfig emailConfig;

	public MailService(SpringTemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	/**
	 * 按资源配置获取邮件主题，内容等，然后再发送给接收者
	 * 
	 * @param mailRequest 邮件发送请求参数
	 */
	@Async
	public void sendEmailByTemplate(MailRequest mailRequest) {

		String to = mailRequest.getTo();

		log.debug("Sending activation e-mail to '{}'", to);
		// Locale locale = Locale.forLanguageTag("zh_cn");// user.getLangKey()
		Locale locale = new Locale("");
		Context context = new Context(locale);
		if (mailRequest.getObject() != null) {
			context.setVariable(KEY_OBJ, mailRequest.getObject());
		}
		context.setVariable(BASE_URL, emailConfig.getBaseUrl());

		String content = mailRequest.getContent();

		String templateName = mailRequest.getTemplateName();

		if (StringUtils.isNotBlank(templateName)) {
			content = templateEngine.process(templateName, context);// "creationEmail"
			mailRequest.setContent(content);
			mailRequest.setHtml(true);
		}

		String subjectCode = mailRequest.getSubject();

		String subject = messageSource.getMessage(subjectCode, null, locale);
		mailRequest.setSubject(subject);

		sendEmail(mailRequest);
	}

	/**
	 * 按请求参数发送邮件
	 * 
	 * @param mailRequest 邮件发送请求参数
	 */
	@Async
	public void sendEmail(MailRequest mailRequest) {

		boolean isHtml = mailRequest.isHtml();
		boolean multipart = mailRequest.isMultipart();
		String to = mailRequest.getTo();
		String subject = mailRequest.getSubject();
		String content = mailRequest.getContent();

		log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", multipart, isHtml, to,
				subject, content);

		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		log.debug("<<<<<>>>>>>>>>>>>>>>" + mimeMessage);
		try {
			String[] toUsers = to.split(SPLIT_SEMICOLON);

			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, multipart, CharEncoding.UTF_8);
			String[] attachmentFiles = mailRequest.getAttachmentFiles();
			
			if (multipart && attachmentFiles!=null) {
				
				for (int i = 0; i < attachmentFiles.length; i++) {

					File file = new File(attachmentFiles[i]);
					FileSystemResource attachmentFile = new FileSystemResource(file);
					messageHelper.addAttachment(file.getName(), attachmentFile);
				}
			}

			messageHelper.setFrom(emailConfig.getFrom());
			messageHelper.setSubject(subject);
			messageHelper.setText(content, isHtml);
			for (int i = 0; i < toUsers.length; i++) {
				messageHelper.setTo(toUsers[i]);
				javaMailSender.send(mimeMessage);
			}

			log.debug("Sent e-mail to User '{}'", to);
		}
		catch (

		Exception e) {
			log.warn("E-mail could not be sent to user '{}'", to, e);
		}
	}

}
