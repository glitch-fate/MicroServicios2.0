package cl.banco.msDashboard.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-citas", url = "http://localhost:8084") 
public interface CitasClient {

    @GetMapping("/api/citas/cliente/{id}")
    List<Object> listarCitasPorClienteId(@PathVariable("id") Long id);
}