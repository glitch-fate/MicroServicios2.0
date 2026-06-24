package cl.banco.msEmpleados.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.banco.msEmpleados.dto.EjecutivoDTO;
import cl.banco.msEmpleados.model.Ejecutivo;
import cl.banco.msEmpleados.service.EjecutivoService;

@RestController
@RequestMapping("/api/ejecutivos")
public class EjecutivController {

    @Autowired
    private EjecutivoService service;

    // 1. LISTAR: Retorna 200 OK con la lista, o 204 No Content si está vacía
    @GetMapping
    public ResponseEntity<List<Ejecutivo>> listar(){
        List<Ejecutivo> lista = service.listar();
        if (lista.isEmpty()){
            return ResponseEntity.noContent().build(); // HTTP 204
        }
        return ResponseEntity.ok(lista); // HTTP 200
    }

    // 2. BUSCAR POR ID: Retorna 200 OK o 404 Not Found
    @GetMapping("/{id}")
    public ResponseEntity<Ejecutivo> buscarPorId(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch(Exception e) {
            return ResponseEntity.notFound().build(); // HTTP 404
        }
    }

    // 3. GUARDAR: Optimizado a 201 Created 
    @PostMapping
    public ResponseEntity<Ejecutivo> guardar(@RequestBody Ejecutivo ejecutivo){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(ejecutivo)); // HTTP 201
    }
    
    // 4. ELIMINAR: Retorna 204 No Content al borrar con éxito, o 404 si el ID falla
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Integer id){
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build(); // HTTP 204
        } catch(Exception e) {
            return ResponseEntity.notFound().build(); // HTTP 404
        }
    }

    // 5. OBTENER DTO: Protegido con try-catch para evitar caídas de contexto si el ID no existe
    @GetMapping("/dto/{id}")
    public ResponseEntity<?> obtenerEmpleadoDTO(@PathVariable Integer id){
        try {
            Ejecutivo ejecutivo = service.buscarPorId(id);
            EjecutivoDTO dto = new EjecutivoDTO(
                ejecutivo.getId(), 
                ejecutivo.getNombre(), 
                ejecutivo.getCargo().getNombre()
            ); 
            return ResponseEntity.ok(dto); // HTTP 200
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // HTTP 404
        }
    }
}