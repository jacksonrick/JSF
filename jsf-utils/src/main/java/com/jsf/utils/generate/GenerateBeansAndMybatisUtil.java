package com.jsf.utils.generate;

import com.jsf.utils.string.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * 自动生成MyBatis的实体类、实体映射XML接口、Mapper SQL、Service、Controller、HTML<br>
 * 对于mybatis xml还需进一步修改使用，实体类可直接使用<br>
 * 适用数据库：MySQL、Postgres
 *
 * @version 4.3
 */
public class GenerateBeansAndMybatisUtil {

    private static final Logger log = LoggerFactory.getLogger(GenerateBeansAndMybatisUtil.class);

    /***************************************
     * 注意数据库表名称，如：t_user、t_user_info
     * 类型如：int、tinyint、decimal、text、char、varchar、datetime
     *************************************/

    static final Map<String, String> TYPES = new HashMap<>();

    static {
        TYPES.put("char", "String");
        TYPES.put("varchar", "String");
        TYPES.put("text", "String");
        TYPES.put("jsonb", "String");
        TYPES.put("date", "Date");
        TYPES.put("datetime", "Date");
        TYPES.put("timestamp", "Date");
        TYPES.put("tinyint", "Integer");
        TYPES.put("int", "Integer");
        TYPES.put("int2", "Integer");
        TYPES.put("int4", "Integer");
        TYPES.put("int8", "Long");
        TYPES.put("bigint", "Long");
        TYPES.put("decimal", "Double");
        TYPES.put("double", "Double");
        TYPES.put("numeric", "Double");
        TYPES.put("bit", "Boolean");
        TYPES.put("bool", "Boolean");
        TYPES.put("blob", "byte[]");
    }

    // 输出路径
    private static String bean_path = null;
    private static String mapper_path = null;
    private static String xml_path = null;
    private static String html_path = null;
    private static String service_path = null;
    private static String controller_path = null;

    // model/mapper包
    private final static String bean_package = "com.jsf.database.model";
    private final static String mapper_package = "com.jsf.database.mapper";
    // 基类
    private final static String base_vo = "com.jsf.database.model.custom.BaseVo;";
    private final static String base_vo2 = "com.jsf.database.model.custom.BaseVo";

    private Integer dbType = 1; // 1-mysql 2-postgres
    private Boolean removePrefix;
    private String schema = null;
    private String tableName = null;
    private String tableComment = null;
    private String beanName = null;
    private Boolean idInt = true;
    private String mapperName = null;
    private Connection conn = null;

    private String author = "";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 aaa HH:mm:ss");
    private int maxCommentLength = 5;

    /**
     * 初始化连接
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     * @author rick
     * @date 2016年8月10日 上午8:54:10
     */
    private void init(GenInfo info) throws Exception {
        bean_path = info.getGlobalPath() + "/database/model";
        mapper_path = info.getGlobalPath() + "/database/mapper";
        xml_path = info.getGlobalPath() + "/mapper";
        html_path = info.getGlobalPath() + "/templates";
        service_path = info.getGlobalPath() + "/service";
        controller_path = info.getGlobalPath() + "/controller";

        author = info.getAuthor();
        removePrefix = info.getRemovePrefix();

        if (info.getDriver().contains("mysql")) {
            log.info("数据库类型：mysql");
            dbType = 1;
        } else if (info.getDriver().contains("postgresql")) {
            log.info("数据库类型：postgresql");
            dbType = 2;
            schema = info.getSchema();
            if (schema == null) {
                throw new RuntimeException("postgres必须指定schema");
            }
        } else {
            throw new RuntimeException("不支持的数据库");
        }

        log.info("清理文件......");
        deleteFile(new File(info.getGlobalPath()));

        log.info("初始化数据库连接......");
        Class.forName(info.getDriver());
        conn = DriverManager.getConnection(info.getDbUrl(), info.getUsername(), info.getPassword());
    }

    /**
     * 获取所有的表
     *
     * @return
     * @throws SQLException
     */
    private List<String> getTables() throws SQLException {
        String sql = "";
        if (dbType == 1) {
            sql = "SHOW TABLES";
        } else if (dbType == 2) {
            sql = "SELECT tablename FROM pg_tables WHERE schemaname = '" + schema + "'";
        }
        List<String> tables = new ArrayList<String>();
        PreparedStatement pstate = conn.prepareStatement(sql);
        ResultSet results = pstate.executeQuery();
        while (results.next()) {
            String tableName = results.getString(1);
            tables.add(tableName);
        }
        return tables;
    }

    /**
     * 获取所有的数据库表注释
     *
     * @return
     * @throws SQLException
     */
    private Map<String, String> getTableComment() throws SQLException {
        String sql = "";
        if (dbType == 1) {
            sql = "SHOW TABLE STATUS";
        } else if (dbType == 2) {
            sql = "SELECT C.relname AS NAME, obj_description ( C.oid ) AS COMMENT \n" +
                    "FROM pg_class C, pg_tables T WHERE T.schemaname = '" + schema + "' AND C.relname = T.tablename AND relkind = 'r'";
        }
        Map<String, String> maps = new HashMap<String, String>();
        PreparedStatement pstate = conn.prepareStatement(sql);
        ResultSet results = pstate.executeQuery();
        while (results.next()) {
            String tableName = results.getString("NAME");
            String comment = results.getString("COMMENT");
            maps.put(tableName, comment);
        }
        return maps;
    }


    /**
     * 处理字段类型
     *
     * @param type
     * @return
     * @author rick
     * @date 2016年8月10日 上午8:54:55
     */
    private String processType(String type) {
        if (type.contains("(")) {
            type = type.substring(0, type.indexOf('('));
        }
        String s = TYPES.get(type);
        if (s == null) {
            return "String";
        }
        return s;
    }

    /**
     * 处理表
     *
     * @param table
     * @author rick
     * @date 2016年8月10日 上午8:53:56
     */
    private void processTable(String table) {
        StringBuffer sb = new StringBuffer(table.length());
        String tableNew = table.toLowerCase();
        String[] tables = tableNew.split("_"); // 去除下划线之前的内容
        if (tables.length > 1) {
            String temp = null;
            int start = 0;
            if (removePrefix) {
                start = 1;
            }
            for (int i = start; i < tables.length; i++) {
                temp = tables[i].trim();
                sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
            }
        } else {
            sb.append(tables[0].substring(0, 1).toUpperCase()).append(tables[0].substring(1));
        }
        beanName = sb.toString();
        mapperName = beanName + "Mapper";
    }

