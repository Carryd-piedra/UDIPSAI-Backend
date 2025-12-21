package com.ucacue.udipsai.modules.asignacion;

import com.ucacue.udipsai.modules.paciente.Paciente;
import com.ucacue.udipsai.modules.paciente.PacienteRepositorio;
import com.ucacue.udipsai.modules.paciente.PacienteService;
import com.ucacue.udipsai.modules.pasante.Pasante;
import com.ucacue.udipsai.modules.pasante.PasanteRepositorio;
import com.ucacue.udipsai.modules.pasante.PasanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
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

    public List<AsignacionDTO> getAllAsignaciones() {
        return asignacionRepositorio.findByActivoTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AsignacionDTO createAsignacion(AsignacionRequest request) {
        if (request.getPacienteId() == null) throw new IllegalArgumentException("Paciente ID requerido");
        if (request.getPasanteId() == null) throw new IllegalArgumentException("Pasante ID requerido");

        Paciente paciente = pacienteRepositorio.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Pasante pasante = pasanteRepositorio.findById(request.getPasanteId())
                .orElseThrow(() -> new RuntimeException("Pasante no encontrado"));

        Asignacion asignacion = new Asignacion();
        asignacion.setPaciente(paciente);
        asignacion.setPasante(pasante);
        asignacion.setActivo(true);

        return convertToDTO(asignacionRepositorio.save(asignacion));
    }
    
    public List<AsignacionDTO> getAsignacionesByPasante(Integer pasanteId) {
        return asignacionRepositorio.findByPasanteIdAndActivoTrue(pasanteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteAsignacion(Long id) {
        if (id == null) return;
        asignacionRepositorio.findById(id).ifPresent(a -> {
            a.setActivo(false);
            asignacionRepositorio.save(a);
        });
    }

    public AsignacionDTO convertToDTO(Asignacion asignacion) {
        return AsignacionDTO.builder()
                .id(asignacion.getId())
                .paciente(pacienteService.convertToDTO(asignacion.getPaciente()))
                .pasante(pasanteService.convertToDTO(asignacion.getPasante()))
                .activo(asignacion.getActivo())
                .build();
    }
}
