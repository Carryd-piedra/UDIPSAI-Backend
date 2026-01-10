package com.ucacue.udipsai.modules.pasante;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasanteRepositorio extends JpaRepository<Pasante, Integer>, JpaSpecificationExecutor<Pasante> {
    Optional<Pasante> findByCedula(String cedula);
    boolean existsByCedula(String cedula);
    List<Pasante> findByActivoTrue();
    Page<Pasante> findByActivoTrue(Pageable pageable);
}
