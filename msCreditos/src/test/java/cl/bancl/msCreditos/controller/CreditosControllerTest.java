package cl.bancl.msCreditos.controller;

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

import cl.bancl.msCreditos.client.ClienteClient;
import cl.bancl.msCreditos.model.Creditos;
import cl.bancl.msCreditos.repository.CreditosRepository;
import cl.bancl.msCreditos.service.CreditoService;

@WebMvcTest(CreditosController.class)
public class CreditosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditoService creditoService;

    @MockBean
    private CreditosRepository repoCredito;

    @MockBean
    private ClienteClient clienteClient;

    private Creditos creditoMock;

    @BeforeEach
    void setUp() {
        creditoMock = new Creditos();
        creditoMock.setId(1L);
        creditoMock.setRutCliente("18333444-5");
        creditoMock.setMontoSolicitado(5000000.0);       
        creditoMock.setMesesPlazo(24); 
        creditoMock.setEstado("VIGENTE");
    }

    //       TESTS METODO SOLICITAR CRÉDITO (POST)

    @Test
    void solicitarCredito_Retorna201Created_CuandoSeProcesaExitosamente() throws Exception {
        when(creditoService.solicitarCredito(any(Creditos.class))).thenReturn(creditoMock);

        mockMvc.perform(post("/api/creditos/solicitar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rutCliente\":\"18333444-5\",\"montoSolicitado\":5000000.0}"))
                .andExpect(status().isCreated()) // Valida HTTP 201 Created
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("VIGENTE"));
    }

    @Test
    void solicitarCredito_Retorna400BadRequest_CuandoFallaLógicaDeNegocio() throws Exception {
        when(creditoService.solicitarCredito(any(Creditos.class)))
                .thenThrow(new RuntimeException("El cliente registra dicom o deudas activas"));

        mockMvc.perform(post("/api/creditos/solicitar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"rutCliente\":\"18333444-5\"}"))
                .andExpect(status().isBadRequest()); // Valida HTTP 400 Bad Request
    }
    //         TESTS METODO PAGAR CUOTA (PUT)

    @Test
    void pagarCreditoPorRut_Retorna200Ok_CuandoExisteCreditoVigente() throws Exception {
        List<Creditos> lista = new ArrayList<>();
        lista.add(creditoMock);

        when(repoCredito.findByRutCliente("18333444-5")).thenReturn(lista);
        when(creditoService.pagarCuota(eq(1L), eq(250000.0))).thenReturn(creditoMock);

        mockMvc.perform(put("/api/creditos/pagar-por-rut")
                .param("rut", "18333444-5")
                .param("monto", "250000.0"))
                .andExpect(status().isOk()); // Valida HTTP 200 OK
    }

    //       TESTS METODO CONSULTAR HISTORIAL (GET)

    @Test
    void consultarCreditosPorRut_Retorna200Ok_CuandoClienteTieneHistorial() throws Exception {
        List<Creditos> historial = new ArrayList<>();
        historial.add(creditoMock);

        when(repoCredito.findByRutCliente("18333444-5")).thenReturn(historial);

        mockMvc.perform(get("/api/creditos/historial/18333444-5"))
                .andExpect(status().isOk()) // Valida HTTP 200 OK con registros
                .andExpect(jsonPath("$[0].rutCliente").value("18333444-5"));
    }

    @Test
    void consultarCreditosPorRut_Retorna204NoContent_CuandoHistorialEstaVacio() throws Exception {
        // Simulamos que la base de datos devuelve una lista vacía para cumplir la regla del 204 
        when(repoCredito.findByRutCliente("11222333-4")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/creditos/historial/11222333-4"))
                .andExpect(status().isNoContent()); // Valida la lógica del HTTP 204 sin contenido
    }
}