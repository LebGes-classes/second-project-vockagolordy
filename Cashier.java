import java.util.*;

class Cashier implements Worker {
    private String name;
    private String id;
    private String contact;
    private String position;
    private boolean employed;
    private double salesVolume;
    private List<Product> soldProducts;
    private Shop workplace;

    public Cashier(String name, String id, String contact, String position, Shop workplace) {
        this.name = name;
        this.id = id;
        this.contact = contact;
        this.position = position;
        this.employed = true; // по умолчанию нанят
        this.salesVolume = 0;
        this.soldProducts = new ArrayList<>();
        this.workplace = workplace;
    }

    // геттеры
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getID() {
        return id;
    }
    @Override
    public String getContact() {
        return contact;
    }
    @Override
    public String getLocation() {
        return workplace.getLocation();
    }
    public String getPosition() {
        return position;
    }

    @Override
    public boolean isEmployed() {
        return employed;
    }

    public double getSalesVolume() {
        return salesVolume;
    }

    public boolean sellProduct(Customer customer, String productName, int quantity) {
        if (!workplace.isOpen()) {
            System.out.println("Магазин закрыт");
            return false;
        }

        boolean success = workplace.processPurchase(customer, productName, quantity);
        if (success) {
            // продукт для добавления в список продаж кассира
            for (WarehouseUnit unit : workplace.getUnits()) {
                for (Product p : unit.getProducts()) {
                    if (p.getName().equals(productName)) {
                        Product soldProduct = new Product(
                                p.getCategory(), p.getName(), p.getInfo(), p.price, quantity
                        );
                        soldProducts.add(soldProduct);
                        salesVolume += p.price * quantity;
                        break;
                    }
                }
            }
        }
        return success;
    }

    // возврат товара
    public boolean processReturn(Customer customer, String productName, int quantity) {
        if (!workplace.isOpen()) {
            System.out.println("Магазин закрыт");
            return false;
        }

        boolean success = workplace.processReturn(customer, productName, quantity);
        if (success) {
            // Обновляем статистику кассира
            salesVolume -= workplace.findProductPrice(productName) * quantity;
            // Удаляем из списка продаж (упрощенная логика)
            soldProducts.removeIf(p -> p.getName().equals(productName));
        }
        return success;
    }

    // Просмотр продаж кассира
    public void showSalesInfo() {
        System.out.println("=== Информация о продажах кассира " + name + " ===");
        System.out.println("Должность: " + position);
        System.out.println("Общий объем продаж: " + salesVolume);
        System.out.println("Количество проданных товаров: " + soldProducts.size());
        System.out.println("Список проданных товаров:");
        soldProducts.forEach(p ->
                System.out.printf("- %s (Категория: %s, Кол-во: %d, Цена: %.2f)%n",
                        p.getName(), p.getCategory(), p.getQuantity(), p.price)
        );
        System.out.println("==============================");
    }

    // Другие методы
    public void setPosition(String newPosition) {
        this.position = newPosition;
    }

    public void dismiss() {
        this.employed = false;
        this.workplace = null;
    }

    public Shop getWorkplace() {
        return workplace;
    }
}