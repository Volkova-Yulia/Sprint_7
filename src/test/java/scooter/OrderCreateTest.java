package scooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import scooter.client.OrderClient;
import scooter.model.Order;

import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private final OrderClient orderClient = new OrderClient();
    private final List<String> color;
    private Integer track;

    public OrderCreateTest(List<String> color) {
        this.color = color;
    }

    // варианты цвета: один, оба, пустой список и без поля вовсе
    @Parameterized.Parameters(name = "Цвет самоката: {0}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()},
                {null},
        };
    }

    // отменяем заказ после теста
    @After
    public void tearDown() {
        if (track != null) {
            orderClient.cancel(track);
        }
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цвета")
    @Description("Можно указать один цвет, оба цвета или не указывать цвет вовсе; ответ содержит track")
    public void orderCanBeCreatedWithAnyColor() {
        // готовим заказ, цвет берём из параметров
        Order order = new Order(
                "Иван",
                "Иванов",
                "Москва, Ленина, 1",
                4,
                "+79991234567",
                5,
                "2026-09-30",
                "Тестовый заказ",
                color);

        // создаём заказ
        Response response = orderClient.create(order);

        // ждём 201 и track в ответе
        response.then()
                .statusCode(SC_CREATED)
                .body("track", notNullValue());

        // запоминаем трек, чтобы отменить заказ после теста
        track = response.then().extract().path("track");
    }
}
