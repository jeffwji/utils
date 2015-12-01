package com.wang.utils.i18n;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * 配置文件占位符
 * 
 * @author 王继
 */
public class I18N {
	private static ResourceBundle resourceBundle;

	public static void setResourceBundle(ResourceBundle resourceBundle) {
		I18N.resourceBundle = resourceBundle;
	}

	/**
	 * 获取国际化信息
	 * 
	 * @param key
	 *            国际化配置文件KEY
	 * @return
	 */
	public static String value(String key) {
		if (null != resourceBundle) {
			return resourceBundle.getString(key);
		}
		else {
			return null;
		}
	}

	/**
	 * 获取替换占位符后的国际化配置信息
	 * 
	 * @param key
	 *            国际化配置文件KEY
	 * @param args
	 *            占位符替换参数
	 * @return
	 */
	public static String value(String key, Object[] args) {
		if (null != resourceBundle) {
			return MessageFormat.format(resourceBundle.getString(key), args);
		}
		else {
			return null;
		}
	}

	public static class I18NConfig {
		public final static String SYSTEM_LANGUAGE_PREFIX = "language";
		public final static String DEFAULT_SYSTEM_LANGUAGE_PREFIX = "resource";
	}
}
