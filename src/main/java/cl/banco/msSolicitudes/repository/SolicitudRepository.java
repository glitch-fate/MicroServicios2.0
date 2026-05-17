package cl.banco.msSolicitudes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.banco.msSolicitudes.model.Solicitud;

@Repository
public interface SolicitudRepository
        extends JpaRepository<Solicitud, Integer> {

    List<Solicitud> findByClienteId(Integer clienteId);

    List<Solicitud> findByEmpleadoId(Integer empleadoId);
}