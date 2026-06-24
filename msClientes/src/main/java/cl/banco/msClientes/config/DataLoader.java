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

                 Cliente cliente2 = new Cliente(null, "15987123-5", "Gustavo", "Mardones",
                  "gustavelemani@gmail.com", "Premium", null, null);
                 Contacto contacto2 = new Contacto(null, "987652526", cliente2);
                 Direccion direccion2 = new Direccion(null, "Los platanos", "Temuco", "Temuco", cliente2);


                
                    Cliente cliente3 = new Cliente(null, "16145889-6", "Felipe", "Andrade", 
                                        "felipe.andrade@gmail.com", "Regular", null, null);
                    Contacto contacto3 = new Contacto(null, "955541234", cliente3);
                    Direccion direccion3 = new Direccion(null, "Av. Alemana 450", "Temuco", "Temuco", cliente3);

                   
                    Cliente cliente4 = new Cliente(null, "18234567-8", "Valentina", "Pérez", 
                                        "valen.perez@gmail.com", "Premium", null, null);
                    Contacto contacto4 = new Contacto(null, "977712345", cliente4);
                    Direccion direccion4 = new Direccion(null, "Providencia 1230", "Santiago", "Santiago", cliente4);

                  
                    Cliente cliente5 = new Cliente(null, "19876543-2", "Matías", "San Martín", 
                                        "matias.sm@gmail.com", "Regular", null, null);
                    Contacto contacto5 = new Contacto(null, "966687654", cliente5);
                    Direccion direccion5 = new Direccion(null, "Pajaritos 4500", "Maipú", "Santiago", cliente5);

                 cliente2.setContacto(contacto2);
                 cliente2.setDireccion(direccion2);

                 cliente3.setDireccion(direccion3);
                 cliente3.setContacto(contacto3);

                 cliente4.setDireccion(direccion4);
                 cliente4.setContacto(contacto4);

                 cliente5.setDireccion(direccion5);
                 cliente5.setContacto(contacto5);
               
                 cliente1.setContacto(contacto1);
                 cliente1.setDireccion(direccion1);

                 repoCliente.save(cliente1);

                 repoContacto.save(contacto1);

                 repoDireccion.save(direccion1);
//-------------------------------------------------------
                  repoCliente.save(cliente2);

                 repoContacto.save(contacto2);

                 repoDireccion.save(direccion2);
//-------------------------------------------------------
                  repoCliente.save(cliente3);

                 repoContacto.save(contacto3);

                 repoDireccion.save(direccion3);
//-------------------------------------------------------
                  repoCliente.save(cliente4);

                 repoContacto.save(contacto4);

                 repoDireccion.save(direccion4);
//-------------------------------------------------------
                repoCliente.save(cliente5);

                 repoContacto.save(contacto5);

                 repoDireccion.save(direccion5);





                 System.out.println("Datos de Clientes cargados con exito");
            }
                 //prueba1
        
    };






    }
}
