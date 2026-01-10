package com.ucacue.udipsai.modules.sedes.service;

import com.ucacue.udipsai.modules.sedes.domain.Sede;
import com.ucacue.udipsai.modules.sedes.dto.SedeCriteriaDTO;
import com.ucacue.udipsai.modules.sedes.repository.SedeRepositorio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class SedeService {

    @Autowired
    private SedeRepositorio sedeRepositorio;

    public Page<Sede> listarSedesActivas(Pageable pageable) {
        log.info("Consultando sedes activas paginadas");
        return sedeRepositorio.findByActivoTrue(pageable);
    }
    
    public List<Sede> listarSedesActivas() {
        log.info("Consultando listas de sedes activas");
        return sedeRepositorio.findByActivoTrue();
    }

    public Optional<Sede> obtenerSedePorId(Integer id) {
        log.debug("Buscando sede con ID: {}", id);
        return sedeRepositorio.findById(id);
    }
    
    public Page<Sede> filtrarSedes(SedeCriteriaDTO criteria, Pageable pageable) {
        log.info("Filtrando sedes con criterios: {}", criteria);
        Specification<Sede> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getSearch())) {
                String likePattern = "%" + criteria.getSearch().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("nombre")), likePattern));
            }

            if (criteria.getActivo() != null) {
                predicates.add(cb.equal(root.get("activo"), criteria.getActivo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return sedeRepositorio.findAll(spec, pageable);
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
