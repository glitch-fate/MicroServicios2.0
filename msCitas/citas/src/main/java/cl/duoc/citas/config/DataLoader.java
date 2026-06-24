package cl.duoc.citas.config;

import cl.duoc.citas.model.Citas;
import cl.duoc.citas.model.TipoCitas;
import cl.duoc.citas.repository.CitasRepository;
import cl.duoc.citas.repository.TipoCitasRepository; // Asegúrate de tener este repo creado
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(TipoCitasRepository repoTipoCitas, CitasRepository repoCitas) {
        return args -> {
            if(repoTipoCitas.count() > 0){
                System.out.println("Ya existen elementos (Citas) en la base de datos");
            }else{
                System.out.println("Insertando datos de cita");
            
                      
            TipoCitas tipo1 = new TipoCitas();
            tipo1.setNombre("Asesoría Comercial");

            TipoCitas tipo2 = new TipoCitas();
            tipo2.setNombre("Seguros");

            TipoCitas tipo3 = new TipoCitas();
            tipo3.setNombre("Crédito Hipotecario");

            TipoCitas tipo4 = new TipoCitas();
            tipo4.setNombre("Apertura de Cuenta Corriente");

            TipoCitas tipo5 = new TipoCitas();
            tipo5.setNombre("Inversiones y Fondos Mutuos");

           
            repoTipoCitas.save(tipo1);
            repoTipoCitas.save(tipo2);
            repoTipoCitas.save(tipo3);
            repoTipoCitas.save(tipo4);
            repoTipoCitas.save(tipo5);

            System.out.println("Datos de TipoCitas cargados con éxito en citas_db");

            System.out.println("====== INYECTANDO CITAS REALES CON TU MODELO ======");

                // 2. Cargamos 5 citas usando tu constructor @AllArgsConstructor
                // Estructura: Citas(id, fechaCita, hora, tipoCitas, clienteId, ejecutivoId)
                
                // Cita 1: Cliente 1 con Ejecutivo 1 (Ejecutivo de Créditos) -> Tipo 1
                Citas cita1 = new Citas(null, java.sql.Date.valueOf("2026-05-20"), "10:00", tipo1, 1, 1);
                
                // Cita 2: Cliente 2 con Ejecutivo 2 (Atención General) -> Tipo 2
                Citas cita2 = new Citas(null, java.sql.Date.valueOf("2026-05-21"), "11:30", tipo2, 2, 2);
                
                // Cita 3: Cliente 3 con Ejecutivo 3 (Cobranzas) -> Tipo 3
                Citas cita3 = new Citas(null, java.sql.Date.valueOf("2026-05-22"), "14:15", tipo3, 3, 3);
                
                // Cita 4: Cliente 4 con Ejecutivo 4 (Hipotecario) -> Tipo 4
                Citas cita4 = new Citas(null, java.sql.Date.valueOf("2026-05-25"), "15:45", tipo4, 4, 4);
                
                // Cita 5: Cliente 5 con Ejecutivo 5 (Inversiones) -> Tipo 5
                Citas cita5 = new Citas(null, java.sql.Date.valueOf("2026-05-26"), "09:30", tipo5, 5, 5);

                // 3. Guardar las citas en el repositorio
                repoCitas.save(cita1);
                repoCitas.save(cita2);
                repoCitas.save(cita3);
                repoCitas.save(cita4);
                repoCitas.save(cita5);

                System.out.println("¡Datos de TipoCitas y Citas creados con éxito en citas_db!");
            }
        
        };
    }
}



