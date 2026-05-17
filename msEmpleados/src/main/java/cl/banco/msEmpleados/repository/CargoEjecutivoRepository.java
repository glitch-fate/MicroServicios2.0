package cl.banco.msEmpleados.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.banco.msEmpleados.model.CargoEjecutivo;

@Repository
public interface CargoEjecutivoRepository extends JpaRepository<CargoEjecutivo, Integer> {

    Optional<CargoEjecutivo> findByNombre(String nombre);


}
