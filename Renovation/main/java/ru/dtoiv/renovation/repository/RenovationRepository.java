package ru.XXXXXXXXX.renovation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.XXXXXXXXX.renovation.domain.entity.Renovation;

public interface RenovationRepository extends JpaRepository<Renovation, Integer>, JpaSpecificationExecutor<Renovation> {

    @Query(value = "select nextval('renovation.renovation_unique_id_seq')", nativeQuery = true)
    Long getNextSeriesId();
}
