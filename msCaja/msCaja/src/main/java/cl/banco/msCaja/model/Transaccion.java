package cl.banco.msCaja.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "transacciones_caja")
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "tipo_transaccion", nullable = false)
    private String tipoTransaccion; 

    @Column(nullable = false)
    private Double monto;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    /*
      PRE-PERSIST:
      Ejecuta esta logica automáticamente un milisegundo antes de que el registro se guarde en MySQL.
     Así nos aseguramos de que la fecha y hora sean siempre las del servidor actual.
     */
    @PrePersist
    protected void onCreate() {
        this.fechaHora = LocalDateTime.now();
    }

    /*
      Nos permite registrar un movimiento de caja pasando solo los 3 datos clave de la operación.
      Omitimos el `id` y la `fechaHora` porque la base de datos y el @PrePersist se encargan de ellos.
     Uso en CajaService: `Transaccion t = new Transaccion(clienteId, "RETIRO", monto);`
     */
    public Transaccion(Long clienteId, String tipoTransaccion, Double monto) {
        this.clienteId = clienteId;
        this.tipoTransaccion = tipoTransaccion;
        this.monto = monto;
    }

}
