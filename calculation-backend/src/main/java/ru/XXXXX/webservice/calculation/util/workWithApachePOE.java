package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.util;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.calculation.model.Group;

import java.util.List;
/**
 * methods fot work With Apache POE
 *
 * @author Samsonov_KY
 * @since 26.02.2018
 */
public class workWithApachePOE {


    /**
     * add Table After Text
     *
     * @param clone   table clone
     * @param mark    string mark of table
     * @param section section
     * @param doc     XWPFDocument
     * @return XWPFTable
     **/
    public static XWPFTable addTableAfterText(XWPFTable clone, String mark, Group section, XWPFDocument doc) {
        XWPFTable t2 = null;
        try {
            XWPFParagraph p = getNextParagraphByMarkAfterSection(mark, section, doc);
            XmlCursor cursor = p.getCTP().newCursor();//this is the key!
            t2 = doc.insertNewTbl(cursor);
            t2.getCTTbl().setTblPr(clone.getCTTbl().getTblPr());
            t2.getCTTbl().setTblGrid(clone.getCTTbl().getTblGrid());

            XWPFTableRow oldRow = clone.getRow(0);
            CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
            CTSdtRun ctSdtRun = CTSdtRun.Factory.parse(clone.getRows().get(1).getCtRow().newInputStream());
            XWPFTableRow newRow = new XWPFTableRow(ctrow, t2);
            t2.addRow(newRow);

            oldRow = clone.getRow(1);
            ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
            ctSdtRun = CTSdtRun.Factory.parse(clone.getRows().get(1).getCtRow().newInputStream());
            newRow = new XWPFTableRow(ctrow, t2);
            t2.addRow(newRow);


            if (clone.getRows().get(1).getTableCells().size() > 3)
                if (existAndSortWordsInRun(clone.getRows().get(1).getTableCells().get(3).getParagraphs().get(0).getRuns(), "в т.ч. з/пл.")
                        || existAndSortWordsInRun(clone.getRows().get(1).getTableCells().get(3).getParagraphs().get(0).getRuns(), "МР")
                        || existAndSortWordsInRun(clone.getRows().get(1).getTableCells().get(3).getParagraphs().get(0).getRuns(), "ЗП")) {
                    oldRow = clone.getRow(2);
                    ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
                    ctSdtRun = CTSdtRun.Factory.parse(clone.getRows().get(2).getCtRow().newInputStream());
                    newRow = new XWPFTableRow(ctrow, t2);
                    t2.addRow(newRow);
                }

            t2.removeRow(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t2;
    }

    /**
     * get Next Paragraph By Mark After Section
     *
     * @param mark string mark of paragraph
     * @param section section in document
     * @param doc     XWPFDocument
     * @return XWPFParagraph
     **/
    public static XWPFParagraph getNextParagraphByMarkAfterSection(String mark, Group section, XWPFDocument doc) {
        String tempWord = mark;
        if (section != null)
            mark = section.getTitle();
        int length = -1;
        String sum = "";
        boolean nextContinue = false;
        for (XWPFParagraph p : doc.getParagraphs()) {
            if (nextContinue)
                if (section != null) {
                    mark = tempWord;
                    section = null;
                    nextContinue = false;
                } else
                    return p;
            List<XWPFRun> newRun = null;
            List<XWPFRun> runs = p.getRuns();
            for (XWPFRun r : runs) {
                if (r.getText(0) != null) sum = sum.concat(r.getText(0));
                length++;
            }
            if (!sum.contains(mark)) continue;
            sum = "";
            for (XWPFRun r : runs) {
                String text = r.getText(0);
                if (text != null && text.contains(mark))
                    nextContinue = true;
            }
            for (XWPFRun r : runs)
                if (r.getText(0) != null) sum = sum.concat(r.getText(0));
            if (!sum.contains(mark)) continue;
        }
        return null;
    }


    /**
     * Sorted runs in paragraph
     *
     * @param mark string mark of paragraph
     * @param doc     XWPFDocument
     * @return XWPFTable
     **/
    public static XWPFParagraph getSortedParagraphByMark(String mark, XWPFDocument doc) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();

            String sum = "";
            boolean left = true;
            boolean right = true;
            int iterLeft = -1;
            int iterRight = -1;
            String lastElem = "";
            int length = -1;
            try {
                List<XWPFRun> newRun = null;
                for (XWPFRun r : runs) {
                    if (r.getText(0) != null) sum = sum.concat(r.getText(0));
                    length++;
                }
                if (!sum.contains(mark)) continue;
                sum = "";
                for (XWPFRun r : runs)
                    if (r.getText(0).contains(mark)) return p;
                sum = "";
                ///erase laft and right
                while (left) {
                    iterLeft++;
                    lastElem = runs.get(iterLeft).toString();
                    runs.get(iterLeft).setText(null, 0);
                    for (XWPFRun r : runs)
                        if (r.getText(0) != null) sum = sum.concat(r.getText(0));
                    if (!sum.contains(mark))
                        left = false;
                    sum = "";
                    runs.get(iterLeft).setText(lastElem, 0);
                }
                while (right) {
                    iterRight++;
                    lastElem = runs.get(length - iterRight).toString();
                    runs.get(length - iterRight).setText(null, 0);
                    for (XWPFRun r : runs)
                        if (r.getText(0) != null) sum = sum.concat(r.getText(0));
                    if (!sum.contains(mark))
                        right = false;
                    sum = "";
                    runs.get(length - iterRight).setText(lastElem, 0);
                }
                runs.get(iterLeft).setText(mark, 0);
                for (int i = iterLeft + 1; i <= length - iterRight; i++)
                    runs.get(i).setText("", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return p;
        }
        return null;
    }


    /**
     * print Text After Table With Mark
     *
     * @param mark mark
     * @param text new Text
     * @param doc     XWPFDocument
     * @return XWPFDocument
     **/
    public static XWPFDocument printTextAfterTableWithStringMark(String mark, String text, XWPFDocument doc) {
        XWPFTable table = findTableByWord(mark, doc);
        org.apache.xmlbeans.XmlCursor cursor = table.getCTTbl().newCursor();
        cursor.toEndToken(); //now we are at end of the CTTbl
        while (cursor.toNextToken() != org.apache.xmlbeans.XmlCursor.TokenType.START) ;
        XWPFParagraph newParagraph = doc.insertNewParagraph(cursor);
        XWPFRun run = newParagraph.createRun();
        run.setText(text);
        return doc;
    }


    /**
     * copy Description for Table
     *
     * @param table table
     * @param runs  description in runs
     * @param doc     doc
     * @return XWPFParagraph
     **/
    public static XWPFParagraph copyTableDescription(XWPFTable table, List<XWPFRun> runs, XWPFDocument doc) {
        XWPFParagraph newParagraph = null;
        try {
            org.apache.xmlbeans.XmlCursor cursor = table.getCTTbl().newCursor();
            cursor.toEndToken(); //now we are at end of the CTTbl
            while (cursor.toNextToken() != org.apache.xmlbeans.XmlCursor.TokenType.START) ;
            newParagraph = doc.insertNewParagraph(cursor);
            int i = 0;
            for (XWPFRun s : runs) {
                XWPFRun run = newParagraph.createRun();
                CTRPr cTRPr = s.getCTR().getRPr();
                if (s.getText(s.getTextPosition()) != null && !s.getText(s.getTextPosition()).equals(""))
                    run.setText(s.getText(s.getTextPosition()));
                run.getCTR().setRPr(cTRPr);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newParagraph;
    }


    /**
     * copy First Temp Description
     *
     * @param table   table
     * @param runs  description in runs
     * @param newName new text
     * @param doc     doc
     * @return XWPFParagraph
     **/
    public static XWPFParagraph copyFirstTempDescription(XWPFTable table, List<XWPFRun> runs, String newName, XWPFDocument doc) {
        XWPFParagraph newParagraph = null;
        try {

            org.apache.xmlbeans.XmlCursor cursor = table.getCTTbl().newCursor();
            cursor.toEndToken(); //now we are at end of the CTTbl
            while (cursor.toNextToken() != org.apache.xmlbeans.XmlCursor.TokenType.START) ;
            newParagraph = doc.insertNewParagraph(cursor);
            int i = 0;
            for (XWPFRun s : runs) {
                XWPFRun run = newParagraph.createRun();
                CTRPr cTRPr = s.getCTR().getRPr();
                if (s.getText(i) != null && !s.getText(i).equals(""))
                    run.setText(newName);
                run.getCTR().setRPr(cTRPr);

                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newParagraph;
    }

    /**
     * Logger object
     *
     * @param oldWord oldWord
     * @param newWord newWord
     * @param doc     doc
     * @return XWPFDocument
     **/
    public static XWPFDocument changeWordsInTable(String oldWord, String newWord, XWPFDocument doc) {
        try {
            for (XWPFTable tbl : doc.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            changeWordsInRun(p.getRuns(), oldWord, newWord);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }


    /**
     * Find Table By Word
     *
     * @param word word
     * @param doc     doc
     * @return XWPFTable XWPFTable
     **/
    public static XWPFTable findTableByWord(String word, XWPFDocument doc) {
        for (XWPFTable tbl : doc.getTables())
            for (XWPFTableRow row : tbl.getRows())
                for (XWPFTableCell cell : row.getTableCells())
                    for (XWPFParagraph p : cell.getParagraphs())
                        for (XWPFRun r : p.getRuns())
                            if (existAndSortWordsInRun(p.getRuns(), word))
                                return tbl;
        return null;
    }


    /**
     * exist And Sort Words In Run
     *
     * @param runs    runs
     * @param word word
     * @return boolean exist
     **/
    public static boolean existAndSortWordsInRun(List<XWPFRun> runs, String word) {
        String sum = "";
        boolean left = true;
        boolean right = true;
        int iterLeft = -1;
        int iterRight = -1;
        String lastElem = "";
        int length = -1;
        try {
            List<XWPFRun> newRun = null;
            for (XWPFRun r : runs) {
                if (r.getText(0) != null) sum = sum.concat(r.getText(0));
                length++;
            }
            if (!sum.contains(word)) return false;
            sum = "";
            for (XWPFRun r : runs) {
                String text = r.getText(0);
                if (text != null && text.contains(word)) {
                    return true;
                }
            }
            for (XWPFRun r : runs)
                if (r.getText(0) != null) sum = sum.concat(r.getText(0));
            if (!sum.contains(word)) return false;
            sum = "";
            ///отрезать с лева и права
            while (left) {
                iterLeft++;
                lastElem = runs.get(iterLeft).toString();
                runs.get(iterLeft).setText(null, 0);
                for (XWPFRun r : runs)
                    if (r.getText(0) != null) sum = sum.concat(r.getText(0));
                if (!sum.contains(word))
                    left = false;
                sum = "";
                runs.get(iterLeft).setText(lastElem, 0);
            }
            while (right) {
                iterRight++;
                lastElem = runs.get(length - iterRight).toString();
                runs.get(length - iterRight).setText(null, 0);
                for (XWPFRun r : runs)
                    if (r.getText(0) != null) sum = sum.concat(r.getText(0));
                if (!sum.contains(word))
                    right = false;
                sum = "";
                runs.get(length - iterRight).setText(lastElem, 0);
            }
            runs.get(iterLeft).setText(word, 0);
            for (int i = iterLeft + 1; i <= length - iterRight; i++)
                runs.get(i).setText("", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * replace Text In Cell
     *
     * @param table table
     * @param row   row
     * @param cell  cell
     * @param text  new text
     **/

    public static void replaceTextInCell(XWPFTable table, int row, int cell, String text) {
        XWPFParagraph paragraph = table.getRow(row).getCell(cell).getParagraphs().get(0);
        XWPFRun newRun =paragraph.createRun();
        XWPFRun run=paragraph.getRuns().get(0);
        newRun.setFontFamily("Century Gothic");
        newRun.setFontSize(7);

        newRun.setBold(run.isBold());
        newRun.setColor(run.getColor());
        newRun.setItalic(run.isItalic());
        newRun.setSubscript(run.getSubscript());
        newRun.setUnderline(run.getUnderline());
        while(paragraph.getRuns().size()>1) paragraph.removeRun(0);

        paragraph.getRuns().get(0).setText(text,0);
    }

    /**
     * change Words In Run
     *
     * @param runs    list of runs
     * @param oldWord oldWord
     * @param newWord newWord
     * @return List<XWPFRun>
     **/
    public static List<XWPFRun> changeWordsInRun(List<XWPFRun> runs, String oldWord, String newWord) {
        String sum = "";
        boolean left = true;
        boolean right = true;
        int iterLeft = -1;
        int iterRight = -1;
        String lastElem = "";
        int length = -1;
        try {
            List<XWPFRun> newRun = null;
            for (XWPFRun r : runs) {
                if (r.getText(0) != null) sum = sum.concat(r.getText(0));
                length++;
            }
            if (!sum.contains(oldWord)) return runs;
            sum = "";
            for (XWPFRun r : runs) {
                String text = r.getText(0);
                if (text != null && text.contains(oldWord))
                    r.setText(r.getText(0).replace(oldWord, newWord), 0);
            }
            for (XWPFRun r : runs)
                if (r.getText(0) != null) sum = sum.concat(r.getText(0));
            if (!sum.contains(oldWord)) return runs;
            sum = "";
            ///отрезать с лева и права
            while (left) {
                iterLeft++;
                lastElem = runs.get(iterLeft).toString();
                runs.get(iterLeft).setText(null, 0);
                for (XWPFRun r : runs)
                    if (r.getText(0) != null) sum = sum.concat(r.getText(0));
                if (!sum.contains(oldWord))
                    left = false;
                sum = "";
                runs.get(iterLeft).setText(lastElem, 0);
            }
            while (right) {
                iterRight++;
                lastElem = runs.get(length - iterRight).toString();
                runs.get(length - iterRight).setText(null, 0);
                for (XWPFRun r : runs)
                    if (r.getText(0) != null) sum = sum.concat(r.getText(0));
                if (!sum.contains(oldWord))
                    right = false;
                sum = "";
                runs.get(length - iterRight).setText(lastElem, 0);
            }
            runs.get(iterLeft).setText(newWord, 0);
            for (int i = iterLeft + 1; i <= length - iterRight; i++)
                runs.get(i).setText("", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return runs;
    }
}
