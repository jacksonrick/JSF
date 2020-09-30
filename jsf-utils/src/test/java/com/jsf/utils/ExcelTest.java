package com.jsf.utils;

import com.jsf.utils.excel.CustList;
import com.jsf.utils.excel.UserTest;
import com.jsf.utils.excel.reader.CSVReader;
import com.jsf.utils.excel.reader.ExcelAnnoReader;
import com.jsf.utils.excel.reader.ExcelJsonCfgReader;
import com.jsf.utils.excel.reader.ExcelToCSVReader;
import com.jsf.utils.excel.writer.CSVWriter;
import com.jsf.utils.excel.writer.ExcelAnnoWriter;
import com.jsf.utils.excel.writer.ExcelWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: Excel 测试类
 * User: xujunfei
 * Date: 2019-03-12
 * Time: 16:36
 */
public class ExcelTest {

    public static void main(String[] args) throws Exception {
        testSXSSFThread();
    }


    /**
     * csv写入
     * 1000000条数据测试
     * testCSVWriter()  3009ms
     * testSXSSAuto()   16045ms
     *
     * @throws Exception
     */
    public static void testCSVWriter() throws Exception {
        long start = System.currentTimeMillis();
        List<UserTest> users = getTestUsers(1000000);
        CSVWriter writer = new CSVWriter();
        writer.write(users, UserTest.class);
        writer.toFile("/Users/xujunfei/Downloads");
        System.out.println("time: " + (System.currentTimeMillis() - start));
    }

    /**
     * 多线程写入
     *
     * @throws Exception
     */
    public static void testCSVWriterThread() throws Exception {
        long start = System.currentTimeMillis();

        System.out.println("time: " + (System.currentTimeMillis() - start));
    }

    /**
     * csv读取
     * 1000000条数据测试
     * testReaderAuto() 3min
     * testCSVReader()  15123ms
     */
    public static void testCSVReader() throws Exception {
        long start = System.currentTimeMillis();
        FileInputStream fis = new FileInputStream(new File("/Users/xujunfei/Downloads/111.csv"));
        List<UserTest> list = CSVReader.read(fis, UserTest.class);
        System.out.println("size: " + list.size());
        //for (UserTest user : list) {
        //    System.out.println(user);
        //}

        //List<String> list = CSVReader.read(fis);
        //System.out.println("size: " + list.size());
        //for (String[] strings : list) {
        //    System.out.println(Arrays.toString(strings));
        //}
        System.out.println("time: " + (System.currentTimeMillis() - start));
    }

    /**
     * 转换为CSV格式读取
     *
     * @throws Exception
     */
    public static void testCSV() throws Exception {
        long start = System.currentTimeMillis();

        File file = new File("/Users/xujunfei/Downloads/test.xlsx");
        InputStream is = new FileInputStream(file);
        ExcelToCSVReader xlsx2csv = new ExcelToCSVReader(true);
        List<String[]> datas = xlsx2csv.process(is);

        long end = System.currentTimeMillis();
        System.out.println("读取时间: " + (end - start) / 1000 + "s");
        System.out.println("总条数：" + datas.size());
        System.out.println("header: " + xlsx2csv.headers);

        // 模拟插入数据库
        int batch = 50; // 一次插入数量
        int total = datas.size();
        int times = total % batch > 0 ? total / batch + 1 : total / batch; // 次数
        for (int i = 0; i < times; i++) {
            List<String[]> batchs = datas.subList(0, Math.min(datas.size(), batch));
            // 数据处理...
            // xxxService.insertBatch(batchs);
            for (int j = 0; j < batchs.size(); j++) {
                System.out.println(Arrays.toString(batchs.get(j)));
            }
            batchs.clear();
        }
    }

    /**
     * 注解导入
     *
     * @throws Exception
     */
    public static void testReaderAuto() throws Exception {
        long start = System.currentTimeMillis();
        File file = new File("/Users/xujunfei/Downloads/test.xlsx");
        InputStream is = new FileInputStream(file);
        ExcelAnnoReader reader = new ExcelAnnoReader();
        List<UserTest> list = reader.read(is, UserTest.class);
        System.out.println(list.size());
        System.out.println("time: " + (System.currentTimeMillis() - start));
    }

