package com.jsf.utils.excel.reader;

import com.jsf.utils.annotation.excel.Fields;
import com.jsf.utils.excel.BaseExcel;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 注解式导入Excel
 * <p>仅支持excel2007及以上版本</p>
 *
 * @author rick
 * @version 1.2
 */
public class ExcelAnnoReader {

    /*
        ExcelReaderXSSAuto xss = new ExcelReaderXSSAuto();
        List<Wage> list = xss.read(is, Wage.class);
    */

    private XSSFWorkbook wb;
    private XSSFSheet sheet;
    private XSSFRow row;

    /**
     * 读取Excel
     *
     * @param is
     * @param clz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> List<T> read(InputStream is, Class<T> clz) throws Exception {
        // 延迟解析比率
        ZipSecureFile.setMinInflateRatio(-1.0d);
        wb = new XSSFWorkbook(is);
        // 获取第一个sheet
        sheet = wb.getSheetAt(0);
        // 读取总行数
        int rowNum = sheet.getLastRowNum();
        // 第一行-标题
        XSSFRow row0 = sheet.getRow(0);
        // 读取总列数
        int colNum = row0.getPhysicalNumberOfCells();

        List<T> list = new ArrayList<>();
        Field[] fs = clz.getDeclaredFields(); // 所有字段

        for (int j = 1; j <= rowNum; j++) { // 读行-数据从第二行起
            T entity = clz.newInstance(); // 实例化实体
            row = sheet.getRow(j); // 获取行记录
            if (isRowEmpty(row, colNum)) { // 判读空行
                continue;
            }
            int k = 0;
            while (k < colNum) { // 读列
                String name = row0.getCell(k).getStringCellValue(); // 读取列名
                for (int i = 0; i < fs.length; i++) {
                    Fields field = fs[i].getAnnotation(Fields.class); // 获取Fields注解信息
                    if (field == null) {
                        continue;
                    }
                    String colName = field.value(); // 指定对应列名
                    if (colName.equals(name)) {
                        String fieldName = fs[i].getName(); // 字段名
                        String fieldType = fs[i].getType().getSimpleName(); // 实体类字段类型
                        Object val = BaseExcel.getCellFormatValue(row.getCell(k));
                        BaseExcel.setEntityValue(fieldName, val, entity, j, k);
                    }
                }
                k++;
            }
            list.add(entity);
        }

        return list;
    }

    /**
     * 判断是否为空白行
     *
     * @param row
     * @param colNum 总列数
     * @return
     */
    private boolean isRowEmpty(XSSFRow row, Integer colNum) {
        int blank = 0;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell == null)
                return true;
            if (cell.getCellTypeEnum() == CellType.BLANK)
                blank++;
        }
        if (blank == colNum) {
            return true;
        }
        return false;
    }

}
