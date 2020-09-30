package com.jsf.utils.excel.writer;

import com.jsf.utils.system.ExecutorPool;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 写入Excel
 *
 * @author rick
 */
public class ExcelWriter {

    // 在内存中保持n行，超过n行将被刷新到磁盘
    private int rowAccessWindowSize;
    private SXSSFWorkbook workbook = null;

    public ExcelWriter() {
        workbook = new SXSSFWorkbook(10000);
    }

    public ExcelWriter(int rowAccessWindowSize) {
        workbook = new SXSSFWorkbook(rowAccessWindowSize);
    }

    /**
     * 写入数据
     * 逗号隔开的数据
     *
     * @param data
     * @param target 输出路径
     */
    public void writeSplit(List<String> data, String target) {
        FileOutputStream out = null;
        try {
            SXSSFSheet sheet = workbook.createSheet("sheet");
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i);
                String[] strs = String.valueOf(data.get(i)).split(",");
                for (int j = 0; j < strs.length; j++) {
                    this.createCell(row, j, strs[j]);
                }
            }

            // 导出文件
            File file = new File(target);
            out = new FileOutputStream(file);
            workbook.write(out);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioException) {
                }
            }
            workbook.dispose();
        }
    }

    /**
     * 写入数据 多线程
     * 写入大量数据时，CPU还是会占满，且执行时间并没有优化多少，推荐用CSV方式导出
     *
     * @param data
     * @param pageSize 每页多少条数据
     * @param target   输出路径
     */
    public void writeSplitMutilThread(List<String> data, int pageSize, String target) {
        FileOutputStream out = null;
        try {
            int total = data.size();
            int sheets = total % pageSize > 0 ? total / pageSize + 1 : total / pageSize;
            // System.out.println("total:" + total + ", sheets:" + sheets);

            ArrayList<Future<Boolean>> futures = new ArrayList<>();
            for (int i = 0; i < sheets; i++) {
                SXSSFSheet sheet = workbook.createSheet("sheet" + i); // 创建多个sheet
                List<String> batchs = data.subList(0, Math.min(data.size(), pageSize)); // 切割行
                // 多线程
                Callable<Boolean> callable = new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        for (int j = 0; j < batchs.size(); j++) {
                            Row row = sheet.createRow(j);
                            String[] strs = String.valueOf(batchs.get(j)).split(",");
                            for (int k = 0; k < strs.length; k++) {
                                createCell(row, k, strs[k]);
                            }
                        }
                        return true;
                    }
                };
                futures.add(ExecutorPool.SERVICE.submit(callable));
            }
            for (Future<Boolean> future : futures) {
                future.get();
            }

            // 导出文件
            File file = new File(target);
            out = new FileOutputStream(file);
            workbook.write(out);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioException) {
                }
            }
            workbook.dispose();
            data.clear();
        }
    }

    /**
     * 写入行标题
     *
     * @param sheet
     * @param rowNum 第几行的行号
     * @param values key:第几列的列号  value:值
     */
    public void setSheetHead(Sheet sheet, int rowNum, Map<Integer, String> values) {
        Row row = sheet.createRow(rowNum);
        for (Integer cellNum : values.keySet()) {
            Cell cell = row.createCell(cellNum);
            String value = values.get(cellNum);
            cell.setCellValue(value);
        }
    }

    /**
     * 写入单元格
     *
     * @param row
     * @param cellNum 第几列的列号
     * @param value   值
     */
    public void createCell(Row row, int cellNum, String value) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value);
    }

}
