package com.jsf.controller.view;

import com.jsf.utils.annotation.excel.Excel;
import com.jsf.utils.annotation.excel.Fields;
import com.jsf.utils.excel.BaseExcel;
import org.joda.time.DateTime;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2020-09-17
 * Time: 18:10
 */
public class ViewCSV<T> extends AbstractView {

    // 是否压缩(CSV文本压缩率很高)
    private boolean zip = false;

    public ViewCSV() {
    }

    public ViewCSV(boolean zip) {
        this.zip = zip;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<T> list = (List<T>) map.get("list");
        if (list.isEmpty()) {
            return;
        }

        T t = list.get(0);
        Excel excel = t.getClass().getAnnotation(Excel.class);
        if (excel == null || excel.name() == null) {
            throw new RuntimeException("请指定表名");
        }

        // 表名
        String filename = new StringBuilder(excel.name())
                .append(new DateTime(System.currentTimeMillis()).toString("yyyy-MM-dd-HH-mm-ss")).toString();
        // 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开
        response.setContentType("APPLICATION/OCTET-STREAM");

        StringBuilder sb = new StringBuilder();

        Field[] fs = t.getClass().getDeclaredFields(); // 所有字段
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
                Method getMethod = t.getClass().getMethod(getMethodName, new Class[]{});
                Object obj = getMethod.invoke(model, new Object[]{}); // 执行get方法
                // 写入单元格
                BaseExcel.writeCSVCell(sb, cell, field, obj);
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append('\n');
        }

        OutputStream os = response.getOutputStream();
        if (zip) {
            response.setHeader("Content-Disposition", "attachment; filename=" + BaseExcel.encodeFileName(filename + ".zip", request));
            ZipOutputStream zos = null;
            try {
                zos = new ZipOutputStream(response.getOutputStream());
                zos.putNextEntry(new ZipEntry(filename + ".csv"));
                zos.write(sb.toString().getBytes("UTF-8"));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                if (zos != null) {
                    zos.closeEntry();
                    zos.close();
                }
            }
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=" + BaseExcel.encodeFileName(filename + ".csv", request));
            os.write(sb.toString().getBytes("UTF-8"));
        }
    }

}
