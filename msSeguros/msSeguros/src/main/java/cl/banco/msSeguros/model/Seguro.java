package cl.banco.msSeguros.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Seguro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroPoliza;

    @Column(nullable = false)
    private String tipoSeguro; 

    @Column(nullable = false)
    private Double montoAsegurado;

    @Column(nullable = false)
    private Double primaMensual;

    private LocalDate fechaContratacion;
    private String estado; 

    @Column(nullable = false)
    private Long clienteId; 
}
