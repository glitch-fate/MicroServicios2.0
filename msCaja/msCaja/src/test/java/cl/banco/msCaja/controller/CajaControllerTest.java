package cl.banco.msCaja.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.banco.msCaja.dto.ResumenSaldoDTO;
import cl.banco.msCaja.model.Transaccion;
import cl.banco.msCaja.service.CajaService;

@WebMvcTest(CajaController.class)
public class CajaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CajaService cajaService;

    private ResumenSaldoDTO resumenEjemplo;
    private Transaccion transaccionDeposito;
    private Transaccion transaccionRetiro;

    @BeforeEach
    void setUp() {
        // 1. Poblamos el ResumenSaldoDTO basado en tu imagen (nombreCompleto, rut, saldoTotal)
        resumenEjemplo = new ResumenSaldoDTO();
        resumenEjemplo.setNombreCompleto("Raúl Jara");
        resumenEjemplo.setRut("12345678-9");
        resumenEjemplo.setSaldoTotal(150000.0);

        // 2. Poblamos una simulación de Transacción para Depósito
        transaccionDeposito = new Transaccion();
        transaccionDeposito.setId(100L);
        transaccionDeposito.setClienteId(1L);
        transaccionDeposito.setTipoTransaccion("DEPOSITO");
        transaccionDeposito.setMonto(50000.0);
        transaccionDeposito.setFechaHora(LocalDateTime.now());

        // 3. Poblamos una simulación de Transacción para Retiro
        transaccionRetiro = new Transaccion();
        transaccionRetiro.setId(101L);
        transaccionRetiro.setClienteId(1L);
        transaccionRetiro.setTipoTransaccion("RETIRO");
        transaccionRetiro.setMonto(20000.0);
        transaccionRetiro.setFechaHora(LocalDateTime.now());
    }
    //          PRUEBAS PARA VER SALDO  
    @Test
    void verSaldo_Retorna200Ok_CuandoRutExiste() throws Exception {
        when(cajaService.obtenerResumenSaldo("12345678-9")).thenReturn(resumenEjemplo);

        mockMvc.perform(get("/api/caja/saldo/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Raúl Jara"))
                .andExpect(jsonPath("$.rut").value("12345678-9"))
                .andExpect(jsonPath("$.saldoTotal").value(150000.0));
    }

    @Test
    void verSaldo_Retorna204NoContent_CuandoNoRegistraSaldosNiMovimientos() throws Exception {
        // Simulamos que el cliente existe en el sistema pero no devuelve datos de saldo (mapeo nulo)
        when(cajaService.obtenerResumenSaldo("11222333-4")).thenReturn(null);

        mockMvc.perform(get("/api/caja/saldo/11222333-4"))
                .andExpect(status().isNoContent()); // Valida directamente el HTTP 204 No Content
    }

    @Test
    void verSaldo_Retorna404NotFound_CuandoRutNoExiste() throws Exception {
        // Al lanzar RuntimeException, el try-catch de nuestro controlador responderá 404
        when(cajaService.obtenerResumenSaldo("99999999-9"))
                .thenThrow(new RuntimeException("Cliente no encontrado"));

        mockMvc.perform(get("/api/caja/saldo/99999999-9"))
                .andExpect(status().isNotFound());
    }

      //          PRUEBAS PARA DEPÓSITO  

    @Test
    void depositar_Retorna200Ok_CuandoEsValido() throws Exception {
        when(cajaService.depositarPorRut(eq("12345678-9"), eq(50000.0)))
                .thenReturn(transaccionDeposito);

        // Enviamos el JSON correspondiente a tu MovimientoDTO (rut, monto)
        mockMvc.perform(post("/api/caja/deposito")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rut\":\"12345678-9\",\"monto\":50000.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.tipoTransaccion").value("DEPOSITO"))
                .andExpect(jsonPath("$.monto").value(50000.0));
    }

    @Test
    void depositar_Retorna400BadRequest_CuandoFallaServicio() throws Exception {
        when(cajaService.depositarPorRut(any(), any()))
                .thenThrow(new RuntimeException("RUT no asociado a ninguna cuenta activa"));

        mockMvc.perform(post("/api/caja/deposito")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rut\":\"99999999-9\",\"monto\":50000.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("RUT no asociado a ninguna cuenta activa"));
    }

      //           PRUEBAS PARA RETIRO  

    @Test
    void retirar_Retorna200Ok_CuandoHayFondosSuficientes() throws Exception {
        when(cajaService.retirarPorRut(eq("12345678-9"), eq(20000.0)))
                .thenReturn(transaccionRetiro);

        mockMvc.perform(post("/api/caja/retiro")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rut\":\"12345678-9\",\"monto\":20000.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.tipoTransaccion").value("RETIRO"))
                .andExpect(jsonPath("$.monto").value(20000.0));
    }

    @Test
    void retirar_Retorna400BadRequest_CuandoFondosSonInsuficientes() throws Exception {
        when(cajaService.retirarPorRut(any(), any()))
                .thenThrow(new RuntimeException("Fondos insuficientes para realizar la operación"));

        mockMvc.perform(post("/api/caja/retiro")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rut\":\"12345678-9\",\"monto\":9999999.0}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Fondos insuficientes para realizar la operación"));
    }
}