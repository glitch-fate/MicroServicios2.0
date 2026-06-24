package cl.banco.msSeguros.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.banco.msSeguros.dto.SeguroRespuestaDTO;
import cl.banco.msSeguros.model.Seguro;
import cl.banco.msSeguros.service.SeguroService;

@RestController
@RequestMapping("/api/seguro")
public class SeguroController {

    @Autowired
    private SeguroService seguroService;

    @PostMapping
    public ResponseEntity<Seguro> crearSeguro(@RequestBody Seguro seguro) {
        return ResponseEntity.ok(seguroService.crearSeguro(seguro));
    }

    // 1. OBTENER TODOS: Retorna 204 si la lista global está vacía
    @GetMapping
    public ResponseEntity<List<SeguroRespuestaDTO>> obtenerTodos() {
        List<SeguroRespuestaDTO> lista = seguroService.obtenerTodos();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }
        return ResponseEntity.ok(lista); // HTTP 200 OK
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SeguroRespuestaDTO> obtenerPorId(@PathVariable Long id) {
        SeguroRespuestaDTO respuesta = seguroService.obtenerPorId(id);
        if (respuesta != null) {
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.notFound().build();
    }
    
    // 2. OBTENER POR CLIENTE: Retorna 204 si el cliente no registra seguros asociados
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Seguro>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<Seguro> listaPorCliente = seguroService.obtenerPorCliente(clienteId);
        if (listaPorCliente.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }
        return ResponseEntity.ok(listaPorCliente); // HTTP 200 OK
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Seguro> cancelarSeguro(@PathVariable Long id) {
        Seguro seguroCancelado = seguroService.cancelarSeguro(id);
        if (seguroCancelado != null) {
            return ResponseEntity.ok(seguroCancelado);
        }
        return ResponseEntity.notFound().build();
    }
}