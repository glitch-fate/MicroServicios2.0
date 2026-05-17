package cl.banco.msEmpleados.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EjecutivoDTO {

    private Integer id;
    

    private String nombre;

    private String cargoEjecutivo; //este es String porque solo enviamos el nombre y no el objeto completo

}
