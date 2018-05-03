package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.utils.common;

import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourceComression;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourcePeriod;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.monitoring.model.ResourcePeriodComression;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samsonov_ky on 24.08.2017.
 */
public class SpeedUpUtil {
    /**
     * Сжатие данных для отправки на клиент
     * @param resourceperiod - ресурспериод
     * @return - сжатый ресусрпериод
     */
    public static ResourcePeriodComression speedUp(ResourcePeriod resourceperiod){
            ResourceComression resTemp = new ResourceComression();
            resTemp.setId(resourceperiod.getFkResource().getId());
            resTemp.setName(resourceperiod.getFkResource().getName());
            resTemp.setFullName(resourceperiod.getFkResource().getFullName());
            resTemp.setCodeTSN(resourceperiod.getFkResource().getCodeTSN());
            resTemp.setIdTsnTransportCost(resourceperiod.getFkResource().getIdTsnTransportCost());
            resTemp.setCodeOKP(resourceperiod.getFkResource().getCodeOKP());
            resTemp.setCodeOKPD(resourceperiod.getFkResource().getCodeOKPD());
            resTemp.setWeightNet(resourceperiod.getFkResource().getWeightNet());
            resTemp.setWeightGross(resourceperiod.getFkResource().getWeightGross());
            resTemp.setBasicPrice(resourceperiod.getFkResource().getBasicPrice());
            resTemp.setSpecification(resourceperiod.getFkResource().getSpecification());
            resTemp.setNote(resourceperiod.getFkResource().getNote());
            resTemp.setResourceRoot(resourceperiod.getFkResource().getResourceRoot());
            resTemp.setIsDeleted(resourceperiod.getFkResource().getIsDeleted());
            resTemp.setIsActive(resourceperiod.getFkResource().getIsActive());
            resTemp.setResourceSection(resourceperiod.getFkResource().getResourceSection().getId());
            resTemp.setResourceType(resourceperiod.getFkResource().getResourceType());
            resTemp.setMeasure(resourceperiod.getFkResource().getMeasure());
            resTemp.setResourceStatusAction(resourceperiod.getFkResource().getResourceStatusAction());
            resTemp.setLastId(resourceperiod.getFkResource().getLastId());
            resTemp.setResourceStatus(resourceperiod.getFkResource().getResourceStatus());
            resTemp.setIdZsr(resourceperiod.getFkResource().getIdZsr());
            resTemp.setIdTsnTransportCost(resourceperiod.getFkResource().getIdTsnTransportCost());

            ResourcePeriodComression resperTemp = new ResourcePeriodComression();
            resperTemp.setId(resourceperiod.getId());
            resperTemp.setPricePrimary(resourceperiod.getPricePrimary());
            resperTemp.setChangePrimary(resourceperiod.getChangePrimary());
            resperTemp.setIsTransportCosts(resourceperiod.getIsTransportCosts());
            resperTemp.setFkCompany(resourceperiod.getFkCompany());
            resperTemp.setManufacturer(resourceperiod.getManufacturer());
            resperTemp.setFkPeriod(resourceperiod.getFkPeriod().getId());
            resperTemp.setFkResource(resTemp);
            resperTemp.setResourceStatus(resourceperiod.getResourceStatus());
            resperTemp.setIsDeleted(resourceperiod.getIsDeleted());
            resperTemp.setIsReturn(resourceperiod.getIsReturn());
            resperTemp.setRemarkChief(resourceperiod.getRemarkChief());
            resperTemp.setRemarkLeader(resourceperiod.getRemarkLeader());
            resperTemp.setIsMainPrice(resourceperiod.getIsMainPrice());
            resperTemp.setMainPrice(resourceperiod.getMainPrice());
            resperTemp.setIsReturnChief(resourceperiod.getIsReturnChief());
            resperTemp.setIsReturnHead(resourceperiod.getIsReturnHead());
            resperTemp.setIsStatus(resourceperiod.getIsStatus());
            resperTemp.setDiviationWithMainPrice(resourceperiod.getDiviationWithMainPrice());


        return resperTemp;
    }
    /**
     * Сжатие данных для отправки на клиент
     * @param rp - лист ресурспериодов
     * @return - сжатый лист ресусрпериодов
     */

