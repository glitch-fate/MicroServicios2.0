package cl.bancl.msTarjetas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import cl.bancl.msTarjetas.client.ClienteClient;
import cl.bancl.msTarjetas.dto.ClientesDTO;
import cl.bancl.msTarjetas.model.Tarjetas;
import cl.bancl.msTarjetas.repository.TarjetaRepository;

@ExtendWith(MockitoExtension.class)
public class TarjetasServiceTest {

    @Mock
    private TarjetaRepository repoTarjeta;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private TarjetaService tarjetaService;

    private Tarjetas tarjetaMock;
    private ClientesDTO clienteMock;

    @BeforeEach
    void setUp() {
        tarjetaMock = new Tarjetas();
        tarjetaMock.setRutCliente("12345678-9");
        tarjetaMock.setTipoTarjeta("CREDITO");
        
        clienteMock = new ClientesDTO();
        clienteMock.setNombre("Raúl");
        clienteMock.setApellido("Jara");
    }

    @Test
    void solicitarTarjeta_DebeAsignarCupoDefault_CuandoCupoEsCero() {
        when(clienteClient.obtenerClientePorRut(anyString())).thenReturn(clienteMock);
        when(repoTarjeta.findByRutClienteAndTipoTarjeta(anyString(), anyString())).thenReturn(Optional.empty());
        when(repoTarjeta.save(any(Tarjetas.class))).thenAnswer(i -> i.getArguments()[0]);

        Tarjetas resultado = tarjetaService.solicitarTarjeta(tarjetaMock);

        assertEquals(500000.0, resultado.getCupoTotal());
        assertEquals("SOLICITADA", resultado.getEstado());
        assertNotNull(resultado.getNumeroTarjeta());
    }

    @Test
    void solicitarTarjeta_DebeLanzarError_CuandoTarjetaYaExiste() {
        when(clienteClient.obtenerClientePorRut(anyString())).thenReturn(clienteMock);
        when(repoTarjeta.findByRutClienteAndTipoTarjeta(anyString(), anyString()))
                .thenReturn(Optional.of(new Tarjetas()));

        assertThrows(ResponseStatusException.class, () -> tarjetaService.solicitarTarjeta(tarjetaMock));
    }

    @Test
    void activarTarjeta_DebeCambiarEstadoAActiva() {
        Tarjetas tarjeta = new Tarjetas();
        tarjeta.setEstado("SOLICITADA");
        when(repoTarjeta.findById(1L)).thenReturn(Optional.of(tarjeta));
        when(repoTarjeta.save(any(Tarjetas.class))).thenAnswer(i -> i.getArguments()[0]);

        Tarjetas resultado = tarjetaService.activarTarjeta(1L, "Ejecutivo Test");

        assertEquals("ACTIVA", resultado.getEstado());
        assertEquals("Ejecutivo Test", resultado.getAprobadoPor());
    }

    @Test
    void activarTarjeta_DebeLanzarError_SiYaEstaActiva() {
        Tarjetas tarjeta = new Tarjetas();
        tarjeta.setEstado("ACTIVA");
        when(repoTarjeta.findById(1L)).thenReturn(Optional.of(tarjeta));

        assertThrows(ResponseStatusException.class, () -> tarjetaService.activarTarjeta(1L, "Ejecutivo"));
    }
}