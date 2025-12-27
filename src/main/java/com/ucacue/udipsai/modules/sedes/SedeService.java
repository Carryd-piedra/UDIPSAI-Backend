package com.ucacue.udipsai.modules.sedes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SedeService {

    @Autowired
    private SedeRepositorio sedeRepositorio;

    public List<Sede> listarSedesActivas() {
        log.info("Consultando listas de sedes activas");
        return sedeRepositorio.findByActivoTrue();
    }

    public Optional<Sede> obtenerSedePorId(Integer id) {
        log.debug("Buscando sede con ID: {}", id);
        return sedeRepositorio.findById(id);
    }

    @Transactional
    public Sede crearSede(Sede sede) {
        log.info("Creando nueva sede con nombre: {}", sede.getNombre());
        sede.setNombre(sede.getNombre());
        sede.setActivo(true);
        return sedeRepositorio.save(sede);
    }

    @Transactional
    public Sede actualizarSede(Integer id, Sede nuevaSede) {
        return sedeRepositorio.findById(id).map(sedeExistente -> {
            log.info("Actualizando sede ID: {}. Nuevo nombre: {}", id, nuevaSede.getNombre());
            sedeExistente.setNombre(nuevaSede.getNombre());
            return sedeRepositorio.save(sedeExistente);
        }).orElseThrow(() -> {
            log.warn("Intento fallido de actualizar: Sede ID {} no encontrada", id);
            return new RuntimeException("Sede no encontrada");
        });
    }

    @Transactional
    public void eliminarSede(Integer id) {
        sedeRepositorio.findById(id).ifPresentOrElse(
                sede -> {
                    log.info("Desactivando sede ID: {}", id);
                    sede.setActivo(false);
                    sedeRepositorio.save(sede);
                },
                () -> log.warn("Intento de eliminar sede inexistente ID: {}", id)
        );
    }
}
