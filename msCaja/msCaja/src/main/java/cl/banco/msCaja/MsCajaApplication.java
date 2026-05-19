package cl.banco.msCaja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsCajaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCajaApplication.class, args);
	}

}
