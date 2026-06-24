package cl.bancl.msCreditos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "creditos")
public class Creditos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rutCliente;

    private double montoSolicitado;

    private Integer mesesPlazo;

    private double montoTotalAPagar; // ojito ojotal ojamen esto es monto solicitado + interes 

    private double valorCuotaMensual;

    private double saldoPendiente; //cuanto le queda por pagar al cliente 

    private String estado; //podria ser vigente, pagado 

    @Transient
    private String nombreCliente;

}
