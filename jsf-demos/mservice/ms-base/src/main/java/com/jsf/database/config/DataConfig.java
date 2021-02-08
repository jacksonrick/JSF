package com.jsf.database.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {DataConfig.mapperPackage}, sqlSessionFactoryRef = "sqlSessionFactory")
@EnableTransactionManagement
public class DataConfig {

    private Logger logger = LoggerFactory.getLogger(DataConfig.class);

    public final static String mapperPackage = "com.jsf.database.mapper";
    public final static String modelPackage = "com.jsf.database.model";
    public final static String xmlMapperLocation = "classpath*:mapper/*.xml";

    /*@Bean(name = "dataSource")
    @ConfigurationProperties("spring.datasource.druid")
    public DataSource dataSource() {
        return new DruidDataSource();
    }

    // seata数据源代理
    @Bean(name = "dataSourceProxy")
    public DataSourceProxy dataSourceProxy(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }*/

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
        try {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dataSource);
            // we MUST set the 'VFS' if you use jar
            bean.setVfs(SpringBootVFS.class);
            // 实体类位置
            bean.setTypeAliasesPackage(modelPackage);
            // 设置mapper.xml文件所在位置
            org.springframework.core.io.Resource[] resources = new PathMatchingResourcePatternResolver().getResources(xmlMapperLocation);
            bean.setMapperLocations(resources);
            return bean.getObject();
        } catch (Exception e) {
            logger.error("Database sqlSessionFactory create error!", e);
            return null;
        }
    }

}
