package cl.banco.msSeguros.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.banco.msSeguros.dto.ClienteDTO;
import cl.banco.msSeguros.dto.SeguroRespuestaDTO;
import cl.banco.msSeguros.model.Seguro;
import cl.banco.msSeguros.service.SeguroService;

@WebMvcTest(SeguroController.class)
public class SeguroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeguroService seguroService;

    private Seguro seguroMock;
    private SeguroRespuestaDTO seguroRespuestaDTOMock;

    @BeforeEach
    void setUp() {
        // 1. Instanciamos el modelo Seguro base 
        seguroMock = new Seguro();
        seguroMock.setId(1L);
        seguroMock.setNumeroPoliza("POL-12345");
        seguroMock.setTipoSeguro("Automotriz");
        seguroMock.setMontoAsegurado(5000000.0);
        seguroMock.setPrimaMensual(25000.0);
        seguroMock.setFechaContratacion(LocalDate.of(2026, 6, 23));
        seguroMock.setEstado("ACTIVO");
        seguroMock.setClienteId(10L);

        // 2. Instanciamos el ClienteDTO anidado 
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(10L);
        clienteDTO.setNombre("Raúl");
        clienteDTO.setApellido("Jara");
        clienteDTO.setRut("12345678-9");
        clienteDTO.setEmail("raul@banco.cl");

        // 3. Instanciamos el SeguroRespuestaDTO 
        seguroRespuestaDTOMock = new SeguroRespuestaDTO();
        seguroRespuestaDTOMock.setId(1L);
        seguroRespuestaDTOMock.setNumeroPoliza("POL-12345");
        seguroRespuestaDTOMock.setTipoSeguro("Automotriz");
        seguroRespuestaDTOMock.setMontoAsegurado(5000000.0);
        seguroRespuestaDTOMock.setPrimaMensual(25000.0);
        seguroRespuestaDTOMock.setFechaContratacion(LocalDate.of(2026, 6, 23));
        seguroRespuestaDTOMock.setEstado("ACTIVO");
        seguroRespuestaDTOMock.setCliente(clienteDTO);
    }
    //          PRUEBAS PARA CREAR SEGURO
    @Test
    void crearSeguro_Retorna200Ok_CuandoEsValido() throws Exception {
        when(seguroService.crearSeguro(any(Seguro.class))).thenReturn(seguroMock);

        mockMvc.perform(post("/api/seguro")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"numeroPoliza\":\"POL-12345\",\"tipoSeguro\":\"Automotriz\",\"montoAsegurado\":5000000.0,\"primaMensual\":25000.0,\"clienteId\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroPoliza").value("POL-12345"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    //        PRUEBAS PARA OBTENER TODOS

    @Test
    void obtenerTodos_Retorna200Ok_CuandoExistenSeguros() throws Exception {
        List<SeguroRespuestaDTO> lista = new ArrayList<>();
        lista.add(seguroRespuestaDTOMock);

        when(seguroService.obtenerTodos()).thenReturn(lista);

        mockMvc.perform(get("/api/seguro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].numeroPoliza").value("POL-12345"))
                .andExpect(jsonPath("$[0].cliente.nombre").value("Raúl"))
                .andExpect(jsonPath("$[0].cliente.rut").value("12345678-9"));
    }

    @Test
    void obtenerTodos_Retorna204NoContent_CuandoListaEstaVacia() throws Exception {
        when(seguroService.obtenerTodos()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/seguro"))
                .andExpect(status().isNoContent()); // HTTP 204 No Content
    }
    //         PRUEBAS PARA OBTENER POR ID
    @Test
    void obtenerPorId_Retorna200Ok_CuandoIdExiste() throws Exception {
        when(seguroService.obtenerPorId(1L)).thenReturn(seguroRespuestaDTOMock);

        mockMvc.perform(get("/api/seguro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numeroPoliza").value("POL-12345"))
                .andExpect(jsonPath("$.cliente.apellido").value("Jara"));
    }

    @Test
    void obtenerPorId_Retorna404NotFound_CuandoIdNoExiste() throws Exception {
        when(seguroService.obtenerPorId(99L)).thenReturn(null);

        mockMvc.perform(get("/api/seguro/99"))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found
    }

    //      PRUEBAS PARA OBTENER POR CLIENTE

    @Test
    void obtenerPorCliente_Retorna200Ok_CuandoClienteTieneSeguros() throws Exception {
        List<Seguro> listaPorCliente = new ArrayList<>();
        listaPorCliente.add(seguroMock);

        when(seguroService.obtenerPorCliente(10L)).thenReturn(listaPorCliente);

        mockMvc.perform(get("/api/seguro/cliente/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tipoSeguro").value("Automotriz"))
                .andExpect(jsonPath("$[0].clienteId").value(10));
    }

    @Test
    void obtenerPorCliente_Retorna204NoContent_CuandoClienteNoTieneSeguros() throws Exception {
        when(seguroService.obtenerPorCliente(10L)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/seguro/cliente/10"))
                .andExpect(status().isNoContent()); // HTTP 204 No Content
    }

    //        PRUEBAS PARA CANCELAR SEGURO

    @Test
    void cancelarSeguro_Retorna200Ok_CuandoCancelacionEsExitosa() throws Exception {
        Seguro seguroCanceladoMock = seguroMock;
        seguroCanceladoMock.setEstado("CANCELADO");

        when(seguroService.cancelarSeguro(1L)).thenReturn(seguroCanceladoMock);

        mockMvc.perform(put("/api/seguro/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }

    @Test
    void cancelarSeguro_Retorna404NotFound_CuandoSeguroNoExiste() throws Exception {
        when(seguroService.cancelarSeguro(99L)).thenReturn(null);

        mockMvc.perform(put("/api/seguro/99/cancelar"))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found
    }
}