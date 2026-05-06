package uk.gov.companieshouse.chskafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@SpringBootApplication
public class Application {

    public static final Logger LOGGER = LoggerFactory.getLogger("chs-kafka-api-java");

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
