package com.ucacue.udipsai.modules.paciente;

import com.ucacue.udipsai.modules.instituciones.InstitucionEducativa;
import com.ucacue.udipsai.modules.instituciones.InstitucionEducativaRepositorio;
import com.ucacue.udipsai.modules.sedes.Sede;
import com.ucacue.udipsai.modules.sedes.SedeRepositorio;
import com.ucacue.udipsai.modules.storage.StorageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import com.ucacue.udipsai.modules.instituciones.InstitucionEducativaDTO;
import com.ucacue.udipsai.modules.sedes.SedeDTO;

import com.ucacue.udipsai.modules.documentos.DocumentoDTO;
import com.ucacue.udipsai.modules.fichamedica.FichaMedicaRepository;
import com.ucacue.udipsai.modules.fonoaudiologia.FonoaudiologiaRepository;
import com.ucacue.udipsai.modules.psicologiaclinica.PsicologiaClinicaRepository;
import com.ucacue.udipsai.modules.psicologiaeducativa.PsicologiaEducativaRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PacienteService {

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private InstitucionEducativaRepositorio institucionEducativaRepositorio;

    @Autowired
    private SedeRepositorio sedeRepositorio;

    @Autowired
    private StorageService storageService;

    @Autowired
    private FichaMedicaRepository fichaMedicaRepository;

    @Autowired
    private FonoaudiologiaRepository fonoaudiologiaRepository;

    @Autowired
    private PsicologiaClinicaRepository psicologiaClinicaRepository;

    @Autowired
    private PsicologiaEducativaRepository psicologiaEducativaRepository;


    @Transactional(readOnly = true)
    public List<PacienteDTO> listarPacientesActivos() {
        log.info("Consultando todos los pacientes activos");
        return pacienteRepositorio.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PacienteDTO obtenerPacientePorId(Integer id) {
        log.info("Consultando paciente por ID: {}", id);
        return pacienteRepositorio.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> {
                    log.error("Error al obtener paciente: Paciente no encontrado ID: {}", id);
                    return new RuntimeException("Paciente no encontrado");
                });
    }

    public List<PacienteDTO> buscarPacientes(String search, Integer sedeId) {
        log.info("Buscando pacientes. Search: {}, SedeId: {}", search, sedeId);
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
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PacienteDTO crearPaciente(PacienteRequest request, MultipartFile foto) {
        log.info("Iniciando creación de paciente: {}", request.getNombresApellidos());
        if (pacienteRepositorio.existsByCedula(request.getCedula())) {
            log.error("Ya existe un paciente con la cédula: {}", request.getCedula());
            throw new RuntimeException("Ya existe un paciente con la cédula: " + request.getCedula());
        }

        Paciente paciente = new Paciente();
        mapearRequestAEntidad(request, paciente);
        paciente.setFechaApertura(LocalDateTime.now());
        paciente.setActivo(true);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            paciente.setFotoUrl(filename);
            log.debug("Foto guardada para paciente ID: {}", paciente.getId());
        }

        Paciente saved = pacienteRepositorio.save(paciente);
        log.info("Paciente creado exitosamente ID: {}", saved.getId());
        return convertirADTO(saved);
    }

    @Transactional
    public PacienteDTO actualizarPaciente(Integer id, PacienteRequest request, MultipartFile foto) {
        log.info("Iniciando actualización de paciente ID: {}", id);
        Paciente paciente = pacienteRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar: Paciente no encontrado ID: {}", id);
                    return new RuntimeException("Paciente no encontrado");
                });

        mapearRequestAEntidad(request, paciente);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            paciente.setFotoUrl(filename);
            log.debug("Foto actualizada para paciente ID: {}", id);
        }

        Paciente saved = pacienteRepositorio.save(paciente);
        log.info("Paciente actualizado exitosamente ID: {}", saved.getId());

        return convertirADTO(saved);
    }

    @Transactional
    public void eliminarPaciente(Integer id) {
        log.info("Iniciando eliminación de paciente ID: {}", id);
        Paciente paciente = pacienteRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de eliminar paciente inexistente ID: {}", id);
                    return new RuntimeException("Paciente no encontrado");
                });
        paciente.setActivo(false);
        pacienteRepositorio.save(paciente);
        log.info("Paciente ID {} desactivado", id);
    }

    private void mapearRequestAEntidad(PacienteRequest request, Paciente paciente) {
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
    }

    public PacienteDTO convertirADTO(Paciente paciente) {
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
                .institucionEducativa(paciente.getInstitucionEducativa() != null ? new InstitucionEducativaDTO(
                    paciente.getInstitucionEducativa().getId(), paciente.getInstitucionEducativa().getNombre()) : null)
                .sede(paciente.getSede() != null ? new SedeDTO(
                    paciente.getSede().getId(), paciente.getSede().getNombre()) : null)
                .proyecto(paciente.getProyecto())
                .jornada(paciente.getJornada())
                .nivelEducativo(paciente.getNivelEducativo())
                .anioEducacion(paciente.getAnioEducacion())
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
                .documentos(paciente.getDocumentos() != null ? 
                        paciente.getDocumentos().stream()
                                .filter(d -> d.getActivo())
                                .map(d -> new DocumentoDTO(d.getId(), d.getUrl(), d.getNombre()))
                                .collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    @Transactional(readOnly = true)
    public PacienteSummaryDTO obtenerResumenFichas(Integer id) {
        log.info("Obteniendo resumen de fichas para paciente ID: {}", id);
        List<String> nombres = new java.util.ArrayList<>();

        if (fichaMedicaRepository.findByPacienteIdAndActivo(id, true) != null) nombres.add("Ficha Médica");
        if (fonoaudiologiaRepository.findByPacienteIdAndActivo(id, true) != null) nombres.add("Fonoaudiología");
        if (psicologiaClinicaRepository.findByPacienteIdAndActivo(id, true) != null) nombres.add("Psicología Clínica");
        if (psicologiaEducativaRepository.findByPacienteIdAndActivo(id, true) != null) nombres.add("Psicología Educativa");

        return PacienteSummaryDTO.builder()
                .totalFichas(nombres.size())
                .nombresFichas(nombres)
                .build();
    }
}


