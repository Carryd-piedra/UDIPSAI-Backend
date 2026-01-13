package com.ucacue.udipsai.modules.citas.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ucacue.udipsai.modules.citas.domain.Cita;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer>, JpaSpecificationExecutor<Cita> {
        
    boolean existsByEstadoAndFechaAndHoraInicioAndIdPaciente(Cita.Estado estado, LocalDate fecha, LocalTime hora, Integer idPaciente);

    boolean existsByEstadoAndFechaAndHoraInicioAndIdProfesional(Cita.Estado estado, LocalDate fecha, LocalTime hora, Integer idProfesional);

    Page<Cita> findAllByIdPaciente(Integer idPaciente, Pageable pageable);

    @Query("SELECT c FROM Cita c WHERE " +
            "(:id IS NULL OR c.id = :id) AND " +
            "(:idPaciente IS NULL OR c.idPaciente = :idPaciente)")
    Page<Cita> findCitasByFilter(@Param("id") Integer id, @Param("idPaciente") Integer idPaciente, Pageable pageable);

    @Query("SELECT c.horaInicio FROM Cita c WHERE c.idProfesional = :idProfesional AND c.fecha = :fecha AND c.estado = 'PENDIENTE'")
    List<LocalTime> findHorasOcupadasByProfesionalAndFecha(Integer idProfesional, LocalDate fecha);

    @Query("SELECT c FROM Cita c WHERE " +
            "(:filtro IS NULL OR CAST(c.idPaciente AS string) LIKE %:filtro%)")
    Page<Cita> findCitasFiltro(@Param("filtro") String filtro, Pageable pageable);

}
