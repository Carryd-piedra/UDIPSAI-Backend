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
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    public List<EvaluacionDTO> listarEvaluaciones() {
        log.info("Consultando todas las evaluaciones activas");
        return evaluacionRepositorio.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<EvaluacionDTO> listarEvaluacionesPorPacienteId(Integer pacienteId) {
        log.info("Consultando evaluaciones activas para el paciente ID: {}", pacienteId);
        return evaluacionRepositorio.findByPacienteIdAndActivoTrue(pacienteId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EvaluacionDTO crearEvaluacion(EvaluacionRequest request, MultipartFile file) {
        log.info("Iniciando creación de evaluación para Paciente ID: {}", request.getPacienteId());
        if (request.getPacienteId() == null) throw new IllegalArgumentException("El ID del paciente es requerido");
        if (request.getEspecialistaId() == null) throw new IllegalArgumentException("El ID del especialista es requerido");

        Paciente paciente = pacienteRepositorio.findById(request.getPacienteId())
                .orElseThrow(() -> {
                    log.error("Error al crear evaluación: Paciente ID {} no encontrado", request.getPacienteId());
                    return new RuntimeException("Paciente no encontrado");
                });
        Especialista especialista = especialistaRepositorio.findById(request.getEspecialistaId())
                .orElseThrow(() -> {
                    log.error("Error al crear evaluación: Especialista ID {} no encontrado", request.getEspecialistaId());
                    return new RuntimeException("Especialista no encontrado");
                });

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setPaciente(paciente);
        evaluacion.setEspecialista(especialista);
        evaluacion.setNombreArchivo(request.getNombreArchivo());
        evaluacion.setFecha(request.getFecha());
        evaluacion.setActivo(true);

        if (file != null && !file.isEmpty()) {
            String filename = storageService.store(file);
            log.info("Archivo de evaluación almacenado: {}", filename);
            Documento documento = new Documento();
            documento.setUrl(filename);
            documento.setNombre(request.getNombreArchivo() != null ? request.getNombreArchivo() : "Evaluación");
            documento.setPaciente(paciente);
            documento.setActivo(true);

            documento = documentoRepositorio.save(documento);
            evaluacion.setDocumento(documento);
        }

        Evaluacion saved = evaluacionRepositorio.save(evaluacion);
        log.info("Evaluación creada exitosamente ID: {}", saved.getId());
        return convertirADTO(saved);
    }

    public Resource cargarArchivoComoRecurso(Long id) {
        log.info("Solicitando recurso para evaluación ID: {}", id);
        Evaluacion evaluacion = evaluacionRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluación ID {} no encontrada", id);
                    return new RuntimeException("Evaluacion no encontrada");
                });
        if (evaluacion.getDocumento() != null) {
            return storageService.loadAsResource(evaluacion.getDocumento().getUrl());
        }
        log.warn("La evaluación ID {} no tiene documento adjunto", id);
        return null;
    }

    public void eliminarEvaluacion(Long id) {
        if (id == null) return;
        evaluacionRepositorio.findById(id).ifPresentOrElse(e -> {
            log.info("Desactivando evaluación ID: {}", id);
            e.setActivo(false);
            evaluacionRepositorio.save(e);
        }, () -> log.warn("Intento de eliminar evaluación inexistente ID: {}", id));
    }

    private EvaluacionDTO convertirADTO(Evaluacion evaluacion) {
        EvaluacionDTO dto = new EvaluacionDTO();
        dto.setId(evaluacion.getId());
        dto.setPaciente(pacienteService.convertirADTO(evaluacion.getPaciente()));
        dto.setEspecialista(especialistaService.convertirADTO(evaluacion.getEspecialista()));
        dto.setNombreArchivo(evaluacion.getNombreArchivo());
        dto.setFecha(evaluacion.getFecha());
        dto.setActivo(evaluacion.getActivo());
        
        if (evaluacion.getDocumento() != null) {
            dto.setDocumento(new DocumentoDTO(evaluacion.getDocumento().getId(), evaluacion.getDocumento().getUrl(), evaluacion.getDocumento().getNombre()));
        }
        return dto;
    }
}
