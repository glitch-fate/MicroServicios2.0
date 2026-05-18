package cl.bancl.msTarjetas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.bancl.msTarjetas.model.Tarjetas;
import cl.bancl.msTarjetas.repository.TarjetaRepository;

@Component
public class TarjetasDataLoader implements CommandLineRunner {

    @Autowired
    private TarjetaRepository tarjetaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (tarjetaRepository.count() == 0) {
            System.out.println("Inicializando datos de tarjetas de prueba...");
            
            
            Tarjetas t1 = new Tarjetas();
            t1.setRutCliente("16145789-6");
            t1.setTipoTarjeta("DEBITO");
            t1.setNumeroTarjeta("4556123487650001");
            t1.setCupoTotal(0.0);
            t1.setCupoDisponible(0.0);
            t1.setEstado("ACTIVA");
            t1.setAprobadoPor("Sistema");
            
            tarjetaRepository.save(t1);
            System.out.println("Datos iniciales cargados con éxito.");
        }
    }

}
