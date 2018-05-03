package ru.XXXXXXXXX.renovation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.XXXXXXXXX.data.jpa.entity.evt.DepartmentEventShort;
import ru.XXXXXXXXX.data.jpa.entity.passport.DistrictPassportShort;
import ru.XXXXXXXXX.renovation.domain.entity.RenovationShort;

public interface RenovationShortRepository extends JpaRepository<RenovationShort, Integer>, JpaSpecificationExecutor<RenovationShort> {

}
