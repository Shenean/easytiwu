package com.easytiwu.servicebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author sheny
 */
@SpringBootApplication(scanBasePackages = {"com.easytiwu.servicebank", "com.easytiwu.starterdb"})
public class ServiceBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceBankApplication.class, args);
    }

}
