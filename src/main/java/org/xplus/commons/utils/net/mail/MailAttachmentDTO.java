package org.xplus.commons.utils.net.mail;

import java.io.File;

/**
 * 邮件附件类封装.
 * 
 * <pre>
 * </pre>
 * 
 * @author JQM [C] 2018年7月16日
 * @version 1.0
 *
 */
public class MailAttachmentDTO {

	private String name;
	private File file;
	private long size;

	public MailAttachmentDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MailAttachmentDTO(String name, File file, long size) {
		super();
		this.name = name;
		this.file = file;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
