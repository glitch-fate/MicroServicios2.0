package cl.banco.msSeguros.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.banco.msSeguros.client.ClienteClient;
import cl.banco.msSeguros.dto.ClienteDTO;
import cl.banco.msSeguros.dto.SeguroRespuestaDTO;
import cl.banco.msSeguros.model.Seguro;
import cl.banco.msSeguros.repository.SeguroRepository;

@ExtendWith(MockitoExtension.class)
public class SeguroServiceTest {

    @Mock
    private SeguroRepository seguroRepository;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private SeguroService seguroService;

    private Seguro seguroMock;
    private ClienteDTO clienteDTOMock;

    @BeforeEach
    void setUp() {
        seguroMock = new Seguro();
        seguroMock.setId(1L);
        seguroMock.setNumeroPoliza("POL-999");
        seguroMock.setTipoSeguro("Vida");
        seguroMock.setMontoAsegurado(100000.0);
        seguroMock.setPrimaMensual(1500.0);
        seguroMock.setFechaContratacion(LocalDate.now());
        seguroMock.setEstado("ACTIVO");
        seguroMock.setClienteId(10L);

        clienteDTOMock = new ClienteDTO();
        clienteDTOMock.setId(10L);
        clienteDTOMock.setNombre("Raúl");
        clienteDTOMock.setApellido("Jara");
    }

    //          TESTS PARA CREAR SEGURO

    @Test
    void crearSeguro_DebeRetornarSeguroGuardadoConEstadoActivoYFechaActual() {
        Seguro inputSeguro = new Seguro();
        inputSeguro.setNumeroPoliza("POL-999");
        inputSeguro.setClienteId(10L);

        when(seguroRepository.save(any(Seguro.class))).thenReturn(seguroMock);

        Seguro resultado = seguroService.crearSeguro(inputSeguro);

        assertNotNull(resultado);
        assertEquals("ACTIVO", resultado.getEstado());
        assertEquals(LocalDate.now(), resultado.getFechaContratacion());
        verify(seguroRepository, times(1)).save(inputSeguro);
    }
    //         TESTS PARA OBTENER TODOS

    @Test
    void obtenerTodos_DebeMapearListaRespuesta_CuandoClienteClientRespondeOk() {
        List<Seguro> listaSeguros = new ArrayList<>();
        listaSeguros.add(seguroMock);

        when(seguroRepository.findAll()).thenReturn(listaSeguros);
        when(clienteClient.obtenerClientePorId(10L)).thenReturn(clienteDTOMock);

        List<SeguroRespuestaDTO> resultado = seguroService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Raúl", resultado.get(0).getCliente().getNombre());
    }

    @Test
    void obtenerTodos_DebeMapearConClienteNoDisponible_CuandoClienteClientFalla() {
        List<Seguro> listaSeguros = new ArrayList<>();
        listaSeguros.add(seguroMock);

        when(seguroRepository.findAll()).thenReturn(listaSeguros);
        // Simulamos una caída de red o de microservicio externo
        when(clienteClient.obtenerClientePorId(10L)).thenThrow(new RuntimeException("Feign Error"));

        List<SeguroRespuestaDTO> resultado = seguroService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        // Comprobamos la lógica del bloque catch en el mapeador
        assertEquals("No disponible", resultado.get(0).getCliente().getNombre());
        assertEquals(10L, resultado.get(0).getCliente().getId());
    }

    //         TESTS PARA OBTENER POR ID
    @Test
    void obtenerPorId_DebeRetornarDTO_CuandoIdExiste() {
        when(seguroRepository.findById(1L)).thenReturn(Optional.of(seguroMock));
        when(clienteClient.obtenerClientePorId(10L)).thenReturn(clienteDTOMock);

        SeguroRespuestaDTO resultado = seguroService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("POL-999", resultado.getNumeroPoliza());
    }

    @Test
    void obtenerPorId_DebeRetornarNull_CuandoIdNoExiste() {
        when(seguroRepository.findById(99L)).thenReturn(Optional.empty());

        SeguroRespuestaDTO resultado = seguroService.obtenerPorId(99L);

        assertNull(resultado);
        verify(clienteClient, never()).obtenerClientePorId(anyLong());
    }

    //      TESTS PARA OBTENER POR CLIENTE

    @Test
    void obtenerPorCliente_DebeRetornarListaDeSegurosBase() {
        List<Seguro> listaSeguros = new ArrayList<>();
        listaSeguros.add(seguroMock);

        when(seguroRepository.findByClienteId(10L)).thenReturn(listaSeguros);

        List<Seguro> resultado = seguroService.obtenerPorCliente(10L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getClienteId());
    }

    //        TESTS PARA CANCELAR SEGURO

    @Test
    void cancelarSeguro_DebeCambiarEstadoAInactivo_CuandoSeguroExiste() {
        when(seguroRepository.findById(1L)).thenReturn(Optional.of(seguroMock));
        // Esperamos que se guarde con el nuevo estado modificado
        when(seguroRepository.save(any(Seguro.class))).thenReturn(seguroMock);

        Seguro resultado = seguroService.cancelarSeguro(1L);

        assertNotNull(resultado);
        assertEquals("INACTIVO", resultado.getEstado());
        verify(seguroRepository, times(1)).save(seguroMock);
    }

    @Test
    void cancelarSeguro_DebeRetornarNull_CuandoSeguroNoExiste() {
        when(seguroRepository.findById(99L)).thenReturn(Optional.empty());

        Seguro resultado = seguroService.cancelarSeguro(99L);

        assertNull(resultado);
        verify(seguroRepository, never()).save(any(Seguro.class));
    }
}