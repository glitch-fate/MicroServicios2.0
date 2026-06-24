package cl.banco.msDashboard.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-empleados", url = "http://localhost:8083") 
public interface ClienteClient {

    @GetMapping("/api/clientes/rut/{rut}")
    Map<String, Object> obtenerClientePorRut(@PathVariable("rut") String rut);
}