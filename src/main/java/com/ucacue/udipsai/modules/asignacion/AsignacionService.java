package com.ucacue.udipsai.modules.asignacion;

import com.ucacue.udipsai.modules.paciente.domain.Paciente;
import com.ucacue.udipsai.modules.paciente.repository.PacienteRepositorio;
import com.ucacue.udipsai.modules.paciente.service.PacienteService;
import com.ucacue.udipsai.modules.pasante.domain.Pasante;
import com.ucacue.udipsai.modules.pasante.repository.PasanteRepositorio;
import com.ucacue.udipsai.modules.pasante.service.PasanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AsignacionService {

    @Autowired
    private AsignacionRepositorio asignacionRepositorio;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private PasanteRepositorio pasanteRepositorio;

    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private PasanteService pasanteService;

    public List<AsignacionDTO> listarAsignaciones() {
        log.info("Consultando todas las asignaciones activas");
        return asignacionRepositorio.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public AsignacionDTO crearAsignacion(AsignacionRequest request) {
        log.info("Creando asignación para Paciente ID: {} y Pasante ID: {}", request.getPacienteId(), request.getPasanteId());
        if (request.getPacienteId() == null) {
            log.error("Error al crear asignación: Paciente ID es nulo");
            throw new IllegalArgumentException("Paciente ID requerido");
        }
        if (request.getPasanteId() == null) {
            log.error("Error al crear asignación: Pasante ID es nulo");
            throw new IllegalArgumentException("Pasante ID requerido");
        }

        Paciente paciente = pacienteRepositorio.findById(request.getPacienteId())
                .orElseThrow(() -> {
                    log.error("Error al crear asignación: Paciente con ID {} no encontrado", request.getPacienteId());
                    return new RuntimeException("Paciente no encontrado");
                });
        Pasante pasante = pasanteRepositorio.findById(request.getPasanteId())
                .orElseThrow(() -> {
                    log.error("Error al crear asignación: Pasante con ID {} no encontrado", request.getPasanteId());
                    return new RuntimeException("Pasante no encontrado");
                });

        Asignacion asignacion = new Asignacion();
        asignacion.setPaciente(paciente);
        asignacion.setPasante(pasante);
        asignacion.setActivo(true);
        
        Asignacion asignacionGuardada = asignacionRepositorio.save(asignacion);
        log.info("Asignación creada exitosamente con ID: {}", asignacionGuardada.getId());

        return convertirADTO(asignacionGuardada);
    }
    
    public List<AsignacionDTO> listarAsignacionesPorPasanteId(Integer pasanteId) {
        log.info("Consultando asignaciones activas para el pasante ID: {}", pasanteId);
        return asignacionRepositorio.findByPasanteIdAndActivoTrue(pasanteId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public void eliminarAsignacion(Long id) {
        if (id == null) {
            log.warn("Intento de eliminar asignación con ID nulo");
            return;
        }
        asignacionRepositorio.findById(id).ifPresentOrElse(a -> {
            log.info("Eliminando (desactivando) asignación ID: {}", id);
            a.setActivo(false);
            asignacionRepositorio.save(a);
        }, () -> log.warn("Intento de eliminar asignación inexistente ID: {}", id));
    }

    public AsignacionDTO convertirADTO(Asignacion asignacion) {
        return AsignacionDTO.builder()
                .id(asignacion.getId())
                .paciente(pacienteService.convertirADTO(asignacion.getPaciente()))
                .pasante(pasanteService.convertirADTO(asignacion.getPasante()))
                .activo(asignacion.getActivo())
                .build();
    }
}
