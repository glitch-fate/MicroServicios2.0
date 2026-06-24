package cl.bancl.msHorarios.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.bancl.msHorarios.model.HorariosEmpleado;

@Repository
public interface HorariosEmpleadoRepository extends JpaRepository<HorariosEmpleado, Integer> {

    List<HorariosEmpleado> findByEmpleadoIdAndActivoTrue(Integer empleadoId); 
    //buscamos solo empleados activos por su id

    List<HorariosEmpleado> findByEmpleadoId(Integer empleadoId);



}
