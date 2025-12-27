package com.ucacue.udipsai.modules.pasante;

import com.ucacue.udipsai.modules.especialistas.EspecialistaRepositorio;

import com.ucacue.udipsai.modules.especialistas.EspecialistaService;
import com.ucacue.udipsai.modules.storage.StorageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PasanteService {

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private PasanteRepositorio pasanteRepositorio;

    @Autowired
    private EspecialistaRepositorio especialistaRepositorio;
    
    @Autowired
    private EspecialistaService especialistaService;

    @Autowired
    private StorageService storageService;

    @Transactional(readOnly = true)
    public List<PasanteDTO> listarPasantesActivos() {
        log.info("Consultando todos los pasantes activos");

        return pasanteRepositorio.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PasanteDTO obtenerPasantePorId(Integer id) {
        log.info("Consultando pasante por ID: {}", id);

        if (id == null) return null;
        return pasanteRepositorio.findById(id)
                .map(this::convertirADTO)
                .orElse(null);
    }

    @Transactional
    public PasanteDTO crearPasante(PasanteRequest request, MultipartFile foto) {
        log.info("Iniciando creación de pasante: {}", request.getCedula());
        if (pasanteRepositorio.existsByCedula(request.getCedula())) {
            log.error("Intento de crear pasante duplicado. Cédula: {}", request.getCedula());
            throw new RuntimeException("Pasante con cédula " + request.getCedula() + " ya existe");
        }

        Pasante pasante = new Pasante();
        mapearRequestAEntidad(request, pasante);
        pasante.setActivo(true);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            pasante.setFotoUrl(filename);
            log.debug("Foto guardada para pasante: {}", filename);
        }

        Pasante saved = pasanteRepositorio.save(pasante);
        log.info("Pasante creado exitosamente ID: {}", saved.getId());
        return convertirADTO(saved);
    }

    @Transactional
    public PasanteDTO actualizarPasante(Integer id, PasanteRequest request, MultipartFile foto) {
        log.info("Iniciando actualización de pasante ID: {}", id);
        if (id == null) throw new IllegalArgumentException("ID requerido para actualizar");
        Pasante pasante = pasanteRepositorio.findById(id)
                .orElseThrow(() -> {
                    log.error("Pasante ID {} no encontrado para actualización", id);
                    return new RuntimeException("Pasante no encontrado");
                });

        mapearRequestAEntidad(request, pasante);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            pasante.setFotoUrl(filename);
            log.debug("Foto actualizada para pasante ID: {}", id);
        }

        Pasante saved = pasanteRepositorio.save(pasante);
        log.info("Pasante actualizado exitosamente ID: {}", saved.getId());
        return convertirADTO(saved);
    }

    public void eliminarPasante(Integer id) {
        log.info("Iniciando eliminación de pasante ID: {}", id);
        if (id == null) return;
        pasanteRepositorio.findById(id).ifPresent(p -> {
            p.setActivo(false);
            pasanteRepositorio.save(p);
            log.info("Pasante ID {} desactivado", id);
        });
    }

    @Transactional(readOnly = true)
    public List<PasanteDTO> buscarPasantes(String search, Integer tutorId) {
        log.info("Buscando pasantes. Search: {}, TutorId: {}", search, tutorId);

        Specification<Pasante> spec = Specification.where(null);

        if (search != null && !search.isEmpty()) {
            String likePattern = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("nombresApellidos")), likePattern),
                    cb.like(cb.lower(root.get("cedula")), likePattern)
            ));
        }

        if (tutorId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tutor").get("id"), tutorId));
        }

        spec = spec.and((root, query, cb) -> cb.equal(root.get("activo"), true));

        return pasanteRepositorio.findAll(spec).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private void mapearRequestAEntidad(PasanteRequest request, Pasante pasante) {
        pasante.setCedula(request.getCedula());
        pasante.setNombresApellidos(request.getNombresApellidos());
        if (request.getContrasenia() != null && !request.getContrasenia().isEmpty()) {
            pasante.setContrasenia(passwordEncoder.encode(request.getContrasenia()));
        }
        pasante.setInicioPasantia(request.getInicioPasantia());
        pasante.setFinPasantia(request.getFinPasantia());

        if (request.getTutorId() != null) {
            pasante.setTutor(especialistaRepositorio.findById(request.getTutorId()).orElse(null));
        }
        
        if (request.getActivo() != null) {
            pasante.setActivo(request.getActivo());
        }
    }

    public PasanteDTO convertirADTO(Pasante pasante) {
        return PasanteDTO.builder()
                .id(pasante.getId())
                .cedula(pasante.getCedula())
                .nombresApellidos(pasante.getNombresApellidos())
                .fotoUrl(pasante.getFotoUrl())
                .inicioPasantia(pasante.getInicioPasantia())
                .finPasantia(pasante.getFinPasantia())
                .tutor(pasante.getTutor() != null ? especialistaService.convertirADTO(pasante.getTutor()) : null)
                .activo(pasante.getActivo())
                .build();
    }
}