    /**
     * Excel写入
     * 1000000测试数据
     * 普通方式  2000刷新   18914
     * 20000刷新  17209
     * 50000刷新  16954
     * 100000刷新 19367
     *
     * @throws Exception
     */
    public static void testSXSSF() throws Exception {
        long start = System.currentTimeMillis();

        // 测试数据
        int total = 1000000;
        List<String> tests = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            tests.add("A,B,C,D,E,F,G,H,I,J,K,L,M,N");
        }

        ExcelWriter writer = new ExcelWriter(50000);
        writer.writeSplit(tests, "/Users/xujunfei/Downloads/output.xlsx");

        System.out.println("time: " + (System.currentTimeMillis() - start));
    }

    /**
     * 多线程写入
     * 1000000测试数据
     * 5页 18132
     * 10页 19026
     *
     * @throws Exception
     */
    public static void testSXSSFThread() throws Exception {
        long start = System.currentTimeMillis();

        // 测试数据
        int total = 1000000;
        List<String> tests = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            tests.add("A,B,C,D,E,F,G,H,I,J,K,L,M,N");
        }

        ExcelWriter writer = new ExcelWriter(50000);
        writer.writeSplitMutilThread(tests, 100000, "/Users/xujunfei/Downloads/output.xlsx");

        System.out.println("time: " + (System.currentTimeMillis() - start));
    }

    /**
     * 注解方式，大批量数据分页写入
     *
     * @throws Exception
     */
    public static void testSXSSAuto() throws Exception {
        long start = System.currentTimeMillis();
        // 测试数据
        int total = 1000000;
        int pagesize = 10000;
        List<UserTest> tests = getTestUsers(total);

        ExcelAnnoWriter<UserTest> writer = new ExcelAnnoWriter<>(UserTest.class);
        // 次数
        int times = total % pagesize > 0 ? total / pagesize + 1 : total / pagesize;
        for (int i = 0; i < times; i++) {
            List<UserTest> fd = tests.subList(0, pagesize);
            writer.writePage(fd);
            fd.clear();
        }
        writer.export("/Users/xujunfei/Downloads/");

        System.out.println("time: " + (System.currentTimeMillis() - start));
    }

    /**
     * @throws Exception
     */
    public static void testSXSSAuto2() throws Exception {
        long start = System.currentTimeMillis();
        // 测试数据
        List<UserTest> tests = getTestUsers(1000);

        ExcelAnnoWriter<UserTest> writer = new ExcelAnnoWriter<>(UserTest.class);
        String path = writer.write(tests, "/Users/xujunfei/Downloads/");
        System.out.println(path);

        System.out.println("time: " + (System.currentTimeMillis() - start));
    }

    /**
     * JSON配置字段 写入
     */
    public static void testReaderConfig() {
        File file = new File("/Users/xujunfei/Downloads/test.xlsx");
        ExcelJsonCfgReader reader = new ExcelJsonCfgReader();
        try {
            String jsonConfig = getConfig();
            List<CustList> list = reader.read(new FileInputStream(file), jsonConfig, CustList.class);
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getConfig() {
        String jsonConfig = "[{\n" +
                "\t\"name\": \"手机号\",\n" +
                "\t\"field\": \"phone\"\n" +
                "}, {\n" +
                "\t\"name\": \"姓名\",\n" +
                "\t\"field\": \"custname\"\n" +
                "}, {\n" +
                "\t\"name\": \"邮箱\",\n" +
                "\t\"field\": \"email\"\n" +
                "}, {\n" +
                "\t\"name\": \"年龄\",\n" +
                "\t\"field\": \"age\"\n" +
                "}]";
        return jsonConfig;
    }

    private static List<UserTest> getTestUsers(int total) {
        List<UserTest> tests = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            UserTest user = new UserTest(i + 1);
            user.setA("aa");
            user.setB("bb");
            user.setC("cc");
            user.setD("dd");
            user.setE("ee");
            user.setF("ff");
            user.setG((i % 2) == 0 ? "0" : "1");
            tests.add(user);
        }
        return tests;
    }
}
