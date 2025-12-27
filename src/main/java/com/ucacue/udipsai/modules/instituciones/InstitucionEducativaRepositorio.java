package com.ucacue.udipsai.modules.instituciones;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitucionEducativaRepositorio extends JpaRepository<InstitucionEducativa, Integer> {
    List<InstitucionEducativa> findByActivoTrue();
    Optional<InstitucionEducativa> findByNombre(String nombre);
    Optional<InstitucionEducativa> findByTipo(String tipo);
    Optional<InstitucionEducativa> findByDireccion(String direccion);
    boolean existsByNombre(String nombre);
}
