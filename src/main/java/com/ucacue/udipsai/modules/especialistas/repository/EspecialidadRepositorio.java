package com.ucacue.udipsai.modules.especialistas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspecialidadRepositorio extends JpaRepository<Especialidad, Integer> {
    List<Especialidad> findByActivoTrue();
}