    public static List<ResourcePeriodComression> speedUp(List<ResourcePeriod> rp){
    List<ResourcePeriodComression> rpSpeed=new ArrayList<ResourcePeriodComression>();
            for (ResourcePeriod resourceperiod:rp) {
                ResourceComression resTemp = new ResourceComression();
                resTemp.setId(resourceperiod.getFkResource().getId());
                resTemp.setName(resourceperiod.getFkResource().getName());
                resTemp.setFullName(resourceperiod.getFkResource().getFullName());
                resTemp.setCodeTSN(resourceperiod.getFkResource().getCodeTSN());
                resTemp.setCodeOKP(resourceperiod.getFkResource().getCodeOKP());
                resTemp.setCodeOKPD(resourceperiod.getFkResource().getCodeOKPD());
                resTemp.setWeightNet(resourceperiod.getFkResource().getWeightNet());
                resTemp.setWeightGross(resourceperiod.getFkResource().getWeightGross());
                resTemp.setIdTsnTransportCost(resourceperiod.getFkResource().getIdTsnTransportCost());
                resTemp.setBasicPrice(resourceperiod.getFkResource().getBasicPrice());
                resTemp.setSpecification(resourceperiod.getFkResource().getSpecification());
                resTemp.setNote(resourceperiod.getFkResource().getNote());
                resTemp.setResourceRoot(resourceperiod.getFkResource().getResourceRoot());
                resTemp.setIsDeleted(resourceperiod.getFkResource().getIsDeleted());
                resTemp.setIsActive(resourceperiod.getFkResource().getIsActive());
                resTemp.setResourceSection(resourceperiod.getFkResource().getResourceSection().getId());
                resTemp.setResourceType(resourceperiod.getFkResource().getResourceType());
                resTemp.setMeasure(resourceperiod.getFkResource().getMeasure());
                resTemp.setResourceStatusAction(resourceperiod.getFkResource().getResourceStatusAction());
                resTemp.setLastId(resourceperiod.getFkResource().getLastId());
                resTemp.setResourceStatus(resourceperiod.getFkResource().getResourceStatus());
                resTemp.setIdZsr(resourceperiod.getFkResource().getIdZsr());
                resTemp.setIdTsnTransportCost(resourceperiod.getFkResource().getIdTsnTransportCost());

                ResourcePeriodComression resperTemp = new ResourcePeriodComression();
                resperTemp.setId(resourceperiod.getId());
                resperTemp.setPricePrimary(resourceperiod.getPricePrimary());
                resperTemp.setChangePrimary(resourceperiod.getChangePrimary());
                resperTemp.setIsTransportCosts(resourceperiod.getIsTransportCosts());
                resperTemp.setFkCompany(resourceperiod.getFkCompany());
                resperTemp.setManufacturer(resourceperiod.getManufacturer());
                resperTemp.setFkPeriod(resourceperiod.getFkPeriod().getId());
                resperTemp.setFkResource(resTemp);
                resperTemp.setResourceStatus(resourceperiod.getResourceStatus());
                resperTemp.setIsDeleted(resourceperiod.getIsDeleted());
                resperTemp.setIsReturn(resourceperiod.getIsReturn());
                resperTemp.setRemarkChief(resourceperiod.getRemarkChief());
                resperTemp.setRemarkLeader(resourceperiod.getRemarkLeader());
                resperTemp.setIsMainPrice(resourceperiod.getIsMainPrice());
                resperTemp.setMainPrice(resourceperiod.getMainPrice());
                resperTemp.setIsReturnChief(resourceperiod.getIsReturnChief());
                resperTemp.setIsReturnHead(resourceperiod.getIsReturnHead());
                resperTemp.setIsStatus(resourceperiod.getIsStatus());
                resperTemp.setDiviationWithMainPrice(resourceperiod.getDiviationWithMainPrice());
                rpSpeed.add(resperTemp);
            }
               return rpSpeed;
            }
}
