package ru.kuzmin.bankaccount;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankAccountApplication {
    @Bean
    public CircuitBreaker circuitBreaker() {
        return CircuitBreaker.ofDefaults("kafkaSender");
    }

    public static void main(String[] args) {
        SpringApplication.run(BankAccountApplication.class, args);
    }

}
