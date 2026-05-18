package cl.banco.msSeguros.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeguroRespuestaDTO {

    private Long id;
    private String numeroPoliza;
    private String tipoSeguro;
    private Double montoAsegurado;
    private Double primaMensual;
    private LocalDate fechaContratacion;
    private String estado;
    private ClienteDTO cliente; 


}