    /**
     * 处理字段
     * user_id -> userId
     *
     * @param field
     * @return
     * @author rick
     * @date 2016年8月10日 上午8:55:21
     */
    private String processField(String field) {
        StringBuffer sb = new StringBuffer(field.length());
        field = field.toLowerCase();
        String[] fields = field.split("_");
        String temp = null;
        sb.append(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            temp = fields[i].trim();
            sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
        }
        return sb.toString();
    }

    /**
     * 获取字段备注名
     *
     * @param field
     * @return
     */
    private String processFieldComment(String field) {
        String label = field;
        if (StringUtil.isBlank(label)) {
            label = field;
        } else if (label.length() > maxCommentLength) {
            label = label.substring(0, maxCommentLength);
        }
        return label;
    }

    /**
     * 处理文件名
     * t_user_info -> user_info
     *
     * @param table
     * @return
     * @author rick
     * @date 2016年8月10日 上午8:55:21
     */
    private String processFile(String table) {
        if (removePrefix) {
            return table.substring(table.indexOf("_") + 1, table.length());
        }
        return table;
    }

    /**
     * 将实体类名首字母改为小写
     * User -> user
     *
     * @param beanName
     * @return
     */
    private String processBeanName(String beanName) {
        return beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
    }

    /**
     * 构建类上面的注释
     *
     * @param bw
     * @param text
     * @return
     * @throws IOException
     */
    private BufferedWriter buildClassComment(BufferedWriter bw, String text) throws IOException {
        bw.newLine();
        bw.newLine();
        bw.write("/**");
        bw.newLine();
        bw.write(" * " + text);
        bw.newLine();
        bw.write(" *");
        bw.newLine();
        bw.write(" * @date " + simpleDateFormat.format(new Date()));
        bw.newLine();
        bw.write(" * @author " + author);
        bw.newLine();
        bw.write(" */");
        return bw;
    }

    /**
     * 构建方法上面的注释
     *
     * @param bw
     * @param text
     * @return
     * @throws IOException
     */
    private BufferedWriter buildMethodComment(BufferedWriter bw, String text) throws IOException {
        bw.newLine();
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t * " + text);
        bw.newLine();
        bw.write("\t */");
        return bw;
    }

    /**
     * @param size
     * @param types
     * @return
     */
    private Boolean checkDate(int size, List<String> types) {
        for (int i = 0; i < size; i++) {
            if ("Date".equals(processType(types.get(i)))) {
                return true;
            }
        }
        return false;
    }


    /**
     * 生成实体类
     *
     * @param columns
     * @param types
     * @param comments
     * @throws IOException
     */
    private void buildEntityBean(List<String> columns, List<String> types, List<String> comments)
            throws IOException {
        File folder = new File(bean_path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        int size = columns.size();

        File beanFile = new File(bean_path, beanName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(beanFile)));
        bw.write("package " + bean_package + ";\n\n");
        // 导入包
        bw.write("import java.io.Serializable;\n");
        bw.write("import " + base_vo + "\n"); // 基类
        bw.write("import com.jsf.utils.annotation.excel.Excel;\n");
        bw.write("import com.jsf.utils.annotation.excel.Fields;\n");
        if (checkDate(size, types)) {
            bw.write("\nimport java.util.Date;");
        }
        bw = buildClassComment(bw, tableComment);
        bw.newLine();
        // 类
        bw.write("@Excel(name = \"" + tableComment + "导出数据\")\n");
        bw.write("public class " + beanName + " extends BaseVo implements Serializable {\n\n");
        bw.write("\tprivate static final long serialVersionUID = 1L;\n\n");

        for (int i = 0; i < size; i++) { // 生成字段
            String field = processField(columns.get(i));
            String type = processType(types.get(i));
            String comment = (StringUtil.isBlank(comments.get(i)) ? field : comments.get(i));
            bw.write("\t/** " + comment + " */\n");
            if ("id".equals(field)) {
                if ("Integer".equals(type)) {
                    idInt = true;
                } else {
                    idInt = false;
                    bw.write("\t//@JsonSerialize(using = ToStringSerializer.class)\n");
                }
            }
            bw.write("\t@Fields(value = \"" + comment + "\")\n");
            bw.write("\tprivate " + type + " " + field + ";\n\n");
        }
        //生成构造方法
        bw.write("\tpublic " + beanName + "() {\n");
        bw.write("\t}\n\n");
        bw.write("\tpublic " + beanName + "(" + (idInt ? "Integer" : "Long") + " id) {\n");
        bw.write("\t\tsuper();\n");
        bw.write("\t\tthis.id = id;\n");
        bw.write("\t}\n");

        // 生成get和 set方法
        String tempField = null;
        String _tempField = null;
        String tempType = null;
        for (int i = 0; i < size; i++) {
            tempType = processType(types.get(i));
            _tempField = processField(columns.get(i));
            tempField = _tempField.substring(0, 1).toUpperCase() + _tempField.substring(1);
            bw.newLine();
            bw.write("\tpublic " + tempType + " get" + tempField + "() {\n");
            bw.write("\t\treturn this." + _tempField + ";\n");
            bw.write("\t}\n\n");
            bw.write("\tpublic void set" + tempField + "(" + tempType + " " + _tempField + ") {\n");
            bw.write("\t\tthis." + _tempField + " = " + _tempField + ";\n");
            bw.write("\t}\n");
        }
        bw.newLine();

        // 生成toString()
        bw.write("\t@Override\n");
        bw.write("\tpublic String toString() {\n");
        bw.write("\t\treturn \"" + beanName + "{\" +\n");
        for (int i = 0; i < size; i++) {
            String field = processField(columns.get(i));
            if (i == 0) {
                bw.write("\t\t\t\t\"" + field + "=\" + " + field + " +\n");
            } else {
                bw.write("\t\t\t\t\", " + field + "=\" + " + field + " +\n");
            }
        }
        bw.write("\t\t\t\t\"}\";\n");
        bw.write("\t}\n");
        bw.write("}\n");
        bw.flush();
        bw.close();
    }

