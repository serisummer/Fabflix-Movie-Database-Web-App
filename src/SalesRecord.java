public class SalesRecord {
    private final String salesId;
    private final String title;
    private final int quantity;
    private final String unitPrice;
    private final String totalPrice;

    public SalesRecord(String salesId, String title, int quantity, String unitPrice, String totalPrice) {
        this.salesId = salesId;
        this.title = title;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public String getSalesId() {
        return this.salesId;
    }

    public String getTitle() {
        return this.title;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getUnitPrice() {
        return this.unitPrice;
    }

    public String getTotalPrice() {
        return this.totalPrice;
    }
}
