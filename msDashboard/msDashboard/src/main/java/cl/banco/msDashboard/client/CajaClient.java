package cl.banco.msDashboard.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "ms-caja", url = "http://localhost:8091")
public interface CajaClient {

    
    @GetMapping("/api/caja/saldo/{rut}")
    Double obtenerSaldoPorRut(@PathVariable("rut") String rut);
}