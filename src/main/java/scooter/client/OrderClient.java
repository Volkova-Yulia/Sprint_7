package scooter.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import scooter.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends ScooterClient {

    private static final String ORDERS_PATH = "/api/v1/orders";
    private static final String CANCEL_PATH = "/api/v1/orders/cancel";

    @Step("Создание заказа")
    public Response create(Order order) {
        return given()
                .spec(getSpec())
                .body(order)
                .when()
                .post(ORDERS_PATH);
    }

    @Step("Получение списка заказов")
    public Response getList() {
        return given()
                .spec(getSpec())
                .when()
                .get(ORDERS_PATH);
    }

    @Step("Отмена заказа по треку {track}")
    public Response cancel(int track) {
        return given()
                .spec(getSpec())
                .queryParam("track", track)
                .when()
                .put(CANCEL_PATH);
    }
}
