package cl.banco.msCaja.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.banco.msCaja.model.Saldo;
import cl.banco.msCaja.model.Transaccion;
import cl.banco.msCaja.repository.SaldoRepository;
import cl.banco.msCaja.repository.TransaccionRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(SaldoRepository saldoRepository, TransaccionRepository transaccionRepository) {
        return args -> {
            if (saldoRepository.count() == 0 && transaccionRepository.count() == 0) {
                
                System.out.println("añadiendo datos a la bd");

                saldoRepository.save(new Saldo(1L, 150000.0));
               
                transaccionRepository.save(new Transaccion(1L, "DEPOSITO", 200000.0));
                transaccionRepository.save(new Transaccion(1L, "RETIRO", 50000.0));

                
                saldoRepository.save(new Saldo(2L, 45500.0));
               
                transaccionRepository.save(new Transaccion(2L, "DEPOSITO", 10000.0));
                transaccionRepository.save(new Transaccion(2L, "DEPOSITO", 35500.0));

                
                saldoRepository.save(new Saldo(3L, 7000.0));
                
                transaccionRepository.save(new Transaccion(3L, "DEPOSITO", 50000.0));
                transaccionRepository.save(new Transaccion(3L, "RETIRO", 43000.0));

                System.out.println("datos cargados correctamente");
            } else {
                System.out.println("Ya existen datos en la bd");
            }
        };
    }



}
