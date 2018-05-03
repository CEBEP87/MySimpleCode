package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Стартовый класс приложения
 */

@SpringBootApplication
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice")
@EnableJpaRepositories("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MonitoringApplication extends SpringBootServletInitializer {
	/**
	 * Стартовый метод приложения
	 *
	 * @param args список атрибутов
	 */
	public static void main(String[] args) {
		SpringApplication.run(new Class<?>[]{MonitoringApplication.class}, args);
	}
}
