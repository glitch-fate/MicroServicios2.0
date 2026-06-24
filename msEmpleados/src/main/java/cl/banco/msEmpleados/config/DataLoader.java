package cl.banco.msEmpleados.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.banco.msEmpleados.model.CargoEjecutivo;
import cl.banco.msEmpleados.model.Ejecutivo;
import cl.banco.msEmpleados.repository.CargoEjecutivoRepository;
import cl.banco.msEmpleados.repository.EjecutivoRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(EjecutivoRepository ejecutivoRepo, CargoEjecutivoRepository cargoRepo){

        return args -> {
            if(ejecutivoRepo.count() > 0){
                System.out.println("no se agregaron datos porque la base de datos no esta vacia");
            }else{
             
            CargoEjecutivo cargo1 = new CargoEjecutivo(null, "Ejecutivo Creditos");
            CargoEjecutivo cargo2 = new CargoEjecutivo(null, "Atencion al Cliente General");
            CargoEjecutivo cargo3 = new CargoEjecutivo(null, "Ejecutivo de Cobranza y Normalizacion");
            CargoEjecutivo cargo4 = new CargoEjecutivo(null, "Ejecutivo Hipotecario");
            CargoEjecutivo cargo5 = new CargoEjecutivo(null, "Ejecutivo de Inversiones y Cuentas");
          
            Ejecutivo ejecutivo1 = new Ejecutivo(null, "15789456-2", "Antonio", "Martínez", cargo1);
        
            Ejecutivo ejecutivo2 = new Ejecutivo(null, "16456789-K", "Camila", "Valenzuela", cargo2);
           
            Ejecutivo ejecutivo3 = new Ejecutivo(null, "17234123-4", "Gonzalo", "Tapia", cargo3);
            
            Ejecutivo ejecutivo4 = new Ejecutivo(null, "18567890-3", "Francisca", "Lorca", cargo4);
            
            Ejecutivo ejecutivo5 = new Ejecutivo(null, "19345678-9", "Ricardo", "Fuenzalida", cargo5);
                             
                                      
            cargoRepo.save(cargo1);
            cargoRepo.save(cargo2);
            cargoRepo.save(cargo3);
            cargoRepo.save(cargo4);
            cargoRepo.save(cargo5); 
           
            ejecutivoRepo.save(ejecutivo1);
            ejecutivoRepo.save(ejecutivo2); 
            ejecutivoRepo.save(ejecutivo3); 
            ejecutivoRepo.save(ejecutivo4); 
            ejecutivoRepo.save(ejecutivo5); 

            System.out.println("Datos de empleados cargados con exito");

            }
        };

    }

}
