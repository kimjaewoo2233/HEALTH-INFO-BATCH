package org.batch.seoulgyminfobatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SeoulGymInfoBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeoulGymInfoBatchApplication.class, args);
    }

}
