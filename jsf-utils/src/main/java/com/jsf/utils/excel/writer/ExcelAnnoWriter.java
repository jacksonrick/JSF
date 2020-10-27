package com.jsf.utils.excel.writer;

import com.jsf.utils.annotation.excel.Excel;
import com.jsf.utils.annotation.excel.Fields;
import com.jsf.utils.excel.BaseExcel;
import com.jsf.utils.exception.SysException;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: Excel导出 注解式
 * <p>导出百万级数据容易OOM、CPU爆满，建议分页</p>
 * <p>2、大量数据时，反射会有严重性能问题，建议去除反射部分</p>
 * User: xujunfei
 * Date: 2019-03-11
 * Time: 10:21
 */
public class ExcelAnnoWriter<T> {

    private List<T> datas = new ArrayList<>();
    public String excelName;
    private int rowIndex = 0; // 行索引
    private int totalRow = 0; // 总列
    private Class<T> clz;
    private Field[] fs;

    // 在内存中保持1000行，超过1000行将被刷新到磁盘
    private SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
    private SXSSFSheet sheet = null;

    public ExcelAnnoWriter(Class<T> clazz) {
        this.clz = clazz;
        Excel excel = clz.getAnnotation(Excel.class);
        if (excel == null || excel.name() == null) {
            throw new RuntimeException("请指定Excel表名");
        }
        // 表名
        excelName = new StringBuilder(excel.name())
                .append(new DateTime(System.currentTimeMillis()).toString("yyyy-MM-dd-HH-mm-ss"))
                .append(".xlsx").toString();
        // 创建sheet
        sheet = workbook.createSheet("sheet1");
        // sheet.setRandomAccessWindowSize(-1);
        // 产生Excel表头
        Row header = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFont(font);

        fs = clz.getDeclaredFields(); // 所有字段
        int cell = 0; // 列序号，从0开始
        for (int i = 0; i < fs.length; i++) {
            Fields field = fs[i].getAnnotation(Fields.class); // 获取Fields注解信息
            if (field != null) {
                Cell c = header.createCell(cell);
                c.setCellValue(field.value());
                c.setCellStyle(style); // 样式
                cell++;
            }
        }
        totalRow = cell;
    }

    /**
     * 写入数据
     *
     * @param data
     * @param path 输出目录
     * @return
     * @throws Exception
     */
    public String write(List<T> data, String path) throws Exception {
        writePage(data);
        return export(path);
    }

    /**
     * 写入分页数据
     *
     * @param data
     */
    public void writePage(List<T> data) throws Exception {
        int line = rowIndex + 1; // 行序号，第二行开始
        for (T model : data) {
            Row row = sheet.createRow(line++); // 创建行
            int cell = 0; // 列序号
            for (int i = 0; i < fs.length; i++) {
                Fields field = fs[i].getAnnotation(Fields.class); // 获取Fields注解信息
                if (field == null) {
                    continue;
                }
                String fieldName = fs[i].getName();
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1); // 属性的get方法，必须以get开头，is不支持
                Method getMethod = clz.getMethod(getMethodName, new Class[]{});
                Object obj = getMethod.invoke(model, new Object[]{}); // 执行get方法
                String val = String.valueOf(obj);
                // 写入单元格
                BaseExcel.writeCell(row, cell, field, val);
                cell++;
            }
        }

        rowIndex += data.size();
    }

    /**
     * 输出文件
     *
     * @param path 路径需要以 / 结尾
     */
    public String export(String path) {
        // 自动列宽
        sheet.trackAllColumnsForAutoSizing();
        // 输出到文件
        File file = new File(path + excelName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            workbook.write(out);
            return file.getAbsolutePath();
        } catch (IOException e) {
            throw new SysException(e.getMessage(), e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                throw new SysException(e.getMessage(), e);
            }
            workbook.dispose();
        }
    }

}
