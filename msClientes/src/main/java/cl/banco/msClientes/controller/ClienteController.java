package cl.banco.msClientes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    // 1. LISTAR: Valida si está vacío para retornar 204, de lo contrario devuelve 200 con la lista
    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        List<Cliente> lista = service.listarClientes();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }
        return ResponseEntity.ok(lista); // HTTP 200 OK
    }

    // 2. BUSCAR POR ID: try-catch para atajar la RuntimeException y devolver 404
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscar(@PathVariable Integer id) {
        try {
            Cliente cliente = service.buscarPorId(id);
            return ResponseEntity.ok(cliente); // HTTP 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // HTTP 404 Not Found
        }
    }

    // 3. BUSCAR POR RUT: Retorna el DTO con 200 o salta al catch con 404 si no existe
    @GetMapping("/rut/{rut}")
    public ResponseEntity<ClienteDTO> buscarRut(@PathVariable String rut) {
        try {
            Cliente cliente = service.buscarPorRut(rut);
            ClienteDTO dto = new ClienteDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getRut()
            );
            return ResponseEntity.ok(dto); // HTTP 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // HTTP 404 Not Found
        }
    }

    // 4. GUARDAR: Crea el registro con éxito y retorna 200 OK
    @PostMapping
    public ResponseEntity<Cliente> guardar(@RequestBody Cliente cliente) {
        Cliente nuevoCliente = service.agregarCliente(cliente);
        return ResponseEntity.ok(nuevoCliente); // HTTP 200 OK
    }

    // 5. ACTUALIZAR: Modifica los datos. Si el ID no existe en la BD, el service lanza error y responde 404
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Integer id, @RequestBody Cliente cliente) {
        try {
            Cliente actualizado = service.actualizar(id, cliente);
            return ResponseEntity.ok(actualizado); // HTTP 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // HTTP 404 Not Found
        }
    }

    // 6. ELIMINAR: void en la lógica de negocio. Si sale todo bien, responde un 204 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // HTTP 404 Not Found
        }
    }

      //   MÉTODOS DTO
       
    @GetMapping("/dto/{id}")
    public ResponseEntity<ClienteDTO> obtenerClienteDTO(@PathVariable Integer id) {
        try {
            Cliente cliente = service.buscarPorId(id);
            ClienteDTO dto = new ClienteDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getRut()
            );
            return ResponseEntity.ok(dto); // HTTP 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // HTTP 404 Not Found
        }
    }
}