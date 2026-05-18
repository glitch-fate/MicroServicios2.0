package cl.banco.msSeguros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.banco.msSeguros.model.Seguro;


@Repository
public interface SeguroRepository extends JpaRepository<Seguro, Long> {

    List<Seguro> findByClienteId(Long clienteId);

}
