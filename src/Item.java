/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping addToCart items.
 */
import java.math.BigDecimal;
import java.util.Random;
import java.math.RoundingMode;

public class Item {

    private final String id;
    private final String title;
    private int quantity;
    private final BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public Item(String id, String title) {
        this.id = id;
        this.title = title;
        this.quantity = 1;
        this.unitPrice = generateUnitPrice(1, 15);
        this.totalPrice = unitPrice;
    }

    private BigDecimal generateUnitPrice(double min, double max)
    {
        Random random = new Random();
        double val = min + (max - min) * random.nextDouble();
        return new BigDecimal(val).setScale(2, RoundingMode.HALF_UP);
    }

    public String getId() {
        return this.id;
    }
    public String getTitle() {
        return this.title;
    }
    public int getQuantity() {
        return this.quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public void change(int num) {
        if (num == 1) {
            this.quantity += num;
            this.totalPrice = this.totalPrice.add(this.unitPrice);
        }
        else if (num == -1 && this.quantity > 1){
            this.quantity += num;
            this.totalPrice = this.totalPrice.subtract(this.unitPrice);
        }
    }

}
