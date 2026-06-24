package cl.banco.msDashboard.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-solicitudes", url = "http://localhost:8090")
public interface SolicitudClient {

    @GetMapping("/api/solicitudes/cliente/{rut}")
    List<Object> listarPorRut(@PathVariable("rut") String rut);
}