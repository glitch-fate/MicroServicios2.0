package cl.banco.msClientes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.banco.msClientes.model.Direccion;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {

    Optional<Direccion> findByClienteId(Integer clienteId);
}
