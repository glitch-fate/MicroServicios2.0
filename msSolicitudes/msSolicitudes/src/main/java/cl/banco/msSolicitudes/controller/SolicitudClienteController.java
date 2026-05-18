package cl.banco.msSolicitudes.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.banco.msSolicitudes.service.SolicitudClienteService;

@RestController
public class SolicitudClienteController {

    @Autowired
    private SolicitudClienteService service;

    @GetMapping("/api/solicitudes/clientes")
    public List<Map<String, Object>>
            obtenerSolicitudesConCliente() {

        return service
                .obtenerSolicitudesConCliente();
    }
}