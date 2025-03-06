import io.qameta.allure.Step;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.is;

import static io.restassured.RestAssured.given;

public class UserSteps {
    private String baseURI;
    private static final String USER_CREATION_API = "/api/auth/register";
    private static final String DELETE_USER_API = "/api/auth/user";
    private static final String AUTH_USER_API = "/api/auth/login";

    public UserSteps(String baseURI) {
        this.baseURI = baseURI;
    }

    @Step("Отправляем POST запрос на ручку /api/auth/register")
    public Response createUser(String email, String password, String name) {
        UserData user = new UserData(email, password, name);
        Response creation = given()
                .baseUri(baseURI)
                .log().all()
                .header("Content-type","application/json")
                .and()
                .body(user)
                .when()
                .post(USER_CREATION_API);
        return creation;
    }

    @Step("Проверяем тело успешного ответа")
    public void checkBody(Response creation, String path, Boolean text) {
        creation.then().assertThat().body(path,is(text));
    }

    @Step("Проверяем тело неуспешного ответа")
    public void checkErrorBody(Response creation, String p_1, Boolean text,String p_2, String message) {
        creation.then().assertThat()
                .body(p_1,is(text))
                .body(p_2,is(message));
    }

    @Step("Получаем Access Token и удаляем пользователя")
    public void getTokenAndDeleteUser(Response creation) {
        String token = creation.then().extract().jsonPath().getString("accessToken");

        Response delete = given()
                .baseUri(baseURI)
                .log().all()
                .header("Authorization",token)
                .when()
                .delete(DELETE_USER_API);
    }

    @Step("Авторизация с существующим пользователем")
    public Response authRealUser(String email, String password) {
        UserData user = new UserData(email,password);
        Response auth = given()
                .baseUri(baseURI)
                .log().all()
                .header("Content-type","application/json")
                .and()
                .body(user)
                .when()
                .post(AUTH_USER_API);
        return auth;
    }

}
