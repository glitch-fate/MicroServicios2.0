package cl.banco.msSolicitudes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.banco.msSolicitudes.dto.SolicitudDTO;
import cl.banco.msSolicitudes.model.Solicitud;
import cl.banco.msSolicitudes.service.SolicitudService;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService service;

    @GetMapping
    public List<Solicitud> listar() {
        return service.listarSolicitudes();
    }

    @GetMapping("/{id}")
    public Solicitud buscar(
            @PathVariable Integer id) {

        return service.buscarPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Solicitud> buscarCliente(
            @PathVariable Integer clienteId) {

        return service.buscarPorCliente(clienteId);
    }

    @GetMapping("/empleado/{empleadoId}")
    public List<Solicitud> buscarEmpleado(
            @PathVariable Integer empleadoId) {

        return service.buscarPorEmpleado(empleadoId);
    }

    @PostMapping
    public Solicitud guardar(
            @RequestBody Solicitud solicitud) {

        return service.agregarSolicitud(solicitud);
    }

    @PutMapping("/{id}")
    public Solicitud actualizar(
            @PathVariable Integer id,
            @RequestBody Solicitud solicitud) {

        return service.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Integer id) {

        service.eliminar(id);
    }

    // DTO --------------------------------------------------

    @GetMapping("/dto/{id}")
    public ResponseEntity<SolicitudDTO>
            obtenerSolicitudDTO(
                    @PathVariable Integer id) {

        Solicitud solicitud =
                service.buscarPorId(id);

        SolicitudDTO dto =
                new SolicitudDTO(

                        solicitud.getId(),
                        solicitud.getAsunto(),
                        solicitud.getEstado()
                );

        return ResponseEntity.ok(dto);
    }
}