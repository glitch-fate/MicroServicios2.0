package cl.duoc.citas.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCitaDTO {

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Santiago")
    private Date fechaCita;

    private String horaCita;

    private String motivoCita;

    //ahora datos externos

    private ClienteDTO cliente;

    private EjecutivosDTO ejecutivos;

    //ahora dato que sacaremos de nuestra bd

    private TipoCitasDTO tipoCitas;

    

}
