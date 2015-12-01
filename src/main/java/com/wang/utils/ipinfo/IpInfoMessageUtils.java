package com.wang.utils.ipinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wang.utils.http.IRestCrudClient;
import com.wang.utils.http.RestClient;
import com.wang.utils.json.JsonMapper;

public class IpInfoMessageUtils {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(IpInfoMessageUtils.class);
	private static List<Map> list = new ArrayList<Map>();
   /**
    * 获取IP地址
    * @param request
    * @return
    */
	public static String getRemortIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		LOGGER.info("获取IP方式：x-forwarded-for------>IP:"+ip);
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			LOGGER.info("获取IP方式：Proxy-Client-IP------>IP:"+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			LOGGER.info("获取IP方式：WL-Proxy-Client-IP------>IP:"+ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			LOGGER.info("获取IP方式：getRemoteAddr()------>IP:"+ip);
		}
		if(ip!=null&&ip.length() != 0&&!"unknown".equalsIgnoreCase(ip)&&ip.indexOf(",")!=-1){
			ip = ip.split(",")[0];
		}
		return ip;
	}
	
	public static Map getIPInfoMessage(String url,String ip) {
		for(Map info : list){//先遍历缓存中是否存在，存在直接返回
			Map data = (Map)info.get("data");
			if(ip.equals(data.get("ip"))){
				return info;
			}
		}
		LOGGER.info("通过IP取得对应省市信息------>IP:"+ip);
		StringBuffer sb = new StringBuffer();
		Map<Object,Object> info = new HashMap<Object,Object>();
		int status=0;
		try {
			status= restClientGet(url+ip,sb);
			if(status!=200){
				for(int i=0;i<5;i++){//如果访问失败，再访问5次，如果5次仍失败，返回空
					status= restClientGet(url+ip,sb);
					if(status==200){
						info = JsonMapper.buildNonEmptyMapper().fromJson(sb.toString(), Map.class);
						if(list.size()==500){//最多缓存500条数据
							list.remove(0);
						}
						if("0".equals(info.get("code").toString())) list.add(info);
						break;
					}
				}
			}else{
				info = JsonMapper.buildNonEmptyMapper().fromJson(sb.toString(), Map.class);
				if(list.size()==500){//最多缓存500条数据
					list.remove(0);
				}
				if("0".equals(info.get("code").toString())) list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("TransportationService.queryObject failed", e);
		}
		return info;
	}
	
	/**
	 * 通过URL发送get请求
	 * @param uri
	 * @param sb
	 * @return
	 * @throws Exception
	 */
	public static Integer restClientGet(String uri, StringBuffer sb) throws Exception {
		IRestCrudClient restClient = new RestClient("", "");
		int status = restClient.get(sb, uri);
		if (status != 200) {
			LOGGER.error("errorCode:" + status + ";errorMessage:"
					+ sb.toString());
		}
		return status;
	}
}
