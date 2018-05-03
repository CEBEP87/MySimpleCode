package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.*;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.service.impl.DigestServiceImpl;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.utils.validation.model.ServiceError;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util.utilsForReportGenerator.*;
import static ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util.workWithApachePOE.*;

/**
 * Digest Service Impl
 *
 * @author Samsonov_KY
 * @since 21.02.2018
 */
@Component
public class DigestWord {
    /**
     * limit rows fo tests
     */
    final private Integer LIMIT = 1000000;
    /**
     * global Row Numeration
     */
    private Integer globalRowNumeration = 1;

    /**
     * Logger object
     */
    private Logger logger = LoggerFactory.getLogger(DigestServiceImpl.class);
    /**
     * periodService
     */
    @Autowired
    private PeriodService periodService;
    /**
     * repairService
     */
    @Autowired
    private RepairService repairService;
    /**
     * constructionService
     */
    @Autowired
    private ConstructionService constructionService;
    /**
     * overheadProfitService
     */
    @Autowired
    private OverheadProfitService overheadProfitService;

    /**
     * getCollectionForReportGenerator
     */
    @Autowired
    private GetCollectionForReportGenerator getCollectionForReportGenerator;
    /**
     * calculationsService
     */
    @Autowired
    private CalculationsService calculationsService;
    /**
     * materialService
     */
    @Autowired
    private MaterialService materialService;
    /**
     * Logger unitOfMeasureService
     */
    @Autowired
    private UnitOfMeasureService unitOfMeasureService;

    /**
     * winterRiseListService
     */
    @Autowired
    private WinterRiseListService winterRiseListService;

    /**
     * get Digest
     *
     * @param idPeriod   idPeriod
     * @param response   response
     * @param fileLogDir fileLogDir
     * @return ResponseEntity
     */

