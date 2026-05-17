package cl.duoc.citas.config;

import cl.duoc.citas.model.TipoCitas;
import cl.duoc.citas.repository.TipoCitasRepository; // Asegúrate de tener este repo creado
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(TipoCitasRepository repoTipoCitas) {
        return args -> {
            if(repoTipoCitas.count() > 0){
                System.out.println("Ya existen elementos (Citas) en la base de datos");
            }else{
            
            
            TipoCitas tipo1 = new TipoCitas();
            tipo1.setNombre("Asesoría Comercial");
            
            
            repoTipoCitas.save(tipo1);

            System.out.println("Datos de TipoCitas cargados con éxito en citas_db");
            }
        };
    }
}


