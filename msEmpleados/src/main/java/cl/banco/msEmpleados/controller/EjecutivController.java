package cl.banco.msEmpleados.controller;

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

import cl.banco.msEmpleados.dto.EjecutivoDTO;

import cl.banco.msEmpleados.model.Ejecutivo;
import cl.banco.msEmpleados.service.EjecutivoService;

@RestController
@RequestMapping("/api/ejecutivos")
public class EjecutivController {

    @Autowired
    private EjecutivoService service;

    //metodo para mostrar toda la lista de ejecutivos
    @GetMapping
    public ResponseEntity<List<Ejecutivo>> listar(){
        List<Ejecutivo> lista = service.listar();
        if (lista.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    //metodo buscar por Id
    @GetMapping("/{id}")
    public ResponseEntity<Ejecutivo> buscarPorId(@PathVariable Integer id){
        try{
            return ResponseEntity.ok(service.buscarPorId(id));
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    //metodo para guardar nuevo ejecutivo
    @PostMapping
    public ResponseEntity<Ejecutivo> guardar(@RequestBody Ejecutivo ejecutivo){
        return ResponseEntity.ok(service.guardar(ejecutivo));
    }
    

    
    //metodo para eliminar ejecutivo por Id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Integer id){
        try{
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    //falta agregar el DTO
    @GetMapping("/dto/{id}")
    public ResponseEntity<EjecutivoDTO> obtenerEmpleadoDTO(@PathVariable Integer id){
        Ejecutivo ejecutivo = service.buscarPorId(id);
        EjecutivoDTO dto = new EjecutivoDTO(
            ejecutivo.getId(), ejecutivo.getNombre(), ejecutivo.getCargo().getNombre()); //con este solo obtenemos el nombre del cargo 
                 return ResponseEntity.ok(dto);
       
    }



}
