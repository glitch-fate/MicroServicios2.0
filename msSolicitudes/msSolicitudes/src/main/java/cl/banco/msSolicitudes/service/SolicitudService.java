package cl.banco.msSolicitudes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.banco.msSolicitudes.Client.ClienteClient;
import cl.banco.msSolicitudes.dto.ClienteDTO;
import cl.banco.msSolicitudes.model.Solicitud;
import cl.banco.msSolicitudes.repository.SolicitudRepository;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private ClienteClient clienteClient; 

    public Solicitud crearSolicitud(String rut, String motivo, String mensaje) {
        ClienteDTO cliente = clienteClient.obtenerClientePorRut(rut);
        
        Solicitud nuevaSolicitud = new Solicitud(cliente.getId(), motivo, mensaje);
        
        return solicitudRepository.save(nuevaSolicitud);
    }

    public List<Solicitud> obtenerSolicitudesPorRut(String rut) {
        ClienteDTO cliente = clienteClient.obtenerClientePorRut(rut);
        
        return solicitudRepository.findByClienteId(cliente.getId());
    }

    public void eliminarSolicitud(Long id) {
        if (!solicitudRepository.existsById(id)) {
            throw new IllegalArgumentException("La solicitud con el ID " + id + " no existe.");
        }
        solicitudRepository.deleteById(id);
    }

}
