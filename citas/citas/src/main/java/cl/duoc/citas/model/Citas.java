package cl.duoc.citas.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
public class Citas {

    @Id
    @GeneratedValue (strategy =GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Date fechaCita;

    @Column(nullable = false)
    private String hora;
    
// relaciones
    @ManyToOne
    @JoinColumn(name = "tipo_Citas_id", nullable = false)
    private TipoCitas tipoCitas;

    //microservicios solo las id

    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;


    @Column(name = "ejecutivo_id", nullable = false)
    private Integer ejecutivoId;

    
   


}
