import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class ChangeUserDataTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private Response creation;
    UserSteps user = new UserSteps(BASE_URI);
    Response secondUser;

    @Before
    public void before() {
        user.setup();
        creation = user.createUser();
        secondUser = user.createSecondUser();
    }

    @Test
    @DisplayName("Возможность смены почты авторизованным пользователем")
    @Description("Для успешного изменения данных необходимо авторизоваться, а также передать в теле запроса параметр с новыми данными")
    public void changeEmailWithAuthTest() {
        user.authRealUser();
        Response change = user.changeEmailAuthUser(creation);
        change.then().statusCode(200);
        user.checkBodyWithChangedEmail(change);
    }

    @Test
    @DisplayName("Появление ошибки при изменении почты на существующую")
    @Description("Если передать почту, которая уже используется, вернётся код ответа 403 Forbidden")
    public void changeOnExistEmailWithAuthTest() {
        user.authRealUser();
        Response change = user.changeSecondUserEmail(creation);
        change.then().statusCode(403)
                .and().assertThat().body("message", is("User with such email already exists"));
    }

    @Test
    @DisplayName("Возможность смены имени авторизованным пользователем")
    @Description("Для успешного изменения данных необходимо авторизоваться, а также передать в теле запроса параметр с новыми данными")
    public void changeNameWithAuthTest() {
        user.authRealUser();
        Response change = user.changeNameAuthUser(creation);
        change.then().statusCode(200);
        user.checkBodyWithChangedName(change);
    }

    @Test
    @DisplayName("Появление ошибки при смене почты неавторизованным пользователем")
    @Description("Если выполнить запрос без авторизации, вернётся код ответа 401 Unauthorized")
    public void changeEmailWithoutAuthTest() {
        user.authRealUser();
        Response change = user.changeEmailNonAuthUser(creation);
        change.then().statusCode(401)
                .and().assertThat().body("message", is("You should be authorised"));
    }

    @Test
    @DisplayName("Появление ошибки при смене имени неавторизованным пользователем")
    @Description("Если выполнить запрос без авторизации, вернётся код ответа 401 Unauthorized")
    public void changeNameWithoutAuthTest() {
        user.authRealUser();
        Response change = user.changeNameNonAuthUser(creation);
        change.then().statusCode(401)
                .and().assertThat().body("message", is("You should be authorised"));
    }

    @After
    public void after() {
        user.getTokenAndDeleteUser(creation);
        user.getTokenAndDeleteUser(secondUser);
    }
}
