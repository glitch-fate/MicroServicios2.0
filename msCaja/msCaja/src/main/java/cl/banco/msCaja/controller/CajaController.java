package cl.banco.msCaja.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/saldo/{rut}")
    public ResponseEntity<ResumenSaldoDTO> verSaldo(@PathVariable String rut) {
        return ResponseEntity.ok(cajaService.obtenerResumenSaldo(rut));
    }

    @PostMapping("/deposito")
    public ResponseEntity<?> depositar(@RequestBody MovimientoDTO dto) {
        Transaccion t = cajaService.depositarPorRut(dto.getRut(), dto.getMonto());
        return ResponseEntity.ok(t);
    }

    @PostMapping("/retiro")
    public ResponseEntity<?> retirar(@RequestBody MovimientoDTO dto) {
        try {
            Transaccion t = cajaService.retirarPorRut(dto.getRut(), dto.getMonto());
            return ResponseEntity.ok(t);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
