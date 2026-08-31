package es.udc.tfg.fios_rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FiosRestApplication {

  public static void main(String[] args) {
    SpringApplication.run(FiosRestApplication.class, args);
  }
}
