package net.tinybrick.utils.mail;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MailBroker {
	final Logger logger = LogManager.getLogger(this.getClass());

	String defaultSender = null;
	String username = null;
	String password = null;
	String host = null;

	public MailBroker(MailConfig config) {
		defaultSender = config.getDefaultSender();
		username = config.getProps().getProperty("mail.smtp.user");
		password = config.getProps().getProperty("mail.smtp.pass");
		host = config.getProps().getProperty("mail.smtp.host");
	}

	public void send(Compose envelop) throws MessagingException {
		this.send(envelop, 0);
	}

	/**
	 * Apache email
	 * 
	 * @param contentType
	 * @return
	 */
	protected Email getEmailDelegate(Integer contentType) {
		if (0 != contentType) {
			HtmlEmail email = new HtmlEmail();
			email.setCharset("utf-8");
			email.setAuthenticator(new DefaultAuthenticator(username, password));
			email.setHostName(host);
			logger.info("Html email delegate is created.");

			return email;
		}
		else {
			Email email = new SimpleEmail();
			email.setCharset("utf-8");
			email.setAuthenticator(new DefaultAuthenticator(username, password));
			email.setHostName(host);
			logger.info("Plain text email delegate is created.");

			return email;
		}
	}

	/**
	 * @param envelop
	 * @param contentType
	 *            邮件格式，0-纯文本，其他-html
	 * @throws MessagingException
	 * @throws EmailException
	 */
	public void send(Compose envelop, Integer contentType) throws MessagingException {
		logger.info("Sending email...");
		Email email = getEmailDelegate(contentType);

		try {
			setReceivers(email, envelop);
			setEmailContent(email, envelop, contentType);

			// send the email
			email.send();
			logger.info("Email is sent");
		}
		catch (EmailException e) {
			throw new MessagingException(e.getMessage(), e);
		}
	}

	protected void setEmailContent(Email email, Compose envelop, Integer contentType) throws EmailException {
		logger.debug("Email content type: " + contentType);

		if (0 != contentType) {
			((HtmlEmail) email).setHtmlMsg(envelop.getContent());
			// set the alternative message
			((HtmlEmail) email).setTextMsg("Your email client does not support HTML messages");
		}
		else {
			email.setMsg(envelop.getContent());
		}
	}

	/**
	 * @param email
	 * @param envelop
	 * @throws EmailException
	 */
	protected void setReceivers(Email email, Compose envelop) throws EmailException {
		try {
			// From
			String sender = null == envelop.getFrom() ? defaultSender : envelop.getFrom();
			logger.debug("Email sender: " + sender);
			email.setFrom(username, MimeUtility.encodeText(sender, "UTF-8", "B"));

			// Reciver
			for (String recipient : envelop.getTo()) {
				if (StringUtils.isNotBlank(recipient)) {
					email.addTo(recipient, MimeUtility.encodeText(recipient, "UTF-8", "B"));
					logger.debug("Receiver: " + recipient + " is added");
				}
			}

			//抄送
			if (envelop.getCc() != null) {
				for (String recipient : envelop.getCc()) {
					if (StringUtils.isNotBlank(recipient)) {
						email.addCc(recipient, MimeUtility.encodeText(recipient, "UTF-8", "B"));
						logger.debug("Cc: " + recipient + " is added");
					}
				}
			}

			//密送
			if (envelop.getBcc() != null) {
				for (String recipient : envelop.getBcc()) {
					if (StringUtils.isNotBlank(recipient)) {
						email.addBcc(recipient, MimeUtility.encodeText(recipient, "UTF-8", "B"));
						logger.debug("Bcc: " + recipient + " is added");
					}
				}
			}

			// Subject
			email.setSubject(MimeUtility.encodeText(envelop.getSubject(), "UTF-8", "B"));
			logger.debug("Subject: " + envelop.getSubject());
		}
		catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
