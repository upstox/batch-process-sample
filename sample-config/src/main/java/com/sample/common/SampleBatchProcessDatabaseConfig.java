package com.sample.common;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import com.batch.process.config.BatchDataSourceConfigurator;

@Configuration
public class SampleBatchProcessDatabaseConfig implements BatchDataSourceConfigurator {

	@Value("${batch-run-data.datasource.driver-class-name}")
	private String driverName;

	@Value("${batch-run-data.datasource.url}")
	private String url;

	@Value("${batch-run-data.datasource.username}")
	private String username;

	@Value("${batch-run-data.datasource.password}")
	private String password;

	@Primary
	@Bean
	@Override
	public DataSource batchDataSource() {
		return DataSourceBuilder.create().driverClassName(driverName).url(url).username(username).password(password)
				.build();
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(batchDataSource());
	}

}
