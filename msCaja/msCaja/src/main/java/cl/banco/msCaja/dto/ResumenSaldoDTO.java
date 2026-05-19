package cl.banco.msCaja.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenSaldoDTO {

    private String nombreCompleto;

    private String rut;

    private Double saldoTotal;

}
