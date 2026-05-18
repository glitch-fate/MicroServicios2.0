package cl.bancl.msTarjetas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.bancl.msTarjetas.model.Tarjetas;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjetas, Long>{

    List<Tarjetas> findByRutCliente(String rutCliente);

    Optional<Tarjetas> findByRutClienteAndTipoTarjeta(String rutCliente, String tipoTarjeta);

}
