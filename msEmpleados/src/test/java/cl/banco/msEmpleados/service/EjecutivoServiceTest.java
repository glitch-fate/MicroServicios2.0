package cl.banco.msEmpleados.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import cl.banco.msEmpleados.model.CargoEjecutivo;
import cl.banco.msEmpleados.model.Ejecutivo;
import cl.banco.msEmpleados.repository.CargoEjecutivoRepository;
import cl.banco.msEmpleados.repository.EjecutivoRepository;

@ExtendWith(MockitoExtension.class)
public class EjecutivoServiceTest {

    @Mock
    private EjecutivoRepository repoEjecutivo;

    @Mock
    private CargoEjecutivoRepository repoCargo;

    @InjectMocks
    private EjecutivoService service;

    private Ejecutivo ejecutivoMock;
    private CargoEjecutivo cargoMock;

    @BeforeEach
    void setUp() {
        cargoMock = new CargoEjecutivo();
        cargoMock.setId(1);
        cargoMock.setNombre("Ejecutivo de Inversiones");

        ejecutivoMock = new Ejecutivo();
        ejecutivoMock.setId(10);
        ejecutivoMock.setRut("17444555-K");
        ejecutivoMock.setNombre("Andrés");
        ejecutivoMock.setApellido("Jara");
        ejecutivoMock.setCargo(cargoMock);
    }
    //            TESTS METODO LISTAR

    @Test
    void listar_RetornaListaDeEjecutivos() {
        List<Ejecutivo> lista = new ArrayList<>();
        lista.add(ejecutivoMock);
        when(repoEjecutivo.findAll()).thenReturn(lista);

        List<Ejecutivo> resultado = service.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Andrés", resultado.get(0).getNombre());
        verify(repoEjecutivo, times(1)).findAll();
    }
    //          TESTS METODO BUSCAR POR ID

    @Test
    void buscarPorId_RetornaEjecutivo_CuandoIdExiste() {
        when(repoEjecutivo.findById(10)).thenReturn(Optional.of(ejecutivoMock));

        Ejecutivo resultado = service.buscarPorId(10);

        assertNotNull(resultado);
        assertEquals(10, resultado.getId());
        assertEquals("Andrés", resultado.getNombre());
    }

    @Test
    void buscarPorId_LanzaRuntimeException_CuandoIdNoExiste() {
        when(repoEjecutivo.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.buscarPorId(99);
        });

        assertEquals("Ejecutivo no encontrado", exception.getMessage());
    }
    //            TESTS METODO GUARDAR

    @Test
    void guardar_UsaCargoExistente_CuandoCargoYaExisteEnBD() {
        // Simulamos que el cargo ya existe en la base de datos
        when(repoCargo.findByNombre("Ejecutivo de Inversiones")).thenReturn(Optional.of(cargoMock));
        when(repoEjecutivo.save(any(Ejecutivo.class))).thenReturn(ejecutivoMock);

        Ejecutivo resultado = service.guardar(ejecutivoMock);

        assertNotNull(resultado);
        assertEquals("Ejecutivo de Inversiones", resultado.getCargo().getNombre());
        // Verificamos que NO se llamó al save de cargos porque ya existía
        verify(repoCargo, times(0)).save(any(CargoEjecutivo.class));
        verify(repoEjecutivo, times(1)).save(ejecutivoMock);
    }

    @Test
    void guardar_CreaNuevoCargo_CuandoCargoNoExisteEnBD() {
        // Simulamos que el cargo NO existe en la base de datos (retorna vacío)
        when(repoCargo.findByNombre("Ejecutivo de Inversiones")).thenReturn(Optional.empty());
        // Simulamos el guardado del nuevo cargo en el orElseGet
        when(repoCargo.save(any(CargoEjecutivo.class))).thenReturn(cargoMock);
        when(repoEjecutivo.save(any(Ejecutivo.class))).thenReturn(ejecutivoMock);

        Ejecutivo resultado = service.guardar(ejecutivoMock);

        assertNotNull(resultado);
        verify(repoCargo, times(1)).save(any(CargoEjecutivo.class)); // Se debió crear
        verify(repoEjecutivo, times(1)).save(ejecutivoMock);
    }

    @Test
    void guardar_LanzaIllegalArgumentException_CuandoCargoEsNull() {
        ejecutivoMock.setCargo(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(ejecutivoMock);
        });

        assertEquals("El ejecutivo debe tener un cargo asignado", exception.getMessage());
    }
    //           TESTS METODO ELIMINAR

    @Test
    void eliminar_BorraEjecutivo_CuandoIdExiste() {
        when(repoEjecutivo.existsById(10)).thenReturn(true);

        assertDoesNotThrow(() -> service.eliminar(10));

        verify(repoEjecutivo, times(1)).deleteById(10);
    }

    @Test
    void eliminar_LanzaRuntimeException_CuandoIdNoExiste() {
        when(repoEjecutivo.existsById(99)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.eliminar(99);
        });

        assertEquals("Ejecutivo no Existe", exception.getMessage());
        verify(repoEjecutivo, times(0)).deleteById(99);
    }
}