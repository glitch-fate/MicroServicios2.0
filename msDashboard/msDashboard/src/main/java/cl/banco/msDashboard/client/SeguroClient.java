package cl.banco.msDashboard.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-seguros", url = "http://localhost:8089")
public interface SeguroClient {

  
    @GetMapping("/api/seguro/cliente/{clienteId}")
    List<Object> listarSegurosPorClienteId(@PathVariable("clienteId") Long clienteId);
}