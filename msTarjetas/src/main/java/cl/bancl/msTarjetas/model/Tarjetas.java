package cl.bancl.msTarjetas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tarjetas")
public class Tarjetas {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Asegúrate de que sean String simples
    @Column(name = "rut_cliente") // Opcional: asegura el mapeo en la BD
    private String rutCliente;
    
    @Column(name = "tipo_tarjeta")
    private String tipoTarjeta; 
    
    @Column(name = "numero_tarjeta")
    private String numeroTarjeta; 
    
    @Column(name = "cupo_total")
    private Double cupoTotal;
    
    @Column(name = "cupo_disponible")
    private Double cupoDisponible;
    
    private String estado; 
    
    @Column(name = "aprobado_por")
    private String aprobadoPor; 

    @Transient
    private String nombreCliente;

}
