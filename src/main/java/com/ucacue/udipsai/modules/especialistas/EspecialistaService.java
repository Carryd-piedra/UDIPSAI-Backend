package com.ucacue.udipsai.modules.especialistas;

import com.ucacue.udipsai.modules.sedes.SedeRepositorio;
import com.ucacue.udipsai.modules.storage.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EspecialistaService {

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private EspecialistaRepositorio especialistaRepositorio;

    // ... existing autowired (implicit to be maintained by tool context, but we are adding passwordEncoder)

// To avoid messing up imports or class structure, I will perform multiple small edits if needed, but a single replace is cleaner if I match context.
// Actually, I need to add the import first and the field. Then update mapRequestToEntity.
// Let's do a replace for the imports and class fields first.


    @Autowired
    private EspecialidadRepositorio especialidadRepositorio;

    @Autowired
    private SedeRepositorio sedeRepositorio;

    @Autowired
    private StorageService storageService;

    public List<EspecialistaDTO> getAllEspecialistas() {
        return especialistaRepositorio.findByActivoTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EspecialistaDTO getEspecialistaById(Integer id) {
        if (id == null) return null;
        return especialistaRepositorio.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public EspecialistaDTO createEspecialista(EspecialistaRequest request, MultipartFile foto) {
        if (especialistaRepositorio.existsByCedula(request.getCedula())) {
            throw new RuntimeException("Especialista con cédula " + request.getCedula() + " ya existe");
        }

        Especialista especialista = new Especialista();
        mapRequestToEntity(request, especialista);
        especialista.setActivo(true);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            especialista.setFotoUrl(filename);
        }

        return convertToDTO(especialistaRepositorio.save(especialista));
    }

    @Transactional
    public EspecialistaDTO updateEspecialista(Integer id, EspecialistaRequest request, MultipartFile foto) {
        if (id == null) throw new IllegalArgumentException("ID requerido para actualizar");
        Especialista especialista = especialistaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialista no encontrado"));

        mapRequestToEntity(request, especialista);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            especialista.setFotoUrl(filename);
        }

        return convertToDTO(especialistaRepositorio.save(especialista));
    }

    public void deleteEspecialista(Integer id) {
        if (id == null) return;
        especialistaRepositorio.findById(id).ifPresent(e -> {
            e.setActivo(false);
            especialistaRepositorio.save(e);
        });
    }

    public List<EspecialistaDTO> searchEspecialistas(String search, Integer especialidadId, Integer sedeId) {
        Specification<Especialista> spec = Specification.where(null);

        if (search != null && !search.isEmpty()) {
            String likePattern = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("nombresApellidos")), likePattern),
                    cb.like(cb.lower(root.get("cedula")), likePattern)
            ));
        }

        if (especialidadId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("especialidad").get("id"), especialidadId));
        }

        if (sedeId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("sede").get("id"), sedeId));
        }

        spec = spec.and((root, query, cb) -> cb.equal(root.get("activo"), true));

        return especialistaRepositorio.findAll(spec).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Mappers

    private void mapRequestToEntity(EspecialistaRequest request, Especialista especialista) {
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

    public EspecialistaDTO convertToDTO(Especialista especialista) {
        return EspecialistaDTO.builder()
                .id(especialista.getId())
                .cedula(especialista.getCedula())
                .nombresApellidos(especialista.getNombresApellidos())
                .fotoUrl(especialista.getFotoUrl())
                .especialidad(especialista.getEspecialidad() != null ? 
                        new EspecialidadDTO(especialista.getEspecialidad().getId(), especialista.getEspecialidad().getArea(), null) : null)
                .sede(especialista.getSede())
                .activo(especialista.getActivo())
                .build();
    }
}
