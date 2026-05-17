package cl.duoc.citas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.citas.model.TipoCitas;

@Repository
public interface TipoCitasRepository extends JpaRepository<TipoCitas, Integer> {

    Optional<TipoCitas> findByNombre(String nombre);

}
