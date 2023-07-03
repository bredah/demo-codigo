package org.example.controller;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.example.model.Mensagem;
import org.example.utils.DisplayTestName;
import org.example.utils.MensagemHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayTestName.class)
class MensagemControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
    }

    @Nested
    @Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    class RegistrarMesangem {

        @Test
        @Tag("smoke")
        void devePermitirRegistrarMensagem() {
            var mensagemRequest = MensagemHelper.gerarMensagemRequest();

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
        void deveGerarExcecao_QuandoRegistrarMensagem_UsuarioEmBranco() {
            var mensagemRequest = MensagemHelper.gerarMensagemRequest();
            mensagemRequest.setUsuario("");

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagemRequest)
                    .when()
                    .post("/mensagens")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("$", hasKey("message"))
                    .body("$", hasKey("errors"))
                    .body("message", equalTo("Validation error"))
                    .body("errors[0]", equalTo("usuário não pode estar vazio"));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_ConteudoEmBranco() {
            var mensagemRequest = MensagemHelper.gerarMensagemRequest();
            mensagemRequest.setConteudo("");

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagemRequest)
                    .when()
                    .post("/mensagens")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("$", hasKey("message"))
                    .body("$", hasKey("errors"))
                    .body("message", equalTo("Validation error"))
                    .body("errors[0]", equalTo("conteúdo não pode estar vazio"));
        }

        @Test
        void devePermitirRegistrarMensagem_ValidarSchema() {
            var mensagemRequest = MensagemHelper.gerarMensagemRequest();

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
            var mensagemRequest = MensagemHelper.gerarMensagemRequest();

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

    @Nested
    @Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    class BuscarMensagem {
        @Test
        @Sql(scripts = {"/data.sql"})
        void devePermitirBuscarMensagem() {
            var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("./schemas/MensagemResponseSchema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistente() {
            var id = "5f789b39-4295-42c1-a65b-cfca5b987db3";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("$", hasKey("message"))
                    .body("$", hasKey("errors"))
                    .body("message", equalTo("requição apresenta erro"))
                    .body("errors[0]", equalTo("mensagem não encontrada"));
        }

        @Nested
        @Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        class AlterarMensagem {
            @Test
            @Sql(scripts = {"/data.sql"})
            void devePermirirAlterarMensagem() {
                var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
                var mensagem = MensagemHelper.gerarMensagemCompleta();
                mensagem.setId(UUID.fromString(id));

                given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(mensagem)
                        .when()
                        .put("/mensagens/{id}", id)
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .body("conteudo", equalTo(mensagem.getConteudo()))
                        .body(matchesJsonSchemaInClasspath("./schemas/MensagemResponseSchema.json"));
            }

            @Test
            void deveGerarExcecao_QuandoAlterar_IdNaoCoincide() {
                var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
                var mensagem = MensagemHelper.gerarMensagemCompleta();

                given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .body(mensagem)
                        .when()
                        .put("/mensagens/{id}", id)
                        .then()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .body("$", hasKey("message"))
                        .body("$", hasKey("errors"))
                        .body("message", equalTo("requição apresenta erro"))
                        .body("errors[0]", equalTo("mensagem não encontrada"));
            }
        }

        @Nested
        @Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        class RemoverMensagem {
            @Test
            @Sql(scripts = {"/data.sql"})
            void devePermitirApagarMensagem() {
                var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
                given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .delete("/mensagens/{id}", id)
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .body(equalTo("mensagem removida"));
            }

            @Test
            void deveGerarExcecao_QuandoApagarMensagem_IdNaoExistente() {
                var id = "5f789b39-4295-42c1-a65b-cfca5b987db3";
                given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .delete("/mensagens/{id}", id)
                        .then()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .body("$", hasKey("message"))
                        .body("$", hasKey("errors"))
                        .body("message", equalTo("requição apresenta erro"))
                        .body("errors[0]", equalTo("mensagem não encontrada"));
            }

        }

        @Nested
        @Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        class IncrementarGostei {

            @Test
            @Sql(scripts = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            void devePermitirIncrementarGostei() {
                var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
                given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .put("/mensagens/{id}/gostei", id)
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .body("gostei", equalTo(1))
                        .body(matchesJsonSchemaInClasspath("./schemas/MensagemResponseSchema.json"));
            }

            @Test
            void deveGerarExcecao_QuandoIncrementarGostei_IdNaoExistente() {
                var id = "5f789b39-4295-42c1-a65b-cfca5b987db3";
                given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .put("/mensagens/{id}/gostei", id)
                        .then()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .body("$", hasKey("message"))
                        .body("$", hasKey("errors"))
                        .body("message", equalTo("requição apresenta erro"))
                        .body("errors[0]", equalTo("mensagem não encontrada"));
            }
        }

        @Nested
        @Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        class ListarMensagem {
            @Test
            @Sql(scripts = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
            void devePermitirListarMensagens() {
                given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .get("/mensagens")
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .body(matchesJsonSchemaInClasspath("./schemas/MensagemPaginationSchema.json"))
                        .body("number", equalTo(0))
                        .body("size", equalTo(10))
                        .body("totalElements", equalTo(5));
            }

            @Test
            void devePermitirListarMensagens_QuandoNaoExisteRegistro() {
                given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when()
                        .get("/mensagens")
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .body(matchesJsonSchemaInClasspath("./schemas/MensagemPaginationSchema.json"))
                        .body("number", equalTo(0))
                        .body("size", equalTo(10))
                        .body("totalElements", equalTo(0));
            }
        }
    }
}
