package cl.banco.msSolicitudes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.banco.msSolicitudes.Client.ClienteClient;
import cl.banco.msSolicitudes.dto.ClienteDTO;
import cl.banco.msSolicitudes.model.Solicitud;
import cl.banco.msSolicitudes.repository.SolicitudRepository;

@ExtendWith(MockitoExtension.class)
public class SolicitudesServiceTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private SolicitudService solicitudService;

    private ClienteDTO clienteDTOMock;
    private Solicitud solicitudMock;

    @BeforeEach
    void setUp() {
        // Configuramos el DTO de respuesta del cliente externo
        clienteDTOMock = new ClienteDTO();
        clienteDTOMock.setId(10L);
        clienteDTOMock.setRut("12345678-9");
        clienteDTOMock.setNombre("Raúl");
        clienteDTOMock.setApellido("Jara");

        // Configuramos la entidad esperada usando el constructor de Solicitud.java
        solicitudMock = new Solicitud(10L, "Soporte Técnico", "Problema de acceso a la App");
        solicitudMock.setId(1L);
        solicitudMock.setEstado("PENDIENTE");
        solicitudMock.setFechaCreacion(LocalDateTime.now());
    }

    //        TESTS PARA CREAR SOLICITUD

    @Test
    void crearSolicitud_DebeGuardarYRetornarSolicitud_CuandoClienteExiste() {
        // GIVEN
        when(clienteClient.obtenerClientePorRut("12345678-9")).thenReturn(clienteDTOMock);
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitudMock);

        // WHEN
        Solicitud resultado = solicitudService.crearSolicitud("12345678-9", "Soporte Técnico", "Problema de acceso a la App");

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getClienteId());
        assertEquals("Soporte Técnico", resultado.getMotivo());
        verify(clienteClient, times(1)).obtenerClientePorRut("12345678-9");
        verify(solicitudRepository, times(1)).save(any(Solicitud.class));
    }

    //     TESTS PARA OBTENER POR RUT

    @Test
    void obtenerSolicitudesPorRut_DebeRetornarLista_CuandoExistenRegistros() {
        // GIVEN
        List<Solicitud> lista = new ArrayList<>();
        lista.add(solicitudMock);

        when(clienteClient.obtenerClientePorRut("12345678-9")).thenReturn(clienteDTOMock);
        when(solicitudRepository.findByClienteId(10L)).thenReturn(lista);

        // WHEN
        List<Solicitud> resultado = solicitudService.obtenerSolicitudesPorRut("12345678-9");

        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Soporte Técnico", resultado.get(0).getMotivo());
        verify(solicitudRepository, times(1)).findByClienteId(10L);
    }

    //       TESTS PARA ELIMINAR SOLICITUD

    @Test
    void eliminarSolicitud_DebeEjecutarDelete_CuandoIdExiste() {
        // GIVEN
        when(solicitudRepository.existsById(1L)).thenReturn(true);
        doNothing().when(solicitudRepository).deleteById(1L);

        // WHEN & THEN
        assertDoesNotThrow(() -> solicitudService.eliminarSolicitud(1L));
        verify(solicitudRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminarSolicitud_DebeLanzarIllegalArgumentException_CuandoIdNoExiste() {
        // GIVEN
        when(solicitudRepository.existsById(99L)).thenReturn(false);

        // WHEN & THEN
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            solicitudService.eliminarSolicitud(99L);
        });

        assertEquals("La solicitud con el ID 99 no existe.", excepcion.getMessage());
        verify(solicitudRepository, never()).deleteById(anyLong());
    }
}