package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service;

import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.Section;

/**
 * Service for work with sections
 *
 * @author samsonov
 * @since 22.09.2017
 */
@Service
public interface SectionService {

    /**
     * Get one sections by id
     *
     * @param idPeriod  -period's identificator
     * @param idSection -section's identificator
     * @return - section
     * @throws Exception
     */
    Section findOne(String idPeriod, String idSection) throws Exception;

    /**
     * Delete one section by id
     *
     * @param idPeriod  -period's identificator
     * @param idSection -section's identificator
     * @throws Exception
     */
    void delete(String idPeriod, String idSection) throws Exception;

    /**
     * Add New section
     *
     * @param section  -section in period
     * @param periodId - Period id
     * @return -new resource
     * @throws Exception
     */
    Section add(Section section, String periodId) throws Exception;

    /**
     * Update New section
     *
     * @param section  -section in period
     * @param periodId - Period id
     * @return - section
     * @throws Exception
     */
    Section update(Section section, String periodId) throws Exception;
}
