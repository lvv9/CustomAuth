package me.liuweiqiang.customauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
@EnableAsync
public class EtransferApplication {
    public static void main(String[] args) {
        SpringApplication.run(EtransferApplication.class, args);
    }
}
