package cl.bancl.msTarjetas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsTarjetasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsTarjetasApplication.class, args);
	}

}
