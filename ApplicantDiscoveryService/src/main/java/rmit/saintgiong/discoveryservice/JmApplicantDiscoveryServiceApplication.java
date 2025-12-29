package rmit.saintgiong.discoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class JmApplicantDiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmApplicantDiscoveryServiceApplication.class, args);
    }

}
