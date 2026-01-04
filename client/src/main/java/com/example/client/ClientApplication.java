package com.example.client;

import com.example.client.entities.Client;
import com.example.client.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    CommandLineRunner initializeH2Database(ClientRepository clientRepository) {
        return args -> {
            clientRepository.save(new Client(1L, "Mohamed", "mohamed@email.com", "0612345678"));
            clientRepository.save(new Client(2L, "Fatima", "fatima@email.com", "0623456789"));
            clientRepository.save(new Client(3L, "Youssef", "youssef@email.com", "0634567890"));
        };
    }
}
