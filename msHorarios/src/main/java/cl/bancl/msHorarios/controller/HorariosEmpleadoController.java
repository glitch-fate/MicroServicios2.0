package cl.bancl.msHorarios.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import cl.bancl.msHorarios.model.HorariosEmpleado;
import cl.bancl.msHorarios.service.HorarioEmpleadoService;

@RestController
@RequestMapping("/api/horarios")
public class HorariosEmpleadoController {

    @Autowired
    private HorarioEmpleadoService service;


    @PostMapping
    public ResponseEntity<HorariosEmpleado> nuevoHorario(@RequestBody HorariosEmpleado horario) {
        try {
            HorariosEmpleado guardado = service.guardarHorario(horario);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

     @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<HorariosEmpleado>> obtenerHorariosPorEmpleado(@PathVariable Integer empleadoId) {
     List<HorariosEmpleado> horarios = service.buscarPorEmpleado(empleadoId);
     return ResponseEntity.ok(horarios);
}



    @PutMapping("/{id}/desactivar")
    public ResponseEntity<HorariosEmpleado> desactivarHorario(@PathVariable Integer id, @RequestParam String motivo) {
        try {
        HorariosEmpleado modificado = service.desactivarPorLicencia(id, motivo);
        return ResponseEntity.ok(modificado);
        } catch (Exception e) {
       
            e.printStackTrace(); 
        
        return ResponseEntity.badRequest().build();
        }
    }
}
