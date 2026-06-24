package cl.bancl.msHorarios.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.bancl.msHorarios.model.HorariosEmpleado;
import cl.bancl.msHorarios.service.HorarioEmpleadoService;

@WebMvcTest(HorariosEmpleadoController.class)
public class HorarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HorarioEmpleadoService service;

    private HorariosEmpleado horarioMock;

    @BeforeEach
    void setUp() {
        // Inicializamos datos
        horarioMock = new HorariosEmpleado();
        horarioMock.setId(1);
        horarioMock.setEmpleadoId(101);
        horarioMock.setDiaSemana("Lunes");
        horarioMock.setHoraInicio("09:00");
        horarioMock.setHoraFin("18:00");
        horarioMock.setActivo(true);
        horarioMock.setObservaciones(null);
    }

    //          TESTS METODO NUEVO HORARIO (POST)
    @Test
    void nuevoHorario_Retorna200Ok_CuandoSeGuardaExitosamente() throws Exception {
        when(service.guardarHorario(any(HorariosEmpleado.class))).thenReturn(horarioMock);

        // Ocupamos los campos exactos mapeados
        mockMvc.perform(post("/api/horarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"empleadoId\":101,\"diaSemana\":\"Lunes\",\"horaInicio\":\"09:00\",\"horaFin\":\"18:00\"}"))
                .andExpect(status().isOk()) // Mantiene ResponseEntity.ok() del controlador
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.empleadoId").value(101))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void nuevoHorario_Retorna400BadRequest_CuandoServiceLanzaExcepcion() throws Exception {
        when(service.guardarHorario(any(HorariosEmpleado.class))).thenThrow(new RuntimeException("Error fatal"));

        mockMvc.perform(post("/api/horarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"empleadoId\":101}"))
                .andExpect(status().isBadRequest());
    }
    //      TESTS METODO OBTENER POR EMPLEADO (GET)

    @Test
    void obtenerHorariosPorEmpleado_Retorna200Ok_CuandoExistenHorarios() throws Exception {
        List<HorariosEmpleado> lista = new ArrayList<>();
        lista.add(horarioMock);
        
        when(service.buscarPorEmpleado(101)).thenReturn(lista);

        mockMvc.perform(get("/api/horarios/empleado/101"))
                .andExpect(status().isOk()) // Valida el camino con datos (200 OK)
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].empleadoId").value(101))
                .andExpect(jsonPath("$[0].diaSemana").value("Lunes"));
    }

    @Test
    void obtenerHorariosPorEmpleado_Retorna204NoContent_CuandoListaEstaVacia() throws Exception {
        // Simulamos que el servicio retorna una lista vacía
        when(service.buscarPorEmpleado(101)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/horarios/empleado/101"))
                .andExpect(status().isNoContent()); // Valida  regla de HTTP 204
    }
    //        TESTS METODO DESACTIVAR HORARIO (PUT)

    @Test
    void desactivarHorario_Retorna200Ok_CuandoSeModificaCorrectamente() throws Exception {
        // Simulamos el impacto de desactivar por licencia
        horarioMock.setActivo(false);
        horarioMock.setObservaciones("Licencia Médica: Gripe");
        
        when(service.desactivarPorLicencia(eq(1), eq("Gripe"))).thenReturn(horarioMock);

        // Realiza el PUT simulando el query param ?motivo=Gripe
        mockMvc.perform(put("/api/horarios/1/desactivar")
                .param("motivo", "Gripe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.activo").value(false))
                .andExpect(jsonPath("$.observaciones").value("Licencia Médica: Gripe"));
    }

    @Test
    void desactivarHorario_Retorna400BadRequest_CuandoServiceFalla() throws Exception {
        when(service.desactivarPorLicencia(eq(99), any(String.class)))
                .thenThrow(new RuntimeException("No encontrado"));

        mockMvc.perform(put("/api/horarios/99/desactivar")
                .param("motivo", "Licencia"))
                .andExpect(status().isBadRequest());
    }
}