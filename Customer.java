import java.util.*;

class Customer implements User {
    private String name;
    private String id;
    private String contact;
    private String location;
    private Map<String, Product> purchases; // Ключ: "категория|название", значение: продукт
    private double totalSpent;
    private Shop shop; // Ссылка на магазин

    public Customer(String name, String id, String contact, String location, Shop shop) {
        this.name = name;
        this.id = id;
        this.contact = contact;
        this.location = location;
        this.purchases = new HashMap<>();
        this.totalSpent = 0.0;
        this.shop = shop;
    }

    // Основные геттеры
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
        return location;
    }
    public Shop getShop(){
        return shop;
    }
    public Map<String, Product> getPurchases() {
        return purchases;
    }
    public double getTotalSpent() {
        return totalSpent;
    }

    public boolean buyProduct(String productName, int quantity) {
        if (!shop.isOpen()) {
            System.out.println("Магазин закрыт");
            return false;
        }

        if (!shop.processPurchase(this, productName, quantity)) {
            System.out.println("Не удалось купить товар");
            return false;
        }

        // Обновляем список покупок покупателя
        String key = findProductKey(productName);
        if (key != null) {
            Product existing = purchases.get(key);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            // Находим информацию о товаре для создания записи
            for (WarehouseUnit unit : shop.getUnits()) {
                for (Product p : unit.getProducts()) {
                    if (p.getName().equals(productName)) {
                        Product newPurchase = new Product(
                                p.getCategory(), p.getName(), p.getInfo(), p.price, quantity
                        );
                        purchases.put(p.getCategory() + "|" + p.getName(), newPurchase);
                        break;
                    }
                }
            }
        }

        totalSpent += shop.findProductPrice(productName) * quantity;
        return true;
    }

    public boolean returnProduct(String productName, int quantity) {
        String key = findProductKey(productName);
        if (key == null || purchases.get(key).getQuantity() < quantity) {
            System.out.println("Нельзя вернуть товар");
            return false;
        }

        if (!shop.processReturn(this, productName, quantity)) {
            System.out.println("Failed to return.");
            return false;
        }

        // Обновляем список покупок
        Product customerProduct = purchases.get(key);
        if (customerProduct.getQuantity() > quantity) {
            customerProduct.setQuantity(customerProduct.getQuantity() - quantity);
        } else {
            purchases.remove(key);
        }

        totalSpent -= customerProduct.price * quantity;
        return true;
    }

    private String findProductKey(String productName) {
        for (String key : purchases.keySet()) {
            if (key.split("\\|")[1].equals(productName)) {
                return key;
            }
        }
        return null;
    }

    // Просмотр текущих покупок
    public void showPurchases() {
        System.out.println("=== Ваши покупки (" + name + ") ===");
        if (purchases.isEmpty()) {
            System.out.println("У вас пока нет покупок");
        } else {
            purchases.values().forEach(item ->
                    System.out.printf("- %s (%s) | Кол-во: %d | Цена: %.2f%n",
                            item.getName(), item.getCategory(), item.getQuantity(), item.price)
            );
            System.out.println("Общая сумма: " + totalSpent);
        }
        System.out.println("=============================");
    }

    // Обновление контактной информации
    public void updateContact(String newContact) {
        this.contact = newContact;
        System.out.println("Контактная информация обновлена");
    }

    // Обновление локации
    public void updateLocation(String newLocation) {
        this.location = newLocation;
        System.out.println("Локация обновлена");
    }

}