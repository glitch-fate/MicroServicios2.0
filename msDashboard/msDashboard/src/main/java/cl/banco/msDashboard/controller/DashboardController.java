package cl.banco.msDashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.banco.msDashboard.dto.ResumenClienteDTO;
import cl.banco.msDashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor 
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/cliente/{rut}")
    public ResponseEntity<ResumenClienteDTO> obtenerDashboardCliente(@PathVariable String rut) {
        ResumenClienteDTO resumen = dashboardService.obtenerResumen360(rut);
        return ResponseEntity.ok(resumen);
    }
}