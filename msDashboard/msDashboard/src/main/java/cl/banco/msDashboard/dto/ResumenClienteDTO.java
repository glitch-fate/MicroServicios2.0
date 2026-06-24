package cl.banco.msDashboard.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumenClienteDTO {
    private String rut;
    private String nombreCompleto;
    private Double saldoBanco;
    private List<Object> tarjetas;      
    private List<Object> solicitudes;   
    private List<Object> seguros;       
    private List<Object> creditos;      
         
}