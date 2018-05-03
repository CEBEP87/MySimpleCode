package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.*;

import java.util.List;
import java.util.stream.Collectors;

import static ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util.workWithApachePOE.*;

/**
 * utils For Report Generator
 *
 * @author Samsonov_KY
 * @since 26.02.2018
 */
public class utilsForReportGenerator {

    /**
     * dublicate Chapter Row
     *
     * @param originalTable originalTable
     * @param chapter       chapter
     */
    public static void dublicateChapterRow(XWPFTable originalTable, Group chapter) {
        try {
            XWPFTableRow oldRow = originalTable.getRow(4);
            CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
            XWPFTableRow newRow = new XWPFTableRow(ctrow, originalTable);
            XWPFParagraph paragraph = newRow.getCell(0).getParagraphs().get(0);
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.getRuns().get(0).setText(chapter.getTitle());
            paragraph.getRuns().get(0).setFontFamily("Century Gothic");
            paragraph.getRuns().get(0).setFontSize(7);
            paragraph.getRuns().get(0).setBold(true);
            originalTable.addRow(newRow);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Logger object
     *
     * @param theProcess       idPeriod
     * @param doc              XWPFDocument
     * @param paragraphPart    idPeriod
     * @param paragraphSection idPeriod
     * @param paragraphChapter idPeriod
     * @param part             idPeriod
     * @param section          idPeriod
     * @param chapter          idPeriod
     **/
    public static void changeFirstTableDescription(Group chapter, Group section, Group part, String paragraphChapter, String paragraphSection, String paragraphPart, boolean theProcess, XWPFDocument doc) {
        try {
            changeWordsInRun(getSortedParagraphByMark(paragraphPart, doc).getRuns(), paragraphPart, part.getTitle());
            if (!theProcess)
                changeWordsInRun(getSortedParagraphByMark(paragraphSection, doc).getRuns(), paragraphSection, section.getTitle());
            if (section != null)
                if (!section.getTitle().equals("Раздел 2. Перебазировка строительных машин и механизмов"))
                    if (!section.getTitle().equals("Раздел 3. Перебазировка дополнительных секций"))
                        changeWordsInRun(getSortedParagraphByMark(paragraphChapter, doc).getRuns(), paragraphChapter, chapter.getTitle());
            if (theProcess)
                changeWordsInRun(getSortedParagraphByMark(paragraphChapter, doc).getRuns(), paragraphChapter, chapter.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * change Tables Description
     *
     * @param doc                                  XWPFDocument
     * @param chapter                              chapter
     * @param section                              section
     * @param theProcess                           theProcess
     * @param etalonParagrapfForDescriptionChapter etalon Paragrapf For Description Chapter
     * @param etalonParagrapfForDescriptionSection etalonParagrapfForDescriptionSection
     * @param etalonParagrapfForDescriptionPart    etalonParagrapfForDescriptionPart
     * @param part                                 part
     * @param oldPart                              oldPart
     * @param lastTable                            lastTable
     * @param firstPart                            firstPart
     * @param oldSection                           oldSection
     * @param firstSection                         firstSection
     * @param oldChapter                           oldChapter
     * @param firstChapter                         firstChapter
     **/
    public static void changeTablesDescription(XWPFDocument doc, Group chapter, Group section, Group part, boolean theProcess, XWPFParagraph etalonParagrapfForDescriptionChapter,
                                               XWPFParagraph etalonParagrapfForDescriptionSection, XWPFParagraph etalonParagrapfForDescriptionPart,
                                               Group oldPart, XWPFTable lastTable, Group firstPart, Group oldSection, Group firstSection, Group oldChapter, Group firstChapter) {
        if (oldPart != part) {
            XWPFParagraph paragraph = copyTableDescription(lastTable, etalonParagrapfForDescriptionPart.getRuns(), doc);
            changeWordsInRun(paragraph.getRuns(), firstPart.getTitle(), part.getTitle());
        }
        if (!theProcess) if (oldSection != section) {
            XWPFParagraph paragraph = copyTableDescription(lastTable, etalonParagrapfForDescriptionSection.getRuns(), doc);
            changeWordsInRun(paragraph.getRuns(), firstSection.getTitle(), section.getTitle());
        }
        if (oldChapter != chapter) {
            XWPFParagraph paragraph = copyTableDescription(lastTable, etalonParagrapfForDescriptionChapter.getRuns(), doc);
            changeWordsInRun(paragraph.getRuns(), firstChapter.getTitle(), chapter.getTitle());
        }

    }

    /**
     * push collection to table
     *
     * @param resultCollection    collection
     * @param part                part
     * @param limit               limit for tests
     * @param table               table
     * @param globalRowNumeration row numeration
     * @return number of row
     **/
    public static Integer putListToTableWithStyle(XWPFTable table, Group part, List<List<String>> resultCollection, Integer globalRowNumeration, Integer limit) {
        int i = 1;
        boolean firstRow = true;
        List<String> lastValues = null;
        String tempTsn = null;
        try {
            for (List<String> mat : resultCollection)
                if (part.getId().equals(mat.get(0)))
                    if (i < limit) {
                        if (identity(mat, lastValues)) {
                            tempTsn = mat.get(1);
                            continue;
                        } else if (tempTsn != null) {
                            String text = table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(0).getRuns().get(0).getText(0);
                            table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(0).getRuns().get(0).setText("÷");
                            XWPFParagraph paragraph = table.getRow(table.getRows().size() - 1).getCell(1).addParagraph();
                            table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(1).setAlignment(ParagraphAlignment.CENTER);

                            table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(1).createRun();
                            table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(1).getRuns().get(0).setText(tempTsn);

                            table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(1).getRuns().get(0).setFontFamily("Century Gothic");
                            table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(1).getRuns().get(0).setFontSize(6);
                            tempTsn = null;
                        }

                        XWPFTableRow oldrow = table.getRow(table.getRows().size() - 1);
                        XWPFTableRow row = table.createRow();
                        if (table.getRow(1).getTableCells().size() > 3)

                            while (row.getTableCells().size() < table.getRow(1).getTableCells().size())
                                row.addNewTableCell();

                        putToTableCell(row, 0, globalRowNumeration.toString());
                        row.getCell(0).getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);
                        putToTableCell(row, 1, mat.get(1));
                        row.getCell(1).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
                        putToTableCell(row, 2, mat.get(2));
                        row.getCell(2).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
                        if (row.getTableCells().size() > 3) {
                            putToTableCell(row, 3, mat.get(3));
                            row.getCell(3).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
                        }
                        if (row.getTableCells().size() > 5) {
                            putToTableCell(row, 4, mat.get(4));
                            row.getCell(4).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
                            putToTableCell(row, 5, mat.get(5));
                            row.getCell(5).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
                            putToTableCell(row, 6, mat.get(6));
                            row.getCell(6).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
                        }
                        globalRowNumeration++;
                        lastValues = mat;
                        if (firstRow) {
                            table.removeRow(table.getNumberOfRows() - 2);
                            firstRow = false;
                            //logger.info("Write old =" + (globalRowNumeration - 1) + " " + mat.getPressmark() + " -");
                        }
                        i++;
                        //oldRow = null;
                        row = null;
                    }
            if (tempTsn != null) {
                String text = table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(0).getRuns().get(0).getText(0);
                table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(0).getRuns().get(0).setText("÷");
                XWPFParagraph paragraph = table.getRow(table.getRows().size() - 1).getCell(1).addParagraph();
                table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(1).setAlignment(ParagraphAlignment.CENTER);

                table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(1).createRun();
                table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(1).getRuns().get(0).setText(tempTsn);

                table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(1).getRuns().get(0).setFontFamily("Century Gothic");
                table.getRow(table.getRows().size() - 1).getCell(1).getParagraphs().get(1).getRuns().get(0).setFontSize(6);
                tempTsn = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return globalRowNumeration;
    }

    /**
     * putToTableCell
     *
     * @param row  new Value
     * @param cell last Value
     * @param text last Value
     **/
    private static void putToTableCell(XWPFTableRow row, int cell, String text) {
        if (row.getCell(cell).getParagraphs().size() < 1) {
            XWPFParagraph paragraph = row.getCell(cell).addParagraph();
            row.getCell(cell).setParagraph(paragraph);
        }
        if (row.getCell(cell).getParagraphs().get(0).getRuns().size() < 1) {
            row.getCell(cell).getParagraphs().get(0).createRun();
        }
        row.getCell(cell).getParagraphs().get(0).getRuns().get(0).setText(text);
        row.getCell(cell).getParagraphs().get(0).getRuns().get(0).setFontFamily("Century Gothic");
        row.getCell(cell).getParagraphs().get(0).getRuns().get(0).setFontSize(6);

    }

    /**
     * check identity
     *
     * @param newValues  new Value
     * @param lastValues last Value
     * @return boolean
     **/
    private static boolean identity(List<String> newValues, List<String> lastValues) {
        if (newValues == null) return false;
        if (lastValues == null) return false;
        for (int i = 2; i < newValues.size(); i++)
            if (!newValues.get(i).equals(lastValues.get(i))) return false;
        return true;
    }


    /**
     * dublicate PartRow
     *
     * @param originalTable originalTable
     * @param part          part
     **/
    public static void dublicatePartRow(XWPFTable originalTable, Group part) {
        try {
            XWPFTableRow oldRow = originalTable.getRow(7);
            CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
            XWPFTableRow newRow = new XWPFTableRow(ctrow, originalTable);
            XWPFParagraph paragraph = newRow.getCell(0).getParagraphs().get(0);
            paragraph.setAlignment(ParagraphAlignment.LEFT);
            paragraph.getRuns().get(0).setText(part.getTitle());
            paragraph.getRuns().get(0).setFontFamily("Century Gothic");
            paragraph.getRuns().get(0).setFontSize(7);
            paragraph.getRuns().get(0).setBold(true);
            originalTable.addRow(newRow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * delete TempRow
     *
     * @param originalTable originalTable
     **/
    public static void deleteTempRow(XWPFTable originalTable) {
        originalTable.removeRow(7);
        originalTable.removeRow(6);
        originalTable.removeRow(5);
        originalTable.removeRow(4);
    }

    /**
     * push collection to table
     *
     * @param resultCollection collection
     * @param part             part
     * @param limit            limit for tests
     * @param originalTable    table
     * @return number of row
     **/
    public static Integer writeThirdRow(List<List<String>> resultCollection, Group part, Integer limit, XWPFTable originalTable) {
        Integer iterator = 1;
        List<String> lastValues = null;
        String tempTsn = null;
        for (List<String> mat : resultCollection)
            if (iterator < limit)
                if (part.getId().equals(mat.get(0))) {
                    if (identity(mat, lastValues)) {
                        tempTsn = mat.get(1);
                        continue;
                    } else if (tempTsn != null) {
                        originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(0).getRuns().get(0).setText("÷");
                        XWPFParagraph paragraph = originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).addParagraph();
                        originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(1).setAlignment(ParagraphAlignment.CENTER);

                        originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(1).createRun();
                        originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(1).getRuns().get(0).setText(tempTsn);

                        originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(1).getRuns().get(0).setFontFamily("Century Gothic");
                        originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(1).getRuns().get(0).setFontSize(7);
                        tempTsn = null;
                    }
                    XWPFTableRow oldrow = null;
                    oldrow = originalTable.createRow();
                    while (oldrow.getTableCells().size() < 9) oldrow.addNewTableCell();
                    for (int iter = 0; iter < oldrow.getTableCells().size(); iter++)
                        oldrow.getCell(iter).getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
                    for (int iter = 0; iter < oldrow.getTableCells().size(); iter++)
                        putToTableCell(oldrow, iter, mat.get(iter + 1).toString());
                    //  oldrow.getCell(iter).setText(mat.get(iter + 1));
                    iterator++;
                    lastValues = mat;
                }
        if (tempTsn != null) {
            XWPFParagraph paragraph = originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).addParagraph();
            originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(1).setAlignment(ParagraphAlignment.CENTER);

            originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(1).createRun();
            originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(1).getRuns().get(0).setText(tempTsn);

            originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(1).getRuns().get(0).setFontFamily("Century Gothic");
            originalTable.getRow(originalTable.getRows().size() - 1).getCell(0).getParagraphs().get(1).getRuns().get(0).setFontSize(7);
            tempTsn = null;
        }
        return iterator;
    }

    /**
     * refactor parent link
     *
     * @param chapterProcess chapterProcess
     * @param period         period
     * @param idGroup        idGroup
     * @return idGroup
     **/
    public static String refactorGroup(Group chapterProcess, Period period, String idGroup) {
        try {
            boolean finded = false;
            String idGroupParent = idGroup;
            String idProcess = idGroup;
            while (!finded) {
                String finalIdGroupParent = idGroupParent;
                Group group = period.getGroupList().stream().filter(s -> s.getId().equals(finalIdGroupParent)).findFirst().get();
                if (group.getIdParent() == null || group.getIdParent().equals(""))
                    if (!group.getId().equals(chapterProcess.getId())) return idGroup;
                if (group.getIdParent() == null || group.getIdParent().equals(""))
                    if (group.getId().equals(chapterProcess.getId())) return idProcess;
                idGroupParent = group.getIdParent();
                idProcess = group.getId();
                if (group.getIdParent() == null || group.getIdParent().equals("")) return idGroup;
            }
            return null;
        } catch (Exception e) {
            return "-";
        }
    }

    /**
     * get Sorted Installation List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<Installation> getSortedInstallationListByIdPeriod(String idPeriod, List<Installation> list) {
        List<Installation> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * Comparator code for refactoring
     *
     * @param e1 String 1
     * @param e2 String 2
     * @return sorted
     **/
    private static int coparatorCode(String e1, String e2) {
        try {
            String[] s1 = e1.split("\\.");
            String[] s3 = e2.split("\\.");
            if ((e1.contains(".")) &
                    (e2.contains("."))) {
                String[] s2 = s1[1].split("\\-");
                String[] s4 = s3[1].split("\\-");
                int result;
                result = Integer.valueOf(s1[0]).compareTo(Integer.valueOf(s3[0]));
                if (result != 0) return result;
                result = Integer.valueOf(s2[0]).compareTo(Integer.valueOf(s4[0]));
                if (result != 0) return result;
                result = Integer.valueOf(s2[1]).compareTo(Integer.valueOf(s4[1]));
                if (result != 0) return result;
                return Integer.valueOf(s2[2]).compareTo(Integer.valueOf(s4[2]));
            } else {
                return Integer.valueOf(s1[0]).compareTo(Integer.valueOf(s3[0]));
            }
        } catch (Exception e) {
            return e1.split("\\.")[0].compareTo(e2.split("\\.")[0]);
        }
    }

    /**
     * get Sorted Repair List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<Repair> getSortedRepairListByIdPeriod(String idPeriod, List<Repair> list) {
        List<Repair> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * get Sorted WinterRise List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<WinterRise> getSortedWinterRiseListByIdPeriod(String idPeriod, List<WinterRise> list) {
        List<WinterRise> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * get Sorted TemporaryBuilding List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<TemporaryBuilding> getSortedTemporaryBuildingListByIdPeriod(String idPeriod, List<TemporaryBuilding> list) {
        List<TemporaryBuilding> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * get Sorted Service List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Service> getSortedServiceListByIdPeriod(String idPeriod, List<ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Service> list) {
        List<ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Service> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }


    /**
     * get Sorted Transport List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<Transport> getSortedTransportListByIdPeriod(String idPeriod, List<Transport> list) {
        List<Transport> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }


    /**
     * get Sorted LargeProcess List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<LargeProcess> getSortedLargeProcessListByIdPeriod(String idPeriod, List<LargeProcess> list) {
        List<LargeProcess> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }


    /**
     * get Sorted CelebrateProcess List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<CelebrateProcess> getSortedCelebrateProcessListByIdPeriod(String idPeriod, List<CelebrateProcess> list) {
        List<CelebrateProcess> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }


    /**
     * get Sorted Restoration List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<Restoration> getSortedRestorationListByIdPeriod(String idPeriod, List<Restoration> list) {
        List<Restoration> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * get Sorted Overhead List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<Overhead> getSortedOverheadListByIdPeriod(String idPeriod, List<Overhead> list) {
        List<Overhead> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }


    /**
     * get Sorted Commissioning List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<Commissioning> getSortedCommissioningListByIdPeriod(String idPeriod, List<Commissioning> list) {
        List<Commissioning> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }


    /**
     * get Sorted Material List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<TransportCost> getSortedTransportCostListByIdPeriod(String idPeriod, List<TransportCost> list) {
        List<TransportCost> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * get Sorted Material List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<Material> getSortedMaterialListByIdPeriod(String idPeriod, List<Material> list) {
        List<Material> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * get Sorted Machine List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<Machine> getSortedMachineListByIdPeriod(String idPeriod, List<Machine> list) {
        List<Machine> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }

    /**
     * get Sorted Construction List By IdPeriod
     *
     * @param idPeriod idPeriod
     * @param list     collection
     * @return sorted list
     **/
    public static List<Construction> getSortedConstructionListByIdPeriod(String idPeriod, List<Construction> list) {
        List<Construction> sortedList = list.stream().sorted((e1, e2) -> {
            return coparatorCode(e1.getPressmark(), e2.getPressmark());
        }).collect(Collectors.toList());
        return sortedList;
    }

}
