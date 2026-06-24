package cl.banco.msClientes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.banco.msClientes.model.Cliente;
import cl.banco.msClientes.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repo;

    // Listar clientes
    public List<Cliente> listarClientes() {
        return repo.findAll();
    }

    // Buscar por ID
    public Cliente buscarPorId(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    // Buscar por RUT
    public Cliente buscarPorRut(String rut) {
        return repo.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    // Agregar cliente
    public Cliente agregarCliente(Cliente cliente) {

        if (cliente.getDireccion() != null) {
            cliente.getDireccion().setCliente(cliente);
        }

        if (cliente.getContacto() != null) {
            cliente.getContacto().setCliente(cliente);
        }

        return repo.save(cliente);
    }

    // Actualizar cliente
    public Cliente actualizar(Integer id, Cliente clienteActualizado) {

        Cliente cliente = buscarPorId(id);

        cliente.setRut(clienteActualizado.getRut());
        cliente.setNombre(clienteActualizado.getNombre());
        cliente.setApellido(clienteActualizado.getApellido());
        cliente.setCorreo(clienteActualizado.getCorreo());
        cliente.setTipoCliente(clienteActualizado.getTipoCliente());

        if (clienteActualizado.getDireccion() != null) {
            clienteActualizado.getDireccion().setCliente(cliente);
            cliente.setDireccion(clienteActualizado.getDireccion());
        }

        if (clienteActualizado.getContacto() != null) {
            clienteActualizado.getContacto().setCliente(cliente);
            cliente.setContacto(clienteActualizado.getContacto());
        }

        return repo.save(cliente);
    }

    // Eliminar cliente
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
