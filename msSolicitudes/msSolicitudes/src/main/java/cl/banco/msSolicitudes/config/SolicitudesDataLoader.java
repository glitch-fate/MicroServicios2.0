package cl.banco.msSolicitudes.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import cl.banco.msSolicitudes.repository.SolicitudRepository;
import cl.banco.msSolicitudes.service.SolicitudService;

@Configuration
public class SolicitudesDataLoader {
    @Bean
    CommandLineRunner initSolicitudesDatabase(SolicitudRepository repository, SolicitudService solicitudService) {
        return args -> {
           
            if (repository.count() == 0) {
                
                solicitudService.crearSolicitud("15987123-5", "Apertura Cuenta Corriente", "Cliente solicita evaluar cuenta joven");
                solicitudService.crearSolicitud("15987123-5", "Tarjeta de Credito Visa", "Cliente solicita evaluar tarjeta de credito con cupo inicial de $500.000");
                solicitudService.crearSolicitud("19876543-2", "Credito de Consumo", "Solicitud de evaluación para crédito automotriz de consumo rápido");

                System.out.println("datos cargados con exito");
            }
        };
    }

}
