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

    //agregar un nuevo día de horario
    /*@PostMapping
    public ResponseEntity<HorariosEmpleado> nuevoHorario(@RequestBody HorariosEmpleado horario) {
    // Al quitar el try-catch, si algo falla, la consola te gritará la verdad
    HorariosEmpleado guardado = service.guardarHorario(horario);
    return ResponseEntity.ok(guardado); */


    @PostMapping
    public ResponseEntity<HorariosEmpleado> nuevoHorario(@RequestBody HorariosEmpleado horario) {
        try {
            HorariosEmpleado guardado = service.guardarHorario(horario);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // obtener la agenda del ejecutivo con su nombre desde msEmpleados
     @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<HorariosEmpleado>> obtenerHorariosPorEmpleado(@PathVariable Integer empleadoId) {
     List<HorariosEmpleado> horarios = service.buscarPorEmpleado(empleadoId);
     return ResponseEntity.ok(horarios);
}

    //desactivar un día específico pasando el id del registro y el motivo por parámetro
    /*@PutMapping("/{id}/desactivar")
    public ResponseEntity<HorariosEmpleado> desactivarHorario(@PathVariable Integer id, @RequestParam String motivo) {
        try {
            HorariosEmpleado modificado = service.desactivarPorLicencia(id, motivo);
            return ResponseEntity.ok(modificado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }*/

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<HorariosEmpleado> desactivarHorario(@PathVariable Integer id, @RequestParam String motivo) {
        try {
        HorariosEmpleado modificado = service.desactivarPorLicencia(id, motivo);
        return ResponseEntity.ok(modificado);
        } catch (Exception e) {
       
            e.printStackTrace(); // esto me ayuda a saber que salio mal en consola
        
        return ResponseEntity.badRequest().build();
        }
    }
}
