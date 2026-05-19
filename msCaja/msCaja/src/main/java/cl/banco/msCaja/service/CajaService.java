package cl.banco.msCaja.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.banco.msCaja.client.ClienteClient;
import cl.banco.msCaja.dto.ClienteDTO;
import cl.banco.msCaja.dto.ResumenSaldoDTO;
import cl.banco.msCaja.model.Saldo;
import cl.banco.msCaja.model.Transaccion;
import cl.banco.msCaja.repository.SaldoRepository;
import cl.banco.msCaja.repository.TransaccionRepository;
import jakarta.transaction.Transactional;

@Service
public class CajaService {

    @Autowired
    private SaldoRepository saldoRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private ClienteClient clienteClient; // Feign para hablar con msClientes

    // 1. CONSULTAR SALDO POR RUT (Retorna Nombre, RUT y Saldo)
    public ResumenSaldoDTO obtenerResumenSaldo(String rut) {
        // Llama a msClientes por Feign. Si el cliente no existe, Feign lanzará una excepción
        ClienteDTO cliente = clienteClient.obtenerClientePorRut(rut);
        
        // Busca su saldo en la BD de caja, si no tiene fila, asumimos 0.0
        Double saldoActual = saldoRepository.findByClienteId(cliente.getId())
                .map(Saldo::getMontoActual)
                .orElse(0.0);

        String nombreCompleto = cliente.getNombre() + " " + cliente.getApellido();
        return new ResumenSaldoDTO(nombreCompleto, cliente.getRut(), saldoActual);
    }

    // 2. DEPOSITAR DINERO POR RUT
    @Transactional
    public Transaccion depositarPorRut(String rut, Double monto) {
        ClienteDTO cliente = clienteClient.obtenerClientePorRut(rut);

        // Buscar el saldo actual por el ID del cliente o inicializarlo en 0 si es su primera vez
        Saldo saldo = saldoRepository.findByClienteId(cliente.getId())
                .orElse(new Saldo(cliente.getId(), 0.0));

        // Sumar el dinero
        saldo.setMontoActual(saldo.getMontoActual() + monto);
        saldoRepository.save(saldo);

        // Guardar registro en el historial de transacciones
        Transaccion t = new Transaccion(cliente.getId(), "DEPOSITO", monto);
        return transaccionRepository.save(t);
    }

    // 3. RETIRAR DINERO POR RUT (Validando fondos suficientes)
    @Transactional
    public Transaccion retirarPorRut(String rut, Double monto) {
        ClienteDTO cliente = clienteClient.obtenerClientePorRut(rut);

        Saldo saldo = saldoRepository.findByClienteId(cliente.getId())
                .orElse(new Saldo(cliente.getId(), 0.0));

        // REGLA DE NEGOCIO: Validar si tiene saldo suficiente antes de sacar
        if (saldo.getMontoActual() < monto) {
            throw new IllegalArgumentException("Fondos insuficientes. Saldo disponible: $" + saldo.getMontoActual());
        }

        // Restar el dinero
        saldo.setMontoActual(saldo.getMontoActual() - monto);
        saldoRepository.save(saldo);

        // Guardar registro en el historial de transacciones
        Transaccion t = new Transaccion(cliente.getId(), "RETIRO", monto);
        return transaccionRepository.save(t);
    }

}
