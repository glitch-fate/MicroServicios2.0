package cl.banco.msSolicitudes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsSolicitudesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSolicitudesApplication.class, args);
	}

}
