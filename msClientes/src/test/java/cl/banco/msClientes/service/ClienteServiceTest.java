package cl.banco.msClientes.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.banco.msClientes.model.Cliente;
import cl.banco.msClientes.model.Contacto;
import cl.banco.msClientes.repository.ClienteRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteEjemplo;

    @BeforeEach
    void setUp() {
        clienteEjemplo = new Cliente();
        clienteEjemplo.setId(1);
        clienteEjemplo.setRut("15.456.123-k");
        clienteEjemplo.setNombre("Tiwita");
        clienteEjemplo.setApellido("lawita");
        clienteEjemplo.setCorreo("prueba@prueba.cl");
        clienteEjemplo.setTipoCliente("Persona");
        
        Contacto contactoEjemplo = new Contacto();
        contactoEjemplo.setId(1);
        contactoEjemplo.setTelefono("9-45457878");

        clienteEjemplo.setContacto(contactoEjemplo);
    }

    @Test
    void listarClientes_DeberiaRetornarListaDeClientes() {
        // ARRANGE
        List<Cliente> listaFalsa = new ArrayList<>();
        listaFalsa.add(clienteEjemplo);
        
        when(clienteRepository.findAll()).thenReturn(listaFalsa);

        // ACT
        List<Cliente> resultado = clienteService.listarClientes();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Tiwita", resultado.get(0).getNombre());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_CuandoClienteExiste_DeberiaRetornarCliente() {
        // ARRANGE
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteEjemplo));

        // ACT
        Cliente resultado = clienteService.buscarPorId(1);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("15.456.123-k", resultado.getRut());
        verify(clienteRepository, times(1)).findById(1);
    }

    @Test
    void buscarPorId_CuandoClienteNoExiste_DeberiaLanzarRuntimeException() {
        // ARRANGE
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.buscarPorId(99);
        });

        assertEquals("Cliente no encontrado", exception.getMessage());
        verify(clienteRepository, times(1)).findById(99);
    }

    @Test
    void buscarPorRut_CuandoClienteExiste_DeberiaRetornarCliente() {
        // ARRANGE
        when(clienteRepository.findByRut("15.456.123-k")).thenReturn(Optional.of(clienteEjemplo));

        // ACT
        Cliente resultado = clienteService.buscarPorRut("15.456.123-k");

        // ASSERT
        assertNotNull(resultado);
        assertEquals("15.456.123-k", resultado.getRut());
        verify(clienteRepository, times(1)).findByRut("15.456.123-k");
    }

    @Test
    void buscarPorRut_CuandoClienteNoExiste_DeberiaLanzarRuntimeException() {
        // ARRANGE
        when(clienteRepository.findByRut("99.999.999-9")).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.buscarPorRut("99.999.999-9");
        });

        assertEquals("Cliente no encontrado", exception.getMessage());
        verify(clienteRepository, times(1)).findByRut("99.999.999-9");
    }

    @Test
    void agregarCliente_DeberiaAsignarRelacionesYGuardarCliente() {
        // ARRANGE
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEjemplo);

        // ACT
        Cliente resultado = clienteService.agregarCliente(clienteEjemplo);

        // ASSERT
        assertNotNull(resultado);
        assertNotNull(resultado.getContacto());
        // Verifica que se cumpla la lógica bidireccional del Service
        assertEquals(clienteEjemplo, resultado.getContacto().getCliente());
        verify(clienteRepository, times(1)).save(clienteEjemplo);
    }

    @Test
    void actualizar_CuandoClienteExiste_DeberiaModificarYGuardarDatos() {
        // ARRANGE
        Cliente clienteConNuevosDatos = new Cliente();
        clienteConNuevosDatos.setRut("15.456.123-k");
        clienteConNuevosDatos.setNombre("Tiwita Modificado");
        clienteConNuevosDatos.setApellido("lawita");
        clienteConNuevosDatos.setCorreo("nuevo_correo@prueba.cl");
        clienteConNuevosDatos.setTipoCliente("Empresa");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteEjemplo));
        // any(Cliente.class) captura el objeto modificado y thenAnswer lo retorna para el assert
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Cliente resultado = clienteService.actualizar(1, clienteConNuevosDatos);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("Tiwita Modificado", resultado.getNombre());
        assertEquals("nuevo_correo@prueba.cl", resultado.getCorreo());
        assertEquals("Empresa", resultado.getTipoCliente());
        verify(clienteRepository, times(1)).findById(1);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void eliminar_DeberiaLlamarAlMetodoDeleteByIdDelRepository() {
        // ACT
        clienteService.eliminar(1);

        // ASSERT
        verify(clienteRepository, times(1)).deleteById(1);
    }
}