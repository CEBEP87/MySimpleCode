package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Стартовый класс приложения
 */

@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice")
@EnableMongoRepositories("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.repository")
@EnableJpaRepositories("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.sqlrepository")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CalculationApplication extends SpringBootServletInitializer {

	/**
	 * Стартовый метод приложения
	 *
	 * @param args список атрибутов
	 */
	public static void main(String[] args) {
		SpringApplication.run(new Class<?>[]{CalculationApplication.class}, args);
	}
}
