package com.ucacue.udipsai.modules.seguimiento;

import com.ucacue.udipsai.modules.documentos.Documento;
import com.ucacue.udipsai.modules.documentos.DocumentoIdDTO;
import com.ucacue.udipsai.modules.documentos.DocumentoRepositorio;
import com.ucacue.udipsai.modules.especialistas.Especialista;
import com.ucacue.udipsai.modules.especialistas.EspecialistaRepositorio;
import com.ucacue.udipsai.modules.especialistas.EspecialistaService;
import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.paciente.PacienteRepositorio;
import com.ucacue.udipsai.modules.paciente.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeguimientoService {

    @Autowired
    private SeguimientoRepositorio seguimientoRepository;

    @Autowired
    private EspecialistaRepositorio especialistaRepositorio;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;
    
    @Autowired
    private DocumentoRepositorio documentoRepositorio;

    @Autowired
    private EspecialistaService especialistaService;

    @Autowired
    private PacienteService pacienteService;

    public List<SeguimientoDTO> listarSeguimientosActivos() {
        log.info("Consultando todos los seguimientos activos");
        return seguimientoRepository.findByActivo(true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<SeguimientoDTO> listarSeguimientosPorPacienteId(Integer pacienteId) {
        log.info("Consultando seguimientos para paciente ID: {}", pacienteId);
        return seguimientoRepository.findByPacienteIdAndActivo(pacienteId, true).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public SeguimientoDTO crearSeguimiento(SeguimientoRequest request) {
        log.info("Creando nuevo seguimiento");
        Seguimiento seguimiento = new Seguimiento();
        mapearRequestAEntidad(request, seguimiento);
        seguimiento.setActivo(true);
        Seguimiento saved = seguimientoRepository.save(seguimiento);
        log.info("Seguimiento creado exitosamente ID: {}", saved.getId());
        return convertirADTO(saved);
    }
    
    public SeguimientoDTO actualizarSeguimiento(Integer id, SeguimientoRequest request) {
        log.info("Actualizando seguimiento ID: {}", id);
        if (id == null) throw new IllegalArgumentException("ID requerido para actualizar");
        Seguimiento seguimiento = seguimientoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Seguimiento ID {} no encontrado", id);
                    return new RuntimeException("Seguimiento no encontrado");
                });
        mapearRequestAEntidad(request, seguimiento);
        Seguimiento saved = seguimientoRepository.save(seguimiento);
        log.info("Seguimiento actualizado exitosamente ID: {}", saved.getId());
        return convertirADTO(saved);
    }

    public void eliminarSeguimiento(Integer id) {
        log.info("Eliminando (desactivando) seguimiento ID: {}", id);
        if (id == null) return;
        seguimientoRepository.findById(id).ifPresent(s -> {
            s.setActivo(false);
            seguimientoRepository.save(s);
            log.info("Seguimiento ID {} desactivado", id);
        });
    }

    private void mapearRequestAEntidad(SeguimientoRequest request, Seguimiento seguimiento) {
        if (request.getEspecialistaId() != null) {
            Especialista esp = especialistaRepositorio.findById(request.getEspecialistaId()).orElse(null);
            seguimiento.setEspecialista(esp);
        }
        if (request.getPacienteId() != null) {
            Paciente pac = pacienteRepositorio.findById(request.getPacienteId()).orElse(null);
            seguimiento.setPaciente(pac);
        }
        seguimiento.setFecha(request.getFecha());
        seguimiento.setObservacion(request.getObservacion());
        if (request.getActivo() != null) seguimiento.setActivo(request.getActivo());
        
        if (request.getDocumentoId() != null) {
            Documento doc = documentoRepositorio.findById(request.getDocumentoId()).orElse(null);
            seguimiento.setDocumento(doc);
        }
    }

    public SeguimientoDTO convertirADTO(Seguimiento seguimiento) {
        SeguimientoDTO dto = new SeguimientoDTO();
        dto.setId(seguimiento.getId());
        dto.setFecha(seguimiento.getFecha());
        dto.setObservacion(seguimiento.getObservacion());
        dto.setActivo(seguimiento.getActivo());

        if (seguimiento.getEspecialista() != null) {
            dto.setEspecialista(especialistaService.convertirADTO(seguimiento.getEspecialista()));
        }
        if (seguimiento.getPaciente() != null) {
            dto.setPaciente(pacienteService.convertirADTO(seguimiento.getPaciente()));
        }
        if (seguimiento.getDocumento() != null) {
            dto.setDocumento(new DocumentoIdDTO(seguimiento.getDocumento().getId()));
        }
        return dto;
    }
}
