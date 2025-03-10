import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserCreationTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private UserSteps user;
    private Response creation;

    @Before
    public void before() {
        user = new UserSteps(BASE_URI);
    }

    @Test
    @DisplayName("Создание нового пользователя")
    @Description("При передаче всех необходимых параметров можно создать нового пользователя")
    public void createUser() {
        creation = user.createUser("kjkj@yandex.ru","555656","Sasha");
        creation.then().statusCode(200);
        user.checkBody(creation,"success",true);
    }

    @Test
    @DisplayName("Создание уже существующего пользователя")
    @Description("Если пользователь существует, вернётся код ответа 403 Forbidden")
    public void createExistingUser() {
        creation = user.createUser("kjkj@yandex.ru","555656","Sasha");
        Response exitingUser = user.createUser("kjkj@yandex.ru","555656","Sasha");
        exitingUser.then().statusCode(403);
        user.checkErrorBody(exitingUser,"success",false,"message","User already exists");
    }

    @Test
    @DisplayName("Создание пользователя без почты")
    @Description("Если нет одного из полей, вернётся код ответа 403 Forbidden")
    public void createWithoutEmail() {
        creation = user.createUser(" ","3333","Ola");
        creation.then().statusCode(403);
        user.checkErrorBody(creation,"success",false,"message","Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Если нет одного из полей, вернётся код ответа 403 Forbidden")
    public void createWithoutPassword() {
        creation = user.createUser("kjkj@yandex.ru"," ","Ola");
        creation.then().statusCode(403);
        user.checkErrorBody(creation,"success",false,"message","Email, password and name are required fields");
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Если нет одного из полей, вернётся код ответа 403 Forbidden")
    public void createWithoutName() {
        creation = user.createUser("kjkj@yandex.ru","8888"," ");
        creation.then().statusCode(403);
        user.checkErrorBody(creation,"success",false,"message","Email, password and name are required fields");
    }

    @After
    public void after() {
        user.getTokenAndDeleteUser(creation);
    }
}
