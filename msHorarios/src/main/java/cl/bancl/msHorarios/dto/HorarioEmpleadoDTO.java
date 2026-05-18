package cl.bancl.msHorarios.dto;

import java.util.List;

import lombok.Data;

@Data
public class HorarioEmpleadoDTO {

    private Integer empleadoId;
    private String nombreCompleto; //este lo traeremos desde otro ms y se juntaran los datos : nombre + apellido en un solo campo
    private List<DiasTrabajoDTO> horarios;

}
