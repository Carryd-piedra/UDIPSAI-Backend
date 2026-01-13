package com.ucacue.udipsai.modules.citas.service;

import com.ucacue.udipsai.modules.citas.domain.Cita;
import com.ucacue.udipsai.modules.citas.dto.*;
import com.ucacue.udipsai.modules.citas.repository.CitaRepository;
import com.ucacue.udipsai.modules.especialidad.domain.Especialidad;
import com.ucacue.udipsai.modules.especialidad.dto.EspecialidadDTO;
import com.ucacue.udipsai.modules.especialidad.repository.EspecialidadRepository;
import com.ucacue.udipsai.modules.especialistas.dto.EspecialistaDTO;
import com.ucacue.udipsai.modules.especialistas.service.EspecialistaService;
import com.ucacue.udipsai.modules.paciente.domain.Paciente;
import com.ucacue.udipsai.modules.paciente.dto.PacienteDTO;
import com.ucacue.udipsai.modules.paciente.repository.PacienteRepository;
import com.ucacue.udipsai.modules.paciente.service.PacienteService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepo;

    @Autowired
    private PacienteRepository pacienteRepo;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private EspecialistaService especialistaService;

    @Autowired
    private EspecialidadRepository especialidadRepo;

    private static final Logger logger = LoggerFactory.getLogger(CitaService.class);

    // Mapear de una Cita a DTO.
    public CitaDTO mapearDTO(Cita cita, PacienteDTO paciente, EspecialistaDTO especialista,
            EspecialidadDTO especialidad) {
        return new CitaDTO(
                cita.getId(),
                cita.getFecha(),
                cita.getHoraInicio(),
                cita.getHoraFin(),
                cita.getEstado().toString(),
                paciente,
                especialista,
                especialidad);
    }

    // Método auxiliar para construir DTO completo desde entidad
    private CitaDTO construirCitaDTO(Cita cita) {
        Paciente pacienteEncontrado = pacienteRepo.findById(cita.getIdPaciente())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Paciente con id " + cita.getIdPaciente() + " asignado a la cita no fue encontrado"));

        PacienteDTO paciente = pacienteService.convertirADTO(pacienteEncontrado);
        EspecialistaDTO especialista = especialistaService.obtenerEspecialistaPorId(cita.getIdProfesional());

        Especialidad especialidadEntity = cita.getEspecialidad();
        EspecialidadDTO especialidad = new EspecialidadDTO(
                especialidadEntity.getId(),
                especialidadEntity.getArea(),
                null);

        return mapearDTO(cita, paciente, especialista, especialidad);
    }

    // Filtrar Citas (Unified Method)
    public Page<CitaDTO> filtrarCitas(CitaCriteriaDTO criteria, Pageable pageable) {
        logger.info("Filtrando citas con criterios: {}", criteria);

        Specification<Cita> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getSearch())) {
                String searchPattern = "%" + criteria.getSearch() + "%";
                predicates.add(cb.like(root.get("idPaciente").as(String.class), searchPattern));
            }

            if (criteria.getId() != null) {
                predicates.add(cb.equal(root.get("id"), criteria.getId()));
            }

            if (criteria.getIdPaciente() != null) {
                predicates.add(cb.equal(root.get("idPaciente"), criteria.getIdPaciente()));
            }

            if (criteria.getIdProfesional() != null) {
                predicates.add(cb.equal(root.get("idProfesional"), criteria.getIdProfesional()));
            }

            if (criteria.getIdEspecialidad() != null) {
                predicates.add(cb.equal(root.get("especialidad").get("id"), criteria.getIdEspecialidad()));
            }

            if (criteria.getEspecialidades() != null && !criteria.getEspecialidades().isEmpty()) {
                predicates.add(root.get("especialidad").get("id").in(criteria.getEspecialidades()));
            }

            if (criteria.getFecha() != null) {
                predicates.add(cb.equal(root.get("fecha"), criteria.getFecha()));
            }

            if (criteria.getEstado() != null) {
                predicates.add(cb.equal(root.get("estado"), criteria.getEstado()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return citaRepo.findAll(spec, pageable).map(this::construirCitaDTO);
    }

    // Obtener una Cita por Id.
    public CitaDTO obtenerCitaPorId(Integer id) {
        logger.info("Obteniendo cita con id {}", id);
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita con id " + id + " no encontrada"));

        return construirCitaDTO(cita);
    }

    // Registrar una Cita.
    public CitaDTO registrarCita(RegistrarCitaDTO dto) {
        logger.info("Registrando una Cita");

        if (dto.getIdPaciente() == null || dto.getIdProfesional() == null || dto.getIdEspecialidad() == null
                || dto.getFecha() == null || dto.getHora() == null) {
            throw new IllegalArgumentException("Faltan datos para el registro de la cita");
        }

        Paciente pacienteEncontrado = pacienteRepo.findById(dto.getIdPaciente())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Paciente con id " + dto.getIdPaciente() + " no encontrado"));

        Especialidad especialidadEntity = especialidadRepo.findById(dto.getIdEspecialidad())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Especialidad con id " + dto.getIdEspecialidad() + " no encontrada"));

        EspecialistaDTO especialista = especialistaService.obtenerEspecialistaPorId(dto.getIdProfesional());
        if (especialista == null) {
            throw new EntityNotFoundException(
                    "Especialista con id " + dto.getIdProfesional() + " no encontrado");
        }

        if (citaRepo.existsByEstadoAndFechaAndHoraInicioAndIdPaciente(Cita.Estado.PENDIENTE, dto.getFecha(),
                dto.getHora(),
                dto.getIdPaciente())) {
            throw new IllegalArgumentException(
                    "Paciente " + pacienteEncontrado.getNombresApellidos()
                            + " ya tiene una cita asignada en la fecha "
                            + dto.getFecha().toString() + " y hora " + dto.getHora().toString());
        }

        if (citaRepo.existsByEstadoAndFechaAndHoraInicioAndIdProfesional(Cita.Estado.PENDIENTE, dto.getFecha(),
                dto.getHora(),
                dto.getIdProfesional())) {
            throw new IllegalArgumentException("Especialista " + especialista.getNombresApellidos()
                    + " ya tiene una cita asignada en la fecha "
                    + dto.getFecha().toString() + " y hora " + dto.getHora().toString());
        }

        Cita cita = new Cita();
        cita.setFecha(dto.getFecha());
        cita.setHoraInicio(dto.getHora());
        cita.setHoraFin(dto.getHora().plusMinutes(60));
        cita.setEstado(Cita.Estado.PENDIENTE);
        cita.setIdPaciente(dto.getIdPaciente());
        cita.setIdProfesional(dto.getIdProfesional());
        cita.setEspecialidad(especialidadEntity);

        Cita citaGuardada = citaRepo.save(cita);
        return construirCitaDTO(citaGuardada);
    }

    // Reagendar una Cita.
    public CitaDTO reagendarCita(Integer id, RegistrarCitaDTO dto) {
        logger.info("Reagendando una Cita");

        Cita citaEncontrada = citaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (citaEncontrada.getEstado() != Cita.Estado.FALTA_JUSTIFICADA
                && citaEncontrada.getEstado() != Cita.Estado.FALTA_INJUSTIFICADA) {
            throw new IllegalArgumentException(
                    "La cita no se puede reagendar porque se encuentra en estado pendiente, ha finalizado o fue cancelada");
        }

        if (dto.getIdPaciente() == null || dto.getIdProfesional() == null || dto.getIdEspecialidad() == null
                || dto.getFecha() == null || dto.getHora() == null) {
            throw new IllegalArgumentException("Faltan datos para el reagendamiento de la cita");
        }

        if (!citaEncontrada.getIdPaciente().equals(dto.getIdPaciente())) {
            pacienteRepo.findById(dto.getIdPaciente())
                    .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        }

        Especialidad especialidadEntity = especialidadRepo.findById(dto.getIdEspecialidad())
                .orElseThrow(() -> new EntityNotFoundException("Especialidad no encontrada"));

        if (citaRepo.existsByEstadoAndFechaAndHoraInicioAndIdPaciente(Cita.Estado.PENDIENTE, dto.getFecha(),
                dto.getHora(), dto.getIdPaciente())) {
            throw new IllegalArgumentException("El paciente ya tiene una cita en ese horario");
        }

        if (citaRepo.existsByEstadoAndFechaAndHoraInicioAndIdProfesional(Cita.Estado.PENDIENTE, dto.getFecha(),
                dto.getHora(), dto.getIdProfesional())) {
            throw new IllegalArgumentException("El especialista ya tiene una cita en ese horario");
        }

        citaEncontrada.setFecha(dto.getFecha());
        citaEncontrada.setHoraInicio(dto.getHora());
        citaEncontrada.setHoraFin(dto.getHora().plusMinutes(60));
        citaEncontrada.setEstado(Cita.Estado.PENDIENTE);
        citaEncontrada.setIdPaciente(dto.getIdPaciente());
        citaEncontrada.setIdProfesional(dto.getIdProfesional());
        citaEncontrada.setEspecialidad(especialidadEntity);

        Cita citaGuardada = citaRepo.save(citaEncontrada);
        return construirCitaDTO(citaGuardada);
    }

    // Encontrar horas libres
    public List<String> encontrarHorasLibresProfesional(Integer idProfesional, LocalDate fecha) {
        logger.info("Encontrando horas libres de Profesional {} en fecha {}", idProfesional, fecha);

        if (especialistaService.obtenerEspecialistaPorId(idProfesional) == null) {
            throw new EntityNotFoundException("Profesional no encontrado");
        }

        List<LocalTime> horasOcupadas = citaRepo.findHorasOcupadasByProfesionalAndFecha(idProfesional, fecha);
        List<String> horasLibres = new ArrayList<>();

        LocalTime horaInicio = LocalTime.of(8, 0);
        LocalTime horaReceso = LocalTime.of(12, 0);
        LocalTime horaFin = LocalTime.of(17, 0);

        while (horaInicio.isBefore(horaFin)) {
            if (!horasOcupadas.contains(horaInicio) && !horaInicio.equals(horaReceso)) {
                horasLibres.add(horaInicio.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            horaInicio = horaInicio.plusHours(1);
        }
        return horasLibres;
    }

    // Finalizar Cita
    public void finalizarCita(Integer id) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (cita.getEstado() != Cita.Estado.PENDIENTE) {
            throw new IllegalArgumentException("La cita no puede finalizarse (estado incorrecto)");
        }
        cita.setEstado(Cita.Estado.FINALIZADA);
        citaRepo.save(cita);
    }

    // Cancelar Cita
    public void cancelarCita(Integer id) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (cita.getEstado() != Cita.Estado.PENDIENTE) {
            throw new IllegalArgumentException("La cita no puede cancelarse (estado incorrecto)");
        }
        cita.setEstado(Cita.Estado.CANCELADA);
        citaRepo.save(cita);
    }

    // Falta Justificada
    public void faltaJustificada(Integer id) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (cita.getEstado() != Cita.Estado.PENDIENTE && cita.getEstado() != Cita.Estado.FALTA_INJUSTIFICADA
                && cita.getEstado() != Cita.Estado.FALTA_JUSTIFICADA) {
            throw new IllegalArgumentException("Estado incorrecto para cambiar a Falta Justificada");
        }
        cita.setEstado(Cita.Estado.FALTA_JUSTIFICADA);
        citaRepo.save(cita);
    }

    // Falta Injustificada
    public void faltaInjustificada(Integer id) {
        Cita cita = citaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada"));

        if (cita.getEstado() != Cita.Estado.PENDIENTE && cita.getEstado() != Cita.Estado.FALTA_INJUSTIFICADA
                && cita.getEstado() != Cita.Estado.FALTA_JUSTIFICADA) {
            throw new IllegalArgumentException("Estado incorrecto para cambiar a Falta Injustificada");
        }
        cita.setEstado(Cita.Estado.FALTA_INJUSTIFICADA);
        citaRepo.save(cita);
    }

    // Generar Reporte
    public ReporteCitaRespuestaDTO generarReportePorPaciente(Integer idPaciente) {
        Pageable pageable = PageRequest.of(0, 15, Sort.by("fecha").descending());
        // Using standard repo instead of vista repo
        Page<Cita> paginaCitas = citaRepo.findAllByIdPaciente(idPaciente, pageable);
        List<Cita> listaCitas = paginaCitas.getContent();

        ReporteCitaRespuestaDTO respuesta = new ReporteCitaRespuestaDTO();

        String nombrePaciente = "Desconocido";
        if (idPaciente != null) {
            nombrePaciente = pacienteRepo.findById(idPaciente)
                    .map(Paciente::getNombresApellidos)
                    .orElse("Desconocido");
        }
        respuesta.setPacienteNombreCompleto(nombrePaciente);

        if (listaCitas.isEmpty()) {
            respuesta.setCitas(List.of());
            return respuesta;
        }

        List<ReporteCitaDTO> citasDTO = listaCitas.stream().map(cita -> {
            String nombreProfesional = "Desconocido";
            if (cita.getIdProfesional() != null) {
                EspecialistaDTO esp = especialistaService.obtenerEspecialistaPorId(cita.getIdProfesional());
                if (esp != null)
                    nombreProfesional = esp.getNombresApellidos();
            }

            return new ReporteCitaDTO(
                    cita.getFecha(),
                    cita.getHoraInicio(),
                    nombreProfesional,
                    cita.getEspecialidad());
        }).collect(Collectors.toList());

        Collections.reverse(citasDTO);
        respuesta.setCitas(citasDTO);

        return respuesta;
    }
}
