package cl.banco.msEmpleados.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import cl.banco.msEmpleados.model.CargoEjecutivo;
import cl.banco.msEmpleados.model.Ejecutivo;
import cl.banco.msEmpleados.service.EjecutivoService;

@WebMvcTest(EjecutivController.class)
public class EmpleadosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EjecutivoService service;

    private Ejecutivo ejecutivoMock;

    @BeforeEach
    void setUp() {
        // 1. Instanciamos el cargo real usando modelo de msEmpleados
        CargoEjecutivo cargo = new CargoEjecutivo();
        cargo.setId(1);
        cargo.setNombre("Ejecutivo de Inversiones");

        // 2. Instanciamos el ejecutivo con datos exactos
        ejecutivoMock = new Ejecutivo();
        ejecutivoMock.setId(10);
        ejecutivoMock.setRut("17444555-K");
        ejecutivoMock.setNombre("Andrés");
        ejecutivoMock.setApellido("Jara");
        ejecutivoMock.setCargo(cargo); // Asignamos la relación
    }
    //            TESTS METODO LISTAR

    @Test
    void listar_Retorna200Ok_CuandoExistenRegistros() throws Exception {
        List<Ejecutivo> lista = new ArrayList<>();
        lista.add(ejecutivoMock);
        
        when(service.listar()).thenReturn(lista);

        mockMvc.perform(get("/api/ejecutivos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].nombre").value("Andrés"))
                .andExpect(jsonPath("$[0].cargo.nombre").value("Ejecutivo de Inversiones"));
    }

    @Test
    void listar_Retorna204NoContent_CuandoListaEstaVacia() throws Exception {
        when(service.listar()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/ejecutivos"))
                .andExpect(status().isNoContent()); // Evalúa correctamente tu HTTP 204
    }

    //          TESTS METODO BUSCAR POR ID

    @Test
    void buscarPorId_Retorna200Ok_CuandoIdExiste() throws Exception {
        when(service.buscarPorId(10)).thenReturn(ejecutivoMock);

        mockMvc.perform(get("/api/ejecutivos/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Andrés"));
    }

    @Test
    void buscarPorId_Retorna404NotFound_CuandoServiceLanzaExcepcion() throws Exception {
        //captura cualquier Exception y retorna un 404
        when(service.buscarPorId(99)).thenThrow(new RuntimeException("Error simulado"));

        mockMvc.perform(get("/api/ejecutivos/99"))
                .andExpect(status().isNotFound());
    }
    //            TESTS METODO GUARDAR

    @Test
    void guardar_Retorna201Created_CuandoSeGuardaExitosamente() throws Exception {
        when(service.guardar(any(Ejecutivo.class))).thenReturn(ejecutivoMock);

        // Enviamos el JSON simulando tu @RequestBody
        mockMvc.perform(post("/api/ejecutivos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Andrés\",\"apellido\":\"Jara\"}"))
                .andExpect(status().isCreated()) // Valida correctamente tu HTTP 201 Created
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Andrés"));
    }
    //          TESTS METODO ELIMINAR POR ID

    @Test
    void eliminarPorId_Retorna204NoContent_CuandoIdExiste() throws Exception {
        doNothing().when(service).eliminar(10);

        mockMvc.perform(delete("/api/ejecutivos/10"))
                .andExpect(status().isNoContent()); // Valida correctamente tu HTTP 204 No Content
    }

    @Test
    void eliminarPorId_Retorna404NotFound_CuandoServiceFalla() throws Exception {
        doThrow(new RuntimeException("Inexistente")).when(service).eliminar(99);

        mockMvc.perform(delete("/api/ejecutivos/99"))
                .andExpect(status().isNotFound());
    }

    //         TESTS METODO OBTENER EMPLEADO DTO

    @Test
    void obtenerEmpleadoDTO_Retorna200Ok_ConCamposMapeadosA_EjecutivoDTO() throws Exception {
        when(service.buscarPorId(10)).thenReturn(ejecutivoMock);

        mockMvc.perform(get("/api/ejecutivos/dto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("Andrés"))             
                .andExpect(jsonPath("$.cargoEjecutivo").value("Ejecutivo de Inversiones")); 
    }
}