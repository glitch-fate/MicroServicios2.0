package cl.bancl.msHorarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.bancl.msHorarios.dto.EjecutivosDTO;

@FeignClient(name = "msEmpleados", url = "http://localhost:8082") //Verificar url del msEmpleados
public interface EmpleadoClient {  

    @GetMapping("/api/ejecutivos/dto/{id}")
    EjecutivosDTO obtenerEjecutivosDTO(@PathVariable("id") Integer id);



}
