package cn.linkey.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.linkey.app.AppUtil;
import cn.linkey.dao.Rdb;
import cn.linkey.doc.Document;
import cn.linkey.factory.BeanCtx;

public class ExcelUtil {
    /**
     * 根据文件名读取excel文件
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    public static List<ArrayList<String>> read(String fileName) {
        List<ArrayList<String>> dataLst = new ArrayList<ArrayList<String>>();

        /** 检查文件名是否为空或者是否是Excel格式的文件 */
        if (fileName == null || !fileName.matches("^.+\\.(?i)((xls)|(xlsx))$")) {
            return dataLst;
        }

        boolean isExcel2003 = true;
        /** 对文件的合法性进行验证 */
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }

        /** 检查文件是否存在 */
        File file = new File(fileName);
        if (file == null || !file.exists()) {
            return dataLst;
        }

        try {
            /** 调用本类提供的根据流读取的方法 */
            dataLst = read(new FileInputStream(file), isExcel2003);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        /** 返回最后读取的结果 */
        return dataLst;
    }

    /**
     * 根据流读取Excel文件
     * 
     * @param inputStream
     * @param isExcel2003
     * @return
     */
    private static List<ArrayList<String>> read(InputStream inputStream, boolean isExcel2003) {
        List<ArrayList<String>> dataLst = null;
        try {
            /** 根据版本选择创建Workbook的方式 */
            Workbook wb = isExcel2003 ? new HSSFWorkbook(inputStream) : new XSSFWorkbook(inputStream);
            dataLst = read(wb);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return dataLst;
    }

    /**
     * 
     * 读取数据
     * 
     * @param wb
     * @return
     */
    private static List<ArrayList<String>> read(Workbook wb) {
        List<ArrayList<String>> dataLst = new ArrayList<ArrayList<String>>();

        /** 得到第一个shell */
        Sheet sheet = wb.getSheetAt(0);
        int totalRows = sheet.getPhysicalNumberOfRows();
        int totalCells = 0;
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }

        /** 循环Excel的行 */
        for (int r = 0; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            ArrayList<String> rowLst = new ArrayList<String>();
            /** 循环Excel的列 */
            for (short c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                String cellValue = "";
                if (cell == null) {
                    rowLst.add(cellValue);
                    continue;
                }

                /** 处理数字型的,自动去零 */
                if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                    /** 在excel里,日期也是数字,在此要进行判断 */
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        cellValue = DateUtil.formatDate(cell.getDateCellValue(), "yyyy-MM-dd hh:mm:ss");
                    }
                    else {
                        cellValue = getRightStr(cell.getNumericCellValue() + "");
                    }
                }
                /** 处理字符串型 */
                else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                    cellValue = cell.getStringCellValue();
                }
                /** 处理布尔型 */
                else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
                    cellValue = cell.getBooleanCellValue() + "";
                }
                /** 其它的,非以上几种数据类型 */
                else {
                    cellValue = cell.toString() + "";
                }

                rowLst.add(cellValue);
            }
            dataLst.add(rowLst);
        }
        return dataLst;
    }

    /**
     * 正确地处理整数后自动加零的情况
     * 
     * @param sNum
     * @return
     */
    private static String getRightStr(String sNum) {
        DecimalFormat decimalFormat = new DecimalFormat("#.000000");
        String resultStr = decimalFormat.format(new Double(sNum));
        if (resultStr.matches("^[-+]?\\d+\\.[0]+$")) {
            resultStr = resultStr.substring(0, resultStr.indexOf("."));
        }
        return resultStr;
    }

    /**
     * grid视图转换为excel文件,文件存于/outfile/目录下
     * 
     * @param gridNumber 要转换的grid编号
     * @param DocUnidList 要转换的文档的UNID
     * @return 近回excel文件名称
     */
    public static String grid2Excel(String gridNumber, String DocUnidList) {

        // 获得grid的文档和数据源的配置信息
        Document gridDoc = AppUtil.getDocByid("BPM_GridList", "GridNum", gridNumber, true);
        if (gridDoc.isNull()) {
            return "Error:The view does not exist!";
        }
        String sql = "select SqlTableName from BPM_DataSourceList where Dataid='" + gridDoc.g("DataSource") + "' and Status='1'";
        String sqlTableName = Rdb.getValueBySql(sql);
        String columnConfig = gridDoc.g("ColumnConfig");
        int spos = columnConfig.indexOf("[");
        if (spos == -1) {
            return "Error:The grid column config is null!";
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("sheet1");

        // 输出视图的列到excel的第一行
        LinkedHashSet<String> fdList = new LinkedHashSet<String>();
        HSSFRow row = sheet.createRow(0);
        columnConfig = columnConfig.substring(spos, columnConfig.lastIndexOf("]") + 1);
        JSONArray jsonArr = JSON.parseArray(columnConfig);
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject colConfigItem = (JSONObject) jsonArr.get(i);
            String colName = colConfigItem.getString("ColName");
            String fdName = colConfigItem.getString("FdName");
            fdList.add(fdName);
            int fdWidth = Integer.valueOf(colConfigItem.getString("ColWidth")) * 10 * 4;
            sheet.setColumnWidth(i, fdWidth);
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(colName);
            HSSFCellStyle style = wb.createCellStyle();
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("h:mm:ss"));
            style.setWrapText(true);// 自动换行
            cell.setCellStyle(style);
        }

        // 开始输出grid中的数据
        String[] docArray = Tools.split(DocUnidList);
        for (int i = 0; i < docArray.length; i++) {
            row = sheet.createRow(i + 1); // 创建一行
            Document doc = Rdb.getDocumentBySql("select * from " + sqlTableName + " where WF_OrUnid='" + docArray[i] + "'"); // 获得数据行
            int j = 0;
            for (String fdName : fdList) {
                HSSFCell cell = row.createCell(j);
                cell.setCellValue(doc.g(fdName));
                HSSFCellStyle style = wb.createCellStyle();
                style.setDataFormat(HSSFDataFormat.getBuiltinFormat("h:mm:ss"));
                style.setWrapText(true);// 自动换行
                j++;
            }
        }

        // 开始输出文件
        String fileName = gridDoc.g("GridName") + "_" + BeanCtx.getUserid() + "_" + Tools.getRandom(3) + ".xls";
        String filePath = AppUtil.getPackagePath() + fileName;
        try {
            FileOutputStream os = new FileOutputStream(filePath);
            wb.write(os);
            os.close();
            return fileName;
        }
        catch (Exception e) {
            BeanCtx.log(e, "E", "Grid导出到Excel时出错");
            return "";
        }
    }

    /**
     * 文档数组转换为Excel格式
     * 
     * @param dc 文档集合对像
     * @param filePath文件全路径
     * @return 返回true表示成功,false表示失败
     */
    public static boolean dc2Excel(Set<Document> dc, String filePath) {
        try {
            HSSFWorkbook wb = new HSSFWorkbook();
            // 创建Excel的工作sheet,对应到一个excel文档的tab
            HSSFSheet sheet = wb.createSheet("sheet1");
            // 设置excel每列宽度
            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 3500);
            int i = 0;
            for (Document doc : dc) {
                HSSFRow row = sheet.createRow(i);
                HashMap<String, String> items = doc.getAllItems();
                int j = 0;
                for (String fdName : items.keySet()) {
                    if (!fdName.equals("WF_OrUnid")) {
                        HSSFCell cell = row.createCell(j);
                        cell.setCellValue(doc.g(fdName));
                        HSSFCellStyle style = wb.createCellStyle();
                        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("h:mm:ss"));
                        style.setWrapText(true);// 自动换行
                        j++;
                    }
                }
                i++;
            }
            FileOutputStream os = new FileOutputStream(filePath);
            wb.write(os);
            os.close();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 文档数组转换为Excel格式
     * 
     * @param dc
     * @param filePath文件全路径
     * @return 返回true表示成功,false表示失败
     */
    public static boolean dc2Excel(Document[] dc, String filePath) {
        try {
            HSSFWorkbook wb = new HSSFWorkbook();
            // 创建Excel的工作sheet,对应到一个excel文档的tab
            HSSFSheet sheet = wb.createSheet("sheet1");
            // 设置excel每列宽度
            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 3500);
            int i = 0;
            for (Document doc : dc) {
                HSSFRow row = sheet.createRow(i);
                HashMap<String, String> items = doc.getAllItems();
                int j = 0;
                for (String fdName : items.keySet()) {
                    if (!fdName.equals("WF_OrUnid")) {
                        HSSFCell cell = row.createCell(j);
                        cell.setCellValue(doc.g(fdName));
                        HSSFCellStyle style = wb.createCellStyle();
                        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("h:mm:ss"));
                        style.setWrapText(true);// 自动换行
                        j++;
                    }
                }
                i++;
            }
            FileOutputStream os = new FileOutputStream(filePath);
            wb.write(os);
            os.close();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 测试main方法
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        List<ArrayList<String>> dataLst = ExcelUtil.read("d:/test.xlsx");
        for (ArrayList<String> innerLst : dataLst) {
            StringBuffer rowData = new StringBuffer();
            for (String dataStr : innerLst) {
                rowData.append(",").append(dataStr);
            }
            if (rowData.length() > 0) {
                System.out.println(rowData.deleteCharAt(0).toString());
            }
        }
    }
}