package cl.duoc.citas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.citas.dto.ClienteDTO;

@FeignClient(name= "msClientes", url = "http://localhost:8083")
public interface ClienteClient {

    @GetMapping("/api/clientes/dto/{id}")
    ClienteDTO obtenerClienteDTO(@PathVariable("id") Integer id);

}
