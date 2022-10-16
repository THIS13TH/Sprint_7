import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ScooterClient {
    public static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    protected RequestSpecification getBaseSpecSettings() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .build();
    }
}
