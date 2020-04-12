package com.iMatch;


import java.sql.SQLException;
import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.iMatch")
@ImportResource({"classpath*:applicationContext.xml"})
public class AppConfig {

    @Bean
    public DatabaseProperties databaseProperties() {
        return new DatabaseProperties();
    }

    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }

            @Override
            protected void postProcessContext(Context context) {
                ContextResource resource = new ContextResource();

                resource.setType("org.apache.tomcat.jdbc.pool.DataSource");
                resource.setName(databaseProperties().getJndiName());
                resource.setProperty("factory", "org.apache.tomcat.jdbc.pool.DataSourceFactory");
                resource.setProperty("driverClassName", databaseProperties().getDriverClassName());
                resource.setProperty("url", databaseProperties().getUrl());
                resource.setProperty("username", databaseProperties().getUsername());
                resource.setProperty("password", databaseProperties().getPassword());

                context.getNamingResources().addResource(resource);
            }
        };
    }

    @Bean(destroyMethod = "")
    public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:comp/env/" + databaseProperties().getJndiName());
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(false);
        bean.afterPropertiesSet();
        return (DataSource) bean.getObject();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() throws SQLException, IllegalArgumentException, NamingException {

        Properties properties = new Properties();
        properties.put("hibernate.max_fetch_depth",3);
        properties.put("hibernate.order_inserts",true);
        properties.put("hibernate.order_updates",true);
        properties.put("hibernate.show_sql",false);
        properties.put("hibernate.hbm2ddl.auto","none");
        properties.put("hibernate.ejb.naming_strategy","org.hibernate.cfg.DefaultNamingStrategy");
        properties.put("hibernate.connection.charSet","UTF-8");
        properties.put("hibernate.default_schema",databaseProperties().getUsername());
        properties.put("hibernate.format_sql",true);
        properties.put("hibernate.connection.autocommit",false);
        properties.put("hibernate.cache.use_second_level_cache",false);
        properties.put("hibernate.cache.use_query_cache",false);
        properties.put("hibernate.cache.region.factory_class","org.hibernate.cache.infinispan.InfinispanRegionFactory");
        properties.put("org.hibernate.FlushMode","always");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(false);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaProperties(properties);
        factory.setPackagesToScan("com.iMatch");
        factory.setDataSource(jndiDataSource());
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager()
            throws SQLException, IllegalArgumentException, NamingException {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        return txManager;
    }
}