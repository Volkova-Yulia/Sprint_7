package scooter.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import scooter.model.Courier;
import scooter.model.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient extends ScooterClient {

    private static final String COURIER_PATH = "/api/v1/courier";
    private static final String LOGIN_PATH = "/api/v1/courier/login";

    @Step("Создание курьера")
    public Response create(Courier courier) {
        return given()
                .spec(getSpec())
                .body(courier)
                .when()
                .post(COURIER_PATH);
    }

    @Step("Логин курьера")
    public Response login(CourierCredentials credentials) {
        return given()
                .spec(getSpec())
                .body(credentials)
                .when()
                .post(LOGIN_PATH);
    }

    @Step("Удаление курьера")
    public Response delete(int courierId) {
        return given()
                .spec(getSpec())
                .when()
                .delete(COURIER_PATH + "/" + courierId);
    }
}
