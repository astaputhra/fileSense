package com.iMatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by Astaputhra on 17-03-2020.
 */
@SpringBootApplication(scanBasePackages = "com.iMatch")
public class ETLParentClass {

    @Bean
    public EtlManager getEtlManager() {
        return new EtlManager();
    }

    public static void main(String[] args) {

        SpringApplication.run(ETLParentClass.class, args);
    }

}
