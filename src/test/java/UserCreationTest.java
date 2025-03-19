import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

public class UserCreationTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private Response creation;
    UserSteps user = new UserSteps(BASE_URI);


    @Before
    public void before() {
        user.setup();
    }

    @Test
    @DisplayName("Создание нового пользователя")
    @Description("При передаче всех необходимых параметров можно создать нового пользователя")
    public void createUser() {
        creation = user.createUser();
        creation.then().statusCode(200);
        user.checkBody(creation,"success",true);
        user.getTokenAndDeleteUser(creation);
    }

    @Test
    @DisplayName("Создание уже существующего пользователя")
    @Description("Если пользователь существует, вернётся код ответа 403 Forbidden")
    public void createExistingUserTest() {
        creation = user.createUser();
        Response exitingUser = user.createUser();
        exitingUser.then().statusCode(403);
        user.checkErrorBody(exitingUser,"success",false,"message","User already exists");
        user.getTokenAndDeleteUser(creation);
    }

    @Test
    @DisplayName("Создание пользователя без почты")
    @Description("Если нет одного из полей, вернётся код ответа 403 Forbidden")
    public void createWithoutEmailTest() {
        creation = user.createUserWithoutEmail();
        creation.then().statusCode(403);
        user.checkErrorBody(creation,"success",false,"message","Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Если нет одного из полей, вернётся код ответа 403 Forbidden")
    public void createWithoutPasswordTest() {
        creation = user.createUserWithoutPassword();
        creation.then().statusCode(403);
        user.checkErrorBody(creation,"success",false,"message","Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Если нет одного из полей, вернётся код ответа 403 Forbidden")
    public void createWithoutNameTest() {
        creation = user.createUserWithoutName();
        creation.then().statusCode(403);
        user.checkErrorBody(creation,"success",false,"message","Email, password and name are required fields");
    }
}
