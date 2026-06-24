package cl.bancl.msTarjetas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

import cl.bancl.msTarjetas.client.ClienteClient;
import cl.bancl.msTarjetas.dto.ClientesDTO;
import cl.bancl.msTarjetas.model.Tarjetas;
import cl.bancl.msTarjetas.repository.TarjetaRepository;
import cl.bancl.msTarjetas.service.TarjetaService;

@WebMvcTest(TarjetasController.class)
public class TarjetasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TarjetaService tarjetaService;

    @MockBean
    private TarjetaRepository repoTarjeta;

    @MockBean
    private ClienteClient clienteClient;

    private Tarjetas tarjetaMock;
    private ClientesDTO clienteMock;

    @BeforeEach
    void setUp() {
        tarjetaMock = new Tarjetas();
        tarjetaMock.setId(1L);
        tarjetaMock.setRutCliente("12345678-9");
        tarjetaMock.setTipoTarjeta("VISA");
        tarjetaMock.setEstado("PENDIENTE");

        clienteMock = new ClientesDTO();
        clienteMock.setNombre("Raúl");
        clienteMock.setApellido("Jara");
    }

    @Test
    void solicitarTarjeta_Retorna201Created() throws Exception {
        when(tarjetaService.solicitarTarjeta(any(Tarjetas.class))).thenReturn(tarjetaMock);

        mockMvc.perform(post("/api/tarjetas/solicitar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rutCliente\":\"12345678-9\",\"tipoTarjeta\":\"VISA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void consultarTarjetasPorRut_Retorna200Ok_CuandoExistenTarjetas() throws Exception {
        List<Tarjetas> lista = new ArrayList<>();
        lista.add(tarjetaMock);

        when(repoTarjeta.findByRutCliente("12345678-9")).thenReturn(lista);
        when(clienteClient.obtenerClientePorRut("12345678-9")).thenReturn(clienteMock);

        mockMvc.perform(get("/api/tarjetas/historial/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreCliente").value("Raúl Jara"));
    }

    @Test
    void consultarTarjetasPorRut_Retorna204NoContent_CuandoListaEstaVacia() throws Exception {
        when(repoTarjeta.findByRutCliente("99999999-9")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/tarjetas/historial/99999999-9"))
                .andExpect(status().isNoContent()); // Verificación del cambio a 204
    }
}