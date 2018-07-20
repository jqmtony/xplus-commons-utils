package org.xplus.commons.utils.net.mail;

import java.io.Serializable;
import java.util.List;

/**
 * 邮件内容封装.
 * <pre>
 * </pre>
 * @author JQM [C] 2018年7月16日
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class MailDTO implements Serializable {
	
	private String receiver;//
    private String subject;//
    private String toAddress;//收件人地址
    private String formAddress;//发件人地址
    private String content;//邮件内容
    private List<MailAttachmentDTO> attachments;//邮件附件
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public String getFormAddress() {
		return formAddress;
	}
	public void setFormAddress(String formAddress) {
		this.formAddress = formAddress;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<MailAttachmentDTO> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<MailAttachmentDTO> attachments) {
		this.attachments = attachments;
	}
    
    

}
