package cl.banco.msSeguros.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.banco.msSeguros.client.ClienteClient;
import cl.banco.msSeguros.dto.ClienteDTO;
import cl.banco.msSeguros.dto.SeguroRespuestaDTO;
import cl.banco.msSeguros.model.Seguro;
import cl.banco.msSeguros.repository.SeguroRepository;

@Service
public class SeguroService {

    @Autowired
    private SeguroRepository seguroRepository;

    @Autowired
    private ClienteClient clienteClient;

   
  // Crear seguro
    public Seguro crearSeguro(Seguro seguro) {
        seguro.setFechaContratacion(java.time.LocalDate.now());
        seguro.setEstado("ACTIVO");
        return seguroRepository.save(seguro);
    }

    // Listar todos 
    public List<SeguroRespuestaDTO> obtenerTodos() {
        List<Seguro> seguros = seguroRepository.findAll();
        List<SeguroRespuestaDTO> respuesta = new ArrayList<>();

        for (Seguro s : seguros) {
            respuesta.add(mapearADTO(s)); // Llama al método de abajo sin guion bajo
        }
        return respuesta;
    }

    // Buscar por ID
    public SeguroRespuestaDTO obtenerPorId(Long id) {
        Optional<Seguro> opcional = seguroRepository.findById(id);
        if (opcional.isPresent()) {
            return mapearADTO(opcional.get());
        }
        return null;
    }

    // Listar por cliente
    public List<Seguro> obtenerPorCliente(Long clienteId) {
        return seguroRepository.findByClienteId(clienteId);
    }

    // Cancelar seguro
    public Seguro cancelarSeguro(Long id) {
        Optional<Seguro> opcional = seguroRepository.findById(id);
        if (opcional.isPresent()) {
            Seguro seguro = opcional.get();
            seguro.setEstado("INACTIVO");
            return seguroRepository.save(seguro);
        }
        return null;
    }

    // El método que junta todo (Asegúrate de que se llame igual que en el ciclo for)
    private SeguroRespuestaDTO mapearADTO(Seguro seguro) {
        SeguroRespuestaDTO dto = new SeguroRespuestaDTO();
        dto.setId(seguro.getId());
        dto.setNumeroPoliza(seguro.getNumeroPoliza());
        dto.setTipoSeguro(seguro.getTipoSeguro());
        dto.setMontoAsegurado(seguro.getMontoAsegurado());
        dto.setPrimaMensual(seguro.getPrimaMensual());
        dto.setFechaContratacion(seguro.getFechaContratacion());
        dto.setEstado(seguro.getEstado());

        try {
            ClienteDTO clienteDto = clienteClient.obtenerClientePorId(seguro.getClienteId());
            dto.setCliente(clienteDto);
        } catch (Exception e) {
            ClienteDTO errorCliente = new ClienteDTO();
            errorCliente.setId(seguro.getClienteId());
            errorCliente.setNombre("No disponible");
            dto.setCliente(errorCliente);
        }
        return dto;
    }
}
