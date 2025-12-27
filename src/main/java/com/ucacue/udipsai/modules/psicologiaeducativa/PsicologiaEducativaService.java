package com.ucacue.udipsai.modules.psicologiaeducativa;

import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.paciente.PacienteRepositorio;
import com.ucacue.udipsai.modules.paciente.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PsicologiaEducativaService {

    @Autowired
    private PsicologiaEducativaRepository repository;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;
    
    @Autowired
    private PacienteService pacienteService;

    public List<PsicologiaEducativaDTO> listarFichasPsicologiaEducativa() {
        log.info("Consultando todas las fichas de psicología educativa activas");
        return repository.findAll().stream()
                .filter(PsicologiaEducativa::getActivo)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public PsicologiaEducativaDTO obtenerFichaPsicologiaEducativaPorPacienteId(Integer pacienteId) {
        log.info("Consultando ficha de psicología educativa por paciente ID: {}", pacienteId);
        PsicologiaEducativa ficha = repository.findByPacienteIdAndActivo(pacienteId, true);
        if (ficha != null && ficha.getActivo()) {
            return convertirADTO(ficha);
        }
        return null;
    }
    
    public PsicologiaEducativa obtenerEntidadFichaPorIdPaciente(Integer pacienteId) {
        log.debug("Consultando entidad ficha psicología educativa por paciente ID: {}", pacienteId);
        return repository.findByPacienteIdAndActivo(pacienteId, true);
    }

    @Transactional
    public PsicologiaEducativaDTO guardarFichaPsicologiaEducativa(PsicologiaEducativaRequest request) {
        log.info("Iniciando guardado de ficha psicología educativa para paciente ID: {}", request.getPacienteId());
        if (request.getPacienteId() == null) {
            log.error("ID de paciente nulo en request");
            throw new IllegalArgumentException("El ID del paciente es requerido");
        }
            PsicologiaEducativa ficha = repository.findByPacienteIdAndActivo(request.getPacienteId(), true);
            
            if (ficha == null ) {
                log.info("Creando nueva ficha para paciente ID: {}", request.getPacienteId());
                ficha = new PsicologiaEducativa();
            Paciente paciente = pacienteRepositorio.findById(request.getPacienteId())
                .orElseThrow(() -> {
                    log.error("Paciente ID {} no encontrado", request.getPacienteId());
                    return new RuntimeException("Paciente no encontrado");
                });
            ficha.setPaciente(paciente);
        } else {
            log.info("Actualizando ficha existente ID: {}", ficha.getId());
        }
        
        if (request.getHistoriaEscolar() != null) ficha.setHistoriaEscolar(request.getHistoriaEscolar());
        if (request.getDesarrollo() != null) ficha.setDesarrollo(request.getDesarrollo());
        if (request.getAdaptacion() != null) ficha.setAdaptacion(request.getAdaptacion());
        if (request.getEstadoGeneral() != null) ficha.setEstadoGeneral(request.getEstadoGeneral());
        
        ficha.setActivo(true);
        PsicologiaEducativa saved = repository.save(ficha);
        log.info("Ficha guardada exitosamente ID: {}", saved.getId());
        return convertirADTO(saved);
    }

    public void eliminarFichaPsicologiaEducativa(Integer id) {
        log.info("Eliminando (desactivando) ficha psicología educativa ID: {}", id);
        if (id != null) {
            repository.findById(id).ifPresent(f -> {
                f.setActivo(false);
                repository.save(f);
                log.info("Ficha ID {} desactivada", id);
            });
        }
    }

    private PsicologiaEducativaDTO convertirADTO(PsicologiaEducativa ficha) {
        PsicologiaEducativaDTO dto = new PsicologiaEducativaDTO();
        dto.setId(ficha.getId());
        dto.setPaciente(pacienteService.convertirADTO(ficha.getPaciente()));
        dto.setActivo(ficha.getActivo());
        
        dto.setHistoriaEscolar(ficha.getHistoriaEscolar());
        dto.setDesarrollo(ficha.getDesarrollo());
        dto.setAdaptacion(ficha.getAdaptacion());
        dto.setEstadoGeneral(ficha.getEstadoGeneral());
        
        return dto;
    }
}