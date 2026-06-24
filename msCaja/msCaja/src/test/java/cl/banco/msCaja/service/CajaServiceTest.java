package cl.banco.msCaja.service;

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

import cl.banco.msCaja.client.ClienteClient;
import cl.banco.msCaja.dto.ClienteDTO;
import cl.banco.msCaja.dto.ResumenSaldoDTO;
import cl.banco.msCaja.model.Saldo;
import cl.banco.msCaja.model.Transaccion;
import cl.banco.msCaja.repository.SaldoRepository;
import cl.banco.msCaja.repository.TransaccionRepository;

@ExtendWith(MockitoExtension.class)
public class CajaServiceTest {

    @InjectMocks
    private CajaService cajaService;

    @Mock
    private SaldoRepository saldoRepository;

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private ClienteClient clienteClient;

    private ClienteDTO clienteMock;
    private Saldo saldoMock;

    @BeforeEach
    void setUp() {
        // Inicializamos un cliente base coincidiendo con tu ClienteDTO (id, nombre, apellido, rut)
        clienteMock = new ClienteDTO();
        clienteMock.setId(1L);
        clienteMock.setNombre("Raúl");
        clienteMock.setApellido("Jara");
        clienteMock.setRut("12345678-9");

        // Inicializamos un saldo base asociado al cliente
        saldoMock = new Saldo(1L, 100000.0);
        saldoMock.setId(50L);
    }

    //       PRUEBAS PARA OBTENER RESUMEN SALDO
 
    @Test
    void obtenerResumenSaldo_CuandoTieneSaldo_RetornaDTOCompleto() {
        // ARRANGE
        when(clienteClient.obtenerClientePorRut("12345678-9")).thenReturn(clienteMock);
        when(saldoRepository.findByClienteId(1L)).thenReturn(Optional.of(saldoMock));

        // ACT
        ResumenSaldoDTO resultado = cajaService.obtenerResumenSaldo("12345678-9");

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Raúl Jara", resultado.getNombreCompleto());
        assertEquals("12345678-9", resultado.getRut());
        assertEquals(100000.0, resultado.getSaldoTotal());
    }

    @Test
    void obtenerResumenSaldo_CuandoNoTieneFilaEnBD_AsumeSaldoCero() {
        // ARRANGE: Si es un cliente nuevo sin registros en la tabla de saldos
        when(clienteClient.obtenerClientePorRut("12345678-9")).thenReturn(clienteMock);
        when(saldoRepository.findByClienteId(1L)).thenReturn(Optional.empty());

        // ACT
        ResumenSaldoDTO resultado = cajaService.obtenerResumenSaldo("12345678-9");

        // ASSERT
        assertNotNull(resultado);
        assertEquals(0.0, resultado.getSaldoTotal()); // Verifica el .orElse(0.0) de tu lógica
    }
    //          PRUEBAS PARA DEPÓSITO
  
    @Test
    void depositarPorRut_FlujoExito() {
        // ARRANGE
        when(clienteClient.obtenerClientePorRut("12345678-9")).thenReturn(clienteMock);
        when(saldoRepository.findByClienteId(1L)).thenReturn(Optional.of(saldoMock));
        
        // Simulamos el guardado de la transacción
        Transaccion transaccionEsperada = new Transaccion(1L, "DEPOSITO", 50000.0);
        transaccionEsperada.setId(770L);
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionEsperada);

        // ACT
        Transaccion resultado = cajaService.depositarPorRut("12345678-9", 50000.0);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(770L, resultado.getId());
        assertEquals("DEPOSITO", resultado.getTipoTransaccion());
        assertEquals(50000.0, resultado.getMonto());
        
        // Verificamos que se sumó el dinero al saldo y se persistió (100.000 + 50.000 = 150.000)
        assertEquals(150000.0, saldoMock.getMontoActual());
        verify(saldoRepository, times(1)).save(saldoMock);
    }

       //           PRUEBAS PARA RETIRO
    
    @Test
    void retirarPorRut_FlujoExito_CuandoFondosSuficientes() {
        // ARRANGE
        when(clienteClient.obtenerClientePorRut("12345678-9")).thenReturn(clienteMock);
        when(saldoRepository.findByClienteId(1L)).thenReturn(Optional.of(saldoMock));

        Transaccion transaccionEsperada = new Transaccion(1L, "RETIRO", 30000.0);
        transaccionEsperada.setId(771L);
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionEsperada);

        // ACT
        Transaccion resultado = cajaService.retirarPorRut("12345678-9", 30000.0);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("RETIRO", resultado.getTipoTransaccion());
        
        // Verificamos que se restó del saldo (100.000 - 30.000 = 70.000)
        assertEquals(70000.0, saldoMock.getMontoActual());
        verify(saldoRepository, times(1)).save(saldoMock);
    }

    @Test
    void retirarPorRut_LanzaIllegalArgumentException_CuandoFondosInsuficientes() {
        // ARRANGE
        when(clienteClient.obtenerClientePorRut("12345678-9")).thenReturn(clienteMock);
        when(saldoRepository.findByClienteId(1L)).thenReturn(Optional.of(saldoMock)); // Tiene 100.000

        // ACT & ASSERT: Intentamos retirar 150.000
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            cajaService.retirarPorRut("12345678-9", 150000.0);
        });

        assertTrue(excepcion.getMessage().contains("Fondos insuficientes"));
        
        // Aseguramos que NO se guardó ningún saldo ni transacción tras el fallo
        verify(saldoRepository, never()).save(any(Saldo.class));
        verify(transaccionRepository, never()).save(any(Transaccion.class));
    }
}