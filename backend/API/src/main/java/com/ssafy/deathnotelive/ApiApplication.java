package com.ssafy.deathnotelive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
public class ApiApplication  {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

//    extends SpringBootServletInitializer
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(Serializable.class);
//    }

    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        System.out.println("현재 시각 : " + new Date());
    }
}
