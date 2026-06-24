package cl.bancl.msTarjetas.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.bancl.msTarjetas.client.ClienteClient;
import cl.bancl.msTarjetas.dto.ClientesDTO;
import cl.bancl.msTarjetas.model.Tarjetas;
import cl.bancl.msTarjetas.repository.TarjetaRepository;

@Service
public class TarjetaService {

    @Autowired
    private TarjetaRepository repoTarjeta;

    @Autowired
    private ClienteClient clienteClient; 

    public Tarjetas solicitarTarjeta(Tarjetas solicitud) {
        try {
            
            ClientesDTO clienteDto = clienteClient.obtenerClientePorRut(solicitud.getRutCliente());
            
            String nombreCompleto = clienteDto.getNombre() + " " + clienteDto.getApellido();
            solicitud.setNombreCliente(nombreCompleto);
            
            var tarjetaExistente = repoTarjeta.findByRutClienteAndTipoTarjeta(
                    solicitud.getRutCliente(), 
                    solicitud.getTipoTarjeta().toUpperCase()
            );
            
            if (tarjetaExistente.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "error: El cliente ya posee una tarjeta de tipo " + solicitud.getTipoTarjeta());
            }

            solicitud.setTipoTarjeta(solicitud.getTipoTarjeta().toUpperCase());
            solicitud.setNumeroTarjeta(generarNumeroTarjetaAleatorio());
            solicitud.setEstado("SOLICITADA"); 
            solicitud.setAprobadoPor(null);   

            if (solicitud.getCupoTotal() == null || solicitud.getCupoTotal() == 0) {
                if ("CREDITO".equals(solicitud.getTipoTarjeta())) {
                    solicitud.setCupoTotal(500000.0);
                } else if ("BLACK".equals(solicitud.getTipoTarjeta())) {
                    solicitud.setCupoTotal(2000000.0);
                } else {
                    solicitud.setCupoTotal(0.0); 
                }
            }
            solicitud.setCupoDisponible(solicitud.getCupoTotal());

            return repoTarjeta.save(solicitud);

        } catch (feign.FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Error: El cliente con el RUT ingresado no existe en el sistema.");
        } catch (ResponseStatusException e) {
            throw e; 
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error de comunicación con el servicio de clientes.");
        }
    }

    public Tarjetas activarTarjeta(Long idTarjeta, String nombreEjecutivo) {
        Tarjetas tarjeta = repoTarjeta.findById(idTarjeta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarjeta no encontrada"));

        if ("ACTIVA".equals(tarjeta.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta tarjeta ya se encuentra activa.");
        }

        tarjeta.setEstado("ACTIVA");
        tarjeta.setAprobadoPor(nombreEjecutivo);

        return repoTarjeta.save(tarjeta);
    }

    private String generarNumeroTarjetaAleatorio() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("4"); 
        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
