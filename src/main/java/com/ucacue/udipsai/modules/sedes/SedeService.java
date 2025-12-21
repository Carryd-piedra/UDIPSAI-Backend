package com.ucacue.udipsai.modules.sedes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SedeService {

    @Autowired
    private SedeRepositorio sedeRepositorio;

    public List<Sede> getAllActiveSedes() {
        return sedeRepositorio.findByActivoTrue();
    }

    public Optional<Sede> getSedeById(Integer id) {
        if (id == null) return Optional.empty();
        return sedeRepositorio.findById(id);
    }

    public Sede saveOrUpdate(Sede sede) {
        sede.setNombre(sede.getNombre().toUpperCase());
        return sedeRepositorio.save(sede);
    }

    public void deleteSede(Integer id) {
        if (id == null) return;
        Optional<Sede> sedeOpt = sedeRepositorio.findById(id);
        sedeOpt.ifPresent(sede -> {
            sede.setActivo(false);
            sedeRepositorio.save(sede);
        });
    }
}
