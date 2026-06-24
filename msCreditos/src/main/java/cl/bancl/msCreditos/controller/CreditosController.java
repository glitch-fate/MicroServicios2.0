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

import cl.bancl.msCreditos.client.ClienteClient;
import cl.bancl.msCreditos.model.Creditos;
import cl.bancl.msCreditos.service.CreditoService;
import cl.bancl.msCreditos.repository.CreditosRepository;

@RestController
@RequestMapping("/api/creditos")
public class CreditosController {

    @Autowired
    private CreditoService creditoService;

    @Autowired
    private CreditosRepository repoCredito; 

    @Autowired
    private ClienteClient clienteClient;

    // 1. SOLICITAR CRÉDITO: Retorna 201 Created si tiene éxito
    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitarCredito(@RequestBody Creditos nuevoCredito) {
        try {
            Creditos creditoProcesado = creditoService.solicitarCredito(nuevoCredito);
            return ResponseEntity.status(HttpStatus.CREATED).body(creditoProcesado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
      
    // 2. PAGAR CRÉDITO: Atrapa excepciones de pago de cuotas de forma segura
    @PutMapping("/pagar-por-rut")
    public ResponseEntity<?> pagarCreditoPorRut(@RequestParam String rut, @RequestParam Double monto) {
        try {
            List<Creditos> listaCreditos = repoCredito.findByRutCliente(rut);
            
            if (listaCreditos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron créditos para el RUT ingresado.");
            }
            
            Creditos creditoActivo = listaCreditos.stream()
                    .filter(c -> "VIGENTE".equals(c.getEstado()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("El cliente no tiene créditos VIGENTES pendientes de pago."));

            Creditos creditoActualizado = creditoService.pagarCuota(creditoActivo.getId(), monto);
            
            try {
                var clienteDto = clienteClient.obtenerClientePorRut(rut);
                String nombreCompleto = clienteDto.getNombre() + " " + clienteDto.getApellido();
                creditoActualizado.setNombreCliente(nombreCompleto);
            } catch (Exception e) {
                System.out.println("No se pudo rescatar el nombre del cliente para el JSON de salida: " + e.getMessage());
            }
            
            return ResponseEntity.ok(creditoActualizado);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
      
    // 3. CONSULTAR HISTORIAL: Retorna 204 No Content si el cliente no registra créditos
    @GetMapping("/historial/{rut}")
    public ResponseEntity<?> consultarCreditosPorRut(@PathVariable String rut) {
        try {
            List<Creditos> historial = repoCredito.findByRutCliente(rut);
            
            // Si la lista está vacía, aplicamos la regla estricta de evaluación: HTTP 204 No Content
            if (historial.isEmpty()) {
                return ResponseEntity.noContent().build(); // (HTTP 204)
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

        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}