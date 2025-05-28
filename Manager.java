import java.util.*;

class Manager implements Worker {
    private String name;
    private String id;
    private String contact;
    private String location;
    private boolean employed;
    private Shop workplace;

    public Manager(String name, String id, String contact, String location, Shop shop) {
        this.name = name;
        this.id = id;
        this.contact = contact;
        this.location = location;
        this.employed = true; // �� ��������� �����
        this.workplace = shop;
    }

    // �������
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
    @Override
    public String getPosition() {
        return "Manager";
    }
    public Shop getWorkplace() {
        return workplace;
    }
    @Override
    public boolean isEmployed() {
        return employed;
    }

    // ���������� ����������
    public boolean hireCashier(Cashier cashier) {
        if (!employed) {
            System.out.println("This manager can not hire workers");
            return false;
        }

        if (workplace != null) {
            cashier.setPosition("Cashier");
            System.out.println("New cashier " + cashier.getName() + " is hired");
            return true;
        }
        return false;
    }
    public boolean fireWorker(Worker worker) {
        if (!employed) {
            System.out.println("This manager can not fire workers");
            return false;
        }

        if (worker instanceof Cashier) {
            ((Cashier) worker).dismiss();
            System.out.println("Cashier " + worker.getName() + " is fired");
            return true;
        }
        System.out.println("Can not fire this worker");
        return false;
    }

    // �������� �������� ��������
    public void changeShopStatus(boolean open) {
        if (open) {
            workplace.openShop();
        } else {
            workplace.closeShop();
        }
    }

    // ������� ������� �� ������ � ����� ������
    public boolean purchaseFromWarehouse(Warehouse warehouse, String productName, int quantity) {
        if (!isEmployed()) {
            System.out.println("Manager is not employed");
            return false;
        }

        if (!workplace.isOpen()) {
            System.out.println("Shop is closed");
            return false;
        }

        // ���� ����� �� ������
        Product warehouseProduct = null;
        for (WarehouseUnit unit : warehouse.getUnits()) {
            for (Product product : unit.getProducts()) {
                if (product.getName().equals(productName)) {
                    warehouseProduct = product;
                    break;
                }
            }
            if (warehouseProduct != null) break;
        }

        if (warehouseProduct == null) {
            System.out.println("Product not found in warehouse");
            return false;
        }

        if (warehouseProduct.getQuantity() < quantity) {
            System.out.println("Not enough quantity in warehouse");
            return false;
        }

        // ������� ������� ��� ��������
        Product shopProduct = new Product(
                warehouseProduct.getCategory(),
                warehouseProduct.getName(),
                warehouseProduct.getInfo(),
                warehouseProduct.price,
                quantity
        );

        // �������� �������� � �������
        if (!workplace.addProduct(shopProduct)) {
            System.out.println("No space in shop");
            return false;
        }

        // ��������� ���������� �� ������
        warehouseProduct.setQuantity(warehouseProduct.getQuantity() - quantity);
        if (warehouseProduct.getQuantity() == 0) {
            for (WarehouseUnit unit : warehouse.getUnits()) {
                if (unit.getProducts().remove(warehouseProduct)) {
                    if (unit.getProducts().isEmpty()) {
                        unit.resetOccupied();
                    }
                    break;
                }
            }
        }

        System.out.println("Successfully purchased " + quantity + " of " + productName + " from warehouse");
        return true;
    }

    // �������� �������������� ���������
    public void changeResponsibleEmployee(String employeeName) {
        workplace.changeResponsibleEmployee(employeeName);
    }

    // �������� ����� � �������
    public boolean addProductToShop(Product product) {
        return workplace.addProduct(product);
    }

    // ������� ����� �� ��������
    public boolean removeProductFromShop(String productName, int quantity) {
        for (WarehouseUnit unit : workplace.getUnits()) {
            Iterator<Product> iterator = unit.getProducts().iterator();
            while (iterator.hasNext()) {
                Product p = iterator.next();
                if (p.getName().equals(productName)) {
                    if (p.getQuantity() <= quantity) {
                        iterator.remove();
                        if (unit.getProducts().isEmpty()) {
                            unit.resetOccupied();
                        }
                        return true;
                    } else {
                        p.setQuantity(p.getQuantity() - quantity);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // ���������� �����������
    public void addCategory(String category) {
        workplace.newAvailableCategory(category);
        System.out.println("Category " + category + " is added");
    }
    public void removeCategory(String category) {
        workplace.newUnavailableCategory(category);
        System.out.println("Category " + category + " is deleted");
    }

    // �������� ����������
    public void showFullShopInfo() {
        System.out.println("=== Full information about shop ===");
        workplace.showShopInfo();
        System.out.println("================================");
    }

    // �������� ����������� ������
    public void showFinancialReport() {
        System.out.println("=== Financial report ===");
        workplace.showFinancialInfo();
        System.out.println("========================");
    }

    // �������� �������
    public void updateShopLocation(String newLocation) {
        workplace.setLocation(newLocation);
        System.out.println("Shops location is updated: " + newLocation);
    }

    // ��������� ���������� � ��������� ��������
    public void updateManagerInfo(String newName, String newContact) {
        this.name = newName;
        this.contact = newContact;
        System.out.println("Manager information is updated");
    }
}