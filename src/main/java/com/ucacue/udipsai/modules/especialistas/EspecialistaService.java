package com.ucacue.udipsai.modules.especialistas;

import com.ucacue.udipsai.modules.sedes.SedeDTO;
import com.ucacue.udipsai.modules.sedes.SedeRepositorio;
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
    public List<EspecialistaDTO> listarEspecialistasActivos() {
        log.info("Consultando todos los especialistas activos");
        return especialistaRepositorio.findByActivoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
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

    @Transactional
    public List<EspecialistaDTO> buscarEspecialistas(String search, Integer especialidadId, Integer sedeId) {
        log.info("Buscando especialistas. Search: {}, EspID: {}, SedeID: {}", search, especialidadId, sedeId);
        Specification<Especialista> spec = Specification.where(null);

        if (search != null && !search.isEmpty()) {
            String likePattern = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("nombresApellidos")), likePattern),
                    cb.like(cb.lower(root.get("cedula")), likePattern)));
        }

        if (especialidadId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("especialidad").get("id"), especialidadId));
        }

        if (sedeId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("sede").get("id"), sedeId));
        }

        spec = spec.and((root, query, cb) -> cb.equal(root.get("activo"), true));

        return especialistaRepositorio.findAll(spec).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
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
