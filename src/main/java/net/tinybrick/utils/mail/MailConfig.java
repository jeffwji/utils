package net.tinybrick.utils.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailConfig {
	Authenticator authenticator;
	Properties props = new Properties();
	String defaulterSender;

	public MailConfig(String mail_stmp_host, int mail_stmp_port, String defaultSender, boolean mail_stmp_auth,
			final String username, final String password) {
		authenticator = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		};

		props.put("mail.smtp.host", mail_stmp_host);
		props.put("mail.smtp.port", mail_stmp_port);
		props.put("mail.smtp.auth", mail_stmp_auth ? "true" : "false");
		props.put("mail.smtp.user", username);
		props.put("mail.smtp.pass", password);
		defaulterSender = (null == defaultSender ? ((null == username ? "nobody" : username) + "@" + mail_stmp_host)
				: defaultSender);
	}

	public Authenticator getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public String getDefaultSender() {
		return defaulterSender;
	}

}
