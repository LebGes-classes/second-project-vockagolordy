import java.util.*;

public class Warehouse implements Storage {
    private List<WarehouseUnit> units;
    private String location;
    private Set<String> availableCategories;

    public Warehouse(String location, int unitCount) {
        this.location = location;
        this.units = new ArrayList<>();
        for (int i = 0; i < unitCount; i++) {
            units.add(new WarehouseUnit());
        }
        this.availableCategories = new HashSet<>();
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

    @Override
    public void newAvailableCategory(String category) {
        availableCategories.add(category);
    }

    @Override
    public void newUnavailableCategory(String category) {
        availableCategories.remove(category);
    }

    // общее количество €чеек
    public int getTotalUnits() {
        return units.size();
    }

    // количество заполненных €чеек
    public int getOccupiedUnits() {
        return (int) units.stream().filter(WarehouseUnit::isOccupied).count();
    }

    // количество пустых €чеек
    public int getEmptyUnits() {
        return getTotalUnits() - getOccupiedUnits();
    }

    // ѕолучить информацию о товарах на складе
    public void showWarehouseInfo() {
        System.out.println("=== Warehouse Information ===");
        System.out.println("Location: " + location);
        System.out.println("Total units: " + getTotalUnits());
        System.out.println("Occupied units: " + getOccupiedUnits());
        System.out.println("Empty units: " + getEmptyUnits());
        System.out.println("Available categories: " + String.join(", ", availableCategories));
        System.out.println("=============================");
    }

    // ѕеремещение €чейки между складами
    public boolean moveUnitTo(Warehouse targetWarehouse, int unitIndex) {
        if (unitIndex < 0 || unitIndex >= units.size()) {
            System.out.println("Invalid unit index");
            return false;
        }

        WarehouseUnit unitToMove = units.get(unitIndex);
        if (unitToMove.getProducts().isEmpty()) {
            System.out.println("Cannot move empty unit");
            return false;
        }

        // ѕровер€ем, есть ли место в целевом складе
        if (targetWarehouse.getEmptyUnits() > 0) {
            // Ќаходим первую пустую €чейку в целевом складе
            Optional<WarehouseUnit> emptyUnit = targetWarehouse.units.stream()
                    .filter(u -> !u.isOccupied()).findFirst();

            if (emptyUnit.isPresent()) {
                // ѕеремещаем все продукты
                for (Product product : unitToMove.getProducts()) {
                    if (!emptyUnit.get().addProduct(product)) {
                        System.out.println("Failed to move products");
                        return false;
                    }
                }
                // ќчищаем исходную €чейку
                unitToMove.getProducts().clear();
                unitToMove.resetOccupied();
                return true;
            }
        }
        System.out.println("No space in target warehouse");
        return false;
    }

    // ƒобавить продукт на склад (находит подход€щую €чейку)
    public boolean addProduct(Product product) {
        // ѕровер€ем доступность категории
        if (!availableCategories.contains(product.getCategory())) {
            System.out.println("Category " + product.getCategory() + " is not available for purchase");
            return false;
        }

        // —начала пытаемс€ найти €чейку с таким же продуктом
        for (WarehouseUnit unit : units) {
            if (unit.isOccupied()) {
                for (Product item : unit.getProducts()) {
                    if (item.getName().equals(product.getName())
                        && item.getCategory().equals(product.getCategory())) {
                        if (unit.addProduct(product)) {
                            return true;
                        }
                    }
                }
            }
        }

        // ≈сли не нашли, ищем пустую €чейку
        for (WarehouseUnit unit : units) {
            if (!unit.isOccupied()) {
                if (unit.addProduct(product)) {
                    return true;
                }
            }
        }

        System.out.println("No available space for the product");
        return false;
    }
}