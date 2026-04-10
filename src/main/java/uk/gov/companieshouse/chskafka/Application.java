package uk.gov.companieshouse.chskafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final String NAMESPACE = "chs-kafka-api-java";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
