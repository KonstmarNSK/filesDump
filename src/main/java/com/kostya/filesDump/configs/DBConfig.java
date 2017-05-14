package com.kostya.filesDump.configs;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created by Костя on 14.05.2017.
 */
@Configuration
@EnableTransactionManagement
@PropertySource("classpath:/properties/db.properties")
@ComponentScan(basePackages = {"com.kostya.filesDump.entities", "com.kostya.filesDump.repositories"})
public class DBConfig {
    @Bean
    @Profile("test")
    public DataSource getTestDataSource(){
        EmbeddedDatabaseBuilder databaseBuilder = new EmbeddedDatabaseBuilder();
        databaseBuilder.setType(EmbeddedDatabaseType.HSQL).addScript("classpath:/DBScripts/testDB.sql");

        return databaseBuilder.build();
    }

    @Autowired
    Environment environment;

    @Bean
    @Profile("!test")
    public DataSource getDataSource(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setPassword(environment.getProperty("db.pass"));
        dataSource.setUsername(environment.getProperty("db.login"));
        dataSource.setUrl(environment.getProperty("db.url"));
        dataSource.setDriverClassName(environment.getProperty("db.driver"));

        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean getSessionFactoryBean(DataSource dataSource){
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);

        sessionFactoryBean.setPackagesToScan("com.kostya.filesDump.entities");

        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager getHibernateTransactionManager(SessionFactory sessionFactoryBean){
        return new HibernateTransactionManager(sessionFactoryBean);
    }
}
