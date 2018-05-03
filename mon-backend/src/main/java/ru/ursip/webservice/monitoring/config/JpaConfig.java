package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Configuration BD connection
 *
 * @author aneichikes
 * @since 03.02.2017
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring")
@EnableJpaRepositories("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository")
@ConfigurationProperties(prefix = "spring.datasource.hikari")
public class JpaConfig extends HikariConfig {
	/**
	 * Initialisation data's source
	 *
	 * @return dataSource
	 */
	@Bean
	public DataSource dataSource() throws SQLException {
		return new HikariDataSource(this);
	}
}
