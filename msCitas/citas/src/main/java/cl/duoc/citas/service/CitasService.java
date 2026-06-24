package cl.duoc.citas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import cl.duoc.citas.client.ClienteClient;
import cl.duoc.citas.client.EmpleadosClient;
import cl.duoc.citas.dto.ClienteDTO;
import cl.duoc.citas.dto.DetalleCitaDTO;
import cl.duoc.citas.dto.EjecutivosDTO;
import cl.duoc.citas.dto.TipoCitasDTO;
import cl.duoc.citas.model.Citas;

import cl.duoc.citas.repository.CitasRepository;
import cl.duoc.citas.repository.TipoCitasRepository;

@Service
public class CitasService {

    @Autowired
    private CitasRepository citasRepository;

    @Autowired
    private ClienteClient clienteClient;

    @Autowired
    private TipoCitasRepository tipoCitasRepo;

    @Autowired
    private EmpleadosClient empleadosClient;

    //acá falta unir las cosas que debe hacer el compañero


    //metodos 

    public List<Citas> listaCitas(){
       return citasRepository.findAll();
    }

    //editare esta

public Citas guardarCita(Citas cita) {
    try {
    
        clienteClient.obtenerClienteDTO(cita.getClienteId());

        
        System.out.println("ID DEL CLIENTE: " + cita.getClienteId());
        System.out.println(" ID DEL EJECUTIVO ENVIADO A FEIGN: " + cita.getEjecutivoId());

     
        empleadosClient.obtenerEjecutivosDTO(cita.getEjecutivoId());

       
        if (cita.getTipoCitas() != null && cita.getTipoCitas().getNombre() != null) {
            String nombreTipo = cita.getTipoCitas().getNombre().trim();

           
            cl.duoc.citas.model.TipoCitas tipoFinal = tipoCitasRepo.findByNombre(nombreTipo)
                .orElseGet(() -> {
                    // Si no existe, creamos el tipo de cita nuevo al vuelo
                    cl.duoc.citas.model.TipoCitas nuevoTipo = new cl.duoc.citas.model.TipoCitas();
                    nuevoTipo.setNombre(nombreTipo);
                    System.out.println("El tipo de cita '" + nombreTipo + "' no existía. Creándolo en la BD...");
                    return tipoCitasRepo.save(nuevoTipo);
                });

            // Se lo asignamos a la cita con su ID correspondiente
            cita.setTipoCitas(tipoFinal);
        } else {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, 
                "El tipo de cita debe incluir un nombre válido."
            );
        }

       
        return citasRepository.save(cita);

    } catch (feign.FeignException.NotFound e) {
        
        System.out.println(" URL DE FEIGN QUE DETECTÓ EL ERROR: " + e.request().url());
        
        throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.BAD_REQUEST,
            "No se puede agendar: El cliente o el ejecutivo especificado no existe."
        );
    }
}

    

    

    public Citas buscarPorId(Integer id){
        return citasRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro"));
    }


    public List<DetalleCitaDTO> listaCitasConDetalle() {
    List<Citas> citasLocales = citasRepository.findAll();

    return citasLocales.stream().map(cita -> {
        DetalleCitaDTO detalle = new DetalleCitaDTO();
        detalle.setId(cita.getId());
        detalle.setFechaCita(cita.getFechaCita());
        detalle.setHoraCita(cita.getHora()); 

      
        try {
            if (cita.getClienteId() != null) {
                ClienteDTO cliente = clienteClient.obtenerClienteDTO(cita.getClienteId());
                detalle.setCliente(cliente);
            }
        } catch (Exception e) {
            System.out.println("No se pudo obtener el cliente para la cita ID " + cita.getId() + ": " + e.getMessage());
        }

        try {
            if (cita.getEjecutivoId() != null) { 
                EjecutivosDTO ejecutivo = empleadosClient.obtenerEjecutivosDTO(cita.getEjecutivoId());
                detalle.setEjecutivos(ejecutivo);
            }
        } catch (Exception e) {
            System.out.println("No se pudo obtener el ejecutivo para la cita ID " + cita.getId() + ": " + e.getMessage());
        }

       
        if (cita.getTipoCitas() != null) {
            TipoCitasDTO tipoDTO = new TipoCitasDTO();
            tipoDTO.setId(cita.getTipoCitas().getId());
            tipoDTO.setNombre(cita.getTipoCitas().getNombre());
            detalle.setTipoCitas(tipoDTO);
            
         
            detalle.setMotivoCita(cita.getTipoCitas().getNombre()); 
        }

        return detalle;
    }).collect(Collectors.toList());
}
    
}













