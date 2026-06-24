package cl.bancl.msCreditos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cl.bancl.msCreditos.client.ClienteClient;
import cl.bancl.msCreditos.dto.ClientesDTO;
import cl.bancl.msCreditos.model.Creditos;
import cl.bancl.msCreditos.repository.CreditosRepository;

@Service
public class CreditoService {
    @Autowired
    private ClienteClient clienteClient;

    @Autowired
    private CreditosRepository repoCredito;

    // mtodo para solicitar el credito (POST)
    public Creditos solicitarCredito(Creditos credito) {
        try {
            // validamos en tiempo real con msClientes usando el RUT
            ClientesDTO clienteDto = clienteClient.obtenerClientePorRut(credito.getRutCliente());
            
            // si existe, rescatamos su nombre completo
            String nombreCompleto = clienteDto.getNombre() + " " + clienteDto.getApellido();
            credito.setNombreCliente(nombreCompleto);
            
            // calculos
            double interes = 1.10; // 10% interes 
            double totalAPagar = Math.round(credito.getMontoSolicitado() * interes);
            double valorCuota = Math.round(totalAPagar / credito.getMesesPlazo());

            credito.setMontoTotalAPagar(totalAPagar);
            credito.setValorCuotaMensual(valorCuota);
            credito.setSaldoPendiente(totalAPagar);
            credito.setEstado("VIGENTE");

            // guardamos en la base de datos de créditos
            return repoCredito.save(credito);

        } catch (feign.FeignException.NotFound e) {
            // Si el otro microservicio responde con 404
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: El cliente con el RUT ingresado no existe en el sistema.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de comunicación con el servicio de clientes.");
        }
    }

    //metodo para pagar
    public Creditos pagarCuota(Long id, Double montoAbono) {
        Creditos credito = repoCredito.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Crédito no encontrado"));

        if (credito.getEstado().equals("PAGADO")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este crédito ya está completamente pagado");
        }

        // Restamos el abono al saldo pendiente
        double nuevoSaldo = credito.getSaldoPendiente() - montoAbono;
        
        if (nuevoSaldo <= 0) {
            credito.setSaldoPendiente(0.0);
            credito.setEstado("PAGADO");
        } else {
            credito.setSaldoPendiente(Math.round(nuevoSaldo * 1.0));
        }

        return repoCredito.save(credito);
    }
}


