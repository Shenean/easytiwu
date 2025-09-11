package com.easytiwu.servicecontent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author sheny
 */
@SpringBootApplication(scanBasePackages = {
    "com.easytiwu.servicecontent",
    "com.easytiwu.commonexception"
})
public class ServiceContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceContentApplication.class, args);
    }

}
