package cl.duoc.citas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.citas.model.Citas;

@Repository
public interface CitasRepository extends JpaRepository<Citas, Integer> {

    List<Citas> findByClienteId(Integer clienteID);

    List<Citas> findByEjecutivoId(Integer clienteID);

    List<Citas> findByTipoCitas(Integer clienteID);

}
