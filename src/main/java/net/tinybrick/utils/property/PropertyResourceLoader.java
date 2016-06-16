package net.tinybrick.utils.property;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.tinybrick.utils.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyResourceLoader {
	Logger logger = LoggerFactory.getLogger(getClass());

	public PropertyResourceLoader() {
		this("zh_CN");
	}

	public PropertyResourceLoader(String system_language) {
		PropertyResourceBundle prb = null;
		ResourceBundle rb = null;
		Locale locale = new Locale(system_language);
		try {
			rb = ResourceBundle.getBundle(I18N.I18NConfig.SYSTEM_LANGUAGE_PREFIX, locale);
		}
		catch (MissingResourceException e) {
			logger.warn("Language resource file can't be found.");
		}

		// 载入缺省国际化文件
		try {
			InputStream is = this
					.getClass()
					.getClassLoader()
					.getResourceAsStream(
							I18N.I18NConfig.DEFAULT_SYSTEM_LANGUAGE_PREFIX + "_" + system_language + ".properties");//new BufferedInputStream(new FileInputStream(new File(Constants.Config.DEFAULT_SYSTEM_LANGUAGE_PREFIX + "_" + system_language +".properties")));
			prb = new PropertyResourceBundle(is, rb);
		}
		catch (FileNotFoundException e1) {
			logger.warn("Default language resource file can't be found.");
		}
		catch (IOException e1) {
			logger.warn("Default language resource file can't be found.");
		}

		I18N.setResourceBundle(prb);
	}

	static class PropertyResourceBundle extends java.util.PropertyResourceBundle {
		public PropertyResourceBundle(InputStream stream, ResourceBundle parent) throws IOException {
			super(stream);
			setParent(parent);
		}
	}
}
