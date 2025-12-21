package com.ucacue.udipsai.modules.paciente;

import com.ucacue.udipsai.modules.documentos.Documento;
import com.ucacue.udipsai.modules.documentos.DocumentoDTO;
import com.ucacue.udipsai.modules.documentos.DocumentoIdDTO;
import com.ucacue.udipsai.modules.documentos.DocumentoRepositorio;
import com.ucacue.udipsai.modules.documentos.DocumentoService;
import com.ucacue.udipsai.modules.instituciones.InstitucionEducativa;
import com.ucacue.udipsai.modules.instituciones.InstitucionEducativaRepositorio;
import com.ucacue.udipsai.modules.sedes.Sede;
import com.ucacue.udipsai.modules.sedes.SedeRepositorio;
import com.ucacue.udipsai.modules.storage.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private InstitucionEducativaRepositorio institucionEducativaRepositorio;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private SedeRepositorio sedeRepositorio;

    @Autowired
    private DocumentoRepositorio documentoRepositorio;

    @Autowired
    private StorageService storageService;

    public Optional<Paciente> getPacienteById(Integer id) {
        return pacienteRepositorio.findById(id);
    }

    public PacienteDTO getPacienteDTOById(Integer id) {
        return pacienteRepositorio.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public List<PacienteDTO> getAllPacientes() {
        return pacienteRepositorio.findByActivoTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PacienteDTO createPaciente(PacienteRequest request, MultipartFile foto) {
        Paciente paciente = new Paciente();
        mapRequestToEntity(request, paciente);

        paciente.setFechaApertura(LocalDateTime.now());
        paciente.setActivo(true);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            paciente.setFotoUrl(filename);
        }

        Paciente saved = pacienteRepositorio.save(paciente);
        return convertToDTO(saved);
    }

    @Transactional
    public PacienteDTO updatePaciente(Integer id, PacienteRequest request, MultipartFile foto) {
        Paciente paciente = pacienteRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        mapRequestToEntity(request, paciente);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            paciente.setFotoUrl(filename);
        }

        Paciente saved = pacienteRepositorio.save(paciente);

        return convertToDTO(saved);
    }

    @Transactional
    public void deletePaciente(Integer id) {
        Paciente paciente = pacienteRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        paciente.setActivo(false);
        pacienteRepositorio.save(paciente);
    }

    public List<PacienteDTO> searchPacientes(String search, Integer sedeId) {
        Specification<Paciente> spec = Specification.where(null);

        if (StringUtils.hasText(search)) {
            String likePattern = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("nombresApellidos")), likePattern),
                    cb.like(cb.lower(root.get("cedula")), likePattern)));
        }

        if (sedeId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("sede").get("id"), sedeId));
        }

        spec = spec.and((root, query, cb) -> cb.equal(root.get("activo"), true));

        return pacienteRepositorio.findAll(spec).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void mapRequestToEntity(PacienteRequest request, Paciente paciente) {
        paciente.setNombresApellidos(request.getNombresApellidos());
        paciente.setCiudad(request.getCiudad());
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setCedula(request.getCedula());
        paciente.setDomicilio(request.getDomicilio());
        paciente.setNumeroTelefono(request.getNumeroTelefono());
        paciente.setNumeroCelular(request.getNumeroCelular());
        paciente.setProyecto(request.getProyecto());
        paciente.setJornada(request.getJornada());
        paciente.setNivelEducativo(request.getNivelEducativo());
        paciente.setAnioEducacion(request.getAnioEducacion());
        paciente.setAnioUniversitario(request.getAnioUniversitario());
        paciente.setCarrera(request.getCarrera());
        paciente.setCiclo(request.getCiclo());
        paciente.setPerteneceInclusion(request.getPerteneceInclusion());
        paciente.setTieneDiscapacidad(request.getTieneDiscapacidad());
        paciente.setPortadorCarnet(request.getPortadorCarnet());
        paciente.setPerteneceAProyecto(request.getPerteneceAProyecto());
        paciente.setDiagnostico(request.getDiagnostico());
        paciente.setMotivoConsulta(request.getMotivoConsulta());
        paciente.setObservaciones(request.getObservaciones());
        paciente.setTipoDiscapacidad(request.getTipoDiscapacidad());
        paciente.setDetalleDiscapacidad(request.getDetalleDiscapacidad());
        paciente.setPorcentajeDiscapacidad(request.getPorcentajeDiscapacidad());

        if (request.getInstitucionEducativaId() != null) {
            InstitucionEducativa ie = institucionEducativaRepositorio.findById(request.getInstitucionEducativaId())
                    .orElseThrow(() -> new RuntimeException("Institucion Educativa not found"));
            paciente.setInstitucionEducativa(ie);
        }

        if (request.getSedeId() != null) {
            Sede sede = sedeRepositorio.findById(request.getSedeId())
                    .orElseThrow(() -> new RuntimeException("Sede not found"));
            paciente.setSede(sede);
        }

        if (request.getFichaCompromisoId() != null) {
            Documento doc = documentoRepositorio.findById(request.getFichaCompromisoId()).orElse(null);
            paciente.setFichaCompromiso(doc);
        }
    }

    public PacienteDTO convertToDTO(Paciente paciente) {
        return PacienteDTO.builder()
                .id(paciente.getId())
                .fechaApertura(paciente.getFechaApertura())
                .activo(paciente.getActivo())
                .nombresApellidos(paciente.getNombresApellidos())
                .ciudad(paciente.getCiudad())
                .fechaNacimiento(paciente.getFechaNacimiento())
                .edad(paciente.getEdad())
                .cedula(paciente.getCedula())
                .domicilio(paciente.getDomicilio())
                .fotoUrl(paciente.getFotoUrl())
                .numeroTelefono(paciente.getNumeroTelefono())
                .numeroCelular(paciente.getNumeroCelular())
                .institucionEducativa(paciente.getInstitucionEducativa())
                .sede(paciente.getSede())
                .proyecto(paciente.getProyecto())
                .jornada(paciente.getJornada())
                .nivelEducativo(paciente.getNivelEducativo())
                .anioEducacion(paciente.getAnioEducacion())
                .anioUniversitario(paciente.getAnioUniversitario())
                .ciclo(paciente.getCiclo())
                .carrera(paciente.getCarrera())
                .perteneceInclusion(paciente.getPerteneceInclusion())
                .tieneDiscapacidad(paciente.getTieneDiscapacidad())
                .portadorCarnet(paciente.getPortadorCarnet())
                .perteneceAProyecto(paciente.getPerteneceAProyecto())
                .diagnostico(paciente.getDiagnostico())
                .motivoConsulta(paciente.getMotivoConsulta())
                .observaciones(paciente.getObservaciones())
                .tipoDiscapacidad(paciente.getTipoDiscapacidad())
                .detalleDiscapacidad(paciente.getDetalleDiscapacidad())
                .porcentajeDiscapacidad(paciente.getPorcentajeDiscapacidad())
                .fichaDiagnostica(paciente.getFichaDiagnostica() != null
                        ? new DocumentoIdDTO(paciente.getFichaDiagnostica().getId())
                        : null)
                .fichaCompromiso(paciente.getFichaCompromiso() != null
                        ? new DocumentoIdDTO(paciente.getFichaCompromiso().getId())
                        : null)
                .fichaUnica(
                        paciente.getFichaUnica() != null ? new DocumentoIdDTO(paciente.getFichaUnica().getId()) : null)
                .documentos_paciente(paciente.getDocumentosPaciente())
                .build();
    }

    @Transactional
    public DocumentoDTO addDocumentoToPaciente(Integer pacienteId, MultipartFile file) throws IOException {
        Paciente paciente = pacienteRepositorio.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        String fileUrl = documentoService.guardarArchivoEnDisco(file.getBytes());
        Documento documento = new Documento();
        documento.setUrl(fileUrl);
        documento = documentoRepositorio.save(documento);

        paciente.setFichaDiagnostica(documento);
        pacienteRepositorio.save(paciente);

        return new DocumentoDTO(documento.getId(), documento.getUrl());
    }
}
