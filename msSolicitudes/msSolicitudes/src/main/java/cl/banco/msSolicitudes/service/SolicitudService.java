package cl.banco.msSolicitudes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.banco.msSolicitudes.model.Solicitud;
import cl.banco.msSolicitudes.repository.SolicitudRepository;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository repo;

    // Listar solicitudes
    public List<Solicitud> listarSolicitudes() {
        return repo.findAll();
    }

    // Buscar por ID
    public Solicitud buscarPorId(Integer id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Solicitud no encontrada"));
    }

    // Buscar por Cliente
    public List<Solicitud> buscarPorCliente(
            Integer clienteId) {

        return repo.findByClienteId(clienteId);
    }

    // Buscar por Empleado
    public List<Solicitud> buscarPorEmpleado(
            Integer empleadoId) {

        return repo.findByEmpleadoId(empleadoId);
    }

    // Agregar solicitud
    public Solicitud agregarSolicitud(
            Solicitud solicitud) {

        if (solicitud.getEstado() == null ||
            solicitud.getEstado().isEmpty()) {

            solicitud.setEstado("PENDIENTE");
        }

        return repo.save(solicitud);
    }

    // Actualizar solicitud
    public Solicitud actualizar(
            Integer id,
            Solicitud solicitudActualizada) {

        Solicitud solicitud = buscarPorId(id);

        solicitud.setAsunto(
                solicitudActualizada.getAsunto());

        solicitud.setMensaje(
                solicitudActualizada.getMensaje());

        solicitud.setEstado(
                solicitudActualizada.getEstado());

        solicitud.setClienteId(
                solicitudActualizada.getClienteId());

        solicitud.setEmpleadoId(
                solicitudActualizada.getEmpleadoId());

        return repo.save(solicitud);
    }

    // Eliminar solicitud
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}