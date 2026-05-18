package cl.banco.msSolicitudes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solicitud")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private Integer clienteId;

    @Column(nullable = false)
    private Integer empleadoId;
}