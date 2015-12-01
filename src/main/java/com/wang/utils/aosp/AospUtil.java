package com.wang.utils.aosp;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ancun.aosp.client.AospClient;
import com.ancun.aosp.dto.AospRequest;
import com.ancun.aosp.dto.AospResponse;
import com.wang.utils.crypto.MD5;

/**
 * <br>
 * <b>安存保全工具类</b>
 * 
 * @author chenzhou
 * @version 1.0
 */
public class AospUtil {

    private static final Logger LOGGER = org.apache.log4j.LogManager.getLogger(MD5.class);

    /**
     * 安存服务地址
     */
    private String apiAddress;
    /**
     * 接入者KEY
     */
    private String partnerKey;
    /**
     * 接入者密钥
     */
    private String secret;

    public String getApiAddress() {
        return apiAddress;
    }

    public void setApiAddress(String apiAddress) {
        this.apiAddress = apiAddress;
    }

    public String getPartnerKey() {
        return partnerKey;
    }

    public void setPartnerKey(String partnerKey) {
        this.partnerKey = partnerKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * 保存信息
     * 
     * @param itemKey
     *            保全事项KEY
     * @param dataMap
     *            需要保全的数据
     * @param fileMap
     *            需要保全的文件
     * @return
     */
    public Map<String, Object> preserveData(String itemKey, Map<String, Object> dataMap, Map<String, String> fileMap)
            throws Exception {
        AospClient aospClient = null;
        //返回结果
        Map<String, Object> context = new HashMap<String, Object>();
        try {
            LOGGER.debug("Aosp params: itemKey | " + itemKey + ", apiAddress | " + apiAddress + ", partnerKey | "
                    + partnerKey + ", secret | " + secret);

            //根据保全服务器的地址、接入者KEY、接入者密钥实例化AospClient
            aospClient = new AospClient(apiAddress, partnerKey, secret);
            //创建AospRequest
            AospRequest aospRequest = AospRequest.create();
            //设置本次保全的事项Key
            aospRequest.setItemKey(itemKey);
            //设置需要传输的数据
            aospRequest.setData(dataMap);
            //增加保全的PDF文件
            for (String key : fileMap.keySet()) {
                aospRequest.addFile(fileMap.get(key), key);
            }
            //进行保全
            AospResponse aospResponse = aospClient.save(aospRequest);
            //获取本次保全操作的处理结果编码
            context.put("code", aospResponse.getCode());
            LOGGER.info("Aosp result code: itemKey | " + aospResponse.getCode());
            context.put("msg", aospResponse.getMsg());
            //获取保全操作返回结果
            //recordNo:保全之后产生的保全号
            //serialNo:流水号，当请求属性中没有流水号的，则返回流水号
            if (aospResponse.getData() != null) {
                context.putAll(aospResponse.getData());
            }
            return context;
        } catch (Exception e) {
            throw e;
        } finally {
            if (aospClient != null) {
                aospClient.close();
            }
        }
    }
}
