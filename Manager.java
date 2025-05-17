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
        this.employed = true;
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
            System.out.println("�������� �� �������� � ��������");
            return false;
        }

        if (workplace != null) {
            cashier.setPosition("������");
            System.out.println("����� ������ " + cashier.getName() + " �����");
            return true;
        }
        return false;
    }

    public boolean fireWorker(Worker worker) {
        if (!employed) {
            System.out.println("�������� �� �������� � ��������");
            return false;
        }

        if (worker instanceof Cashier) {
            ((Cashier) worker).dismiss();
            System.out.println("������ " + worker.getName() + " ������");
            return true;
        }
        System.out.println("����� ������� ������ ��������");
        return false;
    }

    // ���������� ���������
    public void changeShopStatus(boolean open) {
        if (open) {
            workplace.openShop();
        } else {
            workplace.closeShop();
        }
    }

    public void changeResponsibleEmployee(String employeeName) {
        workplace.changeResponsibleEmployee(employeeName);
    }

    // ���������� ��������
    public boolean addProductToShop(Product product) {
        return workplace.addProduct(product);
    }

    public boolean removeProductFromShop(String productName, int quantity) {
        // ����� � �������� ������
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
        System.out.println("��������� " + category + " ���������");
    }

    public void removeCategory(String category) {
        workplace.newUnavailableCategory(category);
        System.out.println("��������� " + category + " �������");
    }

    // �������� ����������
    public void showFullShopInfo() {
        System.out.println("=== ������ ���������� � �������� ===");
        workplace.showShopInfo();
        System.out.println("================================");
    }

    public void showFinancialReport() {
        System.out.println("=== ���������� ����� ===");
        workplace.showFinancialInfo();
        System.out.println("========================");
    }

    // ���������������� �������
    public void updateShopLocation(String newLocation) {
        workplace.setLocation(newLocation);
        System.out.println("������� �������� ���������: " + newLocation);
    }

    // ���������� ���������� � ����
    public void updateManagerInfo(String newName, String newContact) {
        this.name = newName;
        this.contact = newContact;
        System.out.println("���������� � ��������� ���������");
    }
}