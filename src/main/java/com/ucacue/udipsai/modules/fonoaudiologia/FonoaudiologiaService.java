package com.ucacue.udipsai.modules.fonoaudiologia;

import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.paciente.PacienteRepositorio;
import com.ucacue.udipsai.modules.paciente.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FonoaudiologiaService {

    @Autowired
    private FonoaudiologiaRepository repository;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;
    
    @Autowired
    private PacienteService pacienteService;

    public List<FonoaudiologiaDTO> getAll() {
        return repository.findAll().stream()
                .filter(Fonoaudiologia::getActivo)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FonoaudiologiaDTO getByPacienteId(Integer pacienteId) {
        Fonoaudiologia ficha = repository.findByPacienteIdAndActivo(pacienteId, true);
        if (ficha != null && ficha.getActivo()) {
            return convertToDTO(ficha);
        }
        return null;
    }
    
    public Fonoaudiologia obtenerPorIdPaciente(Integer pacienteId) {
        // Legacy/Report support
        return repository.findByPacienteIdAndActivo(pacienteId, true);
    }

    @Transactional
    public FonoaudiologiaDTO createUpdate(FonoaudiologiaRequest request) {
        if (request.getPacienteId() == null) {
            throw new IllegalArgumentException("El ID del paciente es requerido");
        }
        Fonoaudiologia ficha = repository.findByPacienteIdAndActivo(request.getPacienteId(), true);
        
        if (ficha == null) {
            ficha = new Fonoaudiologia();
            Paciente paciente = pacienteRepositorio.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            ficha.setPaciente(paciente);
        }

        if (request.getHabla() != null) ficha.setHabla(request.getHabla());
        if (request.getAudicion() != null) ficha.setAudicion(request.getAudicion());
        if (request.getFonacion() != null) ficha.setFonacion(request.getFonacion());
        if (request.getHistoriaAuditiva() != null) ficha.setHistoriaAuditiva(request.getHistoriaAuditiva());
        if (request.getVestibular() != null) ficha.setVestibular(request.getVestibular());
        if (request.getOtoscopia() != null) ficha.setOtoscopia(request.getOtoscopia());
        
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

    private FonoaudiologiaDTO convertToDTO(Fonoaudiologia ficha) {
        FonoaudiologiaDTO dto = new FonoaudiologiaDTO();
        dto.setId(ficha.getId());
        dto.setPaciente(pacienteService.convertToDTO(ficha.getPaciente()));
        dto.setActivo(ficha.getActivo());
        
        dto.setHabla(ficha.getHabla());
        dto.setAudicion(ficha.getAudicion());
        dto.setFonacion(ficha.getFonacion());
        dto.setHistoriaAuditiva(ficha.getHistoriaAuditiva());
        dto.setVestibular(ficha.getVestibular());
        dto.setOtoscopia(ficha.getOtoscopia());
        
        return dto;
    }
}