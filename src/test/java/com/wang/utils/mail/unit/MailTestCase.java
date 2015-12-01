package com.wang.utils.mail.unit;

import java.util.Arrays;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wang.utils.crypto.shop.ShopDesUtil;
import com.wang.utils.mail.Compose;
import com.wang.utils.mail.MailBroker;

@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class MailTestCase {
	@Autowired MailBroker mailBroker;

	@Test
	public void testSendHtmlMail() throws MessagingException {
		Compose compose = new Compose();
		compose.setFrom("tester@htche.com");
		compose.setTo(Arrays.asList("wangji@htche.net"));
		compose.setSubject("测试");
		String content = "<table><tr><td><img src=\"http://sms.semi.htche.com/images/logo/logo2.png\" alt=\"logo\" width=\"340\"></td></tr>"
				+ "</table>";
		compose.setContent(content);
		mailBroker.send(compose, 1);
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
		mailBroker.send(compose, 0);
		return;
	}

	@Test
	public void orderMail() throws MessagingException {
		EmailHtml bean = new EmailHtml();
		bean.setAddress("");
		bean.setProductName("2015款路虎揽胜HSE版汽油版欧洲版 黑色/黑色");
		bean.setOtherAddress("成都市");
		bean.setCardNo("431227197801012366");
		bean.setContactor("王小二");
		bean.setGuarantDesc("海淘无忧A套餐");
		bean.setGuarantPrice("8,700");
		bean.setLastPrice(" 1,704,000");
		bean.setOrderDate("2015-05-30");
		bean.setOrderNo("1233214646");
		bean.setPayType("全款购车");
		bean.setOrderCode(ShopDesUtil.encryptOrderInfo(1L, "201505302301001101", 1L));
		String templatePath = this.getClass().getResource("/").getPath();
		try {
			String html = FreemarkerUtil.getEmailTemplate(templatePath, "orderEmail.html", bean);
			Compose compose = new Compose();
			compose.setFrom("tester@htche.com");
			compose.setTo(Arrays.asList("liuhongbin@htche.net"));
			compose.setSubject("订单邮件测试");
			compose.setContent(html);
			mailBroker.send(compose, 1);
			return;

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
