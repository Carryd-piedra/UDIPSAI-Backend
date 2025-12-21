package com.ucacue.udipsai.modules.instituciones;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstitucionEducativaService {

    private final InstitucionEducativaRepositorio institucionEducativaRepositorio;

    public InstitucionEducativaService(InstitucionEducativaRepositorio institucionEducativaRepositorio) {
        this.institucionEducativaRepositorio = institucionEducativaRepositorio;
    }

    public List<InstitucionEducativa> listarInstitucionesActivas() {
        return institucionEducativaRepositorio.findByActivoTrue();
    }

    public Optional<InstitucionEducativa> obtenerInstitucionPorId(Integer id) {
        if (id == null) return Optional.empty();
        return institucionEducativaRepositorio.findById(id);
    }

    public InstitucionEducativa guardarInstitucion(InstitucionEducativa institucionEducativa) {
        return institucionEducativaRepositorio.save(institucionEducativa);
    }

    public void cambiarEstadoInstitucion(Integer id) {
        if (id == null) return;
        Optional<InstitucionEducativa> institucionOpt = institucionEducativaRepositorio.findById(id);
        if (institucionOpt.isPresent()) {
            InstitucionEducativa institucion = institucionOpt.get();
            institucion.setActivo(false);
            institucionEducativaRepositorio.save(institucion);
        }
    }

    public InstitucionEducativa actualizarInstitucion(Integer id, InstitucionEducativa nuevaInstitucion) {
        if (id == null) throw new IllegalArgumentException("ID requerido");
        Optional<InstitucionEducativa> institucionOpt = institucionEducativaRepositorio.findById(id);
        if (institucionOpt.isPresent()) {
            InstitucionEducativa institucionExistente = institucionOpt.get();
            institucionExistente.setNombre(nuevaInstitucion.getNombre());
            institucionExistente.setDireccion(nuevaInstitucion.getDireccion());
            institucionExistente.setTipo(nuevaInstitucion.getTipo());
            institucionExistente.setActivo(nuevaInstitucion.getActivo());
            return institucionEducativaRepositorio.save(institucionExistente);
        } else {
            throw new RuntimeException("Institución no encontrada");
        }
    }
}
