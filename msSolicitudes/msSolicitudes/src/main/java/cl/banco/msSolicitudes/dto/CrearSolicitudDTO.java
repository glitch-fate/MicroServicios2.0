package cl.banco.msSolicitudes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearSolicitudDTO {

    private String rut;
    private String motivo;
    private String mensaje;

}
