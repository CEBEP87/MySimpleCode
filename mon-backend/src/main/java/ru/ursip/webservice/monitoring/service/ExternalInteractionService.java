package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.service;

import java.util.HashMap;
import java.util.List;

/**
 * Сервис для работы с мерами
 *
 * @author samsonov
 * @since 09.02.2017
 */
public interface ExternalInteractionService {


    /**
     * Request organization list
     *
     * @return organization list
     */
    List<HashMap> getOrganization();

    /**
     * Request user list
     *
     * @return user list
     */

    List<HashMap> getUsers();
}
