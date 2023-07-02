package org.example.controller;

import io.restassured.RestAssured;
import org.example.dto.MensagemRequest;
import org.example.utils.DisplayTestName;
import org.example.model.Mensagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayTestName.class)
class MensagemControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
//        RestAssured.filters().stream().filter(new AllureRestAssured());
    }

    @Test
    @Tag("smoke")
    void devePermitirRegistrarMensagem() {

        var mensagemRequest = MensagemRequest.builder()
                .usuario("John")
                .conteudo("xpto")
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(mensagemRequest)
                .when()
                .post("/mensagens")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("$", hasKey("id"))
                .body("$", hasKey("usuario"))
                .body("$", hasKey("conteudo"))
                .body("$", hasKey("dataCriacao"))
                .body("$", hasKey("gostei"))
                .body("usuario", equalTo(mensagemRequest.getUsuario()))
                .body("conteudo", equalTo(mensagemRequest.getConteudo()));
    }

    @Test
    void devePermitirRegistrarMensagem_ValidarSchema() {

        var mensagemRequest = MensagemRequest.builder()
                .usuario("John")
                .conteudo("xpto")
                .build();

        given()
                .header("Content-Type", "application/json")
                .body(mensagemRequest)
                .when()
                .post("/mensagens")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .header("Content-Type", notNullValue())
                .header("Content-Type", "application/json")
                .body(matchesJsonSchemaInClasspath("./schemas/MensagemResponseSchema.json"));
    }

    @Test
    void devePermitirRegistrarMensagem_CapturandoResposta() {
        var mensagemRequest = MensagemRequest.builder()
                .usuario("John")
                .conteudo("xpto")
                .build();

        var mensageRecebida = given()
                .header("Content-Type", "application/json")
                .body(mensagemRequest)
                .when()
                .post("/mensagens")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(Mensagem.class);

        assertThat(mensageRecebida.getId())
                .isNotNull();
    }


}
