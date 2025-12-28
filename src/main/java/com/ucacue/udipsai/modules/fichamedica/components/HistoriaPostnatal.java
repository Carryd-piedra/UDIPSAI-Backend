package com.ucacue.udipsai.modules.fichamedica.components;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class HistoriaPostnatal {

    @Column(name = "convulsiones")
    private Boolean convulsiones;

    @Column(name = "medicacion")
    private Boolean medicacion;
}
