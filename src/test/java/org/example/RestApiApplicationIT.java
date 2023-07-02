package org.example;

import io.restassured.RestAssured;
import org.example.utils.DisplayTestName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayTestName.class)
class RestApiApplicationIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() throws Exception {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void deveApliacaoIniciarCorretamente() {
        given()
                .when()
                .get("/actuator/health")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo("UP"));
        ;
    }
}
