package com.ucacue.udipsai.modules.especialistas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EspecialistaRepositorio extends JpaRepository<Especialista, Integer>, JpaSpecificationExecutor<Especialista> {
    Optional<Especialista> findByCedula(String cedula);
    boolean existsByCedula(String cedula);
    List<Especialista> findByActivoTrue();
}
