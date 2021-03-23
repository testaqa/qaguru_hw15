package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static utils.FileUtils.readStringFromFile;

public class ReqresTests {

    @BeforeAll
    static void beforeAll() {
        RestAssured.filters(new AllureRestAssured());
        RestAssured.baseURI = "https://reqres.in/api";
    }

    @Test
    void createUser() {
        String data = readStringFromFile("./src/test/resources/create_data.txt");

        given()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("/api/users")
                .then()
                .log().body()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("createdAt", hasLength(24));
    }

    @Test
    void notFoundUser() {
        var response = when()
                .get("/users/233333")
                .then()
                .log().body()
                .statusCode(404)
                .extract().body().asString();

        assertThat(response, is("{}"));
    }

    @Test
    void delayRequestContainsUrl() {
        when()
                .get("/users?delay=4")
                .then()
                .log().body()
                .statusCode(200)
                .body("support.url", containsString("/#support-heading"));
    }

    @Test
    void colorHasValidLength() {
        when()
                .get("/unknown/2")
                .then()
                .log().body()
                .statusCode(200)
                .body("data.color", hasLength(7));
    }

    @Test
    void itemsOnPage() {
        when()
                .get("/users?page=2")
                .then()
                .log().body()
                .statusCode(200)
                .body("data", hasItem(allOf(hasEntry("id", 7))))
                .body("data", hasItem(allOf(hasEntry("id", 12))))
                .body("data", not(hasItem(allOf(hasEntry("id", 6)))))
                .body("data", not(hasItem(allOf(hasEntry("id", 13)))))
                .body("data.id[1]", is( 8));
    }
}