    public ResponseEntity getDigestWord(String idPeriod, HttpServletResponse response, String fileLogDir) {
        try {
            HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
            OPCPackage pkg = OPCPackage.open(fileLogDir + "template.docx");
            XWPFDocument doc;
            doc = new XWPFDocument(pkg);
            Period period = periodService.findOne(idPeriod);
            doc = changeText(period, doc);
            doc = changeTables(period, doc);
            doc = changeTableNormCurrent(period, doc);
            System.gc();
            doc = changeTableNormBase(period, doc);
            System.gc();
            doc = changeReferenceTable(period, doc);
            doc.write(new FileOutputStream(fileLogDir + "outputFile.docx"));
            pkg.revert();
            doc = null;
            logger.info("File has been generated!");

            response.setHeader("Content-disposition", "attachment;filename=Коэф. пересчета " + period.getTitle() + ".xlsx");
            response.addHeader("Content-Length", "666");
            PrintWriter out = response.getWriter();
            FileInputStream fileToDownload = new FileInputStream(fileLogDir + "outputFile.docx");
            response.setContentType("application/msword");
            response.setHeader("Content-Disposition", "attachment; filename=outputFile.docx");
            response.setContentLength(fileToDownload.available());
            int c;
            while ((c = fileToDownload.read()) != -1) {
                out.write(c);
            }
            out.flush();
            out.close();
            fileToDownload.close();

            return new ResponseEntity("Digest generated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(new ServiceError("Внутренняя ошибка сервера", e, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * change Reference Table
     *
     * @param period period
     * @param doc    XWPFDocument
     * @return XWPFDocument
     */
    private XWPFDocument changeReferenceTable(Period period, XWPFDocument doc) {

        //найти таблицу
        String tableMark = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        XWPFTable originalTable = findTableByWord(tableMark, doc);
        //пробежать по строкам
        int rowIterator = -1;
        //подготовить коллекцию под таблицу
        List<List<String>> resultCollection = getCollectionForReportGenerator.getCollection("Material by reference", period, null, false);
        try {
            while (rowIterator < originalTable.getRows().size()) {
                rowIterator++;
                String rowValue = null;
                if (originalTable.getRow(rowIterator).getCell(0).getParagraphs().get(0).getRuns().size() != 0)
                    if (originalTable.getRow(rowIterator).getCell(0).getParagraphs().get(0).getRuns().get(0).getText(0) != null)
                        rowValue = originalTable.getRow(rowIterator).getCell(0).getParagraphs().get(0).getRuns().get(0).getText(0);
                // идентифицировать ТСН
                if (rowValue == null) continue;
                String pressmark = getPressmarkFromRow(rowValue);
                // соблюсти условия справочного текста
                if (printPriceHardCode(rowValue, resultCollection, originalTable, rowIterator)) continue;
                //найти в ней строку
                List<String> row = resultCollection.stream().filter(r -> r.get(0).equals(pressmark)).findFirst().orElse(null);
                if (row == null) continue;

                //заменить шифры на Названия материалов

                originalTable.getRow(rowIterator).getCell(0).getParagraphs().get(0).removeRun(0);
                if (originalTable.getRow(rowIterator).getCell(0).getParagraphs().get(0).getRuns().size() == 0)
                    originalTable.getRow(rowIterator).getCell(0).getParagraphs().get(0).createRun().setText(row.get(1));
                // вывести ед измерения
                if (originalTable.getRow(rowIterator).getCell(1).getParagraphs().get(0).getRuns().size() == 0)
                    originalTable.getRow(rowIterator).getCell(1).getParagraphs().get(0).createRun().setText(row.get(2));
                // вывести стоимость
                if (originalTable.getRow(rowIterator).getCell(2).getParagraphs().get(0).getRuns().size() == 0)
                    originalTable.getRow(rowIterator).getCell(2).getParagraphs().get(0).createRun().setText(row.get(3));

            }
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
        //удалить справочный текст В НАЧАЛЕ!!!!!
        return doc;
    }

    /**
     * Print hard code rows
     *
     * @param pressmark        pressmark
     * @param resultCollection resultCollection
     * @param originalTable    Table for change
     * @param rowIterator      rowIterator
     * @return Pressmark
     */

    private boolean printPriceHardCode(String pressmark, List<List<String>> resultCollection, XWPFTable originalTable, Integer rowIterator) {
        if (pressmark.equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")) {
            // сунуть в массив
            ArrayList<String> values = new ArrayList();
            values.add("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            // пройти массив проверить и сделать массив значений
            findAndPrintPriceForPressmarks(pressmark, resultCollection, originalTable, rowIterator, values);
            return true;
        }
        if (pressmark.equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")) {
            ArrayList<String> values = new ArrayList();
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
               findAndPrintPriceForPressmarks(pressmark, resultCollection, originalTable, rowIterator, values);
            return true;
        }
        if (pressmark.equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX-")) {
            ArrayList<String> values = new ArrayList();
            XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
               findAndPrintPriceForPressmarks(pressmark, resultCollection, originalTable, rowIterator, values);
            return true;
        }




        return false;
    }
    /**
     * find And Print Price For Pressmarks in rows
     *
     * @param pressmark pressmark
     * @param resultCollection resultCollection
     * @param originalTable originalTable
     * @param rowIterator rowIterator
     * @param values values
     */

    private void findAndPrintPriceForPressmarks(String pressmark, List<List<String>> resultCollection, XWPFTable originalTable, Integer rowIterator, ArrayList<String> values) {
        Double sum = 0d;
        int abortedIteratorSize = 0;
        for (String str : values) {
            if (resultCollection.stream().filter(r -> r.get(0).equals(str)).findFirst().get() == null) {
                if (originalTable.getRow(rowIterator).getCell(2).getParagraphs().get(0).getRuns().size() == 0)
                    originalTable.getRow(rowIterator).getCell(2).getParagraphs().get(0).createRun().setText(str.toString() + " (удален)");
                abortedIteratorSize++;
            } else
                sum += Double.valueOf(resultCollection.stream().filter(r -> r.get(0).equals(str)).findFirst().get().get(3));
        }
        //вычислить
        Integer price = (int) (sum / (values.size() - abortedIteratorSize));
        //заполниьт
        if (abortedIteratorSize == 0)
            if (originalTable.getRow(rowIterator).getCell(2).getParagraphs().get(0).getRuns().size() == 0)
                originalTable.getRow(rowIterator).getCell(2).getParagraphs().get(0).createRun().setText(price.toString());
    }


    /**
     * get Pressmark From Row
     *
     * @param rowValue string
     * @return Pressmark
     */
    private String getPressmarkFromRow(String rowValue) {
        //if will need validate
        if (rowValue.charAt(0) == '1') return rowValue;
        return null;
    }


    /**
     * change Table Norm Base
     *
     * @param period period
     * @param doc    XWPFDocument
     * @return XWPFDocument
     */
    private XWPFDocument changeTableNormBase(Period period, XWPFDocument doc) {
        Group chapter = null;
        String tableMark = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        doc = writeNormativeTableProcess(doc, chapter, period, tableMark, "ConstructionNorm", false, true);

        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        doc = writeNormativeTableProcess(doc, chapter, period, tableMark, "InstallationNorm", false, true);

         chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        doc = writeNormativeTableProcess(doc, chapter, period, tableMark, "ServiceNorm", false, true);

        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        doc = writeNormativeTableProcess(doc, chapter, period, tableMark, "TransportNorm", true, true);


        return doc;

    }

    /**
     * changeTableNormCurrent
     *
     * @param period period
     * @param doc    XWPFDocument
     * @return XWPFDocument
     */
    private XWPFDocument changeTableNormCurrent(Period period, XWPFDocument doc) {
        Group chapter = null;
        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        doc = writeNormativeTableProcess(doc, chapter, period, "Winter_Rise_List_current.winter_rise_mat_extra", "ConstructionNorm", false, true);


        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        doc = writeNormativeTableProcess(doc, chapter, period, "Winter_Rise_List_current.winter_rise_mat_extra", "ServiceNorm", false, true);

        chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
        doc = writeNormativeTableProcess(doc, chapter, period, "Winter_Rise_List_current.winter_rise_mat_extra", "TransportNorm", true, true);


        return doc;

    }

    /**
     * changeText
     *
     * @param period period
     * @param doc    XWPFDocument
     * @return XWPFDocument
     */
    private XWPFDocument changeText(Period period, XWPFDocument doc) {
        OverheadProfitCurrent overheadProfitCurrent = overheadProfitService.getOverheadProfitByIdPeriodAndPressmark("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", period.getId());
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                runs = changeWordsInRun(runs, "(where pressmark=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX)", "");
                  runs = changeWordsInRun(runs, "(XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", "");
                runs = changeWordsInRun(runs, "(Сборник, id_parent=id главы)", "");
            }
        }
        return doc;
    }

    /**
     * changeTables
     *
     * @param period period
     * @param doc    XWPFDocument
     * @return XWPFDocument
     */
    private XWPFDocument changeTables(Period period, XWPFDocument doc) {
        try {
            doc = clearNotes(doc);
            doc = writeTableRepair(period, doc);
            doc = writeTableConstructionFirst(period, doc);
            doc = writeTableConstructionSecond(period, doc);
            Group chapter = null;

            chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
            doc = writeTableNotProcess(doc, chapter, period, "Material.pressmark", "Period_group.titleMaterial", "Period_groupl.titleMaterial", "Period.group.titleMaterial", "Material");
              chapter = period.getGroupList().stream().filter(s -> s.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")).findFirst().get();
            doc = writeTableProcess(doc, chapter, period, "Celebrate_process.pressmark", "Period_group.titleCelebrateProcess", "Period.group.titleCelebrateProcess", "CelebrateProcess");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * writeNormativeTableProcess
     *
     * @param period         period
     * @param chapter        chapter
     * @param doc            XWPFDocument
     * @param tableMark      table Mark
     * @param collectionName collectionName
     * @param lastRow        lastRow or not
     * @param base           base or not
     * @return XWPFDocument
     */
    private XWPFDocument writeNormativeTableProcess(XWPFDocument doc, Group chapter, Period period, String tableMark, String collectionName, boolean lastRow, boolean base) {
        List<List<String>> resultCollection = getCollectionForReportGenerator.getCollection(collectionName, period, chapter, base);
        XWPFTable originalTable = findTableByWord(tableMark, doc);
        Boolean firstRecord = true;
        for (Group part : period.getGroupList()) {
            if (part.getIdParent() != null) {
                if (!part.getIdParent().equals(chapter.getId())) continue;
            } else continue;
            if (firstRecord)
                dublicateChapterRow(originalTable, chapter);
            dublicatePartRow(originalTable, part);
            globalRowNumeration = writeThirdRow(resultCollection, part, LIMIT, originalTable);
            firstRecord = false;
        }
        if (lastRow) deleteTempRow(originalTable);
        return doc;
    }


    /**
     * writeTableNotProcess
     *
     * @param collectionName   collectionName
     * @param paragraphPart    paragraphPart
     * @param paragraphSection paragraphSection
     * @param paragraphChapter paragraphChapter
     * @param tableMark        tableMark
     * @param chapter          chapter
     * @param period           period
     * @param doc              XWPFDocument
     * @return XWPFDocument
     */
    private XWPFDocument writeTableNotProcess(XWPFDocument doc, Group chapter, Period period, String tableMark, String paragraphChapter, String paragraphSection, String paragraphPart, String collectionName) {
        try {
            XWPFTable tb;
            XWPFTable lastTable = null;
            Group firstChapter = null;
            Group firstSection = null;
            Group firstPart = null;
            Group oldChapter = firstChapter;
            Group oldSection = firstSection;
            Group oldPart = firstPart;

            List<List<String>> resultCollection = getCollectionForReportGenerator.getCollection(collectionName, period, null, false);
            XWPFTable originalTable = null;
            boolean firstWrite = true;
            boolean tempFirstWrite = true;
            originalTable = findTableByWord(tableMark, doc);
            XWPFTable copyTable;
            XWPFParagraph etalonParagrapfForDescriptionChapter = null;
            XWPFParagraph etalonParagrapfForDescriptionSection = null;
            XWPFParagraph etalonParagrapfForDescriptionPart = null;

            for (Group section : period.getGroupList()) {
                if (!tableMark.equals("Machine.pressmarkSection1") && section.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"))
                    continue;
                if (!tableMark.equals("Machine.pressmarkSection2") && section.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"))
                    continue;
                if (!tableMark.equals("Machine.pressmarkSection3") && section.getTitle().equals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"))
                    continue;
                if (section.getIdParent() != null) {
                    if (!section.getIdParent().equals(chapter.getId())) continue;
                } else continue;
                for (Group part : period.getGroupList()) {
                    if (part.getIdParent() != null) {
                        if (!part.getIdParent().equals(section.getId())) continue;
                    } else continue;
                    if (firstWrite) {
                        etalonParagrapfForDescriptionChapter = getSortedParagraphByMark(paragraphChapter, doc);
                        etalonParagrapfForDescriptionSection = getSortedParagraphByMark(paragraphSection, doc);
                        etalonParagrapfForDescriptionPart = getSortedParagraphByMark(paragraphPart, doc);
                        firstChapter = chapter;
                        firstSection = section;
                        firstPart = part;
                        changeFirstTableDescription(chapter, section, part, paragraphChapter, paragraphSection, paragraphPart, false, doc);
                        globalRowNumeration = putListToTableWithStyle(originalTable, part, resultCollection, globalRowNumeration, LIMIT);
                        lastTable = originalTable;
                        oldChapter = firstChapter;
                        oldSection = firstSection;
                        oldPart = firstPart;
                        firstWrite = false;
                    } else {
                        logger.info("writing " + part.getTitle());
                        changeTablesDescription(doc, chapter, section, part, false, etalonParagrapfForDescriptionChapter, etalonParagrapfForDescriptionSection, etalonParagrapfForDescriptionPart
                                , oldPart, lastTable, firstPart, oldSection, firstSection, oldChapter, firstChapter);
                        copyTable = addTableAfterText(originalTable, part.getTitle(), section, doc);
                        globalRowNumeration = putListToTableWithStyle(copyTable, part, resultCollection, globalRowNumeration, LIMIT);
                        lastTable = copyTable;
                        oldChapter = chapter;
                        oldSection = section;
                        oldPart = part;
                        oldChapter = chapter;
                        oldSection = section;
                        oldPart = part;

                    }

                }
            }
            System.gc();
            resultCollection = null;
            System.gc();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }


    /**
     * Logger object
     *
     * @param collectionName   collectionName
     * @param paragraphPart    paragraphPart
     * @param paragraphChapter paragraphChapter
     * @param tableMark        tableMark
     * @param chapter          chapter
     * @param period           period
     * @param doc              XWPFDocument
     * @return XWPFDocument
     */
    private XWPFDocument writeTableProcess(XWPFDocument doc, Group chapter, Period period, String tableMark, String paragraphChapter, String paragraphPart, String collectionName) {
        try {
            String idPeriod;
            XWPFTable tb;
            XWPFTable lastTable = null;
            Group firstChapter = null;
            Group firstSection = null;
            Group firstPart = null;
            Group oldChapter = firstChapter;
            Group oldSection = firstSection;
            Group oldPart = firstPart;
            boolean firstWrite = true;
            boolean tempFirstWrite = true;
            XWPFParagraph etalonParagrapfForDescriptionChapter = null;
            XWPFParagraph etalonParagrapfForDescriptionSection = null;
            XWPFParagraph etalonParagrapfForDescriptionPart = null;
            List<List<String>> resultCollection = getCollectionForReportGenerator.getCollection(collectionName, period, chapter, false);
            XWPFTable originalTable = null;
            originalTable = findTableByWord(tableMark, doc);
            XWPFTable copyTable;
            for (Group part : period.getGroupList()) {
                if (part.getIdParent() != null) {
                    if (!part.getIdParent().equals(chapter.getId())) continue;
                } else continue;
                if (firstWrite) {
                    logger.info("writing " + part.getTitle());
                    etalonParagrapfForDescriptionChapter = getSortedParagraphByMark(paragraphChapter, doc);
                    etalonParagrapfForDescriptionPart = getSortedParagraphByMark(paragraphPart, doc);
                    firstChapter = chapter;
                    firstPart = part;
                    changeFirstTableDescription(chapter, null, part, paragraphChapter, null, paragraphPart, true, doc);
                    globalRowNumeration = putListToTableWithStyle(originalTable, part, resultCollection, globalRowNumeration, LIMIT);
                    lastTable = originalTable;
                    oldChapter = firstChapter;
                    oldSection = firstSection;
                    oldPart = firstPart;
                    firstWrite = false;
                } else {
                    logger.info("writing " + part.getTitle());
                    changeTablesDescription(doc, chapter, null, part, true, etalonParagrapfForDescriptionChapter, etalonParagrapfForDescriptionSection, etalonParagrapfForDescriptionPart
                            , oldPart, lastTable, firstPart, oldSection, firstSection, oldChapter, firstChapter);
                    copyTable = addTableAfterText(originalTable, part.getTitle(), chapter, doc);
                    globalRowNumeration = putListToTableWithStyle(copyTable, part, resultCollection, globalRowNumeration, LIMIT);
                    lastTable = copyTable;
                    oldChapter = chapter;
                    oldPart = part;
                    oldChapter = chapter;
                    oldPart = part;
                }
            }
            resultCollection = null;
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }


    /**
     * writeTableRepair
     *
     * @param period period
     * @param doc    XWPFDocument
     * @return XWPFDocument
     **/
    private XWPFDocument writeTableRepair(Period period, XWPFDocument doc) {
        try {

            Repair repair = repairService.findAllByIdPeriodAndPressmark(period.getId(), "6.58-2-1").get(0);
            List<UnitOfMeasure> unitOfMeasures = unitOfMeasureService.findAll();
            List<OverheadProfitBase> overheadProfitBases = overheadProfitService.findAllBase();
            List<OverheadProfitCurrent> overheadProfitCurrents = overheadProfitService.findAllByIdPeriod(period.getId());
            List<WinterRiseListCurrent> winterRiseListCurrentList = winterRiseListService.getAllWinterRiseListCurrentByPeriod(period.getId());

            UnitOfMeasure unitOfMeasure = null;
            OverheadProfitBase overheadProfitBase = null;
            OverheadProfitCurrent overheadProfitCurrent = null;
            WinterRiseListCurrent winterRiseListCurrent = null;
             try {
                overheadProfitBase = overheadProfitBases.stream().filter(s -> s.getIdPressmark().equals(repair.getIdPressmarkOverheadProfit())).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                overheadProfitCurrent = overheadProfitCurrents.stream().filter(s -> s.getIdPressmark().equals(repair.getIdPressmarkOverheadProfit())).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Double calculateSalaryBase = null;
            Double calculateSalaryBaseWithValue = null;
            Double calculateSalaryBaseWithProfitBase = null;
            Double calculateSalaryBaseWithProfitCurrent = null;
            Double calculateSalaryBaseWithOverheadProfitCurrent = null;
            Double calculateFixedTime = null;
            Double calculateSalaryBaseWithOverheadProfitBase = null;
            Double calculateSalaryBaseWithPeriod = null;
            Double calculateEstimateBase = null;
            Double calculateEstimateCurrent = null;

            XWPFTable table = findTableByWord("Всегобаз", doc);
            if (repair != null && repair.getTitle() != null)
                replaceTextInCell(table, 2, 2, repair.getTitle().toString());
            if (unitOfMeasure != null && unitOfMeasure.getAlias() != null)
                replaceTextInCell(table, 2, 3, unitOfMeasure.getAlias().toString());
            if (overheadProfitCurrent != null && overheadProfitBase.getOverheadBase() != null)
                replaceTextInCell(table, 3, 5, repair.getSalaryBase().toString());
            if (winterRiseListCurrent != null && winterRiseListCurrent.getWinterRise() != null)
                replaceTextInCell(table, 3, 7, winterRiseListCurrent.getWinterRise().toString());
            if (repair != null && repair.getSalaryBase() != null && winterRiseListCurrent != null && winterRiseListCurrent.getWinterRise() != null)
                calculateSalaryBase = calculationsService.rounding(10 * repair.getSalaryBase() * winterRiseListCurrent.getWinterRise());
            if (calculateSalaryBase != null) replaceTextInCell(table, 3, 8, calculateSalaryBase.toString());
            if (period.getIndexSalary() != null) replaceTextInCell(table, 3, 9, period.getIndexSalary().toString());
            if (repair != null && repair.getSalaryBase() != null && period.getIndexSalary() != null && winterRiseListCurrent != null && winterRiseListCurrent.getWinterRise() != null)
                calculateSalaryBaseWithPeriod = calculationsService.rounding(10 * repair.getSalaryBase() * winterRiseListCurrent.getWinterRise() * period.getIndexSalary());
            if (calculateSalaryBaseWithPeriod != null)
                calculateEstimateCurrent = calculationsService.rounding(10 * repair.getSalaryBase() * winterRiseListCurrent.getWinterRise() * (1 + overheadProfitCurrent.getOverhead() / 100 + overheadProfitCurrent.getProfit() / 100));
            if (calculateEstimateCurrent != null) replaceTextInCell(table, 8, 10, calculateEstimateCurrent.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }


    /**
     * clearNotes
     *
     * @param doc XWPFDocument
     * @return XWPFDocument
     **/
    private XWPFDocument clearNotes(XWPFDocument doc) {
        try {
            changeWordsInTable("(where pressmark=6.58-2-1)", "", doc);
            changeWordsInTable("(По Id_Unit_of_measure)", "", doc);
            changeWordsInTable("(по id_pressmark_Overhead_Profut)", "", doc);
            changeWordsInTable("(по id_pressmark_Winter_rise_List)", "", doc);
            changeWordsInTable("(по id_pressmark_Overhead_Profut", "", doc);
            changeWordsInTable("(по id_pressmark_Winter_rise_List)", "", doc);
            changeWordsInTable("Коллекция «Construction»", "", doc);
            changeWordsInTable("(Глава 3, id_parent=null) ", "", doc);
            changeWordsInTable("(Сборник, id_parent=id главы)", "", doc);
            changeWordsInTable("(по id_pressmark_overhead_profit)", "", doc);
            changeWordsInTable("(по id_pressmark_overhead_profit)", "", doc);
            changeWordsInTable("(по id_pressmark_overhead_profit)", "", doc);
            changeWordsInTable("(по id_pressmark_overhead_profit)", "", doc);
             } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }
}
