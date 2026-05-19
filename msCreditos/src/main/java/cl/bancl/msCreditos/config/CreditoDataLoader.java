package cl.bancl.msCreditos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.bancl.msCreditos.model.Creditos;
import cl.bancl.msCreditos.repository.CreditosRepository;
import cl.bancl.msCreditos.service.CreditoService;

@Configuration
public class CreditoDataLoader {
    @Bean
    CommandLineRunner initCreditosDatabase(CreditosRepository repository, CreditoService service) {
        return args -> {
            
            if (repository.count() == 0) {
                
                
                Creditos creditoBase = new Creditos();
                creditoBase.setRutCliente("15987123-5");
                creditoBase.setMontoSolicitado(1500000.0);
                creditoBase.setMesesPlazo(24);
                
               
                service.solicitarCredito(creditoBase);
                
                System.out.println("credito cargado a la base de datos");
            }
        };
    }

}
