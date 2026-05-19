package cl.banco.msSolicitudes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.banco.msSolicitudes.dto.CrearSolicitudDTO;
import cl.banco.msSolicitudes.model.Solicitud;
import cl.banco.msSolicitudes.service.SolicitudService;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudesController {

    @Autowired
    private SolicitudService solicitudService;

    @PostMapping
    public ResponseEntity<Solicitud> guardarSolicitud(@RequestBody CrearSolicitudDTO dto) {
        Solicitud nueva = solicitudService.crearSolicitud(dto.getRut(), dto.getMotivo(), dto.getMensaje());
        return ResponseEntity.ok(nueva);
    }

    @GetMapping("/cliente/{rut}")
    public ResponseEntity<List<Solicitud>> listarPorRut(@PathVariable String rut) {
        List<Solicitud> lista = solicitudService.obtenerSolicitudesPorRut(rut);
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarSolicitud(@PathVariable Long id) {
        try {
            solicitudService.eliminarSolicitud(id);
            return ResponseEntity.ok("Solicitud con ID " + id + " eliminada exitosamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
