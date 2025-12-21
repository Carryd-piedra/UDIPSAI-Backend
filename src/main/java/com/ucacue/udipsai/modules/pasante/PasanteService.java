package com.ucacue.udipsai.modules.pasante;

import com.ucacue.udipsai.modules.especialistas.EspecialistaRepositorio;
import com.ucacue.udipsai.modules.especialistas.EspecialistaService;
import com.ucacue.udipsai.modules.storage.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
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

    public List<PasanteDTO> getAllPasantes() {
        return pasanteRepositorio.findByActivoTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PasanteDTO getPasanteById(Integer id) {
        if (id == null) return null;
        return pasanteRepositorio.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public PasanteDTO createPasante(PasanteRequest request, MultipartFile foto) {
        if (pasanteRepositorio.existsByCedula(request.getCedula())) {
            throw new RuntimeException("Pasante con cédula " + request.getCedula() + " ya existe");
        }

        Pasante pasante = new Pasante();
        mapRequestToEntity(request, pasante);
        pasante.setActivo(true);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            pasante.setFotoUrl(filename);
        }

        return convertToDTO(pasanteRepositorio.save(pasante));
    }

    @Transactional
    public PasanteDTO updatePasante(Integer id, PasanteRequest request, MultipartFile foto) {
        if (id == null) throw new IllegalArgumentException("ID requerido para actualizar");
        Pasante pasante = pasanteRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Pasante no encontrado"));

        mapRequestToEntity(request, pasante);

        if (foto != null && !foto.isEmpty()) {
            String filename = storageService.store(foto);
            pasante.setFotoUrl(filename);
        }

        return convertToDTO(pasanteRepositorio.save(pasante));
    }

    public void deletePasante(Integer id) {
        if (id == null) return;
        pasanteRepositorio.findById(id).ifPresent(p -> {
            p.setActivo(false);
            pasanteRepositorio.save(p);
        });
    }

    public List<PasanteDTO> searchPasantes(String search, Integer tutorId) {
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Mappers

    private void mapRequestToEntity(PasanteRequest request, Pasante pasante) {
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

    public PasanteDTO convertToDTO(Pasante pasante) {
        return PasanteDTO.builder()
                .id(pasante.getId())
                .cedula(pasante.getCedula())
                .nombresApellidos(pasante.getNombresApellidos())
                .fotoUrl(pasante.getFotoUrl())
                .inicioPasantia(pasante.getInicioPasantia())
                .finPasantia(pasante.getFinPasantia())
                .tutor(pasante.getTutor() != null ? especialistaService.convertToDTO(pasante.getTutor()) : null)
                .activo(pasante.getActivo())
                .build();
    }
}
