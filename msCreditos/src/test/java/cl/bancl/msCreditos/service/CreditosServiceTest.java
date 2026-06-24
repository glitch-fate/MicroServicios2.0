package cl.bancl.msCreditos.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import cl.bancl.msCreditos.client.ClienteClient;
import cl.bancl.msCreditos.dto.ClientesDTO;
import cl.bancl.msCreditos.model.Creditos;
import cl.bancl.msCreditos.repository.CreditosRepository;
import feign.FeignException;
import feign.Request;

@ExtendWith(MockitoExtension.class)
public class CreditosServiceTest {

    @InjectMocks
    private CreditoService creditoService;

    @Mock
    private CreditosRepository repoCredito;

    @Mock
    private ClienteClient clienteClient;

    private Creditos creditoEntrada;
    private ClientesDTO clienteDtoMock;

    @BeforeEach
    void setUp() {
        // Inicializamos un objeto de entrada limpio para simular la petición de solicitud
        creditoEntrada = new Creditos();
        creditoEntrada.setRutCliente("12345678-9");
        creditoEntrada.setMontoSolicitado(100000.0);
        creditoEntrada.setMesesPlazo(10);

        // Inicializamos el DTO de respuesta esperado del Feign Client
        clienteDtoMock = new ClientesDTO();
        clienteDtoMock.setId(10);
        clienteDtoMock.setRut("12345678-9");
        clienteDtoMock.setNombre("Raúl");
        clienteDtoMock.setApellido("Jara");
    }

    //       PRUEBAS PARA SOLICITAR CRÉDITO

    @Test
    void solicitarCredito_FlujoExito_CalculaInteresyMontoCorrecto() {
        // ARRANGE
        when(clienteClient.obtenerClientePorRut("12345678-9")).thenReturn(clienteDtoMock);
        // Simulamos que el repositorio simplemente retorna el mismo objeto que guardamos
        when(repoCredito.save(any(Creditos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Creditos resultado = creditoService.solicitarCredito(creditoEntrada);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Raúl Jara", resultado.getNombreCliente());
        assertEquals("VIGENTE", resultado.getEstado());
        
        // 100,000 * 1.10 = 110,000
        assertEquals(110000.0, resultado.getMontoTotalAPagar());
        assertEquals(110000.0, resultado.getSaldoPendiente());
        // 110,000 / 10 meses = 11,000
        assertEquals(11000.0, resultado.getValorCuotaMensual());
    }

    @Test
    void solicitarCredito_LanzaBadRequest_CuandoFeignLanzaNotFound() {
        // 404
        Request request = Request.create(Request.HttpMethod.GET, "/url", java.util.Collections.emptyMap(), null, null, null);
        FeignException.NotFound feignException = new FeignException.NotFound("Not Found", request, null, null);
        
        when(clienteClient.obtenerClientePorRut("12345678-9")).thenThrow(feignException);

        // ACT & ASSERT
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            creditoService.solicitarCredito(creditoEntrada);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("El cliente con el RUT ingresado no existe"));
        verify(repoCredito, never()).save(any(Creditos.class));
    }
    //          PRUEBAS PARA PAGAR CUOTA

    @Test
    void pagarCuota_AbonaAlSaldo_CuandoQuedaSaldoPendiente() {
        // ARRANGE: Un crédito existente con $110,000 pendientes
        Creditos creditoExistente = new Creditos();
        creditoExistente.setId(5L);
        creditoExistente.setSaldoPendiente(110000.0);
        creditoExistente.setEstado("VIGENTE");

        when(repoCredito.findById(5L)).thenReturn(Optional.of(creditoExistente));
        when(repoCredito.save(any(Creditos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT: Pagamos una cuota de $11,000
        Creditos resultado = creditoService.pagarCuota(5L, 11000.0);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(99000.0, resultado.getSaldoPendiente());
        assertEquals("VIGENTE", resultado.getEstado());
    }

    @Test
    void pagarCuota_CambiaEstadoAPagado_CuandoSaldoEsCeroOMenor() {
        // ARRANGE: Crédito con $20,000 pendientes
        Creditos creditoExistente = new Creditos();
        creditoExistente.setId(5L);
        creditoExistente.setSaldoPendiente(20000.0);
        creditoExistente.setEstado("VIGENTE");

        when(repoCredito.findById(5L)).thenReturn(Optional.of(creditoExistente));
        when(repoCredito.save(any(Creditos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT: Abonamos $25,000 
        Creditos resultado = creditoService.pagarCuota(5L, 25000.0);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(0.0, resultado.getSaldoPendiente());
        assertEquals("PAGADO", resultado.getEstado());
    }

    @Test
    void pagarCuota_LanzaBadRequest_CuandoCreditoYaEstabaPagado() {
        // ARRANGE
        Creditos creditoCompleto = new Creditos();
        creditoCompleto.setId(5L);
        creditoCompleto.setSaldoPendiente(0.0);
        creditoCompleto.setEstado("PAGADO");

        when(repoCredito.findById(5L)).thenReturn(Optional.of(creditoCompleto));

        // ACT & ASSERT
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            creditoService.pagarCuota(5L, 10000.0);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("ya está completamente pagado"));
        verify(repoCredito, never()).save(any(Creditos.class));
    }

    @Test
    void pagarCuota_LanzaNotFound_CuandoCreditoNoExiste() {
        // ARRANGE
        when(repoCredito.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            creditoService.pagarCuota(99L, 5000.0);
        });

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Crédito no encontrado"));
    }
}