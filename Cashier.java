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
        this.employed = true; // работник нанят по умолчанию
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
    public Shop getWorkplace() {
        return workplace;
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

    // продать товар
    public boolean sellProduct(Customer customer, String productName, int quantity) {
        if (!workplace.isOpen()) {
            System.out.println("Store is closed");
            return false;
        }

        boolean success = workplace.processPurchase(customer, productName, quantity);
        if (success) {
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

    // принять товар на возврат
    public boolean processReturn(Customer customer, String productName, int quantity) {
        if (!workplace.isOpen()) {
            System.out.println("Shop is closed");
            return false;
        }

        boolean success = workplace.processReturn(customer, productName, quantity);
        if (success) {
            // обновить статистику
            salesVolume -= workplace.findProductPrice(productName) * quantity;
            soldProducts.removeIf(p -> p.getName().equals(productName));
        }
        return success;
    }

    // показать информацию о работнике
    public void showSalesInfo() {
        System.out.println("=== Sales Information for Cashier " + name + " ===");
        System.out.println("Position: " + position);
        System.out.println("Total sales volume: " + salesVolume);
        System.out.println("Number of products sold: " + soldProducts.size());
        System.out.println("List of sold products:");
        soldProducts.forEach(p ->
                System.out.printf("- %s (Category: %s, Quantity: %d, Price: %.2f)%n",
                        p.getName(), p.getCategory(), p.getQuantity(), p.price)
        );
        System.out.println("==============================");
    }

    // возможность сделать из кассира мега-кассира, но не менеджера
    public void setPosition(String newPosition) {
        this.position = newPosition;
    }

    // установить статус работника (для сериализации)
    public void setEmployed(boolean status){
        this.employed = status;
    }

    // увольнение
    public void dismiss() {
        this.employed = false;
        this.workplace = null;
    }

}