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
    private CreditosRepository repoCredito; // Lo necesitamos para buscar por RUT antes de pagar

    @Autowired
    private ClienteClient clienteClient;

    
     
      // metodo para solicitar un credito url: http://localhost:8086/api/creditos/solicitar  
     
    @PostMapping("/solicitar")
    public ResponseEntity<Creditos> solicitarCredito(@RequestBody Creditos nuevoCredito) {
        // Tu servicio ya maneja los try-catch y las excepciones internamente
        Creditos creditoProcesado = creditoService.solicitarCredito(nuevoCredito);
        return ResponseEntity.status(HttpStatus.CREATED).body(creditoProcesado);
    }

    //PAGAR / ABONAR CRÉDITO USANDO EL RUT (PUT) URL: http://localhost:8086/api/creditos/pagar-por-rut
      
    @PutMapping("/pagar-por-rut")
    public ResponseEntity<Creditos> pagarCreditoPorRut(@RequestParam String rut, @RequestParam Double monto) {
        
        // 1. Buscamos las operaciones asociadas a ese RUT usando el método corregido del repositorio
        List<Creditos> listaCreditos = repoCredito.findByRutCliente(rut);
        
        // 2. Validamos si el cliente tiene algún crédito
        if (listaCreditos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron créditos para el RUT ingresado.");
        }
        
        // 3. Buscamos el crédito que esté "VIGENTE" para aplicarle el pago
        Creditos creditoActivo = listaCreditos.stream()
                .filter(c -> "VIGENTE".equals(c.getEstado()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cliente no tiene créditos VIGENTES pendientes de pago."));

        // Con el ID del crédito encontrado, llamamos a tu método del Service tal cual lo programaste
        Creditos creditoActualizado = creditoService.pagarCuota(creditoActivo.getId(), monto);
        
        //Recuperamos el nombre en tiempo real mediante Feign para rellenar la variable @Transient
        try {
            var clienteDto = clienteClient.obtenerClientePorRut(rut);
            String nombreCompleto = clienteDto.getNombre() + " " + clienteDto.getApellido();
            creditoActualizado.setNombreCliente(nombreCompleto);
        } catch (Exception e) {
            // Si por alguna razón falla la comunicación con msClientes, 
            // el pago ya se guardó en la BD igual, así que no frenamos la respuesta.
            System.out.println("No se pudo rescatar el nombre del cliente para el JSON de salida: " + e.getMessage());
        }
        
        return ResponseEntity.ok(creditoActualizado);
    }
    
    //http://localhost:8086/api/creditos/historial/{rut}
     
      
     
    @GetMapping("/historial/{rut}")
    public ResponseEntity<List<Creditos>> consultarCreditosPorRut(@PathVariable String rut) {
        
        // 1. Buscamos todos los registros asociados a ese RUT en la BD de créditos
        List<Creditos> historial = repoCredito.findByRutCliente(rut);
        
        if (historial.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "No se registraron solicitudes de crédito para el RUT ingresado.");
        }
        
        //nombre del cliente una sola vez mediante Feign
        String nombreCompleto = null;
        try {
            var clienteDto = clienteClient.obtenerClientePorRut(rut);
            nombreCompleto = clienteDto.getNombre() + " " + clienteDto.getApellido();
        } catch (Exception e) {
            System.out.println("No se pudo conectar con msClientes para obtener el nombre: " + e.getMessage());
        }

        //asignamos el nombre recuperado a cada crédito de la lista antes de mandarlo a Postman
        if (nombreCompleto != null) {
            for (Creditos credito : historial) {
                credito.setNombreCliente(nombreCompleto);
            }
        }
        
        // Retornamos la lista con los nombres ya parchados en tiempo real
        return ResponseEntity.ok(historial);
    }

}
