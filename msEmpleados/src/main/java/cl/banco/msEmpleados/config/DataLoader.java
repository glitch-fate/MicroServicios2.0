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
                CargoEjecutivo cargo3 = new CargoEjecutivo(null, "Ejecutivo de Cobranza y Normalizacion");// Contacta a clientes con morosidad para repactar deudas o gestionar pagos atrasados
                CargoEjecutivo cargo4 = new CargoEjecutivo(null, "Ejecutivo Hipotecario");
                
                Ejecutivo ejecutivo1= new Ejecutivo(null, "15789456-2", "Antonio", "Martinez", cargo1);
                


                cargoRepo.save(cargo1);
                cargoRepo.save(cargo2);
                cargoRepo.save(cargo3);
                cargoRepo.save(cargo4);

                ejecutivoRepo.save(ejecutivo1);

                System.out.println("Datos cargados con exito");

            }
        };

    }

}
