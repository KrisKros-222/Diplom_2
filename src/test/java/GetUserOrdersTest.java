import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;

public class GetUserOrdersTest {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private OrderSteps order;
    private Response creation;
    UserSteps user = new UserSteps(BASE_URI);

    Faker faker = new Faker();
    String email = faker.internet().emailAddress();
    String name = faker.name().firstName();
    String password = faker.internet().password(6,8);

    @Before
    public void before() {
        user.setup();
        order = new OrderSteps(BASE_URI);
        creation = user.createUser();
    }

    @Test
    @DisplayName("Получение списка заказов конкретного авторизованного пользователя ")
    @Description("При успешном подключении вернётся список последних заказов пользователя, включая данные о составе заказа")
    public void getOrderWithAuthTest() {
        Response response = order.getListOfIngredients();
        String firstId = order.getIngredientsId(response,0);
        String secondId = order.getIngredientsId(response,1);
        order.createOrderWithAuth(creation,List.of(firstId,secondId));

        Response orderList = order.getOrdersAuth(creation);
        orderList.then().statusCode(200)
                .and()
                .log().all()
                .assertThat().body("orders.ingredients",contains(List.of(firstId,secondId)));
    }

    @Test
    @DisplayName("Получение списка заказов конкретного пользователя без авторизации")
    @Description("Если выполнить запрос без авторизации, вернётся код ответа 401 Unauthorized")
    public void getOrderWithoutAuthTest() {
        Response response = order.getListOfIngredients();
        String firstId = order.getIngredientsId(response,0);
        String secondId = order.getIngredientsId(response,1);
        order.createOrderWithoutAuth(List.of(firstId,secondId));

        Response orderList = order.getOrdersWithoutAuth();
        orderList.then().statusCode(401)
                .and()
                .assertThat().body("message",is("You should be authorised"));
    }

    @After
    public void after() {
        user.getTokenAndDeleteUser(creation);
    }
}
