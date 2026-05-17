package cl.banco.msClientes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {
    
    private Integer id;
    private String nombre;
    private String apellido;

}
