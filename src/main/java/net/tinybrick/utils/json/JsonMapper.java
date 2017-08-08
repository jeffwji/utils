/**
 * Copyright (c) 2005-2011 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 
 * $Id: JsonMapper.java 1661 2011-11-17 20:35:10Z  $
 */
package net.tinybrick.utils.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.*;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 简单封装Jackson，实现JSON String<->Java Object的Mapper.
 * 封装不同的输出风格, 使用不同的builder函数创建实例.
 */
public class JsonMapper {

    private static Logger logger = LogManager.getLogger(JsonMapper.class);

    private ObjectMapper mapper;

    public JsonMapper(Include inclusion) {
        mapper = new ObjectMapper();
        //设置输出时包含属性的风格
        mapper.setSerializationInclusion(inclusion);
        //设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //禁止使用int代表Enum的order()來反序列化Enum,非常危險
        mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
    }

    /**
     * 创建输出全部属性到Json字符串的Mapper.
     */
    public static JsonMapper buildNormalMapper() {
        return new JsonMapper(Include.ALWAYS);
    }

    /**
     * 创建只输出非空属性到Json字符串的Mapper.
     */
    public static JsonMapper buildNonNullMapper() {
        return new JsonMapper(Include.NON_NULL);
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper.
     */
    public static JsonMapper buildNonDefaultMapper() {
        return new JsonMapper(Include.NON_DEFAULT);
    }

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper.
     */
    public static JsonMapper buildNonEmptyMapper() {
        return new JsonMapper(Include.NON_EMPTY);
    }

    /**
     * 如果对象为Null, 返回"null".
     * 如果集合为空集合, 返回"[]".
     */
    public String toJsonString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.warn("write to json string error:" + object, e);
            return null;
        }
    }

    /**
     * 如果JSON字符串为Null或"null"字符串, 返回Null.
     * 如果JSON字符串为"[]", 返回空集合.
     * 如需读取集合如List/Map, 且不是List<String>这种简单类型时,先使用函數constructParametricType构造类型.
     * 
     * @see #constructParametricType(Class, Class...)
     */
    public <T> T fromJsonString(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 如果JSON字符串为Null或"null"字符串, 返回Null.
     * 如果JSON字符串为"[]", 返回空集合.
     * 如需读取集合如List/Map, 且不是List<String>这种简单类型时,先使用函數constructParametricType构造类型.
     * 
     * @see #constructParametricType(Class, Class...)
     */
    @SuppressWarnings("unchecked")
    public <T> T fromJsonString(String jsonString, JavaType javaType) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            return (T) mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 構造泛型的Type如List<MyBean>, 则调用constructParametricType(ArrayList.class,MyBean.class)
     * Map<String,MyBean>则调用(HashMap.class,String.class, MyBean.class)
     */
    public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
        return mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    /**
     * 當JSON裡只含有Bean的部分屬性時，更新一個已存在Bean，只覆蓋該部分的屬性.
     */
    @SuppressWarnings("unchecked")
    public <T> T update(T object, String jsonString) {
        try {
            return (T) mapper.readerForUpdating(object).readValue(jsonString);
        } catch (JsonProcessingException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        } catch (IOException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
        return null;
    }

    /**
     * 輸出JSONP格式數據.
     */
    public String toJsonString(String functionName, Object object) {
        return toJsonString(new JSONPObject(functionName, object));
    }

    /**
     * 設定是否使用Enum的toString函數來讀寫Enum,
     * 為False時時使用Enum的name()函數來讀寫Enum, 默認為False.
     * 注意本函數一定要在Mapper創建後, 所有的讀寫動作之前調用.
     */
    public void setEnumUseToString(boolean value) {
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, value);
        mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, value);
    }

    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * 从json中读取tagPath处的值 tagPath用 :分隔
     * 
     * @param json
     * @param tagPath
     * @return
     * @throws Exception
     */
    public List<String> readValueFromJson(String json, String tagPath) throws Exception {
        // 返回值   
        List<String> value = new ArrayList<String>();
        if (isEmpty(json) || (isEmpty(tagPath))) {
            return value;
        }

        String[] path = tagPath.split(":");
        JsonNode node = mapper.readTree(json);
        getJsonValue(node, path, value, 1);
        return value;
    }

    public void getJsonValue(JsonNode node, String[] path, List<String> values, int nextIndex) {
        if (isEmpty(node)) {
            return;
        }
        // 是路径的最后就直接取值   
        if (nextIndex == path.length) {
            if (node.isArray()) {
                for (int i = 0; i < node.size(); i++) {
                    JsonNode child = node.get(i).get(path[nextIndex - 1]);
                    if (isEmpty(child)) {
                        continue;
                    }
                    values.add(child.toString());
                }
            } else {
                JsonNode child = node.get(path[nextIndex - 1]);
                if (!isEmpty(child)) {
                    values.add(child.toString());
                }
            }
            return;
        }
        // 判断是Node下是集合还是一个节点   
        node = node.get(path[nextIndex - 1]);
        if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                getJsonValue(node.get(i), path, values, nextIndex + 1);
            }
        } else {
            getJsonValue(node, path, values, nextIndex + 1);
        }
    }

    /**
     * 判断对象是否为空
     * 
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        boolean result = true;
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            result = (obj.toString().trim().length() == 0) || obj.toString().trim().equals("null");
        } else if (obj instanceof Collection) {
            result = ((Collection) obj).size() == 0;
        } else {
            result = ((obj == null) || (obj.toString().trim().length() < 1)) ? true : false;
        }
        return result;
    }

    /**
     * 获取泛型的Collection Type
     * 
     * @param collectionClass
     *            泛型的Collection
     * @param elementClasses
     *            元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
