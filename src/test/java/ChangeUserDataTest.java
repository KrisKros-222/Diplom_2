import io.qameta.allure.Description;
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
    @DisplayName("Возможность смены почты авторизованным пользователем")
    @Description("Для успешного изменения данных необходимо авторизоваться, а также передать в теле запроса параметр с новыми данными")
    public void changeEmailWithAuth() {
        user.authRealUser("sjsjsj@yandex.ru","6565656");
        Response change = user.changeEmailAuthUser(creation,"mkmk@mail.ru");
        change.then().statusCode(200)
                .and().assertThat().body("user.email",is("mkmk@mail.ru"));
    }

    @Test
    @DisplayName("Появление ошибки при изменении почты на существующую")
    @Description("Если передать почту, которая уже используется, вернётся код ответа 403\n" +
            "Forbidden")
    public void changeOnExistEmailWithAuth() {
        user.authRealUser("sjsjsj@yandex.ru","6565656");
        Response change = user.changeEmailAuthUser(creation,"sjsjsj@yandex.ru");
        change.then().statusCode(403)
                .and().assertThat().body("message", is("User with such email already exist"));
    }

    @Test
    @DisplayName("Возможность смены имени авторизованным пользователем")
    @Description("Для успешного изменения данных необходимо авторизоваться, а также передать в теле запроса параметр с новыми данными")
    public void changeNameWithAuth() {
        user.authRealUser("sjsjsj@yandex.ru","6565656");
        Response change = user.changeNameAuthUser(creation,"Sasha");
        change.then().statusCode(200)
                .and().assertThat().body("user.name", is("Sasha"));
    }

    @Test
    @DisplayName("Появление ошибки при смене почты неавторизованным пользователем")
    @Description("Если выполнить запрос без авторизации, вернётся код ответа 401 Unauthorized")
    public void changeEmailWithoutAuth() {
        user.authRealUser("sjsjsj@yandex.ru","6565656");
        Response change = user.changeEmailNonAuthUser(creation,"mkmk@mail.ru");
        change.then().statusCode(401)
                .and().assertThat().body("message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Появление ошибки при смене имени неавторизованным пользователем")
    @Description("Если выполнить запрос без авторизации, вернётся код ответа 401 Unauthorized")
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
