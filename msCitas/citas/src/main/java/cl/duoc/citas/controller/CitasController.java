package cl.duoc.citas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.citas.dto.DetalleCitaDTO;
import cl.duoc.citas.model.Citas;
import cl.duoc.citas.service.CitasService;

@RestController
@RequestMapping("/api/citas")
public class CitasController {

    @Autowired
    private CitasService service;

    // 1. GUARDAR CITA: Retorna 200 OK, si falla por datos inválidos manda un 400 Bad Request
    @PostMapping
    public ResponseEntity<Citas> nuevaCita(@RequestBody Citas cita) {
        try {
            Citas citaGuardada = service.guardarCita(cita);
            return ResponseEntity.ok(citaGuardada); 
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); // HTTP 400
        }
    }

    // 2. BUSCAR POR ID: RuntimeException para calzar con Mockito y retornar 404
    @GetMapping("/{id}")
    public ResponseEntity<Citas> buscarPorId(@PathVariable Integer id){
        try {
            Citas cita = service.buscarPorId(id);
            return ResponseEntity.ok(cita); // HTTP 200
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // HTTP 404
        }
    }

    // 3. LISTAR CITAS: Retorna DTOs en 200 o un 204 si está vacía
    @GetMapping
    public ResponseEntity<List<DetalleCitaDTO>> listarCitas(){
        List<DetalleCitaDTO> listaConDetalles = service.listaCitasConDetalle();

        if (listaConDetalles.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } else {
            return ResponseEntity.ok(listaConDetalles); // HTTP 200 OK
        }
    }
}