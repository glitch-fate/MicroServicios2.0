package cl.bancl.msHorarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class HorariosEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer empleadoId;

    @Column(nullable = false)
    private String diaSemana;

    @Column(nullable = false)
    private String horaInicio;

    @Column(nullable = false)
    private String horaFin;

    @Transient
    private String nombreEmpleado;

    @Column(nullable = false)
private Boolean activo = true;
// esto rompe cualquier conflicto 
public Boolean getActivo() {
    return this.activo != null ? this.activo : true;
}

    @Column(nullable = true)
    private String observaciones;

}
