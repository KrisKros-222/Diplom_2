import java.util.List;

public class OrderData {
    private List<String> ingredients;
    private List<String> data;

    public OrderData(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
