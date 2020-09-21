package com.jsf.utils.excel;

import com.fasterxml.jackson.databind.util.ClassUtil;
import com.jsf.utils.annotation.excel.Fields;
import com.jsf.utils.annotation.excel.TypeValue;
import com.jsf.utils.date.DateUtil;
import com.jsf.utils.exception.SysException;
import com.jsf.utils.excel.render.AbstractCellRender;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2020-09-17
 * Time: 14:26
 */
public class BaseExcel {

    private static final String NULL_VALUE = "";

    /**
     * 写入单元格
     *
     * @param row
     * @param cell
     * @param field
     * @param val
     */
    public static void writeCell(Row row, int cell, Fields field, String val) {
        if ("null".equals(val) || "".equals(val)) {
            row.createCell(cell).setCellValue(NULL_VALUE); // 默认值
        } else {
            // 转换器(优先)
            Class<? extends AbstractCellRender> render = field.render();
            if (render != AbstractCellRender.None.class) {
                AbstractCellRender r = ClassUtil.createInstance(render, true);
                String result = r.render(val);
                row.createCell(cell).setCellValue(result);
            } else {
                switch (field.type()) {
                    // 其他类型暂定
                    case ENUM: // 枚举
                        TypeValue[] values = field.typeValues();
                        for (int j = 0; j < values.length; j++) {
                            if (values[j].value().equals(val)) {
                                row.createCell(cell).setCellValue(values[j].name());
                                break;
                            }
                        }
                        break;
                    case BOOLEAN:
                        if ("1".equals(val)) {
                            row.createCell(cell).setCellValue("是");
                        } else if ("0".equals(val)) {
                            row.createCell(cell).setCellValue("否");
                        } else {
                            row.createCell(cell).setCellValue(NULL_VALUE);
                        }
                        break;
                    default: // 字符
                        row.createCell(cell).setCellValue(val);
                        break;
                }
            }
        }
    }

    /**
     * 写入CSV单元格
     *
     * @param sb
     * @param cell
     * @param field
     * @param val
     */
    public static void writeCSVCell(StringBuilder sb, int cell, Fields field, String val) {
        if ("null".equals(val) || "".equals(val)) {
            sb.append(NULL_VALUE); // 默认值
        } else {
            // 转换器(优先)
            Class<? extends AbstractCellRender> render = field.render();
            if (render != AbstractCellRender.None.class) {
                AbstractCellRender r = ClassUtil.createInstance(render, true);
                String result = r.render(val);
                sb.append(result);
            } else {
                switch (field.type()) {
                    // 其他类型暂定
                    case ENUM: // 枚举
                        TypeValue[] values = field.typeValues();
                        for (int j = 0; j < values.length; j++) {
                            if (values[j].value().equals(val)) {
                                sb.append(values[j].name());
                                break;
                            }
                        }
                        break;
                    case BOOLEAN:
                        if ("1".equals(val)) {
                            sb.append("是");
                        } else if ("0".equals(val)) {
                            sb.append("否");
                        } else {
                            sb.append("NULL");
                        }
                        break;
                    default: // 字符
                        sb.append(val);
                        break;
                }
            }
        }
        // 换行前需要删除最后一个','
        // sb.deleteCharAt(sb.length()-1);
        sb.append(',');
    }

    /**
     * 根据Cell类型返回数据
     *
     * @param cell
     * @return obj
     */
    public static Object getCellFormatValue(XSSFCell cell) {
        if (cell == null)
            return null;
        try {
            // 判断当前Cell的Type
            switch (cell.getCellTypeEnum()) {
                case FORMULA:
                case NUMERIC:
                    try {
                        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            return date;
                        } else {
                            return cell.getNumericCellValue();
                        }
                    } catch (Exception e) {
                        return String.valueOf(cell.getRichStringCellValue());
                    }
                case BOOLEAN:
                    return cell.getBooleanCellValue();
                case BLANK:
                    return null;
                case STRING:
                default:
                    return String.valueOf(cell.getRichStringCellValue());
            }
        } catch (Exception e) {
            throw new SysException(e.getMessage(), e);
        }
    }

    /**
     * 写入类属性
     *
     * @param field
     * @param value
     * @param entity
     * @param j
     * @param k
     * @param <T>
     */
    public static <T> void setEntityValue(String field, Object value, T entity, int j, int k) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(field, entity.getClass());
            String fieldType = pd.getPropertyType().getSimpleName();
            Object finalVal = value;
            // 枚举可能出现的情况
            switch (fieldType) { // 判断实体类字段类型，再根据其转换
                case "Integer":
                    if (value instanceof Double) {
                        finalVal = new Double(String.valueOf(value)).intValue();
                    } else if (value instanceof String) {
                        finalVal = Integer.valueOf(String.valueOf(value));
                    }
                    break;
                case "Date":
                    if (value instanceof String) {
                        finalVal = DateUtil.strToDateDay(String.valueOf(value).replaceAll("[年月]", "-").replace("日", "").replace("/", "-").trim());
                    }
                    break;
                case "String":
                    if (value instanceof Double || value instanceof Integer) {
                        finalVal = String.valueOf(value);
                    } else if (value instanceof Date) {
                        finalVal = DateUtil.dateToStr((Date) value);
                    }
                    break;
            }

            Method method = pd.getWriteMethod();
            method.invoke(entity, finalVal);
        } catch (Exception e) {
            throw new RuntimeException(String.format("导入出错，字段：%s，值：%s，位置：第%d行第%d列", field, value, j + 1, k + 1), e);
        }
    }

    /**
     * 以下载方式需要将文件名编码
     *
     * @param fileNames
     * @param request
     * @return
     */
    public static String encodeFileName(String fileNames, HttpServletRequest request) {
        String codedFilename = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            if (null != agent && -1 != agent.indexOf("MSIE") || null != agent
                    && -1 != agent.indexOf("Trident") || null != agent && -1 != agent.indexOf("Edge")) {// ie浏览器及Edge浏览器
                String name = java.net.URLEncoder.encode(fileNames, "UTF-8");
                codedFilename = name;
            } else if (null != agent && -1 != agent.indexOf("Mozilla")) {// 火狐,Chrome等浏览器
                codedFilename = new String(fileNames.getBytes("UTF-8"), "iso-8859-1");
            }
        } catch (Exception e) {
            throw new SysException(e.getMessage(), e);
        }
        return codedFilename;
    }
}
