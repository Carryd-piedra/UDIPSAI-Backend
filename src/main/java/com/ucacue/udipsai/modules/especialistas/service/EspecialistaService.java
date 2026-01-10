package com.ucacue.udipsai.modules.especialistas.service;

import com.ucacue.udipsai.modules.especialistas.domain.Especialista;
import com.ucacue.udipsai.modules.especialistas.dto.EspecialidadDTO;
import com.ucacue.udipsai.modules.especialistas.dto.EspecialistaCriteriaDTO;
import com.ucacue.udipsai.modules.especialistas.dto.EspecialistaDTO;
import com.ucacue.udipsai.modules.especialistas.dto.EspecialistaRequest;
import com.ucacue.udipsai.modules.especialistas.repository.EspecialidadRepositorio;
import com.ucacue.udipsai.modules.especialistas.repository.EspecialistaRepositorio;
import com.ucacue.udipsai.modules.sedes.dto.SedeDTO;
import com.ucacue.udipsai.modules.sedes.repository.SedeRepositorio;
import com.ucacue.udipsai.infrastructure.storage.StorageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class EspecialistaService {

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private EspecialistaRepositorio especialistaRepositorio;

    @Autowired
    private EspecialidadRepositorio especialidadRepositorio;

    @Autowired
    private SedeRepositorio sedeRepositorio;

    @Autowired
    private StorageService storageService;

    @Transactional(readOnly = true)
    public Page<EspecialistaDTO> listarEspecialistasActivos(Pageable pageable) {
        log.info("Consultando todos los especialistas activos paginados");
        return especialistaRepositorio.findByActivoTrue(pageable)
                .map(this::convertirADTO);
    }

    @Transactional(readOnly = true)
    public EspecialistaDTO obtenerEspecialistaPorId(Integer id) {
        log.debug("Buscando especialista ID: {}", id);
        if (id == null)
            return null;
        return especialistaRepositorio.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> {
                    log.warn("Especialista ID {} no encontrado", id);
                    return new RuntimeException("Especialista no encontrado");
                });
    }

    @Transactional(readOnly = true)
    public Page<EspecialistaDTO> filtrarEspecialistas(EspecialistaCriteriaDTO criteria, Pageable pageable) {
        log.info("Filtrando especialistas con criterios: {}", criteria);
        Specification<Especialista> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getSearch())) {
                String likePattern = "%" + criteria.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nombresApellidos")), likePattern),
                        cb.like(cb.lower(root.get("cedula")), likePattern)));
            }

            if (criteria.getEspecialidadId() != null) {
                Join<Object, Object> espJoin = root.join("especialidad");
                predicates.add(cb.equal(espJoin.get("id"), criteria.getEspecialidadId()));
            }

            if (criteria.getSedeId() != null) {
                Join<Object, Object> sedeJoin = root.join("sede");
                predicates.add(cb.equal(sedeJoin.get("id"), criteria.getSedeId()));
            }

            if (criteria.getActivo() != null) {
                predicates.add(cb.equal(root.get("activo"), criteria.getActivo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return especialistaRepositorio.findAll(spec, pageable).map(this::convertirADTO);
    }

    @Transactional
    public EspecialistaDTO crearEspecialista(EspecialistaRequest request, MultipartFile foto) {
        log.info("Iniciando creación de especialista. Cédula: {}", request.getCedula());
        if (especialistaRepositorio.existsByCedula(request.getCedula())) {
            log.error("Ya existe un especialista con la cédula: {}", request.getCedula());
            throw new RuntimeException("Ya existe un especialista con la cédula: " + request.getCedula());
        }

        Especialista especialista = new Especialista();
        mapearRequestAEntidad(request, especialista);
        especialista.setActivo(true);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            especialista.setFotoUrl(filename);
            log.info("Foto guardada para especialista ID: {}", especialista.getId());
        }

        if (request.getContrasenia() == null || request.getContrasenia().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }

        Especialista saved = especialistaRepositorio.save(especialista);
        log.info("Especialista creado exitosamente ID: {}", saved.getId());
        return convertirADTO(saved);
    }

    @Transactional
    public EspecialistaDTO actualizarEspecialista(Integer id, EspecialistaRequest request, MultipartFile foto) {
        log.info("Iniciando actualización de especialista ID: {}", id);
        if (id == null)
            throw new IllegalArgumentException("ID requerido para actualizar");
        Especialista especialista = especialistaRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar: Especialista no encontrado ID: {}", id);
                    return new RuntimeException("Especialista no encontrado");
                });

        mapearRequestAEntidad(request, especialista);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            especialista.setFotoUrl(filename);
            log.info("Foto guardada para especialista ID: {}", id);
        }

        Especialista saved = especialistaRepositorio.save(especialista);
        log.info("Especialista actualizado exitosamente ID: {}", saved.getId());

        return convertirADTO(saved);
    }

    public void eliminarEspecialista(Integer id) {
        if (id == null)
            return;
        especialistaRepositorio.findById(id).ifPresentOrElse(e -> {
            log.info("Desactivando especialista ID: {}", id);
            e.setActivo(false);
            especialistaRepositorio.save(e);
        }, () -> log.warn("Intento de eliminar especialista inexistente ID: {}", id));
    }

    private void mapearRequestAEntidad(EspecialistaRequest request, Especialista especialista) {
        especialista.setCedula(request.getCedula());
        especialista.setNombresApellidos(request.getNombresApellidos());
        if (request.getContrasenia() != null && !request.getContrasenia().isEmpty()) {
            especialista.setContrasenia(passwordEncoder.encode(request.getContrasenia()));
        }

        if (request.getEspecialidadId() != null) {
            especialista.setEspecialidad(especialidadRepositorio.findById(request.getEspecialidadId()).orElse(null));
        }

        if (request.getSedeId() != null) {
            especialista.setSede(sedeRepositorio.findById(request.getSedeId()).orElse(null));
        }

        if (request.getActivo() != null) {
            especialista.setActivo(request.getActivo());
        }
    }

    public EspecialistaDTO convertirADTO(Especialista especialista) {
        return EspecialistaDTO.builder()
                .id(especialista.getId())
                .cedula(especialista.getCedula())
                .nombresApellidos(especialista.getNombresApellidos())
                .fotoUrl(especialista.getFotoUrl())
                .especialidad(especialista.getEspecialidad() != null ? new EspecialidadDTO(
                        especialista.getEspecialidad().getId(), especialista.getEspecialidad().getArea(), null) : null)
                .sede(especialista.getSede() != null ? new SedeDTO(
                    especialista.getSede().getId(), especialista.getSede().getNombre()) : null)
                .activo(especialista.getActivo())
                .build();
    }
}
