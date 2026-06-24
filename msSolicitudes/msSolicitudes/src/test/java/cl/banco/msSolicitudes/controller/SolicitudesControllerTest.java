package cl.banco.msSolicitudes.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.banco.msSolicitudes.model.Solicitud;
import cl.banco.msSolicitudes.service.SolicitudService;

@WebMvcTest(SolicitudesController.class)
public class SolicitudesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolicitudService solicitudService;

    private Solicitud solicitudMock;

    @BeforeEach
    void setUp() {
        // Instanciamos el objeto usando el constructor personalizado
        solicitudMock = new Solicitud(10L, "Reclamo Cobro", "Se realizó un cobro duplicado en mi cuenta.");
        solicitudMock.setId(1L);
        solicitudMock.setEstado("PENDIENTE");
        solicitudMock.setFechaCreacion(LocalDateTime.now());
    }

    //       PRUEBAS PARA GUARDAR SOLICITUD

    @Test
    void guardarSolicitud_Retorna200Ok_CuandoElDtoEsValido() throws Exception {
        when(solicitudService.crearSolicitud(anyString(), anyString(), anyString()))
                .thenReturn(solicitudMock);

        // Enviamos el JSON simulando un CrearSolicitudDTO válido
        mockMvc.perform(post("/api/solicitudes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rut\":\"12345678-9\",\"motivo\":\"Reclamo Cobro\",\"mensaje\":\"Se realizó un cobro duplicado en mi cuenta.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clienteId").value(10))
                .andExpect(jsonPath("$.motivo").value("Reclamo Cobro"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    //         PRUEBAS PARA LISTAR POR RUT

    @Test
    void listarPorRut_Retorna200Ok_CuandoExistenSolicitudes() throws Exception {
        List<Solicitud> lista = new ArrayList<>();
        lista.add(solicitudMock);

        when(solicitudService.obtenerSolicitudesPorRut("12345678-9")).thenReturn(lista);

        mockMvc.perform(get("/api/solicitudes/cliente/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].motivo").value("Reclamo Cobro"))
                .andExpect(jsonPath("$[0].mensaje").value("Se realizó un cobro duplicado en mi cuenta."));
    }

    @Test
    void listarPorRut_Retorna204NoContent_CuandoElClienteNoTieneSolicitudes() throws Exception {
        // Simulamos que el servicio retorna una lista vacía
        when(solicitudService.obtenerSolicitudesPorRut("99999999-9")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/solicitudes/cliente/99999999-9"))
                .andExpect(status().isNoContent()); // HTTP 204 No Content
    }

    //        PRUEBAS PARA ELIMINAR SOLICITUD

    @Test
    void eliminarSolicitud_Retorna200Ok_CuandoElIdExiste() throws Exception {
        // El método del servicio es void, por lo que no necesita un "when(...).thenReturn(...)"
        mockMvc.perform(delete("/api/solicitudes/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Solicitud con ID 1 eliminada exitosamente."));

        verify(solicitudService).eliminarSolicitud(1L);
    }

    @Test
    void eliminarSolicitud_Retorna400BadRequest_CuandoElIdNoExiste() throws Exception {
        // Simulamos que el servicio arroja la excepción que maneja el bloque catch
        doThrow(new IllegalArgumentException("La solicitud con ID 99 no existe."))
                .when(solicitudService).eliminarSolicitud(99L);

        mockMvc.perform(delete("/api/solicitudes/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La solicitud con ID 99 no existe."));
    }
}