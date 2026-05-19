package cl.banco.msSeguros.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.banco.msSeguros.model.Seguro;
import cl.banco.msSeguros.repository.SeguroRepository;

@Configuration
public class SegurosDataLoader {

    @Bean
    CommandLineRunner initDatabase(SeguroRepository repository) {
        return args -> {
            if(repository.count() > 0){
                System.out.println("Ya existen datos en la base de datos");
            }else{
         
            Seguro s1 = new Seguro();
            s1.setNumeroPoliza("POL-2026-NICO-01");
            s1.setTipoSeguro("AUTOMOTRIZ");
            s1.setMontoAsegurado(12500000.0); 
            s1.setPrimaMensual(24990.0);       
            s1.setFechaContratacion(LocalDate.now());
            s1.setEstado("ACTIVO");
            s1.setClienteId(1L); 
            repository.save(s1);

           
            Seguro s2 = new Seguro();
            s2.setNumeroPoliza("POL-2026-ESTE-04");
            s2.setTipoSeguro("VIDA");
            s2.setMontoAsegurado(45000000.0); 
            s2.setPrimaMensual(14990.0);     
            s2.setFechaContratacion(LocalDate.now());
            s2.setEstado("ACTIVO");
            s2.setClienteId(4L);
            repository.save(s2);

           
            Seguro s3 = new Seguro();
            s3.setNumeroPoliza("POL-2026-NICO-02");
            s3.setTipoSeguro("FRAUDE");
            s3.setMontoAsegurado(3500000.0); 
            s3.setPrimaMensual(5990.0);       
            s3.setFechaContratacion(LocalDate.now());
            s3.setEstado("ACTIVO");
            s3.setClienteId(1L); 
            repository.save(s3);

            System.out.println("Datos cargados");
            }
        };
    }
}
