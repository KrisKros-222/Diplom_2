import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class OrderCreationTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String WRONG_HASH = "666aaa666aaa666aaa666aaa";
    private UserSteps user;
    private OrderSteps order;
    private Response creation;

    @Before
    public void before() {
        user = new UserSteps(BASE_URI);
        order = new OrderSteps(BASE_URI);
        creation = user.createUser("sjsjsj@yandex.ru","6565656","Kolya");
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами авторизованным пользователем")
    public void createOrderWithIngredientsAuth() {
        Response response = order.getListOfIngredients();
        String firstId = order.getIngredientsId(response,0);
        String secondId = order.getIngredientsId(response,1);

        Response newOrder = order.createOrderWithAuth(creation, List.of(firstId,secondId));
        newOrder.then().statusCode(200);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов авторизованным пользователем")
    public void createOrderWithoutIngredientsAuth() {
        Response newOrder = order.createOrderWithAuth(creation, List.of(" "));
        newOrder.then().statusCode(400)
                .and()
                .assertThat().body("message",is("Ingredients ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов авторизованным пользователем")
    public void createOrderWithWrongHashAuth() {
        Response newOrder = order.createOrderWithAuth(creation, List.of(WRONG_HASH));
        newOrder.then().statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа пользователем без авторизации")
    public void createOrderWithoutAuth() {
        Response response = order.getListOfIngredients();
        String firstId = order.getIngredientsId(response,0);
        String secondId = order.getIngredientsId(response,1);

        Response newOrder = order.createOrderWithoutAuth(List.of(firstId,secondId));
        newOrder.then().statusCode(401);
    }

    @After
    public void after() {
        user.getTokenAndDeleteUser(creation);
    }
}
