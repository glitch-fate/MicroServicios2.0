package cl.bancl.msCreditos.controller;

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

import cl.bancl.msCreditos.client.ClienteClient;
import cl.bancl.msCreditos.model.Creditos;
import cl.bancl.msCreditos.repository.CreditosRepository;
import cl.bancl.msCreditos.service.CreditoService;

@RestController
@RequestMapping("/api/creditos")
public class CreditosController {

    @Autowired
    private CreditoService creditoService;

    @Autowired
    private CreditosRepository repoCredito; 

    @Autowired
    private ClienteClient clienteClient;

     
    @PostMapping("/solicitar")
    public ResponseEntity<Creditos> solicitarCredito(@RequestBody Creditos nuevoCredito) {
        Creditos creditoProcesado = creditoService.solicitarCredito(nuevoCredito);
        return ResponseEntity.status(HttpStatus.CREATED).body(creditoProcesado);
    }
      
    @PutMapping("/pagar-por-rut")
    public ResponseEntity<Creditos> pagarCreditoPorRut(@RequestParam String rut, @RequestParam Double monto) {
        
        List<Creditos> listaCreditos = repoCredito.findByRutCliente(rut);
        
        if (listaCreditos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron créditos para el RUT ingresado.");
        }
        
        Creditos creditoActivo = listaCreditos.stream()
                .filter(c -> "VIGENTE".equals(c.getEstado()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cliente no tiene créditos VIGENTES pendientes de pago."));

        Creditos creditoActualizado = creditoService.pagarCuota(creditoActivo.getId(), monto);
        
    
        try {
            var clienteDto = clienteClient.obtenerClientePorRut(rut);
            String nombreCompleto = clienteDto.getNombre() + " " + clienteDto.getApellido();
            creditoActualizado.setNombreCliente(nombreCompleto);
        } catch (Exception e) {

            System.out.println("No se pudo rescatar el nombre del cliente para el JSON de salida: " + e.getMessage());
        }
        
        return ResponseEntity.ok(creditoActualizado);
    }
     
    @GetMapping("/historial/{rut}")
    public ResponseEntity<List<Creditos>> consultarCreditosPorRut(@PathVariable String rut) {
        
        List<Creditos> historial = repoCredito.findByRutCliente(rut);
        
        if (historial.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "No se registraron solicitudes de crédito para el RUT ingresado.");
        }
        
        String nombreCompleto = null;
        try {
            var clienteDto = clienteClient.obtenerClientePorRut(rut);
            nombreCompleto = clienteDto.getNombre() + " " + clienteDto.getApellido();
        } catch (Exception e) {
            System.out.println("No se pudo conectar con msClientes para obtener el nombre: " + e.getMessage());
        }

        if (nombreCompleto != null) {
            for (Creditos credito : historial) {
                credito.setNombreCliente(nombreCompleto);
            }
        }
        
        return ResponseEntity.ok(historial);
    }

}
