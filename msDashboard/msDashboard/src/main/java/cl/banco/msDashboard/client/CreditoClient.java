package cl.banco.msDashboard.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-creditos", url = "http://localhost:8086") 
public interface CreditoClient {

    @GetMapping("/api/creditos/historial/{rut}") 
    List<Object> listarCreditosPorRut(@PathVariable("rut") String rut);
}