package cl.banco.msClientes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import cl.banco.msClientes.model.Cliente;
import cl.banco.msClientes.service.ClienteService;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService service;

    private Cliente clienteEjemplo;

    @BeforeEach
    void setUp() {
        clienteEjemplo = new Cliente();
        clienteEjemplo.setId(1);
        clienteEjemplo.setNombre("Tiwita");
        clienteEjemplo.setApellido("lawita");
        clienteEjemplo.setRut("15.456.123-k");
        clienteEjemplo.setCorreo("prueba@prueba.cl");
    }
    //          PRUEBAS PARA LISTAR

    @Test
    void listar_retorna200ConClientes() throws Exception {
        List<Cliente> listaFalsa = new ArrayList<>();
        listaFalsa.add(clienteEjemplo);

        when(service.listarClientes()).thenReturn(listaFalsa);

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Tiwita"))
                .andExpect(jsonPath("$[0].apellido").value("lawita"))
                .andExpect(jsonPath("$[0].rut").value("15.456.123-k"));
    }

    @Test
    void listar_retorna204SinClientes() throws Exception {
        List<Cliente> listaVacia = new ArrayList<>();
        when(service.listarClientes()).thenReturn(listaVacia);

        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isNoContent()); // HTTP 204 No Content
    }

    //        PRUEBAS PARA BUSCAR POR ID
  
    @Test
    void buscar_retorna200() throws Exception {
        when(service.buscarPorId(1)).thenReturn(clienteEjemplo);

        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Tiwita"))
                .andExpect(jsonPath("$.correo").value("prueba@prueba.cl"));
    }

    @Test
    void buscar_CuandoClienteNoExiste_retorna404() throws Exception {
        when(service.buscarPorId(99)).thenThrow(new RuntimeException("Cliente no encontrado"));

        mockMvc.perform(get("/api/clientes/99"))
                .andExpect(status().isNotFound()); // HTTP 404 atrapado por el catch
    }

      //       PRUEBAS PARA BUSCAR POR RUT

    @Test
    void buscarRut_retorna200() throws Exception {
        when(service.buscarPorRut("15.456.123-k")).thenReturn(clienteEjemplo);

        mockMvc.perform(get("/api/clientes/rut/15.456.123-k"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("15.456.123-k"))
                .andExpect(jsonPath("$.nombre").value("Tiwita"))
                .andExpect(jsonPath("$.apellido").value("lawita"))
                // Verificamos que al ser un DTO no exponga la propiedad correo en el JSON
                .andExpect(jsonPath("$.correo").doesNotExist()); 
    }

    @Test
    void buscarRut_CuandoRutNoExiste_retorna404() throws Exception {
        when(service.buscarPorRut("99.999.999-9")).thenThrow(new RuntimeException("Cliente no encontrado"));

        mockMvc.perform(get("/api/clientes/rut/99.999.999-9"))
                .andExpect(status().isNotFound()); // HTTP 404
    }

      //          PRUEBAS PARA GUARDAR
  
    @Test
    void guardar_retorna200() throws Exception {
        when(service.agregarCliente(any(Cliente.class))).thenReturn(clienteEjemplo);

        mockMvc.perform(post("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"id\":1,\"nombre\":\"Tiwita\",\"apellido\":\"lawita\",\"rut\":\"15.456.123-k\",\"correo\":\"prueba@prueba.cl\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Tiwita"));
    }

    //         PRUEBAS PARA ACTUALIZAR
      @Test
    void actualizar_retorna200() throws Exception {
        when(service.actualizar(eq(1), any(Cliente.class))).thenReturn(clienteEjemplo);

        mockMvc.perform(put("/api/clientes/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"id\":1,\"nombre\":\"Tiwita\",\"apellido\":\"lawita\",\"rut\":\"15.456.123-k\",\"correo\":\"prueba@prueba.cl\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Tiwita"));
    }

    @Test
    void actualizar_CuandoClienteNoExiste_retorna404() throws Exception {
        when(service.actualizar(eq(99), any(Cliente.class))).thenThrow(new RuntimeException("Cliente no encontrado"));

        mockMvc.perform(put("/api/clientes/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"id\":99,\"nombre\":\"No Existe\",\"apellido\":\"error\",\"rut\":\"0-0\",\"correo\":\"err@err.cl\"}"))
                .andExpect(status().isNotFound()); // HTTP 404
    }

    //          PRUEBAS PARA ELIMINAR

    @Test
    void eliminar_retorna204() throws Exception {
        doNothing().when(service).eliminar(1);

        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent()); // HTTP 204 No Content
    }

    @Test
    void eliminar_CuandoNoExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Cliente no existe")).when(service).eliminar(99);

        mockMvc.perform(delete("/api/clientes/99"))
                .andExpect(status().isNotFound()); // HTTP 404
    }

    //         PRUEBAS PARA MÉTODOS DTO

    @Test
    void obtenerClienteDTO_retorna200() throws Exception {
        when(service.buscarPorId(1)).thenReturn(clienteEjemplo);

        mockMvc.perform(get("/api/clientes/dto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Tiwita"))
                .andExpect(jsonPath("$.apellido").value("lawita"))
                .andExpect(jsonPath("$.rut").value("15.456.123-k"))
                .andExpect(jsonPath("$.correo").doesNotExist()); // El DTO no maneja este campo
    }

    @Test
    void obtenerClienteDTO_CuandoNoExiste_retorna404() throws Exception {
        when(service.buscarPorId(99)).thenThrow(new RuntimeException("Cliente no encontrado"));

        mockMvc.perform(get("/api/clientes/dto/99"))
                .andExpect(status().isNotFound()); // HTTP 404
    }
}