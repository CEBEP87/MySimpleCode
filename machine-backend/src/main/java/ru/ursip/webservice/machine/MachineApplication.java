package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Стартовый класс приложения
 */

@SpringBootApplication
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice")
@EnableMongoRepositories("ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.repository")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MachineApplication extends SpringBootServletInitializer {

	/**
	 * Стартовый метод приложения
	 *
	 * @param args список атрибутов
	 */
	public static void main(String[] args) {
		SpringApplication.run(new Class<?>[]{MachineApplication.class}, args);
	}
}
