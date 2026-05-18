package cl.banco.msSolicitudes.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import cl.banco.msSolicitudes.dto.ClienteDTO;
import cl.banco.msSolicitudes.model.Solicitud;
import cl.banco.msSolicitudes.repository.SolicitudRepository;

@Service
public class SolicitudClienteService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String URL_CLIENTES =
            "http://localhost:8083/api/clientes/dto";

    public List<Map<String, Object>>
            obtenerSolicitudesConCliente() {

        List<Solicitud> solicitudes =
                solicitudRepository.findAll();

        List<Map<String, Object>> resultado =
                new ArrayList<>();

        for (Solicitud solicitud : solicitudes) {

            Map<String, Object> map =
                    new HashMap<>();

            map.put("id", solicitud.getId());
            map.put("asunto", solicitud.getAsunto());
            map.put("mensaje", solicitud.getMensaje());
            map.put("estado", solicitud.getEstado());
            map.put("clienteId",
                    solicitud.getClienteId());

            // CONSULTA CLIENTE
            if (solicitud.getClienteId() != null) {

                try {

                    ClienteDTO cliente =
                            restTemplate.getForObject(

                                    URL_CLIENTES + "/" +
                                    solicitud.getClienteId(),

                                    ClienteDTO.class
                            );

                    map.put("cliente", cliente);

                } catch (RestClientException ex) {

                    map.put("cliente",
                            "No disponible");
                }

            } else {

                map.put("cliente",
                        "Sin cliente");
            }

            resultado.add(map);
        }

        return resultado;
    }
}