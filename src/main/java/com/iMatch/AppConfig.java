package com.iMatch;

/**
 * Created by Astaputhra on 01-04-2020.
 */
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@EnableTransactionManagement
public class AppConfig {

    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager _em;

    @Value("#{appProp['spring.datasource.url']}")
    String url;

    @Value("#{appProp['spring.datasource.driverClassName']}")
    String driverClassName;

    @Value("#{appProp['spring.datasource.username']}")
    String userName;

    @Value("#{appProp['spring.datasource.password']}")
    String password;


//    @DependsOn({"jndiDataSource"})
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

                resource.setType("javax.sql.DataSource");
                resource.setName("jdbc/HexGenJNDI");
                resource.setProperty("factory", "JNDIPropertiesResolver");
                resource.setProperty("driverClassName", driverClassName);
                resource.setProperty("url", url);
                resource.setProperty("password", password);
                resource.setProperty("username", userName);
                resource.setProperty("maxActive", "200");
                resource.setProperty("maxIdle", "2");
                resource.setProperty("maxWait", "220000");
                resource.setProperty("initialSize", "30");
                resource.setProperty("validationQuery", "SELECT 1 FROM DUAL");
                resource.setAuth("Container");

//                maxActive="200" maxIdle="2" maxWait="220000"

                context.getNamingResources().addResource(resource);
            }

        };
    }

    @Lazy
    @Bean(destroyMethod = "")
    public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {

        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:jdbc/HexGenJNDI");
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(false);
        bean.afterPropertiesSet();
        return (DataSource) bean.getObject();
    }


    @Bean
    public PlatformTransactionManager transactionManager()
            throws SQLException, IllegalArgumentException, NamingException {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(_em.getEntityManagerFactory());
        txManager.setDataSource(jndiDataSource());
        return txManager;
    }


}