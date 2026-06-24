package cl.banco.msCaja.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.banco.msCaja.model.Saldo;

@Repository
public interface SaldoRepository extends JpaRepository<Saldo, Long>{
    
    Optional<Saldo> findByClienteId(Long clienteId);

}
