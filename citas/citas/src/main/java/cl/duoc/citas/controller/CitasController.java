package cl.duoc.citas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    


    @PostMapping
public ResponseEntity<Citas> nuevaCita(@RequestBody Citas cita) {
    try {
        Citas citaGuardada = service.guardarCita(cita);
        // Retornamos la cita guardada en vez de .build() para ver el JSON en Postman
        return ResponseEntity.ok(citaGuardada); 
    } catch (Exception e) {
        // ¡ESTA LÍNEA ES CLAVE! Imprime el error real en tu consola de VS Code
        e.printStackTrace(); 
        
        return ResponseEntity.badRequest().build();
    }
}

    @GetMapping("/{id}")
    public ResponseEntity<Citas> buscarPorId(@PathVariable Integer id){
        try{
            return ResponseEntity.ok(service.buscarPorId(id));
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    

    @GetMapping
public ResponseEntity<List<DetalleCitaDTO>> listarCitas(){
    // Llamamos al método del servicio que arma la lista combinada
    List<DetalleCitaDTO> listaConDetalles = service.listaCitasConDetalle();

    if(listaConDetalles.isEmpty()){
        return ResponseEntity.noContent().build();
    } else {
        return ResponseEntity.ok(listaConDetalles);
    }
}

}
