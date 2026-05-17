package cl.duoc.citas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.citas.dto.EjecutivosDTO;

@FeignClient(name = "msEmpleados", url = "http://localhost:8082")
public interface EmpleadosClient {

    @GetMapping("/api/ejecutivos/dto/{id}")
    EjecutivosDTO obtenerEjecutivosDTO(@PathVariable("id") Integer id);

}
