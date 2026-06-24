package cl.banco.msDashboard.service;

import java.util.ArrayList;

import java.util.Map;
import org.springframework.stereotype.Service;

import cl.banco.msDashboard.client.CajaClient;

import cl.banco.msDashboard.client.ClienteClient;
import cl.banco.msDashboard.client.CreditoClient;
import cl.banco.msDashboard.client.SeguroClient;
import cl.banco.msDashboard.client.SolicitudClient;
import cl.banco.msDashboard.client.TarjetaClient;
import cl.banco.msDashboard.dto.ResumenClienteDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class DashboardService {

    private final TarjetaClient tarjetaClient;
    private final SolicitudClient solicitudClient;
    private final SeguroClient seguroClient;
    private final CajaClient cajaClient;
    private final CreditoClient creditoClient;
    private final ClienteClient clienteClient; 

    public ResumenClienteDTO obtenerResumen360(String rut) {
        ResumenClienteDTO resumen = new ResumenClienteDTO();
        resumen.setRut(rut);
        resumen.setNombreCompleto("Cliente de Prueba");
        
        Long clienteId = null;
        try {
            Map<String, Object> clienteData = clienteClient.obtenerClientePorRut(rut);
            if (clienteData != null && clienteData.containsKey("id")) {
                clienteId = Long.valueOf(clienteData.get("id").toString());
                
                if (clienteData.containsKey("nombre")) {
                    resumen.setNombreCompleto(clienteData.get("nombre").toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Error en msClientes: " + e.getMessage());
        }

        try {
            Double saldo = cajaClient.obtenerSaldoPorRut(rut);
            resumen.setSaldoBanco(saldo != null ? saldo : 0.0);
        } catch (Exception e) {
            resumen.setSaldoBanco(0.0);
            System.out.println("Error en msCaja (Double): " + e.getMessage());
        }

        try {
            resumen.setTarjetas(tarjetaClient.consultarTarjetasPorRut(rut));
        } catch (Exception e) {
            resumen.setTarjetas(new ArrayList<>());
            System.out.println("Error en msTarjetas: " + e.getMessage());
        }

        try {
            resumen.setSolicitudes(solicitudClient.listarPorRut(rut));
        } catch (Exception e) {
            resumen.setSolicitudes(new ArrayList<>());
            System.out.println("Error en msSolicitudes: " + e.getMessage());
        }

        try {
            if (clienteId != null) {
                resumen.setSeguros(seguroClient.listarSegurosPorClienteId(clienteId));
            } else {
                resumen.setSeguros(new ArrayList<>());
                System.out.println("No se buscaron seguros: ID de cliente nulo.");
            }
        } catch (Exception e) {
            resumen.setSeguros(new ArrayList<>());
            System.out.println("Error en msSeguros: " + e.getMessage());
        }

        try {
            resumen.setCreditos(creditoClient.listarCreditosPorRut(rut));
        } catch (Exception e) {
            resumen.setCreditos(new ArrayList<>());
            System.out.println("Error en msCreditos: " + e.getMessage());
        }

        return resumen;
    }
}