    /**
     * 构建Mapper接口文件
     *
     * @throws IOException
     */
    private void buildMapper() throws IOException {
        File folder = new File(mapper_path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File mapperFile = new File(mapper_path, mapperName + ".java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile), "utf-8"));
        bw.write("package " + mapper_package + ";\n\n");
        bw.write("import java.util.List;\n\nimport " + bean_package + "." + beanName + ";\n");
        // 自定义查询封装类
        bw.write("import " + base_vo + "\n");
        bw.write("import org.apache.ibatis.annotations.Param;");
        bw = buildClassComment(bw, mapperName + " Interface");
        bw.write("\npublic interface " + mapperName + " {\n\n");

        // ----------定义Mapper中的方法Begin----------
        String type = idInt ? "Integer" : "Long";
        bw.write("\tList<" + beanName + "> findByCondition(BaseVo baseVo);\n\n");
        bw.write("\t" + beanName + " findById(" + type + " id);\n\n");
        bw.write("\t" + beanName + " findSimpleById(" + type + " id);\n\n");
        bw.write("\t" + "int insert(" + beanName + " bean);\n\n");
        bw.write("\t" + "int insertSelective(" + beanName + " bean);\n\n");
        bw.write("\t" + "int insertBatch(List<" + beanName + "> list);\n\n");
        bw.write("\t" + "int update(" + beanName + " bean);\n\n");
        bw.write("\t" + "int enable(" + type + " id);\n\n");
        bw.write("\t" + "int delete(" + type + " id);\n\n");
        bw.write("\t" + "int deleteBatch(@Param(\"ids\") " + type + "[] ids);\n\n");

        // ----------定义Mapper中的方法End----------
        bw.write("}");
        bw.flush();
        bw.close();
    }

    /**
     * 构建实体类映射XML文件
     *
     * @param columns
     * @param types
     * @param comments
     * @throws IOException
     */
    private void buildMapperXml(List<String> columns, List<String> types, List<String> comments) throws IOException {
        File folder = new File(xml_path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File mapperXmlFile = new File(xml_path, mapperName + ".xml");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperXmlFile)));
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \n");
        bw.write("    \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n\n");
        // 命名空间
        bw.write("<mapper namespace=\"" + mapper_package + "." + mapperName + "\">\n\n");

        // 下面开始写SqlMapper中的方法
        // 构建通用方法，其它可根据需要新增或改写
        buildSQL(bw, columns, types);

        bw.write("</mapper>");
        bw.flush();
        bw.close();
    }

    private void buildSQL(BufferedWriter bw, List<String> columns, List<String> types) throws IOException {
        int size = columns.size();
        String type = idInt ? "int" : "long";

        bw.write("\t<sql id=\"baseCondition\">\n");
        bw.write("\t\t<where>\n");
        bw.write("\t\t\t<include refid=\"COMMON.DATE\"><property name=\"column\" value=\"create_time\"/></include>\n");
        for (int i = 1; i < size; i++) {
            String tempField = processField(columns.get(i));
            if ("String".equals(processType(types.get(i)))) {
                bw.write("\t\t\t<if test=\"" + tempField + " != null and " + tempField + " != ''\">");
            } else {
                bw.write("\t\t\t<if test=\"" + tempField + " != null\">");
            }
            bw.write("AND " + columns.get(i) + " = #{" + tempField + "}");
            bw.write("</if>\n");
        }
        bw.write("\t\t</where>\n");
        bw.write("\t</sql>\n\n");

        bw.write("\t<sql id=\"simpleColumn\">\n");
        bw.write("\t\tid\n");
        bw.write("\t</sql>\n\n");

        bw.write("\t<sql id=\"allColumn\">\n");
        bw.write("\t\t");
        String allColumn = "";
        for (int i = 0; i < size; i++) {
            allColumn += columns.get(i);
            if (i < size - 1) {
                allColumn += ",\n\t\t";
            }
        }
        bw.write(allColumn);
        bw.newLine();
        bw.write("\t</sql>\n\n");

        bw.write("\t<resultMap type=\"" + bean_package + "." + beanName + "\" id=\"baseResultMap\">\n");
        bw.write("\t\t<id property=\"id\" column=\"id\" />\n");
        for (int i = 0; i < size; i++) {
            if (!"id".equals(columns.get(i))) {
                bw.write("\t\t<result property=\"" + processField(columns.get(i)) + "\" column=\"" + columns.get(i) +
                        "\" />\n");
            }
        }
        bw.write("\t</resultMap>\n\n");

        // 条件查询
        bw.write("\t<select id=\"findByCondition\" resultMap=\"baseResultMap\" parameterType=\"" + base_vo2 + "\">\n");
        bw.write("\t\tSELECT <include refid=\"allColumn\"/> \n\t\tFROM " + tableName + "\n");
        bw.write("\t\t<include refid=\"baseCondition\"/>\n");
        bw.write("\t\t<include refid=\"COMMON.ORDER\"/>\n");
        bw.write("\t</select>\n\n");

        // 通过id查询
        bw.write("\t<select id=\"findById\" resultMap=\"baseResultMap\" parameterType=\"" + type + "\">\n");
        bw.write("\t\tSELECT <include refid=\"allColumn\"/> \n\t\tFROM " + tableName + "\n");
        bw.write("\t\tWHERE id = #{id}\n");
        bw.write("\t</select>\n\n");

        // 通过id查询-简单字段
        bw.write("\t<select id=\"findSimpleById\" resultMap=\"baseResultMap\" parameterType=\"" + type + "\">\n");
        bw.write("\t\tSELECT <include refid=\"simpleColumn\"/> \n\t\tFROM " + tableName + "\n");
        bw.write("\t\tWHERE id = #{id}\n");
        bw.write("\t</select>\n\n");

        // 插入方法
        bw.write("\t<!--这里id自增，时间自动生成-->\n");
        bw.write("\t<insert id=\"insert\" parameterType=\"" + bean_package + "." + beanName + "\">\n");
        bw.write("\t\tINSERT INTO " + tableName + " (\n");
        for (int i = 0; i < size; i++) {
            if ("id".equals(columns.get(i))) {
                continue;
            }
            if (i < size - 1) {
                bw.write("\t\t\t" + columns.get(i) + ",\n");
            } else {
                bw.write("\t\t\t" + columns.get(i) + "\n");
            }
        }
        bw.write("\t\t)\n");
        bw.write("\t\tVALUES (\n");
        for (int i = 0; i < size; i++) {
            if ("id".equals(columns.get(i))) {
                continue;
            }
            if (i < size - 1) {
                bw.write("\t\t\t#{" + processField(columns.get(i)) + "},\n");
            } else {
                bw.write("\t\t\t#{" + processField(columns.get(i)) + "}\n");
            }
        }
        bw.write("\t\t)\n");
        bw.write("\t</insert>\n\n");

        // 有选择性插入（非空）
        bw.write("\t<insert id=\"insertSelective\" parameterType=\"" + bean_package + "." + beanName + "\">\n");
        bw.write("\t\tINSERT INTO " + tableName + "\n");
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (int i = 0; i < size; i++) {
            if ("id".equals(columns.get(i))) {
                continue;
            }
            bw.write("\t\t\t<if test=\"" + processField(columns.get(i)) + " != null\">" + columns.get(i) + ",</if>\n");
        }
        bw.write("\t\t</trim>\n");
        bw.write("\t\t<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">\n");
        for (int i = 0; i < size; i++) {
            if ("id".equals(columns.get(i))) {
                continue;
            }
            bw.write("\t\t\t<if test=\"" + processField(columns.get(i)) + " != null\">#{" + processField(columns.get(i)) + "},</if>\n");
        }
        bw.write("\t\t</trim>\n");
        bw.write("\t</insert>\n\n");

        // 批量插入
        bw.write("\t<insert id=\"insertBatch\" parameterType=\"list\">\n");
        bw.write("\t\tINSERT INTO " + tableName + " (\n");
        for (int i = 0; i < size; i++) {
            if ("id".equals(columns.get(i))) {
                continue;
            }
            if (i < size - 1) {
                bw.write("\t\t\t" + columns.get(i) + ",\n");
            } else {
                bw.write("\t\t\t" + columns.get(i) + "\n");
            }
        }
        bw.write("\t\t)\n");
        bw.write("\t\tVALUES \n");
        bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">\n");
        bw.write("\t\t(\n");
        for (int i = 0; i < size; i++) {
            if ("id".equals(columns.get(i))) {
                continue;
            }
            if (i < size - 1) {
                bw.write("\t\t\t#{item." + processField(columns.get(i)) + "},\n");
            } else {
                bw.write("\t\t\t#{item." + processField(columns.get(i)) + "}\n");
            }
        }
        bw.write("\t\t)\n");
        bw.write("\t\t</foreach>\n");
        bw.write("\t</insert>\n\n");

        // 修改
        bw.write("\t<update id=\"update\" parameterType=\"" + bean_package + "." + beanName + "\">\n");
        bw.write("\t\tUPDATE " + tableName + "\n");
        bw.write("\t\t<set>\n");

        for (int i = 0; i < size; i++) {
            if ("id".equals(columns.get(i))) {
                continue;
            }
            String tempField = processField(columns.get(i));
            if ("String".equals(processType(types.get(i)))) {
                bw.write("\t\t\t<if test=\"" + tempField + " != null and " + tempField + " != ''\">");
            } else {
                bw.write("\t\t\t<if test=\"" + tempField + " != null\">");
            }
            bw.write("" + columns.get(i) + " = #{" + tempField + "},");
            bw.write("</if>\n");
        }
        bw.write("\t\t</set>\n");
        bw.write("\t\tWHERE " + columns.get(0) + " = #{" + processField(columns.get(0)) + "}\n");
        bw.write("\t</update>\n\n");

        // 禁用/启用
        bw.write("\t<update id=\"enable\" parameterType=\"" + type + "\">\n");
        bw.write("\t\tUPDATE " + tableName + "\n");
        bw.write("\t\tSET deleted = !deleted\n");
        bw.write("\t\tWHERE " + columns.get(0) + " = #{" + processField(columns.get(0)) + "}\n");
        bw.write("\t</update>\n\n");
        // 删除
        bw.write("\t<delete id=\"delete\" parameterType=\"" + type + "\">\n");
        bw.write("\t\tDELETE FROM " + tableName + "\n");
        bw.write("\t\tWHERE " + columns.get(0) + " = #{" + processField(columns.get(0)) + "}\n");
        bw.write("\t</delete>\n\n");
        // 批量删除
        bw.write("\t<delete id=\"deleteBatch\">\n");
        bw.write("\t\tDELETE FROM " + tableName + "\n");
        bw.write("\t\tWHERE id IN <foreach item=\"id\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\"> #{id} </foreach>\n");
        bw.write("\t</delete>\n\n");
    }

