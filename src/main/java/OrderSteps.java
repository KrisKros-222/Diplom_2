import ingredients.*;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class OrderSteps {
    private String baseURI;
    private static final String ORDER_CREATION_API = "/api/orders";
    private static final String INGREDIENTS_LIST_API = "/api/ingredients";
    private static final String GET_ORDER_LIST_API = "/api/orders";

    public OrderSteps(String baseURI) {
        this.baseURI = baseURI;
    }

    @Step("Получение списка ингредиентов")
    public Response getListOfIngredients() {
        Response response = given()
                .baseUri(baseURI)
                .when()
                .get(INGREDIENTS_LIST_API);
        return response;
    }

    @Step("Проверяем тело успешного ответа")
    public void checkBody(Response creation, String path, Boolean text) {
        creation.then().assertThat().body(path,is(text));
    }

    @Step("Получение конкретных ингредиентов")
    public String getIngredientsId (Response response, int number) {
        response.then().extract().response();
        ResponseData responseData = response.as(ResponseData.class);
        String id = responseData.getData().get(number).getId();
        return id;
    }

    @Step("Создание заказа с ингредиентами авторизированного пользователя ")
    public Response createOrderWithAuth (Response creation, List<String> ingredients) {
        String token = creation.then().extract().jsonPath().getString("accessToken");
        OrderData order = new OrderData(ingredients);
        Response newOrder = given()
                .baseUri(baseURI)
                .log().all()
                .header("Authorization",token)
                .header("Content-type","application/json")
                .body(order)
                .post(ORDER_CREATION_API);
        return newOrder;
    }

    @Step("Создание заказа с ингредиентами не авторизированного пользователя ")
    public Response createOrderWithoutAuth (List<String> ingredients) {
        OrderData order = new OrderData(ingredients);
        Response newOrder = given()
                .baseUri(baseURI)
                .header("Content-type","application/json")
                .body(order)
                .post(ORDER_CREATION_API);
        return newOrder;
    }

    @Step("Получение списка заказов с авторизацией")
    public Response getOrdersAuth(Response creation) {
        String token = creation.then().extract().jsonPath().getString("accessToken");
        Response orderList = given()
                .baseUri(baseURI)
                .header("Authorization",token)
                .header("Content-type","application/json")
                .when()
                .get(GET_ORDER_LIST_API);
        return orderList;
    }

    @Step("Получение списка заказов без авторизации")
    public Response getOrdersWithoutAuth() {
        Response orderList = given()
                .baseUri(baseURI)
                .header("Content-type","application/json")
                .when()
                .get(GET_ORDER_LIST_API);
        return orderList;
    }
}
