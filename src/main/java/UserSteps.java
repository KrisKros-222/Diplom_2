import changes.*;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import users.WithoutLogin;
import users.WithoutPassword;

import static org.hamcrest.Matchers.is;

import static io.restassured.RestAssured.given;

public class UserSteps {
    private String baseURI;
    private static final String USER_CREATION_API = "/api/auth/register";
    private static final String DELETE_USER_API = "/api/auth/user";
    private static final String AUTH_USER_API = "/api/auth/login";
    private static final String CHANGE_DATA_USER_API = "/api/auth/user";

    Faker faker = new Faker();
    String email = faker.internet().emailAddress();
    String name = faker.name().firstName();
    String password = faker.internet().password(6,8);
    String changedEmail = faker.internet().emailAddress();
    String changedName = faker.name().firstName();
    String wrongEmail = faker.internet().emailAddress();
    String wrongPassword = faker.internet().password(6,8);

    public UserSteps(String baseURI) {
        this.baseURI = baseURI;
    }

    public void setup(){
        RestAssured.baseURI = baseURI;
    }

    @Step("Отправляем POST запрос на ручку /api/auth/register")
    public Response createUser() {
        UserData user = new UserData(email, password, name);
        Response creation = given()
                .baseUri(baseURI)
                //.log().all()
                .header("Content-type","application/json")
                .and()
                .body(user)
                .when()
                .post(USER_CREATION_API);
        return creation;
    }

    @Step("Создаем пользователя без почты")
    public Response createUserWithoutEmail() {
        WithoutLogin user = new WithoutLogin(password, name);
        Response creation = given()
                .baseUri(baseURI)
                //.log().all()
                .header("Content-type","application/json")
                .and()
                .body(user)
                .when()
                .post(USER_CREATION_API);
        return creation;
    }

    @Step("Создаем пользователя без пароля")
    public Response createUserWithoutPassword() {
        WithoutPassword user = new WithoutPassword(email,name);
        Response creation = given()
                .baseUri(baseURI)
                //.log().all()
                .header("Content-type","application/json")
                .and()
                .body(user)
                .when()
                .post(USER_CREATION_API);
        return creation;
    }

    @Step("Создаем пользователя без имени")
    public Response createUserWithoutName() {
        UserData user = new UserData(email, password);
        Response creation = given()
                .baseUri(baseURI)
                //.log().all()
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
                //.log().all()
                .header("Authorization",token)
                .when()
                .delete(DELETE_USER_API);
    }

    @Step("Авторизация с существующим пользователем")
    public Response authRealUser() {
        UserData user = new UserData(email,password);
        Response auth = given()
                .baseUri(baseURI)
                //.log().all()
                .header("Content-type","application/json")
                .and()
                .body(user)
                .when()
                .post(AUTH_USER_API);
        return auth;
    }

    @Step("Авторизация с некорректной почтой")
    public Response authWithIncorrectEmail() {
        UserData user = new UserData(wrongEmail,password);
        Response auth = given()
                .baseUri(baseURI)
                //.log().all()
                .header("Content-type","application/json")
                .and()
                .body(user)
                .when()
                .post(AUTH_USER_API);
        return auth;
    }

    @Step("Авторизация с некорректным паролем")
    public Response authWithIncorrectPassword() {
        UserData user = new UserData(wrongEmail,wrongPassword);
        Response auth = given()
                .baseUri(baseURI)
                //.log().all()
                .header("Content-type","application/json")
                .and()
                .body(user)
                .when()
                .post(AUTH_USER_API);
        return auth;
    }

    @Step("Создаем пользователя с новой почтой")
    public Response createSecondUser() {
        UserData user = new UserData(changedEmail, password, name);
        Response creation = given()
                .baseUri(baseURI)
                //.log().all()
                .header("Content-type","application/json")
                .and()
                .body(user)
                .when()
                .post(USER_CREATION_API);
        return creation;
    }

    @Step("Проверка изменения данных почты авторизованного пользователя")
    public Response changeEmailAuthUser(Response creation) {
        UserEmailChange newEmail = new UserEmailChange(changedEmail);
        String token = creation.then().extract().jsonPath().getString("accessToken");

        Response change = given()
                .baseUri(baseURI)
                .log().all()
                .header("Content-type","application/json")
                .header("Authorization",token)
                .body(newEmail)
                .when()
                .patch(CHANGE_DATA_USER_API);
        return change;
    }

    @Step("")
    public void checkBodyWithChangedEmail(Response change) {
        change.then().assertThat().body("user.email",is(changedEmail));
    }

    @Step("")
    public void checkBodyWithChangedName(Response change) {
        change.then().assertThat().body("user.name",is(changedName));
    }

    @Step("Проверка изменения данных почты авторизованного пользователя")
    public Response changeNameAuthUser(Response creation) {
        UserNameChange newName = new UserNameChange(changedName);
        String token = creation.then().extract().jsonPath().getString("accessToken");

        Response change = given()
                .baseUri(baseURI)
                .log().all()
                .header("Content-type","application/json")
                .header("Authorization",token)
                .body(newName)
                .when()
                .patch(CHANGE_DATA_USER_API);
        return change;
    }

    @Step("Проверка изменения данных почты не авторизованного пользователя")
    public Response changeEmailNonAuthUser(Response creation) {
        UserEmailChange newEmail = new UserEmailChange(changedEmail);

        Response change = given()
                .baseUri(baseURI)
                .log().all()
                .header("Content-type","application/json")
                .body(newEmail)
                .when()
                .patch(CHANGE_DATA_USER_API);
        return change;
    }

    @Step("Проверка изменения данных почты не авторизованного пользователя")
    public Response changeNameNonAuthUser(Response creation) {
        UserNameChange newName = new UserNameChange(changedName);

        Response change = given()
                .baseUri(baseURI)
                .log().all()
                .header("Content-type","application/json")
                .body(newName)
                .when()
                .patch(CHANGE_DATA_USER_API);
        return change;
    }

}
