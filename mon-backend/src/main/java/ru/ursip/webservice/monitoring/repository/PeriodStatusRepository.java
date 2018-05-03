package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.PeriodStatus;

/**
 *  Репозиторий для работы со статусами периода
 *
 * @author samsonov
 * @since 01.03.2017
 */
public interface PeriodStatusRepository extends JpaRepository<PeriodStatus, Integer> {
}
