package team.floracore.data;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@MapperScan("team.floracore.data.mapper")
@EnableScheduling
public class FloraCoreDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(FloraCoreDataApplication.class, args);
    }

}
