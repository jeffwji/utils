package com.wang.utils.crypto.shop;


import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.wang.utils.crypto.DES3;

/**
 * 订单ID加解密
 * 加解密公式
 * 订单ID－订单号-账号ID 进行加密
 * 
 * @author Administrator
 */
public class ShopDesUtil {
	
	//*D58DEE570390DCEF483B2B142146B6A3D83DD0EA
	private static final String PASS_KEY = "*D58DEE570390"; 
	
	private final static Logger LOGGER = Logger.getLogger(ShopDesUtil.class);
	
	
	public static final String TOKEN_VALUE = "*C55A912EA156F7D4A8E460F381DABE04AE17506B";
	
	public static final String TOKEN_KEY_LOGIN = "smsLoginToken"; //登录
	public static final String TOKEN_KEY_CHANNEL= "smsChannelToken"; //定制频道
	
	
	/**
	 * 加密token
	 * 
	 * @param token
	 * @return
	 */
	public static String encryptToken(String token) {
		try {
			Assert.notNull(token, "字符串不能为空");
			DES3 des = new DES3(PASS_KEY, null, DES3.KEY_SPEC_DES);
			return des.encrypt(token);
		}
		catch (Exception e) {
			LOGGER.error("加密字符串失败", e);
		}
		return null;
	}
	
	/**
	 * 解密token
	 * 
	 * @param token
	 * @return
	 */
	public static String dencryptToken(String etoken) {
		try {
			Assert.notNull(etoken, "字符串不能为空");
			LOGGER.info("token------------->"+etoken);
			DES3 des = new DES3(PASS_KEY, null, DES3.KEY_SPEC_DES);
			String vcode = des.decrypt(etoken);
			return vcode;
		}
		catch (Exception e) {
			LOGGER.error("解密字符串失败", e);
		}
		return null;
	}
	
	
	/**
	 * 加密订单号
	 * 加密规则：  订单ID-订单号-账号ID 进行2次 des加密
	 * 
	 * 
	 * @throws Exception
	 */
	public static String encryptOrderInfo(Long orderId, String orderNo, Long accountId){
		try {
			Assert.notNull(orderId, "订单ID不能为空");
			Assert.notNull(orderNo, "订单号不能为空");
			Assert.notNull(accountId, "账户ID不能为空");
			DES3 des = new DES3(PASS_KEY, null, DES3.KEY_SPEC_DES);
			StringBuilder sb = new StringBuilder();
			sb.append(orderId).append("-").append(orderNo).append("-").append(accountId);
			String vcode = des.encrypt(sb.toString());
			vcode = des.encrypt(vcode);
			vcode = vcode.replaceAll("/", "!@");
			vcode = vcode.replaceAll("\\+", "!!");
			vcode = vcode.replaceAll("\n", ""); 
			return vcode;
		}
		catch (Exception e) {
			LOGGER.error("加密订单信息失败",e);
		}
		return null;
	}
	
	/**
	 * 加密字符串
	 * 加密规则：  字符串 进行2次 des加密
	 * 
	 * 
	 * @throws Exception
	 */
	public static String encryptStr(String str){
		try {
			Assert.notNull(str, "字符串不能为空");
			DES3 des = new DES3(PASS_KEY, null, DES3.KEY_SPEC_DES);
			String vcode = des.encrypt(str);
			vcode = des.encrypt(vcode);
			vcode = vcode.replaceAll("/", "!@");
			vcode = vcode.replaceAll("\\+", "!!");
			vcode = vcode.replaceAll("\n", ""); 
			return vcode;
		}
		catch (Exception e) {
			LOGGER.error("加密失败",e);
		}
		return null;
	}
	
	/**
	 * 解密
	 * 解密规则：  字符串 进行2次 des解密
	 * 
	 * 
	 * @throws Exception
	 */
	public static String dencryptStr(String desStr){
		try {
			Assert.notNull(desStr, "字符串不能为空");
			desStr = desStr.replaceAll("!@", "/");
			desStr = desStr.replaceAll("!!", "\\+");
			LOGGER.info("desStr------------->"+desStr);
			DES3 des = new DES3(PASS_KEY, null, DES3.KEY_SPEC_DES);
			String orderInfo = des.decrypt(desStr);
			orderInfo = des.decrypt(orderInfo);
			return orderInfo;
		}
		catch (Exception e) {
			LOGGER.error("解密订单信息失败",e);
		}
		return null;
	}
	
