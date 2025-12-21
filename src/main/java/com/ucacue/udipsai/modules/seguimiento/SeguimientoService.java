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

import java.util.List;
import java.util.stream.Collectors;

@Service
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

    public List<SeguimientoDTO> getAllSeguimientos() {
        return seguimientoRepository.findByActivo(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SeguimientoDTO> getSeguimientosByPacienteId(Integer pacienteId) {
        return seguimientoRepository.findByPacienteIdAndActivo(pacienteId, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SeguimientoDTO createSeguimiento(SeguimientoRequest request) {
        Seguimiento seguimiento = new Seguimiento();
        mapRequestToEntity(request, seguimiento);
        seguimiento.setActivo(true);
        return convertToDTO(seguimientoRepository.save(seguimiento));
    }
    
    public SeguimientoDTO updateSeguimiento(Integer id, SeguimientoRequest request) {
        if (id == null) throw new IllegalArgumentException("ID requerido para actualizar");
        Seguimiento seguimiento = seguimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguimiento no encontrado"));
        mapRequestToEntity(request, seguimiento);
        return convertToDTO(seguimientoRepository.save(seguimiento));
    }

    public void deleteSeguimiento(Integer id) {
        if (id == null) return;
        seguimientoRepository.findById(id).ifPresent(s -> {
            s.setActivo(false);
            seguimientoRepository.save(s);
        });
    }

    private void mapRequestToEntity(SeguimientoRequest request, Seguimiento seguimiento) {
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

    public SeguimientoDTO convertToDTO(Seguimiento seguimiento) {
        SeguimientoDTO dto = new SeguimientoDTO();
        dto.setId(seguimiento.getId());
        dto.setFecha(seguimiento.getFecha());
        dto.setObservacion(seguimiento.getObservacion());
        dto.setActivo(seguimiento.getActivo());

        if (seguimiento.getEspecialista() != null) {
            dto.setEspecialista(especialistaService.convertToDTO(seguimiento.getEspecialista()));
        }
        if (seguimiento.getPaciente() != null) {
            dto.setPaciente(pacienteService.convertToDTO(seguimiento.getPaciente()));
        }
        if (seguimiento.getDocumento() != null) {
            dto.setDocumento(new DocumentoIdDTO(seguimiento.getDocumento().getId()));
        }
        return dto;
    }
}
