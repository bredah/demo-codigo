package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Mensagem;
import org.example.service.MensagemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/mensagens")
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService mensagemService;


    @PostMapping
    public ResponseEntity<Mensagem> registrarMensagem(@Valid @RequestBody Mensagem mensagem) {
        log.info("requisição para registrar mensagem foi efetuada");
        var mensagemCriada = mensagemService.criarMensagem(mensagem);
        return new ResponseEntity<>(mensagemCriada, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mensagem> buscarMensagem(@PathVariable UUID id) {
        log.info("requisição para buscar mensagem foi efetuada");
        var mensagemEncontrada = mensagemService.buscarMensagem(id);
        return new ResponseEntity<>(mensagemEncontrada, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mensagem> atualizarMensagem(@PathVariable UUID id, @RequestBody @Valid Mensagem mensagem) {
        log.info("requisição para atualizar mensagem foi efetuada");
        var mensagemAtualizada = mensagemService.alterarMensagem(id, mensagem);
        return new ResponseEntity<>(mensagemAtualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> apagarMensagem(@PathVariable UUID id) {
        log.info("requisição para apagar mensagem foi efetuada");
        mensagemService.apagarMensagem(id);
        return new ResponseEntity<>("mensagem removida", HttpStatus.OK);
    }

}
