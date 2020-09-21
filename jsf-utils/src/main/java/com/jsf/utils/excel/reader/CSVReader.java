package com.jsf.utils.excel.reader;

import com.jsf.utils.annotation.excel.Fields;
import com.jsf.utils.excel.BaseExcel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 读取CSV
 * User: xujunfei
 * Date: 2020-09-15
 * Time: 16:22
 */
public class CSVReader {

    /**
     * 读取CSV到List<String[]>
     * 注意，如果有标题行，需过滤请从第2行开始读
     *
     * @param is
     * @return
     */
    public static List<String[]> read(InputStream is) {
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(is));
            String line = null;

            List<String[]> list = new ArrayList<>();
            while ((line = buffer.readLine()) != null) {
                if ("".equals(line)) {
                    continue;
                }
                String[] strs = line.split(",");
                list.add(strs);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
                is.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * 读取CSV到实体类，使用注解
     * 注意，必须包含列名
     *
     * @param is
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> List<T> read(InputStream is, Class<T> clz) {
        List<T> list = new ArrayList<>();
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(is));
            Field[] fs = clz.getDeclaredFields(); // 所有字段
            // 读取第一行(标题)
            String header = buffer.readLine();
            String[] headers = header.split(",");
            int colNum = headers.length; // 列数
            int num = 1; // 数据行号(用于报错提示)

            String line = null;
            while ((line = buffer.readLine()) != null) {
                if ("".equals(line)) {
                    continue;
                }
                String[] strs = line.split(",");
                T entity = clz.newInstance(); // 实例化实体
                int k = 0;
                while (k < colNum) { // 读列
                    String name = headers[k];
                    for (int i = 0; i < fs.length; i++) {
                        Fields field = fs[i].getAnnotation(Fields.class); // 获取Fields注解信息
                        if (field == null) {
                            continue;
                        }
                        String colName = field.value(); // 指定对应列名
                        if (colName.equals(name)) {
                            BaseExcel.setEntityValue(fs[i].getName(), strs[k], entity, num, k);
                        }
                    }
                    k++;
                }
                list.add(entity);
                num++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
                is.close();
            } catch (IOException ex) {
            }
        }
        return list;
    }

}