	/**
	 * 加密订单号
	 * 加密规则：  订单ID-订单号-账号ID 进行2次 des加密
	 * 
	 * 
	 * @throws Exception
	 */
	public static String dencryptOrderInfo(String desCode,ShopDesReturn type){
		try {
			Assert.notNull(desCode, "解密code为空");
			desCode = desCode.replaceAll("!@", "/");
			desCode = desCode.replaceAll("!!", "\\+");
			LOGGER.info("desCode------------->"+desCode);
			DES3 des = new DES3(PASS_KEY, null, DES3.KEY_SPEC_DES);
			String orderInfo = des.decrypt(desCode);
			orderInfo = des.decrypt(orderInfo);
			String[] infos = orderInfo.split("-");
			if(type == ShopDesReturn.ORDERID) return infos[0];
			else if(type == ShopDesReturn.ORDERNO) return infos[1];
			else if(type == ShopDesReturn.ACCOUNTID) return infos[2];
		}
		catch (Exception e) {
			LOGGER.error("解密订单信息失败",e);
		}
		return null;
	}
	
	public enum ShopDesReturn{
		ORDERID("orderId"),ORDERNO("orderNo"),ACCOUNTID("accountId");
		private String value;
		ShopDesReturn(String value){
			this.value = value;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	
	/**
	 * 账号ID加密
	 * 加密规则：  账号ID-*D58DEE570390DCEF483B2B142146B6A3D83DD0EA 进行2次 des加密
	 * 
	 * @throws Exception
	 */
	public static String encryptAccountId(Long accountId){
		try {
			Assert.notNull(accountId, "账户ID不能为空");
			DES3 des = new DES3(PASS_KEY, null, DES3.KEY_SPEC_DES);
			StringBuilder sb = new StringBuilder();
			sb.append(accountId).append("-").append(PASS_KEY);
			String vcode = des.encrypt(sb.toString());
			vcode = des.encrypt(vcode);
			vcode = vcode.replaceAll("/", "!@");
			vcode = vcode.replaceAll("\\+", "!!");
			vcode = vcode.replaceAll("\n", ""); 
			return vcode;
		}
		catch (Exception e) {
			LOGGER.error("加密订单信息失败",e);
		}
		return null;
	}
	
	/**
	 * 账号ID解密
	 * 加密规则：  账号ID-*D58DEE570390DCEF483B2B142146B6A3D83DD0EA 进行2次 des加密
	 * 
	 * @throws Exception
	 */
	public static String dencryptAccountId(String desCode){
		try {
			Assert.notNull(desCode, "解密code为空");
			desCode = desCode.replaceAll("!@", "/");
			desCode = desCode.replaceAll("!!", "\\+");
			LOGGER.info("desCode------------->"+desCode);
			DES3 des = new DES3(PASS_KEY, null, DES3.KEY_SPEC_DES);
			String orderInfo = des.decrypt(desCode);
			orderInfo = des.decrypt(orderInfo);
			String[] infos = orderInfo.split("-");
			return infos[0];
		}
		catch (Exception e) {
			LOGGER.error("解密账号ID信息失败",e);
		}
		return null;
	}
	
	
	public static void main(String args[]){
		/*String vcode = encryptOrderInfo(93L,"201506012002001119",2L);
		LOGGER.info("获取订单ID加密值:"+vcode);
		String orderId = dencryptOrderInfo(vcode,ShopDesReturn.ORDERID);
		String orderNo = dencryptOrderInfo(vcode,ShopDesReturn.ORDERNO);
		String accountId = dencryptOrderInfo(vcode,ShopDesReturn.ACCOUNTID);
		LOGGER.info("解密获取orderId:"+orderId+",orderNo:"+orderNo+"accountId:"+accountId);*/
		String s = encryptAccountId(1L);
		System.out.println("s1------->"+s);
		s = s.replace("\n", "");
		System.out.println(s);
		String a = "GFqDXb9MbAPib!@!@1!@pUzEu!@STUsHO782IhgktLvoe!!tya3XjjJsf4iiZkorS3E!@m9KTI5BcfPXL0Wa0wsPnnO!@lJhjGGHIrE";
		String aId = dencryptAccountId(a);
		System.out.println(aId);
		/*
		vcode = "6NP5!@c52emnoTtLAVDR+Okpv3QWMev3NkimaWTJ4F0f5SYYxhhyKxA==";
		String orderId = dencryptOrderInfo(vcode,ShopDesReturn.ORDERID);
		String orderNo = dencryptOrderInfo(vcode,ShopDesReturn.ORDERNO);
		String accountId = dencryptOrderInfo(vcode,ShopDesReturn.ACCOUNTID);
		LOGGER.info("解密获取orderId:"+orderId+",orderNo:"+orderNo+"accountId:"+accountId);*/
		
		/*String s = "6NP5!@c52emnoTtLAVDR+Okpv3QWMev3NkimaWTJ4F0f5SYYxhhyKxA==";
		try {
			LOGGER.info(URLEncoder.encode(s, "utf-8"));
			String orderId = dencryptOrderInfo(s,ShopDesReturn.ORDERID);
			String orderNo = dencryptOrderInfo(s,ShopDesReturn.ORDERNO);
			String accountId = dencryptOrderInfo(s,ShopDesReturn.ACCOUNTID);
			LOGGER.info("解密获取orderId:"+orderId+",orderNo:"+orderNo+"accountId:"+accountId);
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
