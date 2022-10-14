import net.datafaker.Faker;

public class Generator {
    static Faker faker = new Faker();

    public static Courier getRandom() {
        String login = faker.name().fullName();
        String password = String.valueOf(faker.password());
        String firstName = faker.name().fullName();

        return new Courier(firstName, login, password);
    }

    public static Order getDefault() {
        String firstName = faker.name().toString();
        String lastName = faker.name().lastName();
        String address = faker.address().toString();
        String metroStation = "12";
        String phone = faker.phoneNumber().toString();
        int rentTime = 9;
        String deliveryDate = "2023-06-06";
        String comment = "Bistro";
        String[] color = {"BLACK"};
        return new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
    }

    public static Order getWithoutColor(String[] color) {
        String firstName = faker.name().toString();
        String lastName = faker.name().lastName();
        String address = faker.address().toString();
        String metroStation = faker.number().toString();
        String phone = faker.phoneNumber().toString();
        int rentTime = 10;
        String deliveryDate = "2023-06-06";
        String comment = "Не нужно мне привозить";
        return new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
    }
}
