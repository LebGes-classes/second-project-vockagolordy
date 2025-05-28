import java.util.*;

public class Shop implements Storage {
    private static final int MAX_UNITS = 50;
    private List<WarehouseUnit> units;
    private String location;
    public Set<String> availableCategories;
    private String responsibleEmployee;
    private boolean isOpen;
    private double totalSales;
    private double totalReturns;
    private Map<Customer, List<Product>> salesHistory;

    public Shop(String location, int unitCount, String responsibleEmployee) {
        if (unitCount > MAX_UNITS) {
            throw new IllegalArgumentException("Shop cannot have more than " + MAX_UNITS + " units");
        }

        this.location = location;
        this.units = new ArrayList<>();
        for (int i = 0; i < unitCount; i++) {
            units.add(new WarehouseUnit());
        }
        this.availableCategories = new HashSet<>();
        this.responsibleEmployee = responsibleEmployee;
        this.isOpen = false;
        this.totalSales = 0;
        this.totalReturns = 0;
        this.salesHistory = new HashMap<>();
    }

    // геттеры
    public int getOccupiedUnits() {
        return (int) units.stream().filter(WarehouseUnit::isOccupied).count();
    }
    public List<WarehouseUnit> getUnits() {
        return units;
    }
    public boolean isOpen() {
        return isOpen;
    }
    @Override
    public String getLocation() {
        return location;
    }
    @Override
    public void setLocation(String location) {
        this.location = location;
    }
    @Override
    public String[] getAvailableCategories() {
        return availableCategories.toArray(new String[0]);
    }

    // смена ответственного лица
    public void changeResponsibleEmployee(String newEmployee) {
        this.responsibleEmployee = newEmployee;
        System.out.println("Responsible employee changed to: " + newEmployee);
    }

    // управление статусом магазина
    public void openShop() {
        isOpen = true;
        System.out.println("Shop at " + location + " is now open");
    }
    public void closeShop() {
        isOpen = false;
        System.out.println("Shop at " + location + " is now closed");
    }

    // обработать покупку
    public boolean processPurchase(Customer customer, String productName, int quantity) {
        if (!isOpen) return false;

        for (WarehouseUnit unit : units) {
            for (Product product : unit.getProducts()) {
                if (product.getName().equals(productName) && product.getQuantity() >= quantity) {
                    Product soldProduct = new Product(
                            product.getCategory(),
                            product.getName(),
                            product.getInfo(),
                            product.price,
                            quantity
                    );

                    product.setQuantity(product.getQuantity() - quantity);
                    if (product.getQuantity() == 0) {
                        unit.getProducts().remove(product);
                        if (unit.getProducts().isEmpty()) {
                            unit.resetOccupied();
                        }
                    }

                    double saleAmount = quantity * product.price;
                    totalSales += saleAmount;
                    salesHistory.computeIfAbsent(customer, k -> new ArrayList<>()).add(soldProduct);

                    return true;
                }
            }
        }
        return false;
    }

    // обработать возврат без корректировки total sales
    public boolean processReturn(Customer customer, String productName, int quantity) {
        if (!isOpen) return false;

        List<Product> customerPurchases = salesHistory.get(customer);
        if (customerPurchases == null) return false;

        for (Product purchased : customerPurchases) {
            if (purchased.getName().equals(productName) && purchased.getQuantity() >= quantity) {
                Product returnProduct = new Product(
                        purchased.getCategory(),
                        purchased.getName(),
                        purchased.getInfo(),
                        purchased.price,
                        quantity
                );

                if (!addProduct(returnProduct)) return false;

                double returnAmount = quantity * purchased.price;
                totalReturns += returnAmount;
                return true;
            }
        }
        return false;
    }

    // вывод финансовой информации пункта продаж
    public void showFinancialInfo() {
        System.out.println("=== Financial Information ===");
        System.out.println("Total sales: " + totalSales);
        System.out.println("Total returns: " + totalReturns);
        System.out.println("Net profit: " + (totalSales - totalReturns));
        System.out.println("============================");
    }

    // вывод полной информации о пункте продаж
    public void showShopInfo() {
        System.out.println("=== Shop Information ===");
        System.out.println("Location: " + location);
        System.out.println("Responsible employee: " + responsibleEmployee);
        System.out.println("Status: " + (isOpen ? "OPEN" : "CLOSED"));
        System.out.println("Total units: " + units.size());
        System.out.println("Occupied units: " + getOccupiedUnits());
        System.out.println("Available categories: " + String.join(", ", availableCategories));
        showFinancialInfo();
    }

    // добавить продукт в юнит пункта продаж
    public boolean addProduct(Product product) {
        if (!availableCategories.contains(product.getCategory())) {
            System.out.println("Category " + product.getCategory() + " is not available in this shop");
            return false;
        }

        for (WarehouseUnit unit : units) {
            for (Product p : unit.getProducts()) {
                if (p.getName().equals(product.getName()) &&
                        p.getCategory().equals(product.getCategory())) {
                    if (unit.addProduct(product)) {
                        return true;
                    }
                }
            }
        }

        for (WarehouseUnit unit : units) {
            if (!unit.isOccupied()) {
                if (unit.addProduct(product)) {
                    return true;
                }
            }
        }

        System.out.println("No available space in shop");
        return false;
    }

    // метод для сокращения кода, применяется при покупке и продаже
    public double findProductPrice(String productName) {
        for (WarehouseUnit unit : units) {
            for (Product p : unit.getProducts()) {
                if (p.getName().equals(productName)) {
                    return p.price;
                }
            }
        }
        return 0;
    }

    // вывод доступных товаров
    public void displayAvailableProducts() {
        System.out.println("\nAvailable Products at " + location + ":");
        if (units.stream().noneMatch(unit -> unit.isOccupied() && !unit.getProducts().isEmpty())) {
            System.out.println("No products available in this shop.");
            return;
        }

        units.stream()
                .filter(WarehouseUnit::isOccupied)
                .flatMap(unit -> unit.getProducts().stream())
                .forEach(product -> System.out.printf(
                        "- %s (%s) | Price: %.2f | Quantity: %d%n",
                        product.getName(), product.getCategory(),
                        product.price, product.getQuantity()
                ));
    }

    // изменение категорий доступных в пункте продаж
    @Override
    public void newAvailableCategory(String category) {
        availableCategories.add(category);
    }
    @Override
    public void newUnavailableCategory(String category) {
        availableCategories.remove(category);
    }
}