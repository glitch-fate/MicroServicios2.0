package cl.duoc.citas.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCitaDTO {

    private Integer id;


    private Date fechaCita;

    private String horaCita;

    private String motivoCita;

    //ahora datos externos

    private ClienteDTO cliente;

    private EjecutivosDTO ejecutivos;

    //ahora dato que sacaremos de nuestra bd

    private TipoCitasDTO tipoCitas;

    

}
