package cl.banco.msEmpleados.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.banco.msEmpleados.model.CargoEjecutivo;
import cl.banco.msEmpleados.model.Ejecutivo;
import cl.banco.msEmpleados.repository.CargoEjecutivoRepository;
import cl.banco.msEmpleados.repository.EjecutivoRepository;
import jakarta.transaction.Transactional;

@Service
public class EjecutivoService {

    @Autowired
    private EjecutivoRepository repoEjecutivo;

    @Autowired
    private CargoEjecutivoRepository repoCargo;


    public List<Ejecutivo> listar(){
        return repoEjecutivo.findAll();
    }

    public Ejecutivo buscarPorId(Integer id){
        return repoEjecutivo.findById(id).orElseThrow(() -> new RuntimeException("Ejecutivo no encontrado"));
    }


    //guardar?
    @Transactional 
    public Ejecutivo guardar(Ejecutivo ejecutivo) {
        
        // se valida que traiga un cargo
        if (ejecutivo.getCargo() != null && ejecutivo.getCargo().getNombre() != null) {
            String nombreCargo = ejecutivo.getCargo().getNombre().trim();

            // 2. Buscamos en la BD si ya existe un cargo con ese nombre exacto
            CargoEjecutivo cargoFinal = repoCargo.findByNombre(nombreCargo)
                .orElseGet(() -> {
                    // 3. Si NO existe (orElseGet), creamos uno nuevo, lo guardamos y lo retornamos
                    CargoEjecutivo nuevoCargo = new CargoEjecutivo();
                    nuevoCargo.setNombre(nombreCargo);
                    System.out.println("El cargo '" + nombreCargo + " no existía. Creando uno nuevo");
                    return repoCargo.save(nuevoCargo);
                });
            
            ejecutivo.setCargo(cargoFinal);
        } else {
            throw new IllegalArgumentException("El ejecutivo debe tener un cargo asignado");
        }

        
        return repoEjecutivo.save(ejecutivo);
    }


    public void eliminar(Integer id){
        if (!repoEjecutivo.existsById(id)){
            throw new RuntimeException("Ejecutivo no Existe");
        }
        repoEjecutivo.deleteById(id);
    }

}


