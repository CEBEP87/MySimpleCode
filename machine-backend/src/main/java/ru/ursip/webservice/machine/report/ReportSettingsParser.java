package ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.report;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.report.ReportSettings;
import ru.XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.webservice.machine.model.report.ReportTableHeadColumn;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Разбор настроек для Excel
 *
 * @author samsonov
 * @since 15.03.2017
 */
public class ReportSettingsParser {


    /**
     * Парсинг конфигурационного файла для Excel
     *
     * @param settingsString - xml файл содержащий настройки
     * @return - конфигурация строк report документа
     */
    public ReportSettings parseExcelSettings(InputStream settingsString) {
        ReportSettings result = new ReportSettings();
        try {
            SAXBuilder builder = new SAXBuilder();
//            InputStream stream = new ByteArrayInputStream(settingsString.getBytes("UTF-8"));
            Document document = builder.build(settingsString);
            Element rootNode = document.getRootElement();
            List<Element> elements = rootNode.getChildren();
            for (Element element : elements) {
                if (element.getName().equals("docname")) {
                    result.setDocName(element.getText());
                }
                if (element.getName().equals("fontname")) {
                    result.setFontName(element.getText());
                }
                List<Element> colElementsList = element.getChildren();
                List<ReportTableHeadColumn> reportColumns = new ArrayList<>();
                List<String> stringRows = new ArrayList<>();
                List<ReportTableHeadColumn> tableHeadColumns = new ArrayList<>();
                for (Element colElem : colElementsList) {
                    if (colElem.getName().equals("col")) {
                        ReportTableHeadColumn column = new ReportTableHeadColumn();
                        List<Element> rowElementsList = colElem.getChildren();
                        List<String> colRows = new ArrayList<>();
                        for (Element rowElem : rowElementsList) {
                            if (rowElem.getName().equals("row")) {
                                colRows.add(rowElem.getText());
                            }
                        }
                        column.setRows(colRows);
                        tableHeadColumns.add(column);
                    }
                    if (colElem.getName().equals("row")){
                        stringRows.add(colElem.getText());
                    }
                }
                switch (element.getName()) {
                    case "service":
                        result.setServiceList(stringRows);
                        result.setServiceFontSize(Short.parseShort(element.getAttributeValue("fontsize")));
                        break;
                    case "title":
                        result.setTitleList(stringRows);
                        result.setTitleTableFontSize(Short.parseShort(element.getAttributeValue("fontsize")));
                        break;
                    case "footer":
                        result.setFooterList(stringRows);
                        result.setFooterFontSize(Short.parseShort(element.getAttributeValue("fontsize")));
                        break;
                    case "tablehead":
                        result.setTableHeadColumns(tableHeadColumns);
                        result.setTableHeadFontSize(Short.parseShort(element.getAttributeValue("fontsize")));
                        break;
                    case "table":
                        result.setTableFontSize(Short.parseShort(element.getAttributeValue("fontsize")));
                        break;
                }
            }
        } catch (Exception e) {

        }

        return result;
    }
}
