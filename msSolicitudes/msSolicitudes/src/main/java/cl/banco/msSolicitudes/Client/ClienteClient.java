package cl.banco.msSolicitudes.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.banco.msSolicitudes.dto.ClienteDTO;

@FeignClient(name = "msClientes", url = "http://localhost:8083")
public interface ClienteClient {

    @GetMapping("/api/clientes/rut/{rut}")
    ClienteDTO obtenerClientePorRut(@PathVariable("rut") String rut);

}
