public class Product {
    private String category;
    private String name;
    private String info;
    public int price;
    private int quantity;

    public Product(String category, String name, String info, int price, int quantity) {
        this.category = category;
        this.name = name;
        this.info = info;
        this.price = price;
        this.quantity = quantity;
    }

    // геттеры сеттеры
    public void setPrice(int price) {
        this.price = price;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // вывод информации о товаре
    public void showProductInfo() {
        System.out.println("Category: " + category + ", Name: " + name + ", Quantity: " + quantity + ", Info: " + info);
    }
}
