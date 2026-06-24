package cl.bancl.msHorarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import cl.bancl.msHorarios.client.EmpleadoClient;
import cl.bancl.msHorarios.dto.HorarioEmpleadoDTO;
import cl.bancl.msHorarios.dto.EjecutivosDTO;
import cl.bancl.msHorarios.model.HorariosEmpleado;
import cl.bancl.msHorarios.repository.HorariosEmpleadoRepository;

@ExtendWith(MockitoExtension.class)
public class HorarioServiceTest {

    @Mock
    private HorariosEmpleadoRepository repoHorarios;

    @Mock
    private EmpleadoClient empleadoClient;

    @InjectMocks
    private HorarioEmpleadoService service;

    private HorariosEmpleado horarioMock;
    private EjecutivosDTO ejecutivoDtoMock;

    @BeforeEach
    void setUp() {
        // Inicializamos los mocks utilizando la estructura real
        horarioMock = new HorariosEmpleado();
        horarioMock.setId(1);
        horarioMock.setEmpleadoId(101);
        horarioMock.setDiaSemana("Martes");
        horarioMock.setHoraInicio("08:30");
        horarioMock.setHoraFin("17:30");
        horarioMock.setActivo(true);

        ejecutivoDtoMock = new EjecutivosDTO();
        ejecutivoDtoMock.setId(101);
        ejecutivoDtoMock.setNombre("Raúl");
        ejecutivoDtoMock.setApellido("Jara");
    }

    //       TESTS METODO GUARDAR HORARIO

    @Test
    void guardarHorario_GuardaExitosamente_CuandoEmpleadoExisteEnOtroMs() {
        when(empleadoClient.obtenerEjecutivosDTO(101)).thenReturn(ejecutivoDtoMock);
        when(repoHorarios.save(any(HorariosEmpleado.class))).thenReturn(horarioMock);

        HorariosEmpleado resultado = service.guardarHorario(horarioMock);

        assertNotNull(resultado);
        assertTrue(resultado.getActivo()); // Verifica que fuerza el setActivo(true)
        verify(empleadoClient, times(1)).obtenerEjecutivosDTO(101);
        verify(repoHorarios, times(1)).save(horarioMock);
    }

    @Test
    void guardarHorario_LanzaResponseStatusExceptionBadRequest_CuandoFeignLanzaNotFound() {
        when(empleadoClient.obtenerEjecutivosDTO(101)).thenThrow(feign.FeignException.NotFound.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.guardarHorario(horarioMock);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("El empleado no existe", exception.getReason());
    }

    //       TESTS METODO OBTENER HORARIO CON EMPLEADO
    @Test
    void obtenerHorarioConEmpleado_RetornaDTOCompleto_CuandoTodoExiste() {
        List<HorariosEmpleado> listaLocal = new ArrayList<>();
        listaLocal.add(horarioMock);

        when(empleadoClient.obtenerEjecutivosDTO(101)).thenReturn(ejecutivoDtoMock);
        when(repoHorarios.findByEmpleadoIdAndActivoTrue(101)).thenReturn(listaLocal);

        HorarioEmpleadoDTO resultado = service.obtenerHorarioConEmpleado(101);

        assertNotNull(resultado);
        assertEquals(101, resultado.getEmpleadoId());
        assertEquals("Raúl Jara", resultado.getNombreCompleto()); // Comprueba la concatenación
        assertEquals(1, resultado.getHorarios().size());
        assertEquals("Martes", resultado.getHorarios().get(0).getDiaSemana());
    }

    @Test
    void obtenerHorarioConEmpleado_LanzaNotFound_CuandoEmpleadoNoExisteRemotamente() {
        when(empleadoClient.obtenerEjecutivosDTO(999)).thenThrow(feign.FeignException.NotFound.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.obtenerHorarioConEmpleado(999);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Empleado no encontrado en el sistema remoto.", exception.getReason());
    }

    @Test
    void obtenerHorarioConEmpleado_LanzaNoContent_CuandoEmpleadoExistePeroNoTieneHorariosActivos() {
        when(empleadoClient.obtenerEjecutivosDTO(101)).thenReturn(ejecutivoDtoMock);
        // Simulamos que no se encuentran horarios activos locales en la BD
        when(repoHorarios.findByEmpleadoIdAndActivoTrue(101)).thenReturn(new ArrayList<>());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.obtenerHorarioConEmpleado(101);
        });

        // Valida la nueva lógica académica del 204 
        assertEquals(HttpStatus.NO_CONTENT, exception.getStatusCode());
        assertEquals("El empleado no registra horarios activos.", exception.getReason());
    }

    //         TESTS METODO DESACTIVAR POR LICENCIA
    @Test
    void desactivarPorLicencia_CambiaEstadoYGuarda_CuandoIdExiste() {
        when(repoHorarios.findById(1)).thenReturn(Optional.of(horarioMock));
        when(repoHorarios.save(any(HorariosEmpleado.class))).thenReturn(horarioMock);

        HorariosEmpleado resultado = service.desactivarPorLicencia(1, "Licencia Médica");

        assertNotNull(resultado);
        assertFalse(resultado.getActivo()); // Verifica el cambio de estado a inactivo
        assertEquals("Licencia Médica", resultado.getObservaciones()); 
        verify(repoHorarios, times(1)).save(horarioMock);
    }

    @Test
    void desactivarPorLicencia_LanzaNotFound_CuandoIdNoExiste() {
        when(repoHorarios.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            service.desactivarPorLicencia(99, "Vacaciones");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Registro de horario no encontrado.", exception.getReason());
    }
    //          TESTS METODO BUSCAR POR EMPLEADO

    @Test
    void buscarPorEmpleado_MapeaNombreCorrectamente_CuandoExistenHorariosYFeignResponde() {
        List<HorariosEmpleado> lista = new ArrayList<>();
        lista.add(horarioMock);

        when(repoHorarios.findByEmpleadoId(101)).thenReturn(lista);
        when(empleadoClient.obtenerEjecutivosDTO(101)).thenReturn(ejecutivoDtoMock);

        List<HorariosEmpleado> resultado = service.buscarPorEmpleado(101);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Raúl", resultado.get(0).getNombreEmpleado()); 
    }

    @Test
    void buscarPorEmpleado_AsignaTextoPorDefecto_CuandoFeignClientFalla() {
        List<HorariosEmpleado> lista = new ArrayList<>();
        lista.add(horarioMock);

        when(repoHorarios.findByEmpleadoId(101)).thenReturn(lista);
        // Forzamos una excepción genérica
        when(empleadoClient.obtenerEjecutivosDTO(101)).thenThrow(new RuntimeException("Error de conexión"));

        List<HorariosEmpleado> resultado = service.buscarPorEmpleado(101);

        assertNotNull(resultado);
        assertEquals("Nombre no disponible (msEmpleados fuera de linea)", resultado.get(0).getNombreEmpleado());
    }

    @Test
    void buscarPorEmpleado_RetornaListaVaciaYNoLlamaFeign_CuandoNoHayHorarios() {
        when(repoHorarios.findByEmpleadoId(101)).thenReturn(new ArrayList<>());

        List<HorariosEmpleado> resultado = service.buscarPorEmpleado(101);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(empleadoClient, times(0)).obtenerEjecutivosDTO(any(Integer.class));
    }
}