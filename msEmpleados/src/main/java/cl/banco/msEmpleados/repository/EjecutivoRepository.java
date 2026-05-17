package cl.banco.msEmpleados.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.banco.msEmpleados.model.Ejecutivo;

@Repository
public interface EjecutivoRepository extends JpaRepository<Ejecutivo, Integer>{

    Optional<Ejecutivo> findByRut(String rut);
    //investigar si puedo agregarle findbyid

}
