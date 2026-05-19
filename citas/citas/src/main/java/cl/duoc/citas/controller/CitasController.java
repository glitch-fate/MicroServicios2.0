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
        return ResponseEntity.ok(citaGuardada); 
     } catch (Exception e) {
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
        List<DetalleCitaDTO> listaConDetalles = service.listaCitasConDetalle();

    if(listaConDetalles.isEmpty()){
        return ResponseEntity.noContent().build();
    } else {
        return ResponseEntity.ok(listaConDetalles);
    }
}

}
