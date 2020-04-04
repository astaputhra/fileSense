package com.iMatch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Astaputhra on 17-03-2020.
 */
@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
@ComponentScan
@Transactional
@EnableTransactionManagement
public class ETLParentClass {

    @Value("#{appProp['spring.datasource.url']}")
    String url;

    @Value("#{appProp['spring.datasource.driverClassName']}")
    String driverClassName;

    @Value("#{appProp['spring.datasource.username']}")
    String userName;

    @Value("#{appProp['spring.datasource.password']}")
    String password;


    @Bean
    public EtlManager getEtlManager() {
        return new EtlManager();
    }

    public static void main(String[] args) {

        SpringApplication.run(ETLParentClass.class, args);
    }

}
