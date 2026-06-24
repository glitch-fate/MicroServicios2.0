package cl.banco.msDashboard.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.banco.msDashboard.client.*;
import cl.banco.msDashboard.dto.ResumenClienteDTO;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock private TarjetaClient tarjetaClient;
    @Mock private SolicitudClient solicitudClient;
    @Mock private SeguroClient seguroClient;
    @Mock private CajaClient cajaClient;
    @Mock private CreditoClient creditoClient;
    @Mock private ClienteClient clienteClient;

    @InjectMocks
    private DashboardService dashboardService;

    private Map<String, Object> clienteMock;

    @BeforeEach
    void setUp() {
        clienteMock = new HashMap<>();
        clienteMock.put("id", 10L);
        clienteMock.put("nombre", "Raúl Jara");
    }

    @Test
    void obtenerResumen360_DebeRetornarDatosConsolidados_CuandoTodoFunciona() {
        // GIVEN
        when(clienteClient.obtenerClientePorRut("12345678-9")).thenReturn(clienteMock);
        when(cajaClient.obtenerSaldoPorRut("12345678-9")).thenReturn(1500000.0);
        when(tarjetaClient.consultarTarjetasPorRut("12345678-9")).thenReturn(Collections.singletonList(new Object()));

        // WHEN
        ResumenClienteDTO resultado = dashboardService.obtenerResumen360("12345678-9");

        // THEN
        assertEquals("Raúl Jara", resultado.getNombreCompleto());
        assertEquals(1500000.0, resultado.getSaldoBanco());
        assertEquals(1, resultado.getTarjetas().size());
        verify(seguroClient).listarSegurosPorClienteId(10L);
    }

    @Test
    void obtenerResumen360_DebeRetornarListasVacias_CuandoMicroserviciosFallan() {
        // GIVEN: Simulamos que todos los clientes externos lanzan excepción
        when(clienteClient.obtenerClientePorRut(anyString())).thenThrow(new RuntimeException("Error"));
        when(cajaClient.obtenerSaldoPorRut(anyString())).thenThrow(new RuntimeException("Error"));
        when(tarjetaClient.consultarTarjetasPorRut(anyString())).thenThrow(new RuntimeException("Error"));

        // WHEN
        ResumenClienteDTO resultado = dashboardService.obtenerResumen360("12345678-9");

        // THEN
        assertNotNull(resultado.getTarjetas());
        assertEquals(0, resultado.getTarjetas().size());
        assertEquals(0.0, resultado.getSaldoBanco());
        assertEquals("Cliente de Prueba", resultado.getNombreCompleto()); // Valor por defecto
    }
}