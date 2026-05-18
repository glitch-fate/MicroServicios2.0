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
            // Seguro 1: Para Nicolas Vazques (ID 1) - Seguro Obligatorio Automotriz con buena prima
            Seguro s1 = new Seguro();
            s1.setNumeroPoliza("POL-2026-NICO-01");
            s1.setTipoSeguro("AUTOMOTRIZ");
            s1.setMontoAsegurado(12500000.0); // 12.5 Millones
            s1.setPrimaMensual(24990.0);       // $24.990 mensuales
            s1.setFechaContratacion(LocalDate.now());
            s1.setEstado("ACTIVO");
            s1.setClienteId(1L); // Nicolas Vazques
            repository.save(s1);

            // Seguro 2: Para Esteban Quito (ID 4) - Seguro de Vida Integral
            Seguro s2 = new Seguro();
            s2.setNumeroPoliza("POL-2026-ESTE-04");
            s2.setTipoSeguro("VIDA");
            s2.setMontoAsegurado(45000000.0); // 45 Millones
            s2.setPrimaMensual(14990.0);       // $14.990 mensuales
            s2.setFechaContratacion(LocalDate.now());
            s2.setEstado("ACTIVO");
            s2.setClienteId(4L); // Esteban Quito (ID 4 según tu tabla)
            repository.save(s2);

            // Seguro 3: Para Nicolas Vazques (ID 1) - Protección anti fraudes bancarios (Ideal para cuenta preferente)
            Seguro s3 = new Seguro();
            s3.setNumeroPoliza("POL-2026-NICO-02");
            s3.setTipoSeguro("FRAUDE");
            s3.setMontoAsegurado(3500000.0);  // 3.5 Millones
            s3.setPrimaMensual(5990.0);        // $5.990 mensuales
            s3.setFechaContratacion(LocalDate.now());
            s3.setEstado("ACTIVO");
            s3.setClienteId(1L); // Nicolas Vazques de nuevo
            repository.save(s3);

            System.out.println(">> ¡DataLoader de Seguros coordinado exitosamente con msClientes (IDs 1 y 4)!");
            }
        };
    }
}
