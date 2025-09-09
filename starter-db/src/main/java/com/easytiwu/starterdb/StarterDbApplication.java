package com.easytiwu.starterdb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 数据库启动类
 */
@SpringBootApplication
@MapperScan("com.easytiwu.**.mapper")
public class StarterDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarterDbApplication.class, args);
    }

}