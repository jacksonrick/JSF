package com.jsf.utils.excel.reader;

import com.jsf.utils.excel.BaseExcel;
import com.jsf.utils.json.JacksonUtil;
import com.jsf.utils.entity.ExcelJsonConfig;
import com.jsf.utils.system.LogManager;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 从JSON配置中读取
 * User: xujunfei
 * Date: 2018-08-30
 * Time: 11:10
 */
public class ExcelJsonCfgReader {

    private POIFSFileSystem fs;
    private XSSFWorkbook wb;
    private XSSFSheet sheet;

    /**
     * 读取json配置
     *
     * @param is
     * @param jsonConfig
     * @param clz
     * @param <T>
     * @throws Exception
     */
    public <T> List<T> read(InputStream is, String jsonConfig, Class<T> clz) throws Exception {
        // 创建工作簿
        wb = new XSSFWorkbook(is);

        List<ExcelJsonConfig> config = JacksonUtil.jsonToList(jsonConfig, ExcelJsonConfig.class);
        LogManager.info("JSON配置: " + config);
        // 存储读取的记录
        List<T> list = new ArrayList<>();

        // 默认读取第一个Sheet
        sheet = wb.getSheetAt(0);
        // 读取总行数
        int rowNum = sheet.getLastRowNum();
        // 第一列-标题
        XSSFRow row0 = sheet.getRow(0);
        XSSFRow row = null;
        // 读取总列数-第一行
        int colNum = row0.getPhysicalNumberOfCells();
        for (int j = 1; j <= rowNum; j++) { // 读行-数据从第二行起
            T entity = clz.newInstance(); // 实例化实体
            row = sheet.getRow(j); // 获取行记录
            int k = 0;
            Map<String, String> jsonMap = new HashMap<>();
            while (k < colNum) { // 读列
                String cell0 = getStringValue(row0.getCell(k));
                // 比对字段
                for (ExcelJsonConfig field : config) {
                    if (field.getName().equals(cell0)) {
                        Object value = BaseExcel.getCellFormatValue(row.getCell(k));
                        BaseExcel.setEntityValue(field.getField(), value, entity, j, k);
                    }
                }
                k++;
            }
            list.add(entity);
        }

        return list;
    }

    /**
     * @param cell
     * @return
     */
    private String getStringValue(XSSFCell cell) {
        String cellvalue = "";
        if (cell == null) {
            return cellvalue;
        }
        try {
            cellvalue = cell.getRichStringCellValue().getString();
        } catch (Exception e) {
        }
        return cellvalue;
    }

}
