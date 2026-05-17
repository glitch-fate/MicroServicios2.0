package cl.banco.msClientes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.banco.msClientes.dto.ClienteDTO;
import cl.banco.msClientes.model.Cliente;
import cl.banco.msClientes.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping
    public List<Cliente> listar() {
        return service.listarClientes();
    }

    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/rut/{rut}")
    public Cliente buscarRut(@PathVariable String rut) {
        return service.buscarPorRut(rut);
    }

    @PostMapping
    public Cliente guardar(@RequestBody Cliente cliente) {
        return service.agregarCliente(cliente);
    }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Integer id,
                              @RequestBody Cliente cliente) {
        return service.actualizar(id, cliente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }

    // metodos dto -----------------------------------------------------------------------------
    @GetMapping("/dto/{id}")
    public ResponseEntity<ClienteDTO> obtenerClienteDTO(@PathVariable Integer id){

    Cliente cliente = service.buscarPorId(id);

    ClienteDTO dto = new ClienteDTO(
        cliente.getId(),
        cliente.getNombre(),
        cliente.getApellido()
    );
    return ResponseEntity.ok(dto);
    
   
}
}
