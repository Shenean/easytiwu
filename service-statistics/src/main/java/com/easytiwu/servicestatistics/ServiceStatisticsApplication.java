package com.easytiwu.servicestatistics;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 统计服务主启动类
 * @author sheny
 */
@SpringBootApplication(scanBasePackages = {
        "com.easytiwu.servicestatistics",
        "com.easytiwu.starterdb",
        "com.easytiwu.commonexception"
})
@MapperScan("com.easytiwu.servicestatistics.mapper")
public class ServiceStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceStatisticsApplication.class, args);
    }

}