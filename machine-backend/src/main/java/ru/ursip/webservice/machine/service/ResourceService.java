package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.PutResource;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.Resource;

/**
 * Service for work with resource
 *
 * @author samsonov
 * @since 22.09.2017
 */
@Service
public interface ResourceService {
    /**
     * Get one resource by id
     *
     * @param idPeriod   -period's identificator
     * @param idResource -resource's identificator
     * @return - period
     * @throws Exception
     */
    Resource findOne(String idPeriod, String idResource) throws Exception;

    /**
     * Delete one resource by id
     *
     * @param idPeriod   -period's identificator
     * @param idResource -resource's identificator
     * @throws Exception
     */
    void delete(String idPeriod, String idResource) throws Exception;

    /**
     * Add New resource
     *
     * @param resource -resource in period
     * @param periodId - Period id
     * @return -new resource
     * @throws Exception
     */
    Resource add(Resource resource, String periodId) throws Exception;

    /**
     * Update New resource
     *
     * @param resource -resource in period
     * @param periodId - Period id
     * @return - resource
     * @throws Exception
     */
    Resource update(Resource resource, String periodId) throws Exception;

    /**
     * check unique TSN
     *
     * @param resource -resource
     * @param periodId - Period id
     * @return - true\false
     * @throws Exception
     */

    Boolean isUniqueTSN(Resource resource, String periodId) throws Exception;

    /**
     * check is Dublicate for deleting
     *
     * @param resource -resource
     * @param periodId - Period id
     * @return - true\false
     * @throws Exception
     */
    Boolean isDublicate(Resource resource, String periodId) throws Exception;

    /**
     * check Resource and Post
     *
     * @param data -resource
     * @return - true\false
     */
    ResponseEntity postResourceValidation(PutResource data);

    /**
     * check Resource and Post with agreement
     *
     * @param data -resource
     * @return - true\false
     */
    ResponseEntity postResourceValidationWhithAgreement(PutResource data);

    /**
     * check Resource and put with agreement
     *
     * @param data -resource
     * @return - true\false
     */
    ResponseEntity putResourceValidationWhithAgreement(PutResource data);

    /**
     * check Resource and delete with agreement
     *
     * @param idPeriod   - period's id
     * @param idResource - resource's id
     * @return - true\false
     */
    ResponseEntity deleteResourceValidationWhithAgreement(String idPeriod, String idResource);
}
