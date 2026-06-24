package cl.banco.msDashboard.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-tarjetas", url = "http://localhost:8088")
public interface TarjetaClient {
    
    @GetMapping("/api/tarjetas/historial/{rut}")
    List<Object> consultarTarjetasPorRut(@PathVariable("rut") String rut);
}