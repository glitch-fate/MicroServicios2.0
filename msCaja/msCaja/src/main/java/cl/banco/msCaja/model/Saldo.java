package cl.banco.msCaja.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Saldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false, unique = true)
    private Long clienteId;

    @Column(name = "monto_actual", nullable = false)
    private Double montoActual;

    /*
     Lo creamos a mano porque cuando registramos un saldo por primera vez en el Service,
     NO conocemos el `id` (lo genera MySQL).
     Uso en CajaService: `Saldo nuevoSaldo = new Saldo(clienteId, 0.0);`
     */
    public Saldo(Long clienteId, Double montoActual) {
        this.clienteId = clienteId;
        this.montoActual = montoActual;
    }

}
