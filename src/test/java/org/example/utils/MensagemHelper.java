package org.example.utils;

import org.example.model.Mensagem;
import org.example.repository.MensagemRepository;

import java.util.UUID;

public abstract class MensagemHelper {

    public static Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("joe")
                .conteudo("xpto test")
                .build();
    }

    public static Mensagem registrarMensagem(MensagemRepository repository) {
        var mensagem = gerarMensagem();
        mensagem.setId(UUID.randomUUID());
        return repository.save(mensagem);
    }
}
