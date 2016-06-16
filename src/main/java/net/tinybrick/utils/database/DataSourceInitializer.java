package net.tinybrick.utils.database;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

public class DataSourceInitializer extends org.springframework.jdbc.datasource.init.DataSourceInitializer {
	Logger logger = Logger.getLogger(this.getClass());

	JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private String flag_table_name;

	public String getFlagTableName() {
		return flag_table_name;
	}

	public void setFlagTableName(String table_name) {
		this.flag_table_name = table_name;
	}

	@Override
	public void afterPropertiesSet() {
		try {
			logger.debug("SELECT count(*) FROM " + getFlagTableName());
			jdbcTemplate.execute("SELECT count(*) FROM " + getFlagTableName());
		}
		catch (Exception e) {
			if ((e.getMessage().contains(getFlagTableName()))
					&& (e.getMessage().contains("doesn't exist") || e.getMessage().contains("does not exist"))) {
				super.afterPropertiesSet();
			}
			else {
				logger.error(e.getMessage(), e);
			}
		}
	}
}
