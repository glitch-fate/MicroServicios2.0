package cl.banco.msClientes.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.banco.msClientes.model.Cliente;
import cl.banco.msClientes.model.Contacto;
import cl.banco.msClientes.model.Direccion;
import cl.banco.msClientes.repository.ClienteRepository;
import cl.banco.msClientes.repository.ContactoRepository;
import cl.banco.msClientes.repository.DireccionRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(ClienteRepository repoCliente, ContactoRepository repoContacto, DireccionRepository repoDireccion){
        return args -> {
            if(repoCliente.count() > 0){
                System.out.println("Ya existen datos en la base de datos (Clientes)");
            }else{
                Cliente cliente1 = new Cliente(null, "16145789-6", "Nicolas", "Vasquez",
                 "lecoquetenico@gmail.com", "Preferente", null, null);


                 Contacto contacto1 = new Contacto(null, "945236954", cliente1);

                
                 Direccion direccion1 = new Direccion(null, "Jose antonio matta", "Quilicura", "Santiago", cliente1);

                 


                 cliente1.setContacto(contacto1);
                 cliente1.setDireccion(direccion1);

                 repoCliente.save(cliente1);

                 repoContacto.save(contacto1);

                 repoDireccion.save(direccion1);

                 System.out.println("Datos de Clientes cargados con exito");
            }
                 //prueba1
        
    };






    }
}
