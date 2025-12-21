package com.ucacue.udipsai.modules.psicologiaclinica;

import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.paciente.PacienteRepositorio;
import com.ucacue.udipsai.modules.paciente.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PsicologiaClinicaService {

    @Autowired
    private PsicologiaClinicaRepository repository;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;
    
    @Autowired
    private PacienteService pacienteService;

    public List<PsicologiaClinicaDTO> getAll() {
        return repository.findAll().stream()
                .filter(PsicologiaClinica::getActivo)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PsicologiaClinicaDTO getByPacienteId(Integer pacienteId) {
        PsicologiaClinica ficha = repository.findByPacienteIdAndActivo(pacienteId, true);
        if (ficha != null && ficha.getActivo()) {
            return convertToDTO(ficha);
        }
        return null;
    }
    
    public PsicologiaClinica obtenerFichaPorIdPaciente(Integer pacienteId) {
        // Legacy support/Report support
        return repository.findByPacienteIdAndActivo(pacienteId, true);
    }

    @Transactional
    public PsicologiaClinicaDTO createUpdate(PsicologiaClinicaRequest request) {
        if (request.getPacienteId() == null) {
            throw new IllegalArgumentException("El ID del paciente es requerido");
        }
        PsicologiaClinica ficha = repository.findByPacienteIdAndActivo(request.getPacienteId(), true);
        
        if (ficha == null) {
            ficha = new PsicologiaClinica();
            Paciente paciente = pacienteRepositorio.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            ficha.setPaciente(paciente);
        }

        if (request.getAnamnesis() != null) ficha.setAnamnesis(request.getAnamnesis());
        if (request.getSuenio() != null) ficha.setSuenio(request.getSuenio());
        if (request.getConducta() != null) ficha.setConducta(request.getConducta());
        if (request.getSexualidad() != null) ficha.setSexualidad(request.getSexualidad());
        if (request.getEvaluacionLenguaje() != null) ficha.setEvaluacionLenguaje(request.getEvaluacionLenguaje());
        if (request.getEvaluacionAfectiva() != null) ficha.setEvaluacionAfectiva(request.getEvaluacionAfectiva());
        if (request.getEvaluacionCognitiva() != null) ficha.setEvaluacionCognitiva(request.getEvaluacionCognitiva());
        if (request.getEvaluacionPensamiento() != null) ficha.setEvaluacionPensamiento(request.getEvaluacionPensamiento());
        if (request.getDiagnostico() != null) ficha.setDiagnostico(request.getDiagnostico());
        
        ficha.setActivo(true);
        return convertToDTO(repository.save(ficha));
    }

    public void delete(Integer id) {
        if (id == null) return;
        repository.findById(id).ifPresent(f -> {
            f.setActivo(false);
            repository.save(f);
        });
    }

    private PsicologiaClinicaDTO convertToDTO(PsicologiaClinica ficha) {
        PsicologiaClinicaDTO dto = new PsicologiaClinicaDTO();
        dto.setId(ficha.getId());
        dto.setPaciente(pacienteService.convertToDTO(ficha.getPaciente()));
        dto.setActivo(ficha.getActivo());
        
        dto.setAnamnesis(ficha.getAnamnesis());
        dto.setSuenio(ficha.getSuenio());
        dto.setConducta(ficha.getConducta());
        dto.setSexualidad(ficha.getSexualidad());
        dto.setEvaluacionLenguaje(ficha.getEvaluacionLenguaje());
        dto.setEvaluacionAfectiva(ficha.getEvaluacionAfectiva());
        dto.setEvaluacionCognitiva(ficha.getEvaluacionCognitiva());
        dto.setEvaluacionPensamiento(ficha.getEvaluacionPensamiento());
        dto.setDiagnostico(ficha.getDiagnostico());
        
        return dto;
    }
}