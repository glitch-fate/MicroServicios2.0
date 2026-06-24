package cl.duoc.citas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.duoc.citas.dto.ClienteDTO;
import cl.duoc.citas.dto.DetalleCitaDTO;
import cl.duoc.citas.dto.EjecutivosDTO;
import cl.duoc.citas.dto.TipoCitasDTO;
import cl.duoc.citas.model.Citas;
import cl.duoc.citas.model.TipoCitas;
import cl.duoc.citas.service.CitasService;

@WebMvcTest(CitasController.class)
public class CitasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CitasService service;

    private Citas citaEjemplo;
    private DetalleCitaDTO dtoEjemplo;

    @BeforeEach
    void setUp() {
        // 1. Armamos la entidad base modelo  (Citas.java)
        TipoCitas tipoMock = new TipoCitas();
        tipoMock.setId(1);
        tipoMock.setNombre("Médica");

        citaEjemplo = new Citas();
        citaEjemplo.setId(1);
        citaEjemplo.setFechaCita(new Date());
        citaEjemplo.setHora("14:30");
        citaEjemplo.setTipoCitas(tipoMock);
        citaEjemplo.setClienteId(3);
        citaEjemplo.setEjecutivoId(2);

        // 2. Armamos la estructura de los DTOs 
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(3);
        clienteDTO.setNombre("Tiwita");
        clienteDTO.setApellido("lawita");

        EjecutivosDTO ejecutivoDTO = new EjecutivosDTO();
        ejecutivoDTO.setId(2);
        ejecutivoDTO.setNombre("Dr. Juan Pérez");
        ejecutivoDTO.setCargo("Cardiólogo");

        TipoCitasDTO tipoCitasDTO = new TipoCitasDTO();
        tipoCitasDTO.setId(1);
        tipoCitasDTO.setNombre("Médica");

        // 3. Poblamos el DTO principal (DetalleCitaDTO.java)
        dtoEjemplo = new DetalleCitaDTO();
        dtoEjemplo.setId(1);
        dtoEjemplo.setFechaCita(new Date());
        dtoEjemplo.setHoraCita("14:30");
        dtoEjemplo.setMotivoCita("Control General");
        dtoEjemplo.setCliente(clienteDTO);
        dtoEjemplo.setEjecutivos(ejecutivoDTO);
        dtoEjemplo.setTipoCitas(tipoCitasDTO);
    }

      //          PRUEBAS PARA GUARDAR CITA
   
    @Test
    void nuevaCita_retorna200Ok() throws Exception {
        when(service.guardarCita(any(Citas.class))).thenReturn(citaEjemplo);

        mockMvc.perform(post("/api/citas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"id\":1,\"fechaCita\":\"2026-06-25\",\"hora\":\"14:30\",\"tipoCitas\":{\"id\":1,\"nombre\":\"Médica\"},\"clienteId\":3,\"ejecutivoId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hora").value("14:30"));
    }

    @Test
    void nuevaCita_CuandoFalla_retorna400BadRequest() throws Exception {
        when(service.guardarCita(any(Citas.class))).thenThrow(new RuntimeException("Error de negocio"));

        mockMvc.perform(post("/api/citas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"id\":null}"))
                .andExpect(status().isBadRequest());
    }

    //        PRUEBAS PARA BUSCAR POR ID

    @Test
    void buscarPorId_retorna200Ok() throws Exception {
        when(service.buscarPorId(1)).thenReturn(citaEjemplo);

        mockMvc.perform(get("/api/citas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hora").value("14:30"));
    }

    @Test
    void buscarPorId_CuandoNoExiste_retorna404NotFound() throws Exception {
        when(service.buscarPorId(99)).thenThrow(new RuntimeException("Cita no encontrada"));

        mockMvc.perform(get("/api/citas/99"))
                .andExpect(status().isNotFound()); // Retorna 404 limpio gracias al catch del controlador
    }

    //          PRUEBAS PARA LISTAR CITAS

    @Test
    void listarCitas_retorna200ConElementos() throws Exception {
        List<DetalleCitaDTO> listaFalsa = new ArrayList<>();
        listaFalsa.add(dtoEjemplo);

        when(service.listaCitasConDetalle()).thenReturn(listaFalsa);

        mockMvc.perform(get("/api/citas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].horaCita").value("14:30"))
                .andExpect(jsonPath("$[0].cliente.nombre").value("Tiwita"))
                .andExpect(jsonPath("$[0].ejecutivos.nombre").value("Dr. Juan Pérez"));
    }

    @Test
    void listarCitas_CuandoEstaVacia_retorna204NoContent() throws Exception {
        List<DetalleCitaDTO> listaVacia = new ArrayList<>();
        when(service.listaCitasConDetalle()).thenReturn(listaVacia);

        mockMvc.perform(get("/api/citas"))
                .andExpect(status().isNoContent());
    }
}