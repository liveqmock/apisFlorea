package com.openIdeas.apps.apisflorea.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.openIdeas.apps.apisflorea.enums.HandlerStatus;

@Entity
@Table(name = "AF_MAIL_MESSAGE")
public class MailEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6487418491176525017L;

	/** 邮件唯一编码*/
	private String messageId;

	/** 主题*/
	private String subject;
	
	/** 处理状态 */
	private HandlerStatus status;

	@Id
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Enumerated(javax.persistence.EnumType.STRING)
	public HandlerStatus getStatus() {
		return status;
	}

	public void setStatus(HandlerStatus status) {
		this.status = status;
	}
}
