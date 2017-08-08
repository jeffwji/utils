package net.tinybrick.utils.date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	private final static Logger logger = LogManager.getLogger(DateUtil.class);
	
	private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String toLocaleString(Date date){
		if(date == null) return "";
		return format.format(date);
	}
	
	public static void main(String args[]){
		logger.info(DateUtil.toLocaleString(new Date()));
	}

}
