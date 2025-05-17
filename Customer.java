import java.util.*;

class Customer implements User {
    private String name;
    private String id;
    private String contact;
    private String location;
    private Map<String, Product> purchases; // ����: "���������|��������", ��������: �������
    private double totalSpent;
    private Shop shop; // ������ �� �������

    public Customer(String name, String id, String contact, String location, Shop shop) {
        this.name = name;
        this.id = id;
        this.contact = contact;
        this.location = location;
        this.purchases = new HashMap<>();
        this.totalSpent = 0.0;
        this.shop = shop;
    }

    // �������� �������
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
            System.out.println("������� ������");
            return false;
        }

        if (!shop.processPurchase(this, productName, quantity)) {
            System.out.println("�� ������� ������ �����");
            return false;
        }

        // ��������� ������ ������� ����������
        String key = findProductKey(productName);
        if (key != null) {
            Product existing = purchases.get(key);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            // ������� ���������� � ������ ��� �������� ������
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
            System.out.println("������ ������� �����");
            return false;
        }

        if (!shop.processReturn(this, productName, quantity)) {
            System.out.println("Failed to return.");
            return false;
        }

        // ��������� ������ �������
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

    // �������� ������� �������
    public void showPurchases() {
        System.out.println("=== ���� ������� (" + name + ") ===");
        if (purchases.isEmpty()) {
            System.out.println("� ��� ���� ��� �������");
        } else {
            purchases.values().forEach(item ->
                    System.out.printf("- %s (%s) | ���-��: %d | ����: %.2f%n",
                            item.getName(), item.getCategory(), item.getQuantity(), item.price)
            );
            System.out.println("����� �����: " + totalSpent);
        }
        System.out.println("=============================");
    }

    // ���������� ���������� ����������
    public void updateContact(String newContact) {
        this.contact = newContact;
        System.out.println("���������� ���������� ���������");
    }

    // ���������� �������
    public void updateLocation(String newLocation) {
        this.location = newLocation;
        System.out.println("������� ���������");
    }

}