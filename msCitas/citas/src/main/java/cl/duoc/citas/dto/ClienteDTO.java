package cl.duoc.citas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//acá van todos los datos que obtenemos desde el msClientes, como es dto solo lo importante

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Integer id;

    private String nombre;

    private String apellido;


}
