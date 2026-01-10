package com.ucacue.udipsai.modules.documentos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentoIdDTO {
    private Integer id;

    public DocumentoIdDTO(Integer id) {
        this.id = id;
    }

    public DocumentoIdDTO() {
    }
}
