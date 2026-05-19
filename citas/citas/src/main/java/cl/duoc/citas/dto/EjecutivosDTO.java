package cl.duoc.citas.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EjecutivosDTO {

    private Integer id;

    private String nombre;

    @JsonIgnore
    private String cargo;

}
