package net.tinybrick.utils.mail.unit;

import java.util.Arrays;

import javax.mail.MessagingException;

import net.tinybrick.utils.mail.Compose;
import net.tinybrick.utils.mail.MailBroker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class MailTestCase {
	@Autowired
	MailBroker mailBroker;

	@Test
	public void testSendHtmlMail() throws MessagingException {
		Compose compose = new Compose();
		compose.setFrom("tester@htche.com");
		compose.setTo(Arrays.asList("wangji@htche.net"));
		compose.setSubject("测试");
		String content = "<table><tr><td><img src=\"http://sms.semi.htche.com/images/logo/logo2.png\" alt=\"logo\" width=\"340\"></td></tr>"
				+ "</table>";
		compose.setContent(content);
		//mailBroker.send(compose, 1);
		return;
	}

	@Test
	public void testSendMail() throws MessagingException {
		Compose compose = new Compose();
		compose.setFrom("tester@htche.com");
		compose.setTo(Arrays.asList("wangji@htche.net"));
		compose.setSubject("测试");
		String content = "测试纯文本各式邮件";
		compose.setContent(content);
		//mailBroker.send(compose, 0);
		return;
	}
}
