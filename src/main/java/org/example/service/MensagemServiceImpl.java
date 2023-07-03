package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.exception.MensagemNotFoundException;
import org.example.model.Mensagem;
import org.example.repository.MensagemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MensagemServiceImpl implements MensagemService {

    private final MensagemRepository mensagemRepository;

    @Override
    public Mensagem criarMensagem(Mensagem mensagem) {
        mensagem.setId(UUID.randomUUID());
        return mensagemRepository.save(mensagem);
    }

    @Override
    public Mensagem buscarMensagem(UUID id) {
        return mensagemRepository.findById(id)
                .orElseThrow(() -> new MensagemNotFoundException("mensagem não encontrada"));
    }

@Override
public Mensagem alterarMensagem(Mensagem mensagemAntiga, Mensagem mensagemNova) {
    if (!mensagemAntiga.getId().equals(mensagemNova.getId())) {
        throw new MensagemNotFoundException("mensagem não apresenta o ID correto");
    }
    mensagemAntiga.setConteudo(mensagemNova.getConteudo());
    return mensagemRepository.save(mensagemAntiga);
}

@Override
public void apagarMensagem(UUID id) {
    mensagemRepository.deleteById(id);
}

@Override
public Mensagem incrementarGostei(Mensagem mensagem) {
    mensagem.setGostei(mensagem.getGostei() + 1);
    return mensagemRepository.save(mensagem);
}

    @Override
    public Page<Mensagem> listarMensagens(Pageable pageable) {
        return mensagemRepository.listarMensagens(pageable);
    }

}
