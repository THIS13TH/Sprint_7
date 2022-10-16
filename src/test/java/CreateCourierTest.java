import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateCourierTest {
    private Courier courier;
    private CourierClient courierClient;
    private int courierId;


    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void tearDown() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Проверка создания курьера (валидные данные)")
    @Description("endpoint /api/v1/courier")
    public void courierCreatedTest() {
        courier = Generator.getRandom();
        ValidatableResponse response = courierClient.create(courier);
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));

        int loginStatusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, loginStatusCode);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_CREATED, statusCode);


        boolean isCreated = response.extract().path("ok");
        assertTrue(isCreated);

        courierId = loginResponse.extract().path("id");
        assertNotEquals(0, courierId);
    }

    @Test
    @DisplayName("Проверка создания 2х одинаковых курьеров(ошибка)")
    @Description("endpoint /api/v1/courier")
    public void createTwoIdenticalCouriersTest() {
        Courier courierTEST = new Courier(Generator.getRandom().getFirstName(),
                Generator.getRandom().getLogin(),
                Generator.getRandom().getPassword());
        courierClient.create(courierTEST);
        ValidatableResponse response = courierClient.create(courierTEST);
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courierTEST));
        courierId = loginResponse.extract().path("id");

        String bodyAnswer = response.extract().path("message");
        assertEquals("Этот логин уже используется. Попробуйте другой.", bodyAnswer);

        int StatusCode = response.extract().statusCode();
        assertEquals(SC_CONFLICT, StatusCode);
    }

    @Test
    @DisplayName("Проверка создания курьера с паролем - null")
    @Description("endpoint /api/v1/courier")
    public void createCourierWithNullPasswordTest() {
        Courier courierTEST = new Courier(Generator.getRandom().getFirstName(),
                Generator.getRandom().getLogin(),
                null);
        ValidatableResponse response = courierClient.create(courierTEST);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = response.extract().path("message");
        assertEquals("Недостаточно данных для создания учетной записи", bodyAnswer);
    }

    @Test
    @DisplayName("Проверка создания курьера с  именем - null")
    @Description("endpoint /api/v1/courier")
    public void courierCreateWithNullFirstNameTest() {
        Courier courierTEST = new Courier(null,
                Generator.getRandom().getLogin(),
                Generator.getRandom().getPassword());
        ValidatableResponse response = courierClient.create(courierTEST);
        courierId = courierClient.login(CourierCredentials.from(courierTEST)).extract().path("id");

        int statusCode = response.extract().statusCode();
        assertEquals(SC_CREATED, statusCode);

        boolean isCreated = response.extract().path("ok");
        assertTrue(isCreated);
    }

    @Test
    @DisplayName("Проверка создания курьера с пустым значением поля логин")
    @Description("endpoint /api/v1/courier")
    public void createCourierWithEmptyLoginTest() {
        Courier courierTEST = new Courier(Generator.getRandom().getFirstName(),
                "",
                Generator.getRandom().getPassword());
        ValidatableResponse response = courierClient.create(courierTEST);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = response.extract().path("message");
        assertEquals("Недостаточно данных для создания учетной записи", bodyAnswer);
    }

    @Test
    @DisplayName("Проверка создания курьера с пустым значением поля пароля")
    @Description("endpoint /api/v1/courier")
    public void createCourierWithEmptyPasswordTest() {
        Courier courierTEST = new Courier(Generator.getRandom().getFirstName(),
                Generator.getRandom().getLogin(),
                "");
        ValidatableResponse response = courierClient.create(courierTEST);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        String bodyAnswer = response.extract().path("message");
        assertEquals("Недостаточно данных для создания учетной записи", bodyAnswer);
    }
}
