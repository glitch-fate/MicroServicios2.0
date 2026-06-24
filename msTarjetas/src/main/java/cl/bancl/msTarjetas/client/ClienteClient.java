package cl.bancl.msTarjetas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.bancl.msTarjetas.dto.ClientesDTO;

@FeignClient(name = "msClientes", url = "http://localhost:8083")
public interface ClienteClient {

    @GetMapping("api/clientes/rut/{rut}")
    ClientesDTO obtenerClientePorRut(@PathVariable("rut") String rut);

}
