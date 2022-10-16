import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class CourierLoginTest {
    private Courier courier;
    private CourierClient courierClient;
    private int courierId;


    @Before
    public void setUp() {
        courier = Generator.getRandom();
        courierClient = new CourierClient();
        courierClient.create(courier);
    }

    @After
    public void tearDown() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Успешная авторизация")
    public void courierCanLoginValidTest() {
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        courierId = loginResponse.extract().path("id");
        assertNotEquals(0, courierId);
    }

    @Test
    @DisplayName("Авторизация с пустым логином(нельзя авторизоваться с пустым логином)")
    @Description("endpoint /api/v1/courier/login")
    public void courierLoginWithEmptyLoginTest() {
        ValidatableResponse loginResponse = courierClient.login(new CourierCredentials("", courier.getPassword()));
        courierId = courierClient.login(CourierCredentials.from(courier)).extract().path("id");

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Недостаточно данных для входа", bodyAnswer); // Проверка сообщения в теле ответа
    }

    @Test
    @DisplayName("Авторизация с пустым паролем(нельзя авторизоваться с пустым паролем)")
    @Description("endpoint /api/v1/courier/login")
    public void courierLoginWithEmptyPasswordTest() {
        ValidatableResponse loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), ""));
        courierId = courierClient.login(CourierCredentials.from(courier)).extract().path("id");

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Недостаточно данных для входа", bodyAnswer);
    }

    @Test
    @DisplayName("Если требуемого поля нет(логин), возращаем ошибку")
    @Description("endpoint /api/v1/courier/login")
    public void courierLoginWithNullLoginTest() {
        ValidatableResponse loginResponse = courierClient.login(new CourierCredentials(null, courier.getPassword()));
        courierId = courierClient.login(CourierCredentials.from(courier)).extract().path("id");

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Недостаточно данных для входа", bodyAnswer);
    }

    @Test
    @DisplayName("Проверка неудачной авторизации с неверным логином")
    @Description("endpoint /api/v1/courier/login")
    public void courierLoginWithIncorrectLoginTest() {
        ValidatableResponse loginResponse = courierClient.login(new CourierCredentials("Test", courier.getPassword()));
        courierId = courierClient.login(CourierCredentials.from(courier)).extract().path("id");

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_NOT_FOUND, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Учетная запись не найдена", bodyAnswer); // Проверка сообщения в теле ответа
    }

    @Test
    @DisplayName("Проверка неудачной авторизации с неверным паролем")
    @Description("endpoint /api/v1/courier/login")
    public void courierLoginWithIncorrectPasswordTest() {
        ValidatableResponse loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), "Test"));
        courierId = courierClient.login(CourierCredentials.from(courier)).extract().path("id");

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_NOT_FOUND, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Учетная запись не найдена", bodyAnswer);
    }

    @Test
    @DisplayName("Попытка авторизации под несуществующим пользователем")
    @Description("endpoint /api/v1/courier/login")
    public void loginWitNotExistLoginTest() {
        ValidatableResponse loginResponse = courierClient.login(new CourierCredentials("Evklid", "Qwerty123$"));

        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_NOT_FOUND, statusCode);

        String bodyAnswer = loginResponse.extract().path("message");
        assertEquals("Учетная запись не найдена", bodyAnswer);
    }
}
