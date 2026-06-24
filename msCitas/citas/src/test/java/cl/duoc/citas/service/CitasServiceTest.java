package cl.duoc.citas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.citas.client.ClienteClient;
import cl.duoc.citas.client.EmpleadosClient;
import cl.duoc.citas.dto.ClienteDTO;
import cl.duoc.citas.dto.DetalleCitaDTO;
import cl.duoc.citas.dto.EjecutivosDTO;
import cl.duoc.citas.model.Citas;
import cl.duoc.citas.model.TipoCitas;
import cl.duoc.citas.repository.CitasRepository;
import cl.duoc.citas.repository.TipoCitasRepository;
import feign.FeignException;
import feign.Request;

@ExtendWith(MockitoExtension.class) 
public class CitasServiceTest {

    @InjectMocks
    private CitasService citasService; 

    @Mock
    private CitasRepository citasRepository;

    @Mock
    private TipoCitasRepository tipoCitasRepo;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private EmpleadosClient empleadosClient;

    private Citas citaBase;
    private TipoCitas tipoCitaBase;

    @BeforeEach
    void setUp() {
        tipoCitaBase = new TipoCitas();
        tipoCitaBase.setId(1);
        tipoCitaBase.setNombre("Médica");

        citaBase = new Citas();
        citaBase.setId(1);
        citaBase.setFechaCita(new Date());
        citaBase.setHora("15:00");
        citaBase.setClienteId(10);
        citaBase.setEjecutivoId(20);
        citaBase.setTipoCitas(tipoCitaBase);
    }
   //          PRUEBAS PARA BUSCAR POR ID

    @Test
    void buscarPorId_CuandoExiste_RetornaCita() {
        when(citasRepository.findById(1)).thenReturn(Optional.of(citaBase));

        Citas resultado = citasService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("15:00", resultado.getHora());
    }

    @Test
    void buscarPorId_CuandoNoExiste_LanzaRuntimeException() {
        when(citasRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            citasService.buscarPorId(99);
        });

        assertEquals("No se encontro", excepcion.getMessage());
    }

    //          PRUEBAS PARA GUARDAR CITA

    @Test
    void guardarCita_FlujoExito_TipoCitaExistente() {
        // ARRANGE: Feign responde bien y el tipo de cita ya existe en la BD
        when(clienteClient.obtenerClienteDTO(10)).thenReturn(new ClienteDTO());
        when(empleadosClient.obtenerEjecutivosDTO(20)).thenReturn(new EjecutivosDTO());
        when(tipoCitasRepo.findByNombre("Médica")).thenReturn(Optional.of(tipoCitaBase));
        when(citasRepository.save(any(Citas.class))).thenReturn(citaBase);

        // ACT
        Citas guardada = citasService.guardarCita(citaBase);

        // ASSERT
        assertNotNull(guardada);
        verify(tipoCitasRepo, never()).save(any(TipoCitas.class)); 
        verify(citasRepository, times(1)).save(citaBase);
    }

    @Test
    void guardarCita_FlujoExito_CreaTipoCitaAlVuelo() {
        // ARRANGE: El tipo no existe por nombre, así que simula la creación interna
        when(clienteClient.obtenerClienteDTO(10)).thenReturn(new ClienteDTO());
        when(empleadosClient.obtenerEjecutivosDTO(20)).thenReturn(new EjecutivosDTO());
        when(tipoCitasRepo.findByNombre("Médica")).thenReturn(Optional.empty());
        when(tipoCitasRepo.save(any(TipoCitas.class))).thenReturn(tipoCitaBase);
        when(citasRepository.save(any(Citas.class))).thenReturn(citaBase);

        // ACT
        Citas guardada = citasService.guardarCita(citaBase);

        // ASSERT
        assertNotNull(guardada);
        verify(tipoCitasRepo, times(1)).save(any(TipoCitas.class)); // Valida que entró al orElseGet
    }

    @Test
    void guardarCita_CuandoTipoEsNulo_LanzaResponseStatusException() {
        citaBase.setTipoCitas(null); // Provocamos el fallo de validación

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class, () -> {
            citasService.guardarCita(citaBase);
        });

        assertTrue(excepcion.getStatusCode().is4xxClientError());
        assertTrue(excepcion.getReason().contains("El tipo de cita debe incluir un nombre válido."));
    }

   @Test
    void guardarCita_CuandoFeignLanzaNotFound_LanzaBadRequest() {
        // ARRANGE: Forzamos un FeignException.NotFound usando la estructura compatible actual de Feign
        Request requestFalso = Request.create(
            Request.HttpMethod.GET, 
            "http://ms-clientes/api/clientes/10", 
            new java.util.HashMap<>(), 
            Request.Body.empty(), 
            null
        );
        
        FeignException.NotFound feignException = new FeignException.NotFound(
            "No encontrado", 
            requestFalso, 
            null, 
            null
        );
        
        
        when(clienteClient.obtenerClienteDTO(10)).thenThrow(feignException);

        // ACT & ASSERT
        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class, () -> {
            citasService.guardarCita(citaBase);
        });

        assertTrue(excepcion.getStatusCode().is4xxClientError());
        assertTrue(excepcion.getReason().contains("No se puede agendar: El cliente o el ejecutivo especificado no existe."));
    }

        //       PRUEBAS PARA LISTAR CON DETALLE

    @Test
    void listaCitasConDetalle_ArmaListaCorrectamente() {
        // ARRANGE: Lista local con un elemento
        List<Citas> listaLocales = new ArrayList<>();
        listaLocales.add(citaBase);
        when(citasRepository.findAll()).thenReturn(listaLocales);

        // Clientes Feign responden con datos válidos
        ClienteDTO mockCliente = new ClienteDTO();
        mockCliente.setNombre("Raúl");
        when(clienteClient.obtenerClienteDTO(10)).thenReturn(mockCliente);

        EjecutivosDTO mockEjecutivo = new EjecutivosDTO();
        mockEjecutivo.setNombre("Andrés");
        when(empleadosClient.obtenerEjecutivosDTO(20)).thenReturn(mockEjecutivo);

        // ACT
        List<DetalleCitaDTO> resultado = citasService.listaCitasConDetalle();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        
        DetalleCitaDTO det = resultado.get(0);
        assertEquals("15:00", det.getHoraCita());
        assertEquals("Médica", det.getMotivoCita());
        assertEquals("Raúl", det.getCliente().getNombre());
        assertEquals("Andrés", det.getEjecutivos().getNombre());
    }

   @Test
    void listaCitasConDetalle_CuandoFeignFalla_IgualArmaLaCita() {
        // ARRANGE: Si la llamada externa de Feign se cae, el try-catch interno la rescata
        List<Citas> listaLocales = new ArrayList<>();
        listaLocales.add(citaBase);
        when(citasRepository.findAll()).thenReturn(listaLocales);

       
        when(clienteClient.obtenerClienteDTO(10)).thenThrow(new RuntimeException("Timeout o Caído"));
        when(empleadosClient.obtenerEjecutivosDTO(20)).thenReturn(new EjecutivosDTO());

        // ACT
        List<DetalleCitaDTO> resultado = citasService.listaCitasConDetalle();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertNull(resultado.get(0).getCliente()); // Quedó nulo por el catch interno, no explotó el flujo
        assertNotNull(resultado.get(0).getEjecutivos()); // Este se pobló bien
    }
}