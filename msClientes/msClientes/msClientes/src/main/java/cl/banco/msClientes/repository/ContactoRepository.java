package cl.banco.msClientes.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.banco.msClientes.model.Contacto;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Integer> {
}
