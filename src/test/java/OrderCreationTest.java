import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class OrderCreationTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String WRONG_HASH = "6666666666";
    private Response creation;
    OrderSteps order = new OrderSteps(BASE_URI);
    UserSteps user = new UserSteps(BASE_URI);

    @Before
    public void before() {
        user.setup();
        creation = user.createUser();
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем")
    @Description("Для успешного создания заказа необходимо авторизоваться и передать в теле запроса необходимые ингредиенты")
    public void createOrderWithIngredientsAuthTest() {
        Response response = order.getListOfIngredients();
        String firstId = order.getIngredientsId(response,0);
        String secondId = order.getIngredientsId(response,1);

        Response newOrder = order.createOrderWithAuth(creation, List.of(firstId,secondId));
        newOrder.then().statusCode(200);
        order.checkBody(newOrder,"success",true);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов авторизованным пользователем")
    @Description("Если не передать ни один ингредиент, вернётся код ответа 400 Bad Request")
    public void createOrderWithoutIngredientsAuthTest() {
        Response newOrder = order.createOrderWithAuth(creation, List.of());
        newOrder.then().statusCode(400)
                .and()
                .assertThat().body("message",is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов авторизованным пользователем")
    @Description("Если в запросе передан невалидный хеш ингредиента, вернётся код ответа 500\n" +
            "Internal Server Error")
    public void createOrderWithWrongHashAuthTest() {
        Response newOrder = order.createOrderWithAuth(creation, List.of(WRONG_HASH));
        newOrder.then().statusCode(500);
    }

    //Наставник сказал, что так как в требованиях нет ожидаемого ответа, можно зафиксировать ответ 200
    @Test
    @DisplayName("Создание заказа пользователем без авторизации")
    @Description("Создать заказ может только авторизованный пользователь, следовательно, появится ошибка")
    public void createOrderWithoutAuthTest() {
        Response response = order.getListOfIngredients();
        String firstId = order.getIngredientsId(response,0);
        String secondId = order.getIngredientsId(response,1);

        Response newOrder = order.createOrderWithoutAuth(List.of(firstId,secondId));
        newOrder.then().statusCode(200)
                .and()
                .assertThat().body("success",is(true));
    }

    @After
    public void after() {
        user.getTokenAndDeleteUser(creation);
    }
}