package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.CategoryDrivers;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.Period;

/**
 * Category-drivers Repository
 *
 * @author samsonov
 * @since 12.01.2018
 */
public interface CategoryDriversRepository extends MongoRepository<CategoryDrivers, String> {

}
