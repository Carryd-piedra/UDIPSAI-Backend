package com.ucacue.udipsai.modules.instituciones;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InstitucionEducativaService {

    private final InstitucionEducativaRepositorio institucionEducativaRepositorio;

    public InstitucionEducativaService(InstitucionEducativaRepositorio institucionEducativaRepositorio) {
        this.institucionEducativaRepositorio = institucionEducativaRepositorio;
    }

    public List<InstitucionEducativa> listarInstitucionesActivas() {
        log.info("Consultando todas las instituciones educativas activas");
        return institucionEducativaRepositorio.findByActivoTrue();
    }

    public Optional<InstitucionEducativa> obtenerInstitucionPorId(Integer id) {
        log.info("Consultando institución educativa ID: {}", id);
        if (id == null) return Optional.empty();
        return institucionEducativaRepositorio.findById(id);
    }

    public InstitucionEducativa crearInstitucion(InstitucionEducativa institucionEducativa) {
        if (institucionEducativaRepositorio.existsByNombre(institucionEducativa.getNombre())) {
            log.error("Intento de crear institución duplicada. ID: {}", institucionEducativa.getNombre());
            throw new RuntimeException("Institucion con nombre " + institucionEducativa.getNombre() + " ya existe");
        }
        log.info("Creando nueva institución educativa: {}", institucionEducativa.getNombre());
        institucionEducativa.setActivo(true); 
        return institucionEducativaRepositorio.save(institucionEducativa);
    }

    public InstitucionEducativa actualizarInstitucion(Integer id, InstitucionEducativa nuevaInstitucion) {
        log.info("Actualizando institución educativa ID: {}", id);
        if (id == null) throw new IllegalArgumentException("Id de institucion requerido");
        Optional<InstitucionEducativa> institucionOpt = institucionEducativaRepositorio.findById(id);
        if (institucionOpt.isPresent()) {
            InstitucionEducativa institucionExistente = institucionOpt.get();
            institucionExistente.setNombre(nuevaInstitucion.getNombre());
            institucionExistente.setDireccion(nuevaInstitucion.getDireccion());
            institucionExistente.setTipo(nuevaInstitucion.getTipo());
            if (nuevaInstitucion.getActivo() != null) {
                institucionExistente.setActivo(nuevaInstitucion.getActivo());
            }
            InstitucionEducativa updated = institucionEducativaRepositorio.save(institucionExistente);
            log.info("Institución educativa actualizada exitosamente ID: {}", updated.getId());
            return updated;
        } else {
            log.warn("Intento de actualizar institución inexistente ID: {}", id);
            throw new RuntimeException("Institución no encontrada");
        }
    }

    public void eliminarInstitucion(Integer id) {
        log.info("Eliminando institución educativa ID: {}", id);
        if (id == null) return;
        Optional<InstitucionEducativa> institucionOpt = institucionEducativaRepositorio.findById(id);
        if (institucionOpt.isPresent()) {
            InstitucionEducativa institucion = institucionOpt.get();
            institucion.setActivo(false);
            institucionEducativaRepositorio.save(institucion);
            log.info("Institución educativa desactivada ID: {}", id);
        } else {
            log.warn("Intento de eliminar institución inexistente ID: {}", id);
        }
    }
}
