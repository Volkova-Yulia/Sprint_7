package scooter;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scooter.client.CourierClient;
import scooter.model.Courier;
import scooter.model.CourierCredentials;
import scooter.util.CourierGenerator;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {

    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    // чистим за собой: удаляем созданного курьера
    @After
    public void tearDown() {
        if (courier != null) {
            return;
        }
        courierId = courierClient.login(new CourierCredentials(courier))
                .then().extract().path("id");

        if (courierId != null) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Курьера можно создать")
    @Description("Успешное создание курьера возвращает 201 и ok: true")
    public void courierCanBeCreated() {
        // готовим данные
        courier = CourierGenerator.getRandom();

        // создаём курьера
        Response response = courierClient.create(courier);

        // проверяем код и тело ответа
        response.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void cannotCreateTwoIdenticalCouriers() {
        // создаём первого курьера
        courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        courierId = courierClient.login(new CourierCredentials(courier))
                .then().extract().path("id");

        // пытаемся создать такого же ещё раз
        Response response = courierClient.create(courier);

        // ждём 409 и текст ошибки
        response.then()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    public void cannotCreateCourierWithoutLogin() {
        // курьер без логина
        courier = CourierGenerator.getRandom();
        courier.setLogin(null);

        Response response = courierClient.create(courier);

        // ждём 400 и текст ошибки
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    public void cannotCreateCourierWithoutPassword() {
        // курьер без пароля
        courier = CourierGenerator.getRandom();
        courier.setPassword(null);

        Response response = courierClient.create(courier);

        // ждём 400 и текст ошибки
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера с уже существующим логином")
    public void cannotCreateCourierWithExistingLogin() {
        // создаём курьера
        courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        courierId = courierClient.login(new CourierCredentials(courier))
                .then().extract().path("id");

        // другой курьер, но с тем же логином
        Courier duplicate = new Courier(courier.getLogin(), "other_password", "other_name");
        Response response = courierClient.create(duplicate);

        // ждём 409 и текст ошибки
        response.then()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }
}
