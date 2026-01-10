package com.ucacue.udipsai.modules.especialistas.repository;

import com.ucacue.udipsai.modules.especialistas.domain.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspecialidadRepositorio extends JpaRepository<Especialidad, Integer> {
    List<Especialidad> findByActivoTrue();
}