    /**
     * 构建Service
     *
     * @throws IOException
     */
    public void buildService() throws IOException {
        File folder = new File(service_path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File serviceFile = new File(service_path, beanName + "Service.java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serviceFile)));
        bw.write("package com.jsf.service;\n\n");
        bw.write("import javax.annotation.Resource;\n");
        bw.write("import java.util.List;\n\n");
        bw.write("import " + bean_package + "." + beanName + ";\n");
        bw.write("import " + mapper_package + "." + beanName + "Mapper;\n");
        bw.write("import com.jsf.utils.string.StringUtil;\n");
        bw.write("import org.springframework.stereotype.Service;\n");
        bw.write("import com.github.pagehelper.PageInfo;\n\n");

        String bean = processBeanName(beanName);
        String mapper = bean + "Mapper";
        String type = idInt ? "Integer" : "Long";

        bw.write("/**\n");
        bw.write(" * Created By AutoGenerate.\n");
        bw.write(" * Description: " + tableComment + " 服务类\n");
        bw.write(" * User: " + author + " \n");
        bw.write(" * Date: " + simpleDateFormat.format(new Date()) + "\n");
        bw.write(" */\n");

        bw.write("@Service\n");
        bw.write("public class " + beanName + "Service {\n\n");
        bw.write("\t@Resource\n");
        bw.write("\tprivate " + beanName + "Mapper " + mapper + ";\n\n");
        // 分页查询
        bw.write("\tpublic PageInfo findByPage(" + beanName + " condition) {\n");
        bw.write("\t\tif (StringUtil.isBlank(condition.getPageSort())) {\n");
        bw.write("\t\t\tcondition.setPageSort(\"id DESC\");\n");
        bw.write("\t\t}\n");
        bw.write("\t\tcondition.setPage(true);\n");
        bw.write("\t\tList<" + beanName + "> list = " + mapper + ".findByCondition(condition);\n");
        bw.write("\t\treturn new PageInfo(list);\n");
        bw.write("\t}\n\n");
        // 导出
        bw.write("\tpublic List<" + beanName + "> export(" + beanName + " condition) {\n");
        bw.write("\t\tif (StringUtil.isBlank(condition.getPageSort())) {\n");
        bw.write("\t\t\tcondition.setPageSort(\"id DESC\");\n");
        bw.write("\t\t}\n");
        bw.write("\t\treturn " + mapper + ".findByCondition(condition);\n");
        bw.write("\t}\n\n");
        // id查询
        bw.write("\tpublic " + beanName + " findById(" + type + " id) {\n");
        bw.write("\t\treturn " + mapper + ".findById(id);\n");
        bw.write("\t}\n\n");
        // 新增
        bw.write("\tpublic int insert(" + beanName + " " + bean + ") {\n");
        bw.write("\t\treturn " + mapper + ".insert(" + bean + ");\n");
        bw.write("\t}\n\n");
        // 更新
        bw.write("\tpublic int update(" + beanName + " " + bean + ") {\n");
        bw.write("\t\treturn " + mapper + ".update(" + bean + ");\n");
        bw.write("\t}\n\n");
        // 禁用/启用
        bw.write("\tpublic int enable(" + type + " id) {\n");
        bw.write("\t\treturn " + mapper + ".enable(id);\n");
        bw.write("\t}\n\n");
        // 删除
        bw.write("\tpublic int delete(" + type + " id) {\n");
        bw.write("\t\treturn " + mapper + ".delete(id);\n");
        bw.write("\t}\n\n");
        // 批量删除
        bw.write("\tpublic int deleteBatch(" + type + "[] ids) {\n");
        bw.write("\t\treturn " + mapper + ".deleteBatch(ids);\n");
        bw.write("\t}\n\n");

        bw.write("}");
        bw.flush();
        bw.close();
    }

    /**
     * 构建Controller
     *
     * @throws IOException
     */
    public void buildController() throws IOException {
        File folder = new File(controller_path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File controllerFile = new File(controller_path, beanName + "Controller.java");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(controllerFile)));
        bw.write("package com.jsf.controller;\n\n");
        bw.write("import org.springframework.stereotype.Controller;\n");
        bw.write("import org.springframework.ui.ModelMap;\n");
        bw.write("import org.springframework.web.bind.annotation.*;\n");
        bw.write("import com.github.pagehelper.PageInfo;\n");
        bw.write("import com.jsf.utils.string.StringUtil;\n");
        bw.write("import com.jsf.utils.annotation.AuthPassport;\n");
        bw.write("import com.jsf.utils.entity.ResMsg;\n");
        bw.write("import " + bean_package + "." + beanName + ";\n");
        bw.write("import com.jsf.service." + beanName + "Service;\n");
        bw.write("import com.jsf.controller.view.ViewExcel;\n");
        bw.write("import com.jsf.common.BaseController;\n\n");
        bw.write("import javax.annotation.Resource;\n" +
                "import java.util.HashMap;\n" +
                "import java.util.Map;\n\n");

        String bean = processBeanName(beanName);
        String service = bean + "Service";
        String type = idInt ? "Integer" : "Long";

        bw.write("/**\n");
        bw.write(" * Created By AutoGenerate.\n");
        bw.write(" * Description: " + tableComment + " 接口类\n");
        bw.write(" * User: " + author + " \n");
        bw.write(" * Date: " + simpleDateFormat.format(new Date()) + "\n");
        bw.write(" */\n");

        bw.write("@Controller\n");
        bw.write("@RequestMapping(\"/admin/" + bean + "\")\n");
        bw.write("public class " + beanName + "Controller extends BaseController {\n\n");
        bw.write("\t@Resource\n");
        bw.write("\tprivate " + beanName + "Service " + service + ";\n\n");
        // 页面
        bw.write("\t@GetMapping(\"/page\")\n");
        bw.write("\t@AuthPassport(right = false)\n");
        bw.write("\tpublic String page() {\n");
        bw.write("\t\treturn \"" + bean + "/" + processFile(tableName) + "_list\";\n");
        bw.write("\t}\n\n");
        // JSON数据
        bw.write("\t@RequestMapping(value = \"/list\", method = {RequestMethod.POST, RequestMethod.GET})\n");
        bw.write("\t@ResponseBody\n");
        bw.write("\t@AuthPassport\n");
        bw.write("\tpublic PageInfo list(" + beanName + " condition) {\n");
        bw.write("\t\treturn " + service + ".findByPage(condition);\n");
        bw.write("\t}\n\n");
        // 导出
        bw.write("\t@GetMapping(\"/export\")\n");
        bw.write("\t@AuthPassport\n");
        bw.write("\tpublic ModelAndView export(" + beanName + " condition) {\n");
        bw.write("\t\tMap<String, Object> model = new HashMap<String, Object>();\n");
        bw.write("\t\tmodel.put(\"list\", " + service + ".export(condition));\n");
        bw.write("\t\treturn new ModelAndView(new ViewExcel<" + beanName + ">(), model);\n");
        bw.write("\t}\n\n");
        // 详情
        bw.write("\t@GetMapping(\"/detail\")\n");
        bw.write("\t@AuthPassport\n");
        bw.write("\tpublic String detail(" + type + " id, ModelMap model) {\n");
        bw.write("\t\tif (id != null) {\n");
        bw.write("\t\t\t" + beanName + " map" + " = " + service + ".findById(id);\n");
        bw.write("\t\t\tmodel.addAttribute(\"map\", map);\n");
        bw.write("\t\t}\n");
        bw.write("\t\treturn \"" + bean + "/" + processFile(tableName) + "_edit\";\n");
        bw.write("\t}\n\n");
        // 编辑-新增/更新
        bw.write("\t@PostMapping(\"/edit\")\n");
        bw.write("\t@ResponseBody\n");
        bw.write("\t@AuthPassport\n");
        bw.write("\tpublic ResMsg edit(" + beanName + " " + bean + ") {\n");
        bw.write("\t\tif (" + bean + ".getId() == null) {\n");
        bw.write("\t\t\tint res = " + service + ".insert(" + bean + ");\n");
        bw.write("\t\t\tif (res > 0) {\n");
        bw.write("\t\t\t\treturn ResMsg.success(\"新增成功\");\n");
        bw.write("\t\t\t}\n");
        bw.write("\t\t\treturn ResMsg.fail(\"新增失败\");\n");
        bw.write("\t\t} else {\n");
        bw.write("\t\t\tint res = " + service + ".update(" + bean + ");\n");
        bw.write("\t\t\tif (res > 0) {\n");
        bw.write("\t\t\t\treturn ResMsg.success(\"更新成功\");\n");
        bw.write("\t\t\t}\n");
        bw.write("\t\t\treturn ResMsg.fail(\"更新失败\");\n");
        bw.write("\t\t}\n");
        bw.write("\t}\n\n");
        // 禁用/启用
        bw.write("\t@PostMapping(\"/enable\")\n");
        bw.write("\t@ResponseBody\n");
        bw.write("\t@AuthPassport\n");
        bw.write("\tpublic ResMsg enable(" + type + " id) {\n");
        bw.write("\t\tif (" + service + ".enable(id) > 0) {\n");
        bw.write("\t\t\treturn ResMsg.success(\"操作成功\");\n");
        bw.write("\t\t}\n");
        bw.write("\t\treturn ResMsg.fail(\"操作失败\");\n");
        bw.write("\t}\n\n");
        // 删除
        bw.write("\t@PostMapping(\"/delete\")\n");
        bw.write("\t@ResponseBody\n");
        bw.write("\t@AuthPassport\n");
        bw.write("\tpublic ResMsg delete(" + type + " id) {\n");
        bw.write("\t\tif (" + service + ".delete(id) > 0) {\n");
        bw.write("\t\t\treturn ResMsg.success(\"删除成功\");\n");
        bw.write("\t\t}\n");
        bw.write("\t\treturn ResMsg.fail(\"删除失败\");\n");
        bw.write("\t}\n\n");
        // 批量删除
        bw.write("\t@PostMapping(\"/deleteBatch\")\n");
        bw.write("\t@ResponseBody\n");
        bw.write("\t@AuthPassport\n");
        bw.write("\tpublic ResMsg deleteBatch(String ids) {\n");
        bw.write("\t\tif (StringUtil.isBlank(ids)) {\n");
        bw.write("\t\t\treturn ResMsg.fail(\"参数错误\");\n");
        bw.write("\t\t}\n");
        bw.write("\t\tLong[] longIds = StringUtil.strToLongArray(ids);\n");
        bw.write("\t\tif (longIds.length == 0) {\n");
        bw.write("\t\t\treturn ResMsg.fail(\"请选择至少一项\");\n");
        bw.write("\t\t}\n");
        bw.write("\t\tif (messageService.deleteBatch(longIds) > 0) {\n");
        bw.write("\t\t\treturn ResMsg.success(\"批量删除成功\");\n");
        bw.write("\t\t}\n");
        bw.write("\t\treturn ResMsg.fail(\"批量删除失败\");\n");
        bw.write("\t}\n\n");

        bw.write("}");
        bw.flush();
        bw.close();
    }


    /**
     * 生成html表单和列表
     * <p>基于h5 bootstrap</p>
     *
     * @param columns
     * @param types
     * @param comments
     */
    private void buildHtml(List<String> columns, List<String> types, List<String> comments) throws IOException {
        String bean = processBeanName(beanName);
        File folder = new File(html_path + "/" + bean);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // form表单
        File htmlEditFile = new File(folder, processFile(tableName) + "_edit.ftl");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlEditFile)));
        bw.write("<!DOCTYPE html>\n<html>\n<head>\n\t<meta charset=\"utf-8\">\n\t<title>" + tableComment + "</title>\n");
        bw.write("    <#include \"include.ftl\"/>\n</head>\n");
        bw.write("<body>\n\n<div class=\"ibox-content\">\n\t<form class=\"form-horizontal\" id=\"" + beanName + "EditForm\">\n");

        int size = columns.size();
        boolean hasDate = false;
        List<String> dateIds = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            if ("id".equals(columns.get(i))) {
                continue;
            }
            String name = processField(columns.get(i));
            String comment = processFieldComment(comments.get(i));
            bw.write("\t\t<div class=\"form-group\">\n");
            bw.write("\t\t\t<label class=\"col-sm-2 control-label\">" + comment + "：</label>\n");
            bw.write("\t\t\t<div class=\"col-sm-9\">\n");
            if ("Boolean".equals(processType(types.get(i)))) { // boolean类型转换为radio
                bw.write("\t\t\t\t<div class=\"radio radio-info radio-inline\">\n");
                bw.write("\t\t\t\t\t<input type=\"radio\" name=\"" + name + "\" id=\"" + name + "1\" value=\"1\" ${map." + name + "?string('checked','') }>\n\t\t\t\t\t<label for=\"" + name + "1\">1</label>\n");
                bw.write("\t\t\t\t</div>\n\t\t\t\t<div class=\"radio radio-info radio-inline\">\n");
                bw.write("\t\t\t\t\t<input type=\"radio\" name=\"" + name + "\" id=\"" + name + "0\" value=\"0\" ${map." + name + "?string('checked','') }>\n\t\t\t\t\t<label for=\"" + name + "0\">0</label>\n\t\t\t\t</div>");
            } else if ("Date".equals(processType(types.get(i)))) { // 时间类型添加时间选择插件
                bw.write("\t\t\t\t<input type=\"text\" id=\"" + name + "\" name=\"" + name + "\" value=\"${map." + name + "?string('yyyy-MM-dd HH:mm:ss') }\" class=\"form-control\" placeholder=\"" + comment + "\">");
                hasDate = true;
                dateIds.add("#" + name);
            } else { // 否则为普通文本框
                bw.write("\t\t\t\t<input type=\"text\" name=\"" + name + "\" value=\"${map." + name + " }\" class=\"form-control\" placeholder=\"" + comment + "\">");
            }
            bw.newLine();
            bw.write("\t\t\t</div>\n\t\t</div>\n");
        }

        bw.write("\t\t<div class=\"form-group\">\n\t\t\t<div class=\"col-sm-offset-2 col-sm-3\">\n");
        bw.write("\t\t\t\t<input type=\"hidden\" name=\"id\" value=\"${map.id }\">\n");
        bw.write("\t\t\t\t<button type=\"submit\" class=\"btn btn-info btn-block\">保存</button>\n");
        bw.write("\t\t\t</div>\n\t\t</div>\n\t</form>\n</div>\n\n");

        bw.write("<script type=\"text/javascript\">\n");
        bw.write("\t$(function () {\n");
        if (hasDate) { // 添加时间选择插件
            bw.write("\t\tdatePicker(\"" + StringUtils.join(dateIds, ",") + "\", \"yyyy-mm-dd\");\n");
        }
        bw.write("\t\tvar index = parent.layer.getFrameIndex(window.name);\n\n");
        bw.write("\t\t$(\"#" + beanName + "EditForm\").bootstrapValidator({\n");
        bw.write("\t\t\texcluded: [':disabled'],\n");
        bw.write("\t\t\tfields: {\n");
        for (int i = 0; i < size; i++) {
            if ("id".equals(columns.get(i))) {
                continue;
            }
            String comment = processFieldComment(comments.get(i));
            bw.write("\t\t\t\t" + processField(columns.get(i)) + ": {\n\t\t\t\t\tvalidators: {\n");
            bw.write("\t\t\t\t\t\tnotEmpty: {\n\t\t\t\t\t\t\tmessage: '" + comment + "不能为空'\n\t\t\t\t\t\t}\n\t\t\t\t\t}\n\t\t\t\t},\n");
        }
        bw.write("\t\t\t}\n\t\t}).on('success.form.bv', function (e) {\n\t\t\te.preventDefault();\n\t\t\tvar $form = $(e.target);\n");
        bw.write("\t\t\tAjax.ajax({\n");
        bw.write("\t\t\t\turl: '/admin/" + bean + "/edit',\n\t\t\t\tparams: $form.serialize(),\n\t\t\t\tsuccess: function (data) {\n");
        bw.write("\t\t\t\t\tif (data.code == 0) {\n\t\t\t\t\t\tshowMsg(data.msg, 1, function () {\n\t\t\t\t\t\t\tparent.layer.close(index);\n\t\t\t\t\t\t\tparent.reload();\n");
        bw.write("\t\t\t\t\t\t});\n\t\t\t\t\t} else {\n\t\t\t\t\t\tshowMsg(data.msg, 2);\n\t\t\t\t\t}\n\t\t\t\t}\n");
        bw.write("\t\t\t});\n");
        bw.write("\t\t});\n");
        bw.write("\t});\n");
        bw.write("</script>\n");
        bw.write("</body>\n</html>");
        bw.flush();
        bw.close();

        // 列表-DataTables
        File htmlListFile = new File(folder, processFile(tableName) + "_list.ftl");
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlListFile)));
        bw2.write("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title></title>\n" +
                "    <#include \"include.ftl\"/>\n" +
                "</head>\n" +
                "\n" +
                "<body class=\"gray-bg\">\n" +
                "<div class=\"ibox float-e-margins\">\n" +
                "    <div class=\"ibox-content\">\n" +
                "        <div class=\"row\">\n" +
                "            <div class=\"col-md-8\">\n" +
                "                <form class=\"form-inline\" method=\"post\" id=\"queryForm\">\n" +
                "                    <label> 关键字:<input type=\"text\" name=\"keywords\" class=\"form-control input-sm\"></label>\n" +
                "                    <label> 时间段:\n" +
                "                        <input type=\"text\" class=\"form-control input-sm\" id=\"startDate\" name=\"startDate\" placeholder=\"开始时间\">\n" +
                "                        <input type=\"text\" class=\"form-control input-sm\" id=\"endDate\" name=\"endDate\" placeholder=\"结束时间\">\n" +
                "                    </label>\n" +
                "                    <button type=\"button\" id=\"search\" class=\"btn btn-sm btn-info\"><i class=\"fa fa-search\"></i></button>\n" +
                "                    <button type=\"button\" id=\"export\" class=\"btn btn-sm btn-info\"><i class=\"fa fa-share\"></i>导出到Excel</button>\n" +
                "                </form>\n" +
                "            </div>\n" +
                "            <div class=\"col-md-4 text-right\">\n" +
                "                <button type=\"button\" class=\"btn btn-primary btn-sm\" data-open=\"modal\" data-width=\"800px\" data-height=\"500px\" href=\"/admin/" + bean + "/detail\" id=\"btn-add\">\n" +
                "                    <i class=\"fa fa-plus\"></i> 添加\n" +
                "                </button>\n" +
                "                <button type=\"button\" class=\"btn btn-danger btn-sm\" id=\"btn-del\"><i class=\"fa fa-remove\"></i> 批量删除</button>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <table id=\"table\" class=\"table table-striped table-bordered table-hover\" width=\"100%\"></table>\n" +
                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "    var tables;\n" +
                "    $(function () {\n" +
                "        var columns = [\n" +
                "            CONSTANT.COLUMN.CHECKALL, // 全选\n");

        for (int i = 0; i < size; i++) {
            String comment = processFieldComment(comments.get(i));
            bw2.write("\t\t\t{title: \"" + comment + "\", data: \"" + processField(columns.get(i)) + "\", defaultContent: \"--\"" + (i == 0 ? "" : ", orderable: false") + "},");
            bw2.newLine();
        }

        bw2.write("            {\n" +
                "                title: \"操作\", data: null, orderable: false,\n" +
                "                render: function (data, type, full, callback) {\n" +
                "                    var btns = CONSTANT.BUTTON.EDIT(\"/admin/" + bean + "/detail?id=\" + full.id);\n" +
                "                    btns += CONSTANT.BUTTON.ENABLE();\n" +
                "                    btns += CONSTANT.BUTTON.DELETE();\n" +
                "                    return btns;\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "        var $table = $('#table');\n" +
                "        tables = $table.DataTable($.extend(true, {}, CONSTANT.DEFAULT_OPTION, {\n" +
                "            columns: columns,\n" +
                "            ajax: function (data, callback, settings) {\n" +
                "                CONSTANT.AJAX(\"/admin/" + bean + "/list\", [[1, \"id\"]], data, callback, settings);\n" +
                "            },\n" +
                "            columnDefs: [CONSTANT.BUTTON.CHECKBOXS],\n" +
                "            drawCallback: function (settings) {\n" +
                "                $(\":checkbox[name='check-all']\").prop(\"checked\", false);\n" +
                "                /* 自增序列\n" +
                "                    this.api().column(0).nodes().each(function (cell, i) {\n" +
                "                        cell.innerHTML = i + 1;\n" +
                "                    });\n" +
                "                */\n" +
                "            }\n" +
                "        }));\n" +
                "\n" +
                "        $(\"#search\").on(\"click\", function () {\n" +
                "            tables.draw();\n" +
                "        });\n" +
                "\n" +
                "        $(\"#export\").click(function () {\n" +
                "            var formData = $(\"#queryForm\").serialize();\n" +
                "            location.href = '/admin/" + bean + "/export?' + formData;\n" +
                "        });\n" +
                "\n" +
                "        $(\"#btn-del\").click(function () {\n" +
                "            var arrItemId = [];\n" +
                "            $(\"tbody :checkbox:checked\", $table).each(function (i) {\n" +
                "                var item = tables.row($(this).closest('tr')).data();\n" +
                "                arrItemId.push(item.id);\n" +
                "            });\n" +
                "            if (arrItemId.length > 0) {\n" +
                "                Ajax.ajax({\n" +
                "                    url: \"/admin/" + bean + "/deleteBatch\",\n" +
                "                    params: {\"ids\": arrItemId.join(\",\")},\n" +
                "                    success: function (data) {\n" +
                "                        if (data.code == 0) {\n" +
                "                            showMsg(data.msg, 1, function () {\n" +
                "                                reload();\n" +
                "                            });\n" +
                "                        } else {\n" +
                "                            showMsg(data.msg, 2);\n" +
                "                        }\n" +
                "                    }\n" +
                "                });\n" +
                "            } else {\n" +
                "                showMsg(\"请选择\", 2);\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        $table.on(\"change\", \":checkbox\", function () {\n" +
                "            if ($(this).is(\"[name='check-all']\")) {\n" +
                "                $(\":checkbox\", $table).prop(\"checked\", $(this).prop(\"checked\"));\n" +
                "            } else {\n" +
                "                var checkbox = $(\"tbody :checkbox\", $table);\n" +
                "                $(\":checkbox[name='check-all']\", $table).prop('checked', checkbox.length == checkbox.filter(':checked').length);\n" +
                "            }\n" +
                "        }).on(\"click\", \".btn-enable\", function () {\n" +
                "            var item = tables.row($(this).closest('tr')).data();\n" +
                "            layerConfirm('确定要禁用或启用该条数据吗', \"/admin/" + bean + "/enable?id=\" + item.id, function () {\n" +
                "                reload();\n" +
                "            });\n" +
                "        }).on(\"click\", \".btn-del\", function () {\n" +
                "            var item = tables.row($(this).closest('tr')).data();\n" +
                "            layerConfirm('确定要删除该条数据吗', \"/admin/" + bean + "/delete?id=\" + item.id, function () {\n" +
                "                reload();\n" +
                "            });\n" +
                "        });\n" +
                "        // 行点击事件\n" +
                "        // $('#table tbody').on('click', 'tr', function () {\n" +
                "        //     $(this).closest('tr').toggleClass(\"red\");\n" +
                "        // });\n" +
                "\n" +
                "        datePicker('#startDate,#endDate', \"yyyy-mm-dd\");\n" +
                "    });\n" +
                "\n" +
                "    // 重新加载表格\n" +
                "    function reload() {\n" +
                "        tables.draw('page');\n" +
                "    }\n" +
                "\n" +
                "    // 选择框的回调方法\n" +
                "    function dealData(data) {\n" +
                "        // 处理data\n" +
                "    }\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>");
        bw2.flush();
        bw2.close();
    }

    /**
     * 构建权限菜单
     * 需少量修改
     *
     * @param info
     * @param tables
     * @param tableComments
     */
    private void buildMenu(GenInfo info, List<String> tables, Map<String, String> tableComments) throws IOException {
        File htmlEditFile = new File(info.getGlobalPath() + "/menu.sql");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlEditFile)));
        bw.write("-- 此脚本仅在MySQL测试通过，其它数据库请稍作修改\n");
        bw.write("-- id为自增，插入后请手动关联模块关系\n");
        bw.write("-- fa图标请从后台或官网查询\n\n");

        for (String table : tables) {
            if (!info.getTables().isEmpty() && !info.getTables().contains(table)) {
                continue;
            }
            processTable(table);
            String bean = processBeanName(beanName);
            bw.write("\n-- menu for " + table + "\n");
            bw.write("INSERT INTO s_module (parent_id, name, action, icon_name, flag, sort, create_time, deleted) VALUES (0, '" + tableComments.get(table) + "管理', NULL, 'fa fa-gear', 1, 0, now(), 0);\n");
            bw.write("INSERT INTO s_module (parent_id, name, action, icon_name, flag, sort, create_time, deleted) VALUES (0, '列表管理', '/admin/" + bean + "/page', 'fa fa-gear', 2, 0, now(), 0);\n");
            bw.write("INSERT INTO s_module (parent_id, name, action, icon_name, flag, sort, create_time, deleted) VALUES (0, '列表数据', '/admin/" + bean + "/list', 'fa fa-gear', 3, 0, now(), 0);\n");
            bw.write("INSERT INTO s_module (parent_id, name, action, icon_name, flag, sort, create_time, deleted) VALUES (0, '编辑', '/admin/" + bean + "/edit', 'fa fa-gear', 3, 0, now(), 0);\n");
            bw.write("INSERT INTO s_module (parent_id, name, action, icon_name, flag, sort, create_time, deleted) VALUES (0, '详情', '/admin/" + bean + "/detail', 'fa fa-gear', 3, 0, now(), 0);\n");
            bw.write("INSERT INTO s_module (parent_id, name, action, icon_name, flag, sort, create_time, deleted) VALUES (0, '启用/禁用', '/admin/" + bean + "/enable', 'fa fa-gear', 3, 0, now(), 0);\n");
            bw.write("INSERT INTO s_module (parent_id, name, action, icon_name, flag, sort, create_time, deleted) VALUES (0, '导出', '/admin/" + bean + "/export', 'fa fa-gear', 3, 0, now(), 0);\n");
            bw.write("INSERT INTO s_module (parent_id, name, action, icon_name, flag, sort, create_time, deleted) VALUES (0, '删除', '/admin/" + bean + "/delete', 'fa fa-gear', 3, 0, now(), 0);\n");
        }

        bw.flush();
        bw.close();
    }


    /**
     * 构建主方法
     *
     * @param info
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws IOException
     * @author rick
     * @date 2016年8月10日 上午9:00:31
     */
    public void generate(GenInfo info) {
        try {
            this.init(info);

            String sql = "";
            if (dbType == 1) {
                sql = "SHOW FULL FIELDS FROM {0}";
            } else if (dbType == 2) {
                sql = "SELECT col_description ( A.attrelid, A.attnum ) AS COMMENT, pg_type.typname AS TYPE, A.attname AS FIELD \n" +
                        "FROM pg_class AS C, pg_attribute AS A INNER JOIN pg_type ON pg_type.oid = A.atttypid \n" +
                        "WHERE C.relname = ''{0}'' AND A.attrelid = C.oid AND A.attnum > 0";
            }
            List<String> columns = null;
            List<String> types = null;
            List<String> comments = null;
            PreparedStatement pstate = null;
            List<String> tables = getTables();
            Map<String, String> tableComments = getTableComment();
            for (String table : tables) {
                if (!info.getTables().isEmpty() && !info.getTables().contains(table)) {
                    continue;
                }
                columns = new ArrayList<String>();
                types = new ArrayList<String>();
                comments = new ArrayList<String>();
                pstate = conn.prepareStatement(MessageFormat.format(sql, table));
                ResultSet results = pstate.executeQuery();
                while (results.next()) {
                /*if (dbType == 1 && "VIRTUAL GENERATED".equals(results.getString("Extra"))) {
                    continue; // 去除虚拟列
                }*/
                    columns.add(results.getString("FIELD"));
                    types.add(results.getString("TYPE"));
                    comments.add(results.getString("COMMENT"));
                }
                // 表名和表注释
                tableName = table;
                tableComment = tableComments.get(tableName);
                processTable(table);

                // 以下是构建方法
                buildEntityBean(columns, types, comments);
                buildMapper();
                buildMapperXml(columns, types, comments);
                buildService();
                buildController();
                buildHtml(columns, types, comments);
            }
            buildMenu(info, tables, tableComments);

            log.info("数据库表共" + tables.size() + "个");
            if (!info.getTables().isEmpty()) {
                log.info("已生成" + info.getTables().size() + "个");
            } else {
                log.info("已生成" + tables.size() + "个");
            }
            log.info("文件输出到：" + info.getGlobalPath());
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException throwables) {
                }
            }
            throw new RuntimeException(e);
        }
    }

    public static void deleteFile(File dirFile) {
        if (dirFile.exists()) {
            if (dirFile.isFile()) {
                dirFile.delete();
            } else {
                for (File file : dirFile.listFiles()) {
                    deleteFile(file);
                }
            }
        }
    }
}