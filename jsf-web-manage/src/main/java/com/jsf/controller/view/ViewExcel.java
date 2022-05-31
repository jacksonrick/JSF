package com.jsf.controller.view;

import com.jsf.utils.annotation.excel.Excel;
import com.jsf.utils.annotation.excel.Fields;
import com.jsf.utils.excel.BaseExcel;
import com.jsf.utils.system.LogManager;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 生成Excel视图-通用模板
 * <p>1.map{name:表格名称,list:List类型}</p>
 * <p>2.SpringMvc用法:return new ModelAndView(new ViewExcel<UserModel>(), model);</p>
 *
 * @author rick
 * @version 2.0
 */
public class ViewExcel<T> extends AbstractXlsView {

    @Override
    protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return new SXSSFWorkbook(-1);
    }

    @Override
    protected void buildExcelDocument(Map<String, Object> map, Workbook workbook, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // List<T>类型
        List<T> list = (List<T>) map.get("list");
        if (list.isEmpty()) {
            return;
        }

        T t = list.get(0);
        Excel excel = t.getClass().getAnnotation(Excel.class);
        if (excel == null || excel.name() == null) {
            throw new RuntimeException("请指定Excel表名");
        }

        // 表名
        String excelName = String.valueOf(map.get("name"));
        if (excel == null || "null".equals(excelName)) {
            excelName = new StringBuilder(excel.name())
                    .append(new DateTime(System.currentTimeMillis()).toString("yyyy-MM-dd-HH-mm-ss"))
                    .append(".xlsx").toString(); // 文件后缀为xlsx(excel 2010)
        }
        // 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开
        response.setContentType("APPLICATION/OCTET-STREAM"); // application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
        response.setHeader("Content-Disposition", "attachment; filename=" + BaseExcel.encodeFileName(excelName, request));

        // 创建sheet
        SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet("sheet1");
        // 产生Excel表头
        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setFont(font);

        Field[] fs = t.getClass().getDeclaredFields(); // 所有字段
        int cell = 0; // 列序号，从0开始
        for (int i = 0; i < fs.length; i++) {
            Fields field = fs[i].getAnnotation(Fields.class); // 获取Fields注解信息
            if (field != null) {
                Cell c = header.createCell(cell);
                c.setCellValue(field.value());
                c.setCellStyle(headerStyle); // 样式
                if (field.width() != -1) { // 列宽
                    sheet.setColumnWidth(cell, field.width() * 256);
                }
                cell++;
            }
        }

        int totalRow = cell; // 总列
        int line = 1; // 行序号，第二行开始
        for (T model : list) {
            Row row = sheet.createRow(line++); // 创建行
            cell = 0; // 列序号
            for (int i = 0; i < fs.length; i++) {
                Fields field = fs[i].getAnnotation(Fields.class); // 获取Fields注解信息
                if (field == null) {
                    continue;
                }
                String fieldName = fs[i].getName();
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1); // 属性的get方法，必须以get开头，is不支持
                Method getMethod = t.getClass().getMethod(getMethodName, new Class[]{});
                Object obj = getMethod.invoke(model, new Object[]{}); // 执行get方法
                // 写入单元格
                BaseExcel.writeCell(row, cell, field, obj);
                cell++;
            }
        }

        LogManager.info(excelName + " 已导出，总计" + (line - 1) + "行，" + totalRow + "列", ViewExcel.class);

        // 单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true); // 自动换行
        cellStyle.setVerticalAlignment(cellStyle.getVerticalAlignmentEnum().CENTER); // 垂直居中
        for (int i = 0; i < totalRow; i++) {
            for (int j = 1; j < line; j++) {
                sheet.getRow(j).getCell(i).setCellStyle(cellStyle);
            }
        }
        // 自动列宽
        //sheet.trackAllColumnsForAutoSizing();
        //for (int i = 0; i < total; i++) {
        //    sheet.autoSizeColumn(i);
        //}
    }

}