package cyx.secondary.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cyx.secondary.mall.dao")
public class SecondaryMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondaryMallApplication.class, args);
    }

}
