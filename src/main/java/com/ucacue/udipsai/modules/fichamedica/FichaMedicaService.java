package com.ucacue.udipsai.modules.fichamedica;

import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.paciente.PacienteRepositorio;
import com.ucacue.udipsai.modules.paciente.PacienteService;
import com.ucacue.udipsai.modules.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FichaMedicaService {

    @Autowired
    private FichaMedicaRepository fichaMedicaRepository;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;
    
    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private StorageService storageService;

    public List<FichaMedicaDTO> getAllFichas() {
        return fichaMedicaRepository.findAll().stream()
                .filter(FichaMedica::getActivo)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FichaMedicaDTO getFichaByPacienteId(Integer pacienteId) {
        FichaMedica ficha = fichaMedicaRepository.findByPacienteIdAndActivo(pacienteId, true);
        if (ficha != null && ficha.getActivo()) {
            return convertToDTO(ficha);
        }
        return null;
    }

    public FichaMedica obtenerFichaPorIdPaciente(Integer pacienteId) {
         // Legacy helper for Reports if needed, strictly returns Entity. 
         // Should ideally use DTO, but ReportService might need Entity.
         // Keeping for compatibility with ReportService.
         return fichaMedicaRepository.findByPacienteIdAndActivo(pacienteId, true);
    }

    @Transactional
    public FichaMedicaDTO createUpdateFicha(FichaMedicaRequest request, MultipartFile genogramaFile) {
        if (request.getPacienteId() == null) {
            throw new IllegalArgumentException("El ID del paciente es requerido");
        }
        FichaMedica ficha = fichaMedicaRepository.findByPacienteIdAndActivo(request.getPacienteId(), true);
        
        if (ficha == null) {
            ficha = new FichaMedica();
            Paciente paciente = pacienteRepositorio.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            ficha.setPaciente(paciente);
        }

        // Map components
        if (request.getDatosFamiliares() != null) ficha.setDatosFamiliares(request.getDatosFamiliares());
        if (request.getHistoriaPrenatal() != null) ficha.setHistoriaPrenatal(request.getHistoriaPrenatal());
        if (request.getHistoriaNatal() != null) ficha.setHistoriaNatal(request.getHistoriaNatal());
        if (request.getHistoriaPostnatal() != null) ficha.setHistoriaPostnatal(request.getHistoriaPostnatal());
        if (request.getDesarrolloMotor() != null) ficha.setDesarrolloMotor(request.getDesarrolloMotor());
        if (request.getAlimentacion() != null) ficha.setAlimentacion(request.getAlimentacion());
        if (request.getAntecedentesMedicos() != null) ficha.setAntecedentesMedicos(request.getAntecedentesMedicos());
        
        ficha.setActivo(true);

        if (genogramaFile != null && !genogramaFile.isEmpty()) {
            String filename = storageService.store(genogramaFile);
            ficha.setGenogramaUrl(filename);
        }

        return convertToDTO(fichaMedicaRepository.save(ficha));
    }

    public Resource loadGenogramaAsResource(Integer pacienteId) {
        FichaMedica ficha = fichaMedicaRepository.findByPacienteIdAndActivo(pacienteId, true);
        if (ficha != null && ficha.getGenogramaUrl() != null) {
            return storageService.loadAsResource(ficha.getGenogramaUrl());
        }
        return null;
    }

    public void deleteFicha(Integer id) {
        if (id == null) return;
        fichaMedicaRepository.findById(id).ifPresent(f -> {
            f.setActivo(false);
            fichaMedicaRepository.save(f);
        });
    }

    private FichaMedicaDTO convertToDTO(FichaMedica ficha) {
        FichaMedicaDTO dto = new FichaMedicaDTO();
        dto.setId(ficha.getId());
        dto.setPaciente(pacienteService.convertToDTO(ficha.getPaciente()));
        dto.setActivo(ficha.getActivo());
        dto.setGenogramaUrl(ficha.getGenogramaUrl());
        
        dto.setDatosFamiliares(ficha.getDatosFamiliares());
        dto.setHistoriaPrenatal(ficha.getHistoriaPrenatal());
        dto.setHistoriaNatal(ficha.getHistoriaNatal());
        dto.setHistoriaPostnatal(ficha.getHistoriaPostnatal());
        dto.setDesarrolloMotor(ficha.getDesarrolloMotor());
        dto.setAlimentacion(ficha.getAlimentacion());
        dto.setAntecedentesMedicos(ficha.getAntecedentesMedicos());
        
        return dto;
    }
}
