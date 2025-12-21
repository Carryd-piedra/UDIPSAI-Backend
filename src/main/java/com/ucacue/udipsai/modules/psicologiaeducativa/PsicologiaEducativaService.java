package com.ucacue.udipsai.modules.psicologiaeducativa;

import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.paciente.PacienteRepositorio;
import com.ucacue.udipsai.modules.paciente.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PsicologiaEducativaService {

    @Autowired
    private PsicologiaEducativaRepository repository;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;
    
    @Autowired
    private PacienteService pacienteService;

    public List<PsicologiaEducativaDTO> getAll() {
        return repository.findAll().stream()
                .filter(PsicologiaEducativa::getActivo)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PsicologiaEducativaDTO getByPacienteId(Integer pacienteId) {
        PsicologiaEducativa ficha = repository.findByPacienteIdAndActivo(pacienteId, true);
        if (ficha != null && ficha.getActivo()) {
            return convertToDTO(ficha);
        }
        return null;
    }
    
    public PsicologiaEducativa obtenerPorIdPaciente(Integer pacienteId) {
        // Legacy/Report support
        return repository.findByPacienteIdAndActivo(pacienteId, true);
    }

    @Transactional
    public PsicologiaEducativaDTO createUpdate(PsicologiaEducativaRequest request) {
        if (request.getPacienteId() == null) {
            throw new IllegalArgumentException("El ID del paciente es requerido");
        }
            PsicologiaEducativa ficha = repository.findByPacienteIdAndActivo(request.getPacienteId(), true);
            
            if (ficha == null ) {
                ficha = new PsicologiaEducativa();
            Paciente paciente = pacienteRepositorio.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            ficha.setPaciente(paciente);
        }
        
        if (request.getHistoriaEscolar() != null) ficha.setHistoriaEscolar(request.getHistoriaEscolar());
        if (request.getDesarrollo() != null) ficha.setDesarrollo(request.getDesarrollo());
        if (request.getAdaptacion() != null) ficha.setAdaptacion(request.getAdaptacion());
        if (request.getEstadoGeneral() != null) ficha.setEstadoGeneral(request.getEstadoGeneral());
        
        ficha.setActivo(true);
        return convertToDTO(repository.save(ficha));
    }

    public void delete(Integer id) {
        if (id != null) {
            repository.findById(id).ifPresent(f -> {
                f.setActivo(false);
                repository.save(f);
            });
        }
    }

    private PsicologiaEducativaDTO convertToDTO(PsicologiaEducativa ficha) {
        PsicologiaEducativaDTO dto = new PsicologiaEducativaDTO();
        dto.setId(ficha.getId());
        dto.setPaciente(pacienteService.convertToDTO(ficha.getPaciente()));
        dto.setActivo(ficha.getActivo());
        
        dto.setHistoriaEscolar(ficha.getHistoriaEscolar());
        dto.setDesarrollo(ficha.getDesarrollo());
        dto.setAdaptacion(ficha.getAdaptacion());
        dto.setEstadoGeneral(ficha.getEstadoGeneral());
        
        return dto;
    }
}