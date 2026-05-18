package cl.bancl.msTarjetas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import cl.bancl.msTarjetas.client.ClienteClient;
import cl.bancl.msTarjetas.model.Tarjetas;
import cl.bancl.msTarjetas.repository.TarjetaRepository;
import cl.bancl.msTarjetas.service.TarjetaService;


@RestController
@RequestMapping("/api/tarjetas")
public class TarjetasRepository {

    @Autowired
    private TarjetaService tarjetaService;

    @Autowired
    private TarjetaRepository repoTarjeta;

    @Autowired
    private ClienteClient clienteClient;

    
    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitarTarjeta(@RequestBody Tarjetas nuevaTarjeta) {
        
        System.out.println("funciono la cosa " + nuevaTarjeta.getRutCliente());
        
        Tarjetas tarjetaProcesada = tarjetaService.solicitarTarjeta(nuevaTarjeta);
        return new ResponseEntity<>(tarjetaProcesada, HttpStatus.CREATED);
    }

    
    @PutMapping("/{id}/activar")
    public ResponseEntity<Tarjetas> activarTarjeta(
            @PathVariable Long id, 
            @RequestParam String ejecutivo) {
        
        Tarjetas tarjetaActivada = tarjetaService.activarTarjeta(id, ejecutivo);
        
        try {
            var clienteDto = clienteClient.obtenerClientePorRut(tarjetaActivada.getRutCliente());
            tarjetaActivada.setNombreCliente(clienteDto.getNombre() + " " + clienteDto.getApellido());
        } catch (Exception e) {
            System.out.println("No se pudo mapear el nombre en el PUT: " + e.getMessage());
        }

        return ResponseEntity.ok(tarjetaActivada);
    }

   
    @GetMapping("/historial/{rut}")
    public ResponseEntity<List<Tarjetas>> consultarTarjetasPorRut(@PathVariable String rut) {
        
        List<Tarjetas> historial = repoTarjeta.findByRutCliente(rut);
        
        if (historial.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "No se encontraron tarjetas ni solicitudes para el RUT ingresado.");
        }
        
        
        String nombreCompleto = null;
        try {
            var clienteDto = clienteClient.obtenerClientePorRut(rut);
            nombreCompleto = clienteDto.getNombre() + " " + clienteDto.getApellido();
        } catch (Exception e) {
            System.out.println("no se pudo obtener el nombre en el get: " + e.getMessage());
        }

        if (nombreCompleto != null) {
            for (Tarjetas tarjeta : historial) {
                tarjeta.setNombreCliente(nombreCompleto);
            }
        }
        
        return ResponseEntity.ok(historial);
    }

}
