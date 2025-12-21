package com.ucacue.udipsai.modules.evaluacion;

import com.ucacue.udipsai.modules.documentos.Documento;
import com.ucacue.udipsai.modules.documentos.DocumentoDTO;
import com.ucacue.udipsai.modules.documentos.DocumentoRepositorio;
import com.ucacue.udipsai.modules.especialistas.Especialista;
import com.ucacue.udipsai.modules.especialistas.EspecialistaRepositorio;
import com.ucacue.udipsai.modules.especialistas.EspecialistaService;
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
public class EvaluacionService {

    @Autowired
    private EvaluacionRepositorio evaluacionRepositorio;

    @Autowired
    private DocumentoRepositorio documentoRepositorio;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private EspecialistaRepositorio especialistaRepositorio;
    
    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private EspecialistaService especialistaService;

    @Autowired
    private StorageService storageService;

    public List<EvaluacionDTO> getAllEvaluaciones() {
        return evaluacionRepositorio.findByActivoTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EvaluacionDTO> getEvaluacionesByPaciente(Integer pacienteId) {
        return evaluacionRepositorio.findByPacienteIdAndActivoTrue(pacienteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EvaluacionDTO createEvaluacion(EvaluacionRequest request, MultipartFile file) {
        if (request.getPacienteId() == null) throw new IllegalArgumentException("El ID del paciente es requerido");
        if (request.getEspecialistaId() == null) throw new IllegalArgumentException("El ID del especialista es requerido");

        Paciente paciente = pacienteRepositorio.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Especialista especialista = especialistaRepositorio.findById(request.getEspecialistaId())
                .orElseThrow(() -> new RuntimeException("Especialista no encontrado"));

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setPaciente(paciente);
        evaluacion.setEspecialista(especialista);
        evaluacion.setNombreArchivo(request.getNombreArchivo());
        evaluacion.setFecha(request.getFecha());
        evaluacion.setActivo(true);

        if (file != null && !file.isEmpty()) {
            String filename = storageService.store(file);
            Documento documento = new Documento();
            documento.setUrl(filename);
            documento.setPaciente(paciente);
            documento.setActivo(true);
            documento = documentoRepositorio.save(documento);
            evaluacion.setDocumento(documento);
        }

        return convertToDTO(evaluacionRepositorio.save(evaluacion));
    }

    public Resource loadFileAsResource(Long id) {
        Evaluacion evaluacion = evaluacionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluacion no encontrada"));
        if (evaluacion.getDocumento() != null) {
            return storageService.loadAsResource(evaluacion.getDocumento().getUrl());
        }
        return null;
    }

    public void deleteEvaluacion(Long id) {
        if (id == null) return;
        evaluacionRepositorio.findById(id).ifPresent(e -> {
            e.setActivo(false);
            evaluacionRepositorio.save(e);
        });
    }

    private EvaluacionDTO convertToDTO(Evaluacion evaluacion) {
        EvaluacionDTO dto = new EvaluacionDTO();
        dto.setId(evaluacion.getId());
        dto.setPaciente(pacienteService.convertToDTO(evaluacion.getPaciente()));
        dto.setEspecialista(especialistaService.convertToDTO(evaluacion.getEspecialista()));
        dto.setNombreArchivo(evaluacion.getNombreArchivo());
        dto.setFecha(evaluacion.getFecha());
        dto.setActivo(evaluacion.getActivo());
        
        if (evaluacion.getDocumento() != null) {
            dto.setDocumento(new DocumentoDTO(evaluacion.getDocumento().getId(), evaluacion.getDocumento().getUrl()));
            dto.setFileUrl(evaluacion.getDocumento().getUrl());
        }
        return dto;
    }
}
