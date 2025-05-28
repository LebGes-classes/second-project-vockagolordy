import java.util.*;

public class WarehouseUnit {
    private List<Product> products;
    private boolean occupied;
    final private int maxCapacity = 1000;
    private int currentCapacity = 0;

    public WarehouseUnit() {
        this.products = new ArrayList<>();
        this.occupied = false;
    }

    // ���������� ������ � ������, boolean ����� ��� �����������
    public boolean addProduct(Product product) {
        if (!this.isFull(product.getQuantity())) {
            for (Product existingProduct : products) {
                if (existingProduct.getName().equals(product.getName())
                        && existingProduct.getCategory().equals(product.getCategory())) {
                    existingProduct.setQuantity(existingProduct.getQuantity() + product.getQuantity());
                    currentCapacity += product.getQuantity();
                    occupied = true;
                    return true;
                }
            }
            products.add(product);
            currentCapacity += product.getQuantity();
            occupied = true;
            return true;
        } else {
            System.out.println("Cannot add or move product: WarehouseUnit is full.");
            return false;
        }
    }

    // ����������� ������ ����� ��������
    public boolean moveProductTo(WarehouseUnit targetUnit, Product product) {
        if (!targetUnit.isFull(product.getQuantity())) {
            if (targetUnit.addProduct(product)) {
                this.products.remove(product);
                currentCapacity += products.size();
                if (this.products.isEmpty()) {
                    this.occupied = false;
                }
                return true;
            }
        }
        return false;
    }

    // �������� ������������� � �������������� ���������� (���� ��� ����� ������������ true)
    private boolean isFull(int quantity){
        return currentCapacity + quantity > maxCapacity;
    }
    private boolean isFull(){
        return currentCapacity > maxCapacity;
    }

    // �������� ������� ������ (��������������� �����)
    public boolean isOccupied() {
        return occupied;
    }

    public void resetOccupied(){
        this.occupied = false;
    }

    // ������ ������� � ������
    public List<Product> getProducts() {
        return products;
    }

    // ����������� ���������� � �������
    public void showProductsInfo() {
        if (products.isEmpty()) {
            System.out.println("Unit is empty.");
        } else {
            System.out.println("Info about products in this unit: ");
            for (Product product : products) {
                product.showProductInfo();
            }
        }
    }
}

