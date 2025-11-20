package com.ucacue.udipsai.modules.sedes.domain.repository;

import com.ucacue.udipsai.modules.sedes.domain.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SedeRepositorio extends JpaRepository<Sede, Integer> {
    List<Sede> findByEstado(Integer estado);
    Optional<Sede> findByNombre(String nombre);
    List<Sede> findByNombreIgnoreCase(String nombre);


}
