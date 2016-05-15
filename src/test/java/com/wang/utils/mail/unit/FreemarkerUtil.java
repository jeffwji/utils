package com.wang.utils.mail.unit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;


import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class FreemarkerUtil {


	/**
	 * templatePath模板文件存放路径
	 * templateName 模板文件名称
	 * filename 生成的文件名称
	 */
	public static void analysisTemplate(String templatePath, String templateName, String fileName, Object root) throws Exception {
		FileOutputStream fos = null;
		Writer out = null;
		try {
			Configuration config = new Configuration();
			// 设置要解析的模板所在的目录，并加载模板文件
			config.setDirectoryForTemplateLoading(new File(templatePath));
			// 设置包装器，并将对象包装为数据模型
			config.setObjectWrapper(new DefaultObjectWrapper());
			// 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
			// 否则会出现乱码
			Template template = config.getTemplate(templateName, "UTF-8");
			// 合并数据模型与模板
			String parentPath = fileName.substring(0, fileName.lastIndexOf(File.separatorChar)) + File.separatorChar;
			File parentPathDir = new File(parentPath);
			if (!parentPathDir.exists())
				parentPathDir.mkdirs();
			fos = new FileOutputStream(fileName);
			out = new OutputStreamWriter(fos, "UTF-8");
			template.process(root, out);
			out.flush();
		} finally {
			if (fos != null)
				fos.close();
			if (out != null)
				out.close();
		}
	}
	
	/**
	 * 获取订单email html 文件
	 * @param templatePath
	 * @param templateName
	 * @param emailHtml
	 * @return
	 * @throws Exception
	 */
	public static String getEmailTemplate(String templatePath, String templateName,EmailHtml emailHtml) throws Exception {
		FileOutputStream fos = null;
		StringWriter writer = new StringWriter();
		try {
			Configuration config = new Configuration();
			// 设置要解析的模板所在的目录，并加载模板文件
			config.setDirectoryForTemplateLoading(new File(templatePath));
			// 设置包装器，并将对象包装为数据模型
			config.setObjectWrapper(new DefaultObjectWrapper());
			// 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
			// 否则会出现乱码
			Template template = config.getTemplate(templateName, "UTF-8");
			template.process(emailHtml, writer);
			return writer.toString();
		} finally {
			if (fos != null) fos.close();
			if (writer != null) writer.close();
		}
	}

	public static Template findTemplate(String templatePath, String templateName) {
		Configuration config = new Configuration();
		// 设置要解析的模板所在的目录，并加载模板文件
		try {
			config.setDirectoryForTemplateLoading(new File(templatePath));
			// 设置包装器，并将对象包装为数据模型
			config.setObjectWrapper(new DefaultObjectWrapper());
			// 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
			// 否则会出现乱码
			Template template = config.getTemplate(templateName, "UTF-8");
			return template;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
