package cl.bancl.msCreditos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.bancl.msCreditos.dto.ClientesDTO;

@FeignClient(name = "msClientes", url= "http://localhost:8083") //debo revisar que ese sea el puerto de clientes
public interface ClienteClient {

    @GetMapping("/api/clientes/rut/{rut}") //revisar bien este url
    ClientesDTO obtenerClientePorRut(@PathVariable("rut") String rut);

}
