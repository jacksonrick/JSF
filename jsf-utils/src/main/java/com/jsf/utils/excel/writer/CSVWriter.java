package com.jsf.utils.excel.writer;

import com.jsf.utils.annotation.excel.Excel;
import com.jsf.utils.annotation.excel.Fields;
import com.jsf.utils.excel.BaseExcel;
import org.joda.time.DateTime;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * Description: 导出到CSV文件
 * <p>1、导出百万级数据容易OOM、CPU爆满，建议分页</p>
 * <p>2、大量数据时，反射会有严重性能问题，建议去除反射部分</p>
 * User: xujunfei
 * Date: 2020-09-15
 * Time: 16:22
 */
public class CSVWriter {

    StringBuilder sb = new StringBuilder();
    String filename = "temp";

    /**
     * 写入
     *
     * @param list
     * @param clz
     * @param <T>
     * @throws Exception
     */
    public <T> void write(List<T> list, Class<T> clz) throws Exception {
        Excel excel = clz.getAnnotation(Excel.class);
        if (excel == null || excel.name() == null) {
            throw new RuntimeException("请指定表名");
        }
        // 表名
        filename = new StringBuilder(excel.name())
                .append(new DateTime(System.currentTimeMillis()).toString("yyyy-MM-dd-HH-mm-ss"))
                .toString();

        Field[] fs = clz.getDeclaredFields(); // 所有字段
        int cell = 0; // 列序号，从0开始
        for (int i = 0; i < fs.length; i++) {
            Fields field = fs[i].getAnnotation(Fields.class); // 获取Fields注解信息
            if (field != null) {
                sb.append(field.value()).append(',');
                cell++;
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append('\n');

        for (T model : list) {
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
                BaseExcel.writeCSVCell(sb, cell, field, val);
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append('\n');
        }
    }

    /**
     * 生成csv文件
     *
     * @param output /data/output
     * @throws IOException
     */
    public void toFile(String output) {
        if (!output.endsWith("/")) {
            output = output + "/";
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(output + filename + ".csv");
            fos.write(sb.toString().getBytes("UTF-8"));
            fos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            sb = null;
        }
    }

    /**
     * 生成zip压缩文件
     *
     * @param output /data/output
     * @throws IOException
     */
    public void toZip(String output) {
        if (!output.endsWith("/")) {
            output = output + "/";
        }
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(output + filename + ".zip"));
            zos.putNextEntry(new ZipEntry(filename + ".csv"));
            zos.write(sb.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (zos != null) {
                try {
                    zos.closeEntry();
                    zos.close();
                } catch (IOException e) {
                }
            }
            sb = null;
        }
    }

}
