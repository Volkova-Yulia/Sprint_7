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

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    private CourierClient courierClient;
    private Courier courier;
    private Integer courierId;

    // перед каждым тестом создаём курьера
    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandom();
        courierClient.create(courier);
        courierId = courierClient.login(new CourierCredentials(courier))
                .then().extract().path("id");
    }

    // после теста удаляем его
    @After
    public void tearDown() {
        if (courierId != null) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    @Description("Успешная авторизация возвращает 200 и id курьера")
    public void courierCanLogin() {
        // логинимся под созданным курьером
        Response response = courierClient.login(new CourierCredentials(courier));

        // ждём 200 и id в ответе
        response.then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Нельзя авторизоваться без логина")
    public void cannotLoginWithoutLogin() {
        // логинимся без логина
        Response response = courierClient.login(new CourierCredentials(null, courier.getPassword()));

        // ждём 400 и текст ошибки
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Нельзя авторизоваться без пароля")
    public void cannotLoginWithoutPassword() {
        // логинимся без пароля
        Response response = courierClient.login(new CourierCredentials(courier.getLogin(), null));

        // ждём 404
        response.then()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Нельзя авторизоваться с неверным паролем")
    public void cannotLoginWithWrongPassword() {
        // правильный логин, но чужой пароль
        Response response = courierClient.login(new CourierCredentials(courier.getLogin(), "wrong_password"));

        // ждём 404 и текст ошибки
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Нельзя авторизоваться под несуществующим пользователем")
    public void cannotLoginAsNonExistentCourier() {
        // случайный логин, которого нет в системе
        Courier nonExistent = CourierGenerator.getRandom();
        Response response = courierClient.login(new CourierCredentials(nonExistent));

        // ждём 404 и текст ошибки
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
