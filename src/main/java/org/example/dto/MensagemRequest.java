package org.example.dto;

import lombok.*;

@Builder
public class MensagemRequest {
    @Getter @Setter
    private String usuario;
    @Getter @Setter
    private String conteudo;
}
