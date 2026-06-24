package cl.banco.msCaja.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.banco.msCaja.dto.MovimientoDTO;
import cl.banco.msCaja.dto.ResumenSaldoDTO;
import cl.banco.msCaja.model.Transaccion;
import cl.banco.msCaja.service.CajaService;

@RestController
@RequestMapping("/api/caja")
public class CajaController {

    @Autowired
    private CajaService cajaService;

    // 1. VER SALDO: Retorna 200 OK, 404 si no existe, o 204 No Content si viene vacío o nulo
    @GetMapping("/saldo/{rut}")
    public ResponseEntity<ResumenSaldoDTO> verSaldo(@PathVariable String rut) {
        try {
            ResumenSaldoDTO resumen = cajaService.obtenerResumenSaldo(rut);
            
            // Si el servicio responde con nulo o un objeto sin saldos, aplicamos el estándar académico del 204
            if (resumen == null) {
                return ResponseEntity.noContent().build(); // HTTP 204 No Content
            }
            
            return ResponseEntity.ok(resumen); // HTTP 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // HTTP 404 Not Found
        }
    }

    // 2. DEPOSITAR: Protegido con try-catch para capturar errores de cuenta/RUT inexistente
    @PostMapping("/deposito")
    public ResponseEntity<?> depositar(@RequestBody MovimientoDTO dto) {
        try {
            Transaccion t = cajaService.depositarPorRut(dto.getRut(), dto.getMonto());
            return ResponseEntity.ok(t); // HTTP 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // HTTP 400 Bad Request
        }
    }

    // 3. RETIRAR: Captura RuntimeException (fondos insuficientes, RUT inválido, etc.)
    @PostMapping("/retiro")
    public ResponseEntity<?> retirar(@RequestBody MovimientoDTO dto) {
        try {
            Transaccion t = cajaService.retirarPorRut(dto.getRut(), dto.getMonto());
            return ResponseEntity.ok(t); // HTTP 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // HTTP 400 Bad Request
        }
    }
}