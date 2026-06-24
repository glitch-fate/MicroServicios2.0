package cl.bancl.msHorarios.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.bancl.msHorarios.model.HorariosEmpleado;
import cl.bancl.msHorarios.repository.HorariosEmpleadoRepository;

@Configuration
public class HorariosDataLoader {

    @Bean
    CommandLineRunner initHorariosDatabase(HorariosEmpleadoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                
             
                HorariosEmpleado turnoManana = new HorariosEmpleado();
                turnoManana.setEmpleadoId(1);
                turnoManana.setDiaSemana("LUNES");
                turnoManana.setHoraInicio("09:00");
                turnoManana.setHoraFin("14:00");
                turnoManana.setActivo(true);
                turnoManana.setObservaciones("Turno AM ejecutivo");
                repository.save(turnoManana);

             
                HorariosEmpleado turnoTarde = new HorariosEmpleado();
                turnoTarde.setEmpleadoId(1);
                turnoTarde.setDiaSemana("MARTES");
                turnoTarde.setHoraInicio("15:00");
                turnoTarde.setHoraFin("19:00");
                turnoTarde.setActivo(true);
                turnoTarde.setObservaciones("Turno PM ejecutivo");
                repository.save(turnoTarde);

               
                HorariosEmpleado turnoEmpleado2 = new HorariosEmpleado();
                turnoEmpleado2.setEmpleadoId(2);
                turnoEmpleado2.setDiaSemana("MIERCOLES");
                turnoEmpleado2.setHoraInicio("10:00");
                turnoEmpleado2.setHoraFin("16:00");
                turnoEmpleado2.setActivo(true);
                turnoEmpleado2.setObservaciones("Horario continuado");
                repository.save(turnoEmpleado2);

                System.out.println("datos cargados ");
            }
        };
    }

}
