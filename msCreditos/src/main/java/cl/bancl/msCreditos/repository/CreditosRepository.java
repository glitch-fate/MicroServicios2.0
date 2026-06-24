package cl.bancl.msCreditos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.bancl.msCreditos.model.Creditos;

@Repository
public interface CreditosRepository extends JpaRepository<Creditos, Long>{

    List<Creditos> findByRutCliente(String rutCliente);

}
