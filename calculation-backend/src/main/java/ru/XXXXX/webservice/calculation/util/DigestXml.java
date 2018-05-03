package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Group;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Period;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.PeriodService;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util.jaxb.INDEX;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

/**
 * Digest Service Impl
 *
 * @author Samsonov_KY
 * @since 21.02.2018
 */
@Component
public class DigestXml {
    /**
     * file name
     */
    private String fileName;
    /**
     * limit item of items FOR DEVELOPERS
     */
    private final int LIMIT = 1000000;
    /**
     *  list
     */
    private INDEX group = null;

    /**
     * Logger
     */
    private static Logger log =
            Logger.getLogger(DigestXml.class.getName());
    /**
     * getCollectionForReportGenerator
     */
    @Autowired
    private GetCollectionForReportGenerator getCollectionForReportGenerator;

    /**
     * periodService
     */
    @Autowired
    private PeriodService periodService;

    /**
     * get Digest
     *
     * @param idPeriod   idPeriod
     * @param fileLogDir fileLogDir
     * @return ResponseEntity
     */
    public ResponseEntity getDigestXml(String idPeriod, String fileLogDir) {
        try {
            Period period = periodService.findOne(idPeriod);
            fileName = "Коэфициенты пересчета+ «" + period.getTitle() + "».xml";
            createXml(period, fileLogDir + fileName);
            File result = new File(fileLogDir + fileName);
            String type = result.toURL().openConnection().guessContentTypeFromName(fileName);
            InputStream inputStream = new FileInputStream(fileLogDir + fileName);
            byte[] out = org.apache.commons.io.IOUtils.toByteArray(inputStream);
            inputStream.close();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-disposition", "attachment; filename=" + fileName);
            responseHeaders.add("Content-Type", type);
            return new ResponseEntity(out, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * create XML
     *
     * @param period   period
     * @param path path to file
     */
    public void createXml(Period period, String path) {
        try {
            JAXBContext context = JAXBContext.newInstance(INDEX.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(createJavaObject(period), new File(path));
        } catch (JAXBException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * createJavaObject
     *
     * @param period   period
     * @return body of file
     */
    public INDEX createJavaObject(Period period) {
        group = new INDEX();
        group.setPERIOD(period.getTitle());
        group.setITEMS(new INDEX.ITEMS());
        group.setCHAPTER("[1,2,3,4,5,6,7,8,9,10,14,15,16,17]");
        for (int part = 0; part < 15; part++) {
            List<List<String>> values = getValues(period, part);
            int count = 0;
            for (List<String> val : values) {
                INDEX.ITEMS.ITEM items = new INDEX.ITEMS.ITEM();
                if (count >= LIMIT) break;
                count++;
                if (!val.get(2).equals("-")) items.setRATETRANSPORT(new BigDecimal(val.get(2)));
                if (!val.get(3).equals("-")) items.setRATEMATERIAL(new BigDecimal(val.get(3)));
                if (!val.get(4).equals("-")) items.setRATEMACHINE(new BigDecimal(val.get(4)));
                if (!val.get(5).equals("-")) items.setOVERHEAD(new BigDecimal(val.get(5)));
                if (!val.get(6).equals("-")) items.setEXTRAOVERHEAD(new BigDecimal(val.get(6)));
                if (!val.get(7).equals("-")) items.setPROFIT(new BigDecimal(val.get(7)));
                if (!val.get(8).equals("-")) items.setEXTRAPROFIT(new BigDecimal(val.get(8)));
                if (!val.get(9).equals("-")) items.setWINTERRISEPRICE(new BigDecimal(val.get(9)));
                if (!val.get(10).equals("-")) items.setEXTRAWINTERRISEPRICE(new BigDecimal(val.get(10)));
                if (!val.get(11).equals("-")) items.setWINTERRISEPRICEMAT(new BigDecimal(val.get(11)));
                if (!val.get(12).equals("-")) items.setEXTRAWINTERRISEPRICEMAT(new BigDecimal(val.get(12)));
                if (!val.get(13).equals("-")) items.setRATETOTAL(new BigDecimal(val.get(13)));
                if (!val.get(14).equals("-")) items.setRATESALARY(new BigDecimal(val.get(14)));
                if (!val.get(15).equals("-")) items.setRATEEXTRA(new BigDecimal(val.get(15)));
                if (!val.get(16).equals("-")) items.setBASEOVERHEAD(new BigDecimal(val.get(16)));
                if (!val.get(17).equals("-")) items.setBASEEXTRAOVERHEAD(new BigDecimal(val.get(17)));
                if (!val.get(18).equals("-")) items.setBASEPROFIT(new BigDecimal(val.get(18)));
                if (!val.get(19).equals("-")) items.setBASEEXTRAPROFIT(new BigDecimal(val.get(19)));
                if (!val.get(20).equals("-")) items.setBASEWINTERRISEPRICE(new BigDecimal(val.get(20)));
                if (!val.get(21).equals("-")) items.setEXTRAWINTERRISEPRICE(new BigDecimal(val.get(21)));
                if (!val.get(22).equals("-")) items.setBASEWINTERRISEPRICEMAT(new BigDecimal(val.get(22)));
                if (!val.get(23).equals("-")) items.setEXTRAWINTERRISEPRICEMAT(new BigDecimal(val.get(23)));
                if (items != null)
                    group.getITEMS().getITEM().add(items);
            }
        }
        return group;
    }

    /**
     * prepare data for print
     *
     * @param period   period
     * @param part   part of digest
     * @return data
     */
    private List<List<String>> getValues(Period period, int part) {
        Group chapter = null;
        String collectionName = null;

        if (part == 0) {
            chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
            collectionName = "TransportCostXml";
        }

        if (part == 1) {
            chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
            collectionName = "MaterialXml";
        }
        if (part == 2) {
            chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
            collectionName = "MachineXml";
        }
        if (part == 3) {
            chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();

        return getCollectionForReportGenerator.getCollection(collectionName, period, chapter, false);
    }


}

