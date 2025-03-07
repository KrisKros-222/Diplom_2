import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class ChangeUserDataTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private UserSteps user;
    private Response creation;

    @Before
    public void before() {
        user = new UserSteps(BASE_URI);
        creation = user.createUser("sjsjsj@yandex.ru","6565656","Kolya");
    }

    @Test
    @DisplayName("Проверяем, что авторизированный пользователь может сменить почту")
    public void changeEmailWithAuth() {
        user.authRealUser("sjsjsj@yandex.ru","6565656");
        Response change = user.changeEmailAuthUser(creation,"mkmk@mail.ru");
        change.then().statusCode(200)
                .and().assertThat().body("user.email",is("mkmk@mail.ru"));
    }

    @Test
    @DisplayName("Меняем почту на существующую")
    public void changeOnExistEmailWithAuth() {
        user.authRealUser("sjsjsj@yandex.ru","6565656");
        Response change = user.changeEmailAuthUser(creation,"sjsjsj@yandex.ru");
        change.then().statusCode(403)
                .and().assertThat().body("message", is("User with such email already exist"));
    }

    @Test
    @DisplayName("Проверяем, что авторизированный пользователь может сменить имя")
    public void changeNameWithAuth() {
        user.authRealUser("sjsjsj@yandex.ru","6565656");
        Response change = user.changeNameAuthUser(creation,"Sasha");
        change.then().statusCode(200)
                .and().assertThat().body("user.name", is("Sasha"));
    }

    @Test
    @DisplayName("Проверяем, что не авторизированный пользователь может сменить почту")
    public void changeEmailWithoutAuth() {
        user.authRealUser("sjsjsj@yandex.ru","6565656");
        Response change = user.changeEmailNonAuthUser(creation,"mkmk@mail.ru");
        change.then().statusCode(401)
                .and().assertThat().body("message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Проверяем, что не авторизированный пользователь может сменить имя")
    public void changeNameWithoutAuth() {
        user.authRealUser("sjsjsj@yandex.ru","6565656");
        Response change = user.changeNameNonAuthUser(creation,"Sasha");
        change.then().statusCode(401)
                .and().assertThat().body("message", is("You should be authorised"));
    }

    @After
    public void after() {
        user.getTokenAndDeleteUser(creation);
    }
}
