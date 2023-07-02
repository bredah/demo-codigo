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
                .orElseThrow(() -> new MensagemNotFoundException("Mensagem não encontrada"));
    }

    @Override
    public Mensagem alterarMensagem(UUID id, Mensagem mensagem) {
        var mensageEncontrada = mensagemRepository.findById(id)
                .orElseThrow(() -> new MensagemNotFoundException("Mensagem não encontrada"));
        if (mensageEncontrada.getId() != mensagem.getId()) {
            throw new MensagemNotFoundException("Mensagem não apresenta o ID correto");
        }
        mensageEncontrada.setConteudo(mensagem.getConteudo());
        return mensagemRepository.save(mensageEncontrada);
    }

    @Override
    public void apagarMensagem(UUID id) {
        mensagemRepository.findById(id)
                .orElseThrow(() -> new MensagemNotFoundException("Mensagem não encontrada"));
        mensagemRepository.deleteById(id);
    }

    @Override
    public Mensagem incrementarGostei(UUID id) {
        var mensageEncontrada = mensagemRepository.findById(id)
                .orElseThrow(() -> new MensagemNotFoundException("Mensagem não encontrada"));
        mensageEncontrada.setGostei(mensageEncontrada.getGostei() + 1);
        return mensagemRepository.save(mensageEncontrada);
    }

    @Override
    public Page<Mensagem> listarMensagens(Pageable pageable) {
        return mensagemRepository.listarMensagens(pageable);
    }

}
