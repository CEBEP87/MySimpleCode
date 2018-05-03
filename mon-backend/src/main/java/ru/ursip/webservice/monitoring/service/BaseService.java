package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service;

import java.io.Serializable;
import java.util.List;


/**
 * Сервис с общими методами работы с сущностями
 *
 * @param <T> класс над которым будут произодиться действия
 * @author samsonov
 * @since 13.12.2016
 */
public interface BaseService<T extends Serializable> {

	/**
	 * Получить список сущностей
	 *
	 * @return список сущностей
	 */
	List<T> get() throws Exception;


	/**
	 * Получить сущность по идентификатору (коду)
	 *
	 * @param id идентификатор (код) сущности
	 * @return модель сущности
	 */
	T get(Integer id) throws Exception;

	/**
	 * Создать новую сущность
	 *
	 * @param entity модель сущности
	 * @return модель сущности
	 */
	T create(T entity) throws Exception;

	/**
	 * Редактировать сущность
	 *
	 * @param entity модель сущности
	 * @return модель сущности
	 */
	T update(T entity) throws Exception;

	/**
	 * Удалить сущность
	 *
	 * @param entity модель сущности
	 */
	void remove(T entity) throws Exception;

}
