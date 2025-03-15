import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoginUserTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private Response creation;
    UserSteps user = new UserSteps(BASE_URI);

    @Before
    public void before() {
        user.setup();
        creation = user.createUser();
    }

    @Test
    @DisplayName("Успешной авторизация пользователя")
    @Description("Для успешной авторизации необходимо передать существующие email и password в теле запроса")
    public void loginSuccessTest() {
        Response auth = user.authRealUser();
        auth.then().statusCode(200);
        user.checkBody(auth,"success",true);
    }

    @Test
    @DisplayName("Ошибка авторизации с неверной почтой")
    @Description("Если логин неверный, вернётся код ответа 401 Unauthorized")
    public void loginWithIncorrectEmailTest() {
        Response auth = user.authWithIncorrectEmail();
        auth.then().statusCode(401);
        user.checkErrorBody(auth,"success",false,"message","email or password are incorrect");
    }

    @Test
    @DisplayName("Ошибка авторизации с неверным паролем")
    @Description("Если пароль неверный, вернётся код ответа 401 Unauthorized")
    public void loginWithIncorrectPasswordTest() {
        Response auth = user.authWithIncorrectPassword();
        auth.then().statusCode(401);
        user.checkErrorBody(auth,"success",false,"message","email or password are incorrect");
    }

    @After
    public void after() {
        user.getTokenAndDeleteUser(creation);
    }
}