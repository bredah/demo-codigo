
package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Mensagem;
import org.example.service.MensagemService;
import org.example.utils.ConverterId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/mensagens")
@RequiredArgsConstructor
public class MensagemController {

  private final MensagemService mensagemService;

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Mensagem> registrarMensagem(@Valid @RequestBody Mensagem mensagem) {
    log.info("requisição para registrar mensagem foi efetuada");
    var mensagemCriada = mensagemService.criarMensagem(mensagem);
    return new ResponseEntity<>(mensagemCriada, HttpStatus.CREATED);
  }

  @GetMapping(
      value = "/{id}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> buscarMensagem(@PathVariable String id) {
    log.info("requisição para buscar mensagem foi efetuada");
    var converterId = new ConverterId(id);
    if (converterId.hasError()) {
      return ResponseEntity.badRequest().body(converterId.getError());
    }
    var uuid = converterId.getId();
    var mensagemEncontrada = mensagemService.buscarMensagem(uuid);
    return new ResponseEntity<>(mensagemEncontrada, HttpStatus.OK);
  }

  @GetMapping(
      value = "",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Page<Mensagem>> listarMensagens(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    log.info("requisição para listar mensagens foi efetuada: Página={}, Tamanho={}", page, size);
    Page<Mensagem> mensagens = mensagemService.listarMensagens(pageable);
    return new ResponseEntity<>(mensagens, HttpStatus.OK);
  }

  @PutMapping(
      value = "/{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> atualizarMensagem(
      @PathVariable String id,
      @RequestBody @Valid Mensagem mensagem) {
    log.info("requisição para atualizar mensagem foi efetuada");
    var converterId = new ConverterId(id);
    if (converterId.hasError()) {
      return ResponseEntity.badRequest().body(converterId.getError());
    }
    var uuid = converterId.getId();
    var mensagemAntiga = mensagemService.buscarMensagem(uuid);
    var mensagemAtualizada = mensagemService.alterarMensagem(mensagemAntiga, mensagem);
    return new ResponseEntity<>(mensagemAtualizada, HttpStatus.OK);
  }

  @PutMapping("/{id}/gostei")
  public ResponseEntity<?> incrementarGostei(@PathVariable String id) {
    log.info("requisição para incrementar gostei foi efetuada");
    var converterId = new ConverterId(id);
    if (converterId.hasError()) {
      return ResponseEntity.badRequest().body(converterId.getError());
    }
    var uuid = converterId.getId();
    var mensagem = mensagemService.buscarMensagem(uuid);
    var mensagemAtualizada = mensagemService.incrementarGostei(mensagem);
    return new ResponseEntity<>(mensagemAtualizada, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> apagarMensagem(@PathVariable String id) {
    log.info("requisição para apagar mensagem foi efetuada");
    var converterId = new ConverterId(id);
    if (converterId.hasError()) {
      return ResponseEntity.badRequest().body(converterId.getError());
    }
    var uuid = converterId.getId();
    mensagemService.buscarMensagem(uuid);
    mensagemService.apagarMensagem(uuid);
    return new ResponseEntity<>("mensagem removida", HttpStatus.OK);
  }

}
