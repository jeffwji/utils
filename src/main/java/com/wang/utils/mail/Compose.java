package com.wang.utils.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Compose {
	String from;
	List<String> to;
	List<String> cc;
	List<String> bcc;
	String content;
	String subject;

	public List<String> getBcc() {
		return bcc;
	}
	
	public List<String> getSendList(String emails){
		if(StringUtils.isBlank(emails)) return new ArrayList<String>();
		return Arrays.asList(emails.split("[,;，；]"));
	}

	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}


	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

}
