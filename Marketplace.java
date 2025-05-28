import java.io.*;
import java.util.*;
import java.util.stream.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class Marketplace {
    private static List<Shop> shops = new ArrayList<>();
    private static List<Warehouse> warehouses = new ArrayList<>();
    private static List<Customer> customers = new ArrayList<>();
    private static List<Worker> workers = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    public static final String MANAGER_PASSWORD = "admin123";

    public static void main(String[] args) {
        System.out.println("=== Marketplace System ===");
        System.out.println("\nInitial Data Loading:");
        System.out.println("1. Load saved data (deserialize from CSV)");
        System.out.println("2. Load new data from CSV file");
        System.out.println("3. Use sample data");
        System.out.println("4. Exit");
        System.out.print("Select option: ");

        int choice = readIntInput(1, 4);

        switch (choice) {
            case 1:
                deserializeFromCSV("marketplace_data.csv");
                break;
            case 2:
                initializeCSVData();
                break;
            case 3:
                initializeSampleData();
                break;
            case 4:
                System.out.println("Exiting the system. Goodbye!");
                return;
        }

        showMainMenu();
    }

    // меню выбора роли
    private static void showMainMenu() {
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Customer");
            System.out.println("2. Employee");
            System.out.println("3. View instructions");
            System.out.println("4. Save data and exit");
            System.out.print("Select option: ");

            int choice = readIntInput(1, 4);

            switch (choice) {
                case 1:
                    customerMenu();
                    break;
                case 2:
                    employeeMenu();
                    break;
                case 3:
                    showInstructions();
                    break;
                case 4:
                    serializeToCSV("marketplace_data.csv");
                    System.out.println("Data saved successfully. Exiting the system. Goodbye!");
                    return;
            }
        }
    }

    // нструкции к пользованию приложением
    private static void showInstructions() {
        System.out.println("\n=== Marketplace System Instructions ===");
        System.out.println("1. Data Loading Options:");
        System.out.println("   - Load saved data: Restores the system state from previous session");
        System.out.println("   - Load new data: Imports data from 'Online Sales Data.csv' file");
        System.out.println("   - Sample data: Uses built-in sample data for demonstration");

        System.out.println("\n2. Main Features:");
        System.out.println("   - Customer Menu: Browse products, make purchases, view history");
        System.out.println("   - Employee Menu: Manager and cashier functionalities");

        System.out.println("\n3. Manager Capabilities:");
        System.out.println("   - Manage shop operations (open/close, location)");
        System.out.println("   - Hire/fire cashiers");
        System.out.println("   - Manage products and categories");
        System.out.println("   - Purchase products from warehouse to shop");
        System.out.println("   - View financial reports");

        System.out.println("\n4. Cashier Capabilities:");
        System.out.println("   - Process sales and returns");
        System.out.println("   - View sales information");

        System.out.println("\n5. Data Persistence:");
        System.out.println("   - System automatically saves data to 'marketplace_data.csv'");
        System.out.println("   - You can also manually save before exiting");
        System.out.println("=======================================");
    }

    // меню выбора нового/существующего покупателя
    private static void customerMenu() {
        while (true) {
            System.out.println("\nCustomer Menu:");
            System.out.println("1. Login as existing customer");
            System.out.println("2. Register new customer");
            System.out.println("3. Back to main menu");
            System.out.print("Select option: ");

            int choice = readIntInput(1, 3);

            switch (choice) {
                case 1:
                    loginCustomer();
                    break;
                case 2:
                    registerCustomer();
                    break;
                case 3:
                    return;
            }
        }
    }

    // выбор аккаунта покупателя
    private static void loginCustomer() {
        if (customers.isEmpty()) {
            System.out.println("No customers registered yet.");
            return;
        }

        System.out.println("\nExisting Customers:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }

        System.out.print("Select customer: ");
        int customerIndex = readIntInput(1, customers.size()) - 1;

        Customer customer = customers.get(customerIndex);
        customerActions(customer);
    }

    // добавить покупателя
    private static void registerCustomer() {
        System.out.println("\nRegister New Customer:");

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter contact info: ");
        String contact = scanner.nextLine();

        System.out.print("Enter location: ");
        String location = scanner.nextLine();

        System.out.println("Available Shops:");
        for (int i = 0; i < shops.size(); i++) {
            System.out.println((i + 1) + ". " + shops.get(i).getLocation());
        }
        System.out.print("Select shop: ");
        int shopIndex = readIntInput(1, shops.size()) - 1;

        Customer newCustomer = new Customer(name, id, contact, location, shops.get(shopIndex));
        customers.add(newCustomer);
        System.out.println("Customer registered successfully!");
    }

    // меню покупателя
    private static void customerActions(Customer customer) {
        while (true) {
            System.out.println("\nCustomer: " + customer.getName());
            System.out.println("1. View available products");
            System.out.println("2. Buy product");
            System.out.println("3. Return product");
            System.out.println("4. View purchase history");
            System.out.println("5. Update contact info");
            System.out.println("6. Back to customer menu");
            System.out.print("Select action: ");

            int choice = readIntInput(1, 6);

            switch (choice) {
                case 1:
                    customer.getShop().displayAvailableProducts();;
                    break;
                case 2:
                    buyProduct(customer);
                    break;
                case 3:
                    returnProduct(customer);
                    break;
                case 4:
                    customer.showPurchases();
                    break;
                case 5:
                    System.out.print("Enter new contact info: ");
                    String newContact = scanner.nextLine();
                    customer.updateContact(newContact);
                    break;
                case 6:
                    return;
            }
        }
    }

    // покупка взаимодействует с покупателем и магазином который к нему привязан (кассир не задействован)
    private static void buyProduct(Customer customer) {
        customer.getShop().displayAvailableProducts();;

        System.out.print("Enter product name to buy: ");
        String productName = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = readIntInput(1, Integer.MAX_VALUE);

        if (customer.buyProduct(productName, quantity) && customer.getShop().processPurchase(customer, productName, quantity)) {
            System.out.println("Purchase successful!");
        } else {
            System.out.println("Purchase failed. Please check product availability.");
        }
    }

    // возврат взаимодействует с покупателем и магазином который к нему привязан (кассир не задействован)
    private static void returnProduct(Customer customer) {
        customer.showPurchases();

        System.out.print("Enter product name to return: ");
        String productName = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = readIntInput(1, Integer.MAX_VALUE);

        if (customer.returnProduct(productName, quantity) && customer.getShop().processReturn(customer, productName, quantity)) {
            System.out.println("Return successful!");
        } else {
            System.out.println("Return failed. Please check your purchase history and shop availability");
        }
    }

    // выбор аккаунта работника
    private static void employeeMenu() {
        while (true) {
            System.out.println("\nEmployee Menu:");
            System.out.println("1. Login as manager");
            System.out.println("2. Login as cashier");
            System.out.println("3. Back to main menu");
            System.out.print("Select option: ");

            int choice = readIntInput(1, 3);

            switch (choice) {
                case 1:
                    loginManager();
                    break;
                case 2:
                    loginCashier();
                    break;
                case 3:
                    return;
            }
        }
    }

    // выбор аккаунта менеджера, ввод общего пароля
    private static void loginManager() {
        List<Manager> managers = workers.stream()
                .filter(w -> w instanceof Manager)
                .map(w -> (Manager)w)
                .collect(Collectors.toList());

        if (managers.isEmpty()) {
            System.out.println("No managers registered yet.");
            return;
        }

        System.out.println("\nManagers:");
        for (int i = 0; i < managers.size(); i++) {
            System.out.println((i + 1) + ". " + managers.get(i).getName());
        }

        System.out.print("Select manager: ");
        int managerIndex = readIntInput(1, managers.size()) - 1;

        System.out.println("Enter the password");
        String password = scanner.nextLine();

        if (password.equals(MANAGER_PASSWORD)){
            Manager manager = managers.get(managerIndex);
            managerActions(manager);
        } else {
            System.out.println("Failed to log in.");
        }

        return;
    }

    // выбор аккаунта кассира
    private static void loginCashier() {
        List<Cashier> cashiers = workers.stream()
                .filter(w -> w instanceof Cashier)
                .map(w -> (Cashier)w)
                .collect(Collectors.toList());

        if (cashiers.isEmpty()) {
            System.out.println("No cashiers registered yet.");
            return;
        }

        System.out.println("\nCashiers:");
        for (int i = 0; i < cashiers.size(); i++) {
            System.out.println((i + 1) + ". " + cashiers.get(i).getName());
        }

        System.out.print("Select cashier: ");
        int cashierIndex = readIntInput(1, cashiers.size()) - 1;

        Cashier cashier = cashiers.get(cashierIndex);
        cashierActions(cashier);
    }

    // центральное меню менеджера
    private static void managerActions(Manager manager) {
        while (true) {
            System.out.println("\nManager: " + manager.getName());
            System.out.println("1. Manage shop");
            System.out.println("2. Manage employees");
            System.out.println("3. Manage products");
            System.out.println("4. View financial reports");
            System.out.println("5. Update manager info");
            System.out.println("6. Purchase from warehouse");
            System.out.println("7. Back to employee menu");
            System.out.print("Select action: ");

            int choice = readIntInput(1, 7);

            switch (choice) {
                case 1:
                    manageShop(manager);
                    break;
                case 2:
                    manageEmployees(manager);
                    break;
                case 3:
                    manageProducts(manager);
                    break;
                case 4:
                    manager.showFinancialReport();
                    break;
                case 5:
                    updateManagerInfo(manager);
                    break;
                case 6:
                    purchaseFromWarehouse(manager);
                    break;
                case 7:
                    return;
            }
        }
    }

    // меню менеджера, процесс закупки товаров со склада
    private static void purchaseFromWarehouse(Manager manager) {
        if (warehouses.isEmpty()) {
            System.out.println("No warehouses available");
            return;
        }

        System.out.println("\nAvailable warehouses:");
        for (int i = 0; i < warehouses.size(); i++) {
            System.out.println((i + 1) + ". " + warehouses.get(i).getLocation());
        }
        System.out.print("Select warehouse: ");
        int warehouseIndex = readIntInput(1, warehouses.size()) - 1;
        Warehouse warehouse = warehouses.get(warehouseIndex);

        warehouse.showWarehouseInfo();
        
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = readIntInput(1, Integer.MAX_VALUE);

        if (manager.purchaseFromWarehouse(warehouse, productName, quantity)) {
            System.out.println("Product purchased successfully!");
        } else {
            System.out.println("Purchase failed. Please check product availability.");
        }
    }

    // меню менеджера, управление пунктом продажи
    private static void manageShop(Manager manager) {
        System.out.println("\nShop Management:");
        System.out.println("1. Open/close shop");
        System.out.println("2. Change shop location");
        System.out.println("3. View shop info");
        System.out.println("4. Back to manager menu");
        System.out.print("Select action: ");

        int choice = readIntInput(1, 4);

        switch (choice) {
            case 1:
                System.out.println("Current status: " + (manager.getWorkplace().isOpen() ? "OPEN" : "CLOSED"));
                System.out.println("1. Open shop");
                System.out.println("2. Close shop");
                System.out.print("Select action: ");
                int statusChoice = readIntInput(1, 2);
                manager.changeShopStatus(statusChoice == 1);
                break;
            case 2:
                System.out.print("Enter new location: ");
                String newLocation = scanner.nextLine();
                manager.updateShopLocation(newLocation);
                break;
            case 3:
                manager.showFullShopInfo();
                break;
            case 4:
                return;
        }
    }

    // меню менеджера, HR-менеджмент
    private static void manageEmployees(Manager manager) {
        System.out.println("\nEmployee Management:");
        System.out.println("1. Hire cashier");
        System.out.println("2. Fire cashier");
        System.out.println("3. Back to manager menu");
        System.out.print("Select action: ");

        int choice = readIntInput(1, 3);

        switch (choice) {
            case 1:
                hireCashier(manager);
                break;
            case 2:
                fireCashier(manager);
                break;
            case 3:
                return;
        }
    }

    // ответвление меню менеджера, объемные методы - нанять, уволить работника
    private static void hireCashier(Manager manager) {
        System.out.println("\nRegister New Cashier:");

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter contact info: ");
        String contact = scanner.nextLine();

        System.out.print("Enter position: ");
        String position = scanner.nextLine();

        Cashier newCashier = new Cashier(name, id, contact, position, manager.getWorkplace());
        workers.add(newCashier);
        manager.hireCashier(newCashier);
        System.out.println("Cashier hired successfully!");
    }
    private static void fireCashier(Manager manager) {
        List<Cashier> cashiers = workers.stream()
                .filter(w -> w instanceof Cashier && w.isEmployed())
                .map(w -> (Cashier)w)
                .collect(Collectors.toList());

        if (cashiers.isEmpty()) {
            System.out.println("No cashiers to fire.");
            return;
        }

        System.out.println("\nCashiers:");
        for (int i = 0; i < cashiers.size(); i++) {
            System.out.println((i + 1) + ". " + cashiers.get(i).getName());
        }

        System.out.print("Select cashier to fire: ");
        int cashierIndex = readIntInput(1, cashiers.size()) - 1;

        Cashier cashier = cashiers.get(cashierIndex);
        manager.fireWorker(cashier);
    }

    // меню менеджера, взаимодействие с продуктами и категориями
    private static void manageProducts(Manager manager) {
        System.out.println("\nProduct Management:");
        System.out.println("1. Add product");
        System.out.println("2. Remove product");
        System.out.println("3. Add category");
        System.out.println("4. Remove category");
        System.out.println("5. Back to manager menu");
        System.out.print("Select action: ");

        int choice = readIntInput(1, 5);

        switch (choice) {
            case 1:
                addProduct(manager);
                break;
            case 2:
                removeProduct(manager);
                break;
            case 3:
                System.out.print("Enter category name to add: ");
                String categoryToAdd = scanner.nextLine();
                manager.addCategory(categoryToAdd);
                break;
            case 4:
                System.out.print("Enter category name to remove: ");
                String categoryToRemove = scanner.nextLine();
                manager.removeCategory(categoryToRemove);
                break;
            case 5:
                return;
        }
    }

    // ответвление меню менеджера, из-за большого кол-ва последовательно вводимой информации это отдельный метод
    private static void addProduct(Manager manager) {
        System.out.println("\nAdd New Product:");

        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter info: ");
        String info = scanner.nextLine();

        System.out.print("Enter price: ");
        int price = readIntInput(0, Integer.MAX_VALUE);

        System.out.print("Enter quantity: ");
        int quantity = readIntInput(1, Integer.MAX_VALUE);

        Product product = new Product(category, name, info, price, quantity);
        if (manager.addProductToShop(product)) {
            System.out.println("Product added successfully!");
        } else {
            System.out.println("Failed to add product. Check category availability or shop capacity.");
        }
    }

    // ответвление меню менеджера, из-за большого кол-ва последовательно вводимой информации это отдельный метод
    private static void removeProduct(Manager manager) {
        System.out.println("\nRemove Product:");
        manager.getWorkplace().displayAvailableProducts();;

        System.out.print("Enter product name to remove: ");
        String productName = scanner.nextLine();

        System.out.print("Enter quantity to remove: ");
        int quantity = readIntInput(1, Integer.MAX_VALUE);

        if (manager.removeProductFromShop(productName, quantity)) {
            System.out.println("Product removed successfully!");
        } else {
            System.out.println("Failed to remove product. Check product availability.");
        }
    }

    // обновление информации менеджера, если на смену старого приходит новый человек
    private static void updateManagerInfo(Manager manager) {
        System.out.println("\nUpdate Manager Info:");

        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();

        System.out.print("Enter new contact info: ");
        String newContact = scanner.nextLine();

        manager.updateManagerInfo(newName, newContact);
    }

    // меню кассира
    private static void cashierActions(Cashier cashier) {
        while (true) {
            System.out.println("\nCashier: " + cashier.getName());
            System.out.println("1. Process sale");
            System.out.println("2. Process return");
            System.out.println("3. View sales info");
            System.out.println("4. Back to employee menu");
            System.out.print("Select action: ");

            int choice = readIntInput(1, 4);

            switch (choice) {
                case 1:
                    processSale(cashier);
                    break;
                case 2:
                    processReturn(cashier);
                    break;
                case 3:
                    cashier.showSalesInfo();
                    break;
                case 4:
                    return;
            }
        }
    }

    // оформление покупки из меню кассира, задействует магазин, кассира, покупателя
    private static void processSale(Cashier cashier) {
        System.out.println("\nProcess Sale:");

        System.out.println("Available Customers:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }
        System.out.print("Select customer: ");
        int customerIndex = readIntInput(1, customers.size()) - 1;
        Customer customer = customers.get(customerIndex);

        cashier.getWorkplace().displayAvailableProducts();;

        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = readIntInput(1, Integer.MAX_VALUE);

        if (cashier.sellProduct(customer, productName, quantity)) {
            System.out.println("Sale processed successfully!");
        } else {
            System.out.println("Sale failed. Check product availability or shop status.");
        }
    }

    // оформление возврата из меню кассира, задействует магазин, кассира, покупателя
    private static void processReturn(Cashier cashier) {
        System.out.println("\nProcess Return:");
        System.out.println("Customers with purchases:");
        List<Customer> customersWithPurchases = customers.stream()
                .filter(c -> !c.getPurchases().isEmpty())
                .collect(Collectors.toList());

        if (customersWithPurchases.isEmpty()) {
            System.out.println("No customers with purchases found.");
            return;
        }

        for (int i = 0; i < customersWithPurchases.size(); i++) {
            System.out.println((i + 1) + ". " + customersWithPurchases.get(i).getName());
        }

        System.out.print("Select customer: ");
        try {
            int customerIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (customerIndex >= 0 && customerIndex < customersWithPurchases.size()) {
                Customer customer = customersWithPurchases.get(customerIndex);
                customer.showPurchases();

                System.out.print("Enter product name to return: ");
                String productName = scanner.nextLine();

                System.out.print("Enter quantity to return: ");
                int quantity = Integer.parseInt(scanner.nextLine());
                if (quantity > 0) {
                    if (cashier.processReturn(customer, productName, quantity)) {
                        System.out.println("Return processed successfully!");
                    } else {
                        System.out.println("Return failed. Check customer's purchase history.");
                    }
                } else {
                    System.out.println("Quantity must be positive.");
                }
            } else {
                System.out.println("Invalid customer number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    // убрать это и заменить на считывание csv
    private static void initializeSampleData() {
        Warehouse warehouse1 = new Warehouse("Main Warehouse", 10);
        warehouse1.newAvailableCategory("Electronics");
        warehouse1.newAvailableCategory("Clothing");
        warehouse1.newAvailableCategory("Food");
        warehouses.add(warehouse1);

        Shop shop1 = new Shop("Downtown", 5, "John Doe");
        shop1.newAvailableCategory("Electronics");
        shop1.newAvailableCategory("Clothing");
        shops.add(shop1);

        Shop shop2 = new Shop("Mall", 8, "Jane Smith");
        shop2.newAvailableCategory("Electronics");
        shop2.newAvailableCategory("Food");
        shops.add(shop2);

        warehouse1.addProduct(new Product("Electronics", "Laptop", "High performance laptop", 999, 10));
        warehouse1.addProduct(new Product("Clothing", "T-Shirt", "Cotton t-shirt", 20, 50));
        warehouse1.addProduct(new Product("Food", "Chocolate", "Dark chocolate", 5, 100));

        shop1.addProduct(new Product("Electronics", "Smartphone", "Latest model", 699, 15));
        shop1.addProduct(new Product("Clothing", "Jeans", "Blue denim", 49, 30));

        shop2.addProduct(new Product("Electronics", "Headphones", "Noise cancelling", 199, 20));
        shop2.addProduct(new Product("Food", "Coffee", "Arabica beans", 8, 50));

        Manager manager1 = new Manager("John Doe", "M001", "john@example.com", "Downtown", shop1);
        Manager manager2 = new Manager("Jane Smith", "M002", "jane@example.com", "Mall", shop2);
        workers.add(manager1);
        workers.add(manager2);

        Cashier cashier1 = new Cashier("Alice Brown", "C001", "alice@example.com", "Senior Cashier", shop1);
        Cashier cashier2 = new Cashier("Bob Green", "C002", "bob@example.com", "Junior Cashier", shop2);
        workers.add(cashier1);
        workers.add(cashier2);

        customers.add(new Customer("Mike Johnson", "CU001", "mike@example.com", "Downtown", shop1));
        customers.add(new Customer("Sarah Williams", "CU002", "sarah@example.com", "Mall", shop2));
    }

    private static void initializeCSVData() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("Online Sales Data.csv"));

            // создаем/находим центральный склад
            Warehouse centralWarehouse = warehouses.isEmpty()
                    ? new Warehouse("Central Warehouse", 1000)
                    : warehouses.get(0);

            if (warehouses.isEmpty()) {
                warehouses.add(centralWarehouse);
            }

            // собираем все регионы и категории
            Set<String> allRegions = new HashSet<>();
            Set<String> allCategories = new HashSet<>();

            lines.stream()
                    .skip(1)
                    .map(line -> line.split(","))
                    .forEach(fields -> {
                        allRegions.add(fields[7].trim());
                        allCategories.add(fields[2].trim());
                    });

            // создаем магазины
            allRegions.forEach(region -> {
                if (shops.stream().noneMatch(s -> s.getLocation().equals(region))) {
                    Shop newShop = new Shop(region, 50, "Auto-generated Manager");
                    shops.add(newShop);

                    String managerName = "Manager_" + region.replace(" ", "_");
                    String managerId = "M_" + UUID.randomUUID().toString().substring(0, 6);
                    String managerContact = managerName.toLowerCase() + "@marketplace.com";

                    Manager manager = new Manager(
                            managerName,
                            managerId,
                            managerContact,
                            region,
                            newShop
                    );
                    workers.add(manager);
                }
            });

            // добавляем все категории в центральный склад
            allCategories.forEach(category -> {
                if (!centralWarehouse.availableCategories.contains(category)) {
                    centralWarehouse.newAvailableCategory(category);
                }
            });

            lines.stream()
                    .skip(1)
                    .map(line -> line.split(","))
                    .forEach(fields -> {
                        try {
                            String category = fields[2].trim();
                            String productName = fields[3].trim();
                            int quantity = Integer.parseInt(fields[4].trim());
                            double unitPrice = Double.parseDouble(fields[5].trim());
                            String productInfo = "Payment method: " + fields[8].trim();

                            Product product = new Product(
                                    category,
                                    productName,
                                    productInfo,
                                    (int) unitPrice,
                                    quantity
                            );

                            if (!centralWarehouse.addProduct(product)) {
                                System.err.println("Failed to add product to warehouse: " + productName);
                            }
                        } catch (Exception e) {
                            System.err.println("Error processing line: " + String.join(",", fields));
                            e.printStackTrace();
                        }
                    });

            System.out.println("Online sales data loaded successfully!");
            System.out.println("Total products in central warehouse: " +
                    centralWarehouse.getOccupiedUnits() + "/" + centralWarehouse.getTotalUnits());
            System.out.println("Created empty shops in regions: " + allRegions);
            System.out.println("Created managers for each shop: " + shops.size());

        } catch (IOException e) {
            System.err.println("error reading online sales data file:");
            e.printStackTrace();
        }
    }

    public static void serializeToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("TYPE,ID,NAME,CONTACT,LOCATION,POSITION,SHOP_LOCATION,IS_EMPLOYED,CATEGORY,PRODUCT_NAME,PRODUCT_INFO,PRICE,QUANTITY");

            for (Shop shop : shops) {
                writer.println(String.join(",",
                        "SHOP", "", "", "", shop.getLocation(), "", "", "", "", "", "", "", ""
                ));

                for (String category : shop.availableCategories) {
                    writer.println(String.join(",",
                            "SHOP_CATEGORY", "", "", "", shop.getLocation(), "", "", "", category, "", "", "", ""
                    ));
                }
            }

            for (Warehouse warehouse : warehouses) {
                writer.println(String.join(",",
                        "WAREHOUSE", "", warehouse.getLocation(), "", warehouse.getLocation(), "", "", "", "", "", "", "", ""
                ));

                for (String category : warehouse.availableCategories) {
                    writer.println(String.join(",",
                            "WAREHOUSE_CATEGORY", "", "", "", warehouse.getLocation(), "", "", "", category, "", "", "", ""
                    ));
                }
            }

            for (Worker worker : workers) {
                if (worker instanceof Manager) {
                    Manager manager = (Manager) worker;
                    writer.println(String.join(",",
                            "MANAGER", manager.getID(), manager.getName(), manager.getContact(),
                            manager.getLocation(), "Manager", manager.getWorkplace().getLocation(),
                            String.valueOf(manager.isEmployed()), "", "", "", "", ""
                    ));
                } else if (worker instanceof Cashier) {
                    Cashier cashier = (Cashier) worker;
                    writer.println(String.join(",",
                            "CASHIER", cashier.getID(), cashier.getName(), cashier.getContact(),
                            cashier.getLocation(), cashier.getPosition(), cashier.getWorkplace().getLocation(),
                            String.valueOf(cashier.isEmployed()), "", "", "", "", ""
                    ));
                }
            }

            for (Customer customer : customers) {
                writer.println(String.join(",",
                        "CUSTOMER", customer.getID(), customer.getName(), customer.getContact(),
                        customer.getLocation(), "", customer.getShop().getLocation(), "", "", "", "", "", ""
                ));
            }

            for (Shop shop : shops) {
                for (WarehouseUnit unit : shop.getUnits()) {
                    for (Product product : unit.getProducts()) {
                        writer.println(String.join(",",
                                "SHOP_PRODUCT", "", "", "", shop.getLocation(), "", "", "",
                                product.getCategory(), product.getName(), product.getInfo(),
                                String.valueOf(product.price), String.valueOf(product.getQuantity())
                        ));
                    }
                }
            }

            for (Warehouse warehouse : warehouses) {
                for (WarehouseUnit unit : warehouse.getUnits()) {
                    for (Product product : unit.getProducts()) {
                        writer.println(String.join(",",
                                "WAREHOUSE_PRODUCT", "", "", "", warehouse.getLocation(), "", "", "",
                                product.getCategory(), product.getName(), product.getInfo(),
                                String.valueOf(product.price), String.valueOf(product.getQuantity())
                        ));
                    }
                }
            }

            System.out.println("Data saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public static void deserializeFromCSV(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            shops.clear();
            warehouses.clear();
            workers.clear();
            customers.clear();

            reader.readLine();

            Map<String, Shop> shopMap = new HashMap<>();
            Map<String, Warehouse> warehouseMap = new HashMap<>();
            Map<String, Customer> customerMap = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length < 13) continue;

                switch (fields[0].trim()) {
                    case "SHOP":
                        Shop shop = new Shop(fields[4].trim(), 50, "Manager");
                        shopMap.put(shop.getLocation(), shop);
                        shops.add(shop);
                        break;

                    case "WAREHOUSE":
                        Warehouse warehouse = new Warehouse(fields[4].trim(), 50);
                        warehouseMap.put(warehouse.getLocation(), warehouse);
                        warehouses.add(warehouse);
                        break;

                    case "MANAGER":
                        Shop managerShop = shopMap.get(fields[6].trim());
                        if (managerShop != null) {
                            workers.add(new Manager(
                                    fields[2].trim(), fields[1].trim(), fields[3].trim(),
                                    fields[4].trim(), managerShop
                            ));
                        }
                        break;

                    case "CASHIER":
                        Shop cashierShop = shopMap.get(fields[6].trim());
                        if (cashierShop != null) {
                            Cashier cashier = new Cashier(
                                    fields[2].trim(), fields[1].trim(), fields[3].trim(),
                                    fields[5].trim(), cashierShop
                            );
                            cashier.setEmployed(Boolean.parseBoolean(fields[7].trim()));
                            workers.add(cashier);
                        }
                        break;

                    case "CUSTOMER":
                        Shop customerShop = shopMap.get(fields[6].trim());
                        if (customerShop != null) {
                            Customer customer = new Customer(
                                    fields[2].trim(), fields[1].trim(), fields[3].trim(),
                                    fields[4].trim(), customerShop
                            );
                            customers.add(customer);
                        }
                        break;

                    case "SHOP_CATEGORY":
                        Shop categoryShop = shopMap.get(fields[4].trim());
                        if (categoryShop != null) {
                            categoryShop.newAvailableCategory(fields[8].trim());
                        }
                        break;

                    case "WAREHOUSE_CATEGORY":
                        Warehouse categoryWarehouse = warehouseMap.get(fields[4].trim());
                        if (categoryWarehouse != null) {
                            categoryWarehouse.newAvailableCategory(fields[8].trim());
                        }
                        break;

                    case "SHOP_PRODUCT":
                        Shop productShop = shopMap.get(fields[4].trim());
                        if (productShop != null) {
                            productShop.addProduct(new Product(
                                    fields[8].trim(), fields[9].trim(), fields[10].trim(),
                                    Integer.parseInt(fields[11].trim()), Integer.parseInt(fields[12].trim())
                            ));
                        }
                        break;

                    case "WAREHOUSE_PRODUCT":
                        Warehouse productWarehouse = warehouseMap.get(fields[4].trim());
                        if (productWarehouse != null) {
                            productWarehouse.addProduct(new Product(
                                    fields[8].trim(), fields[9].trim(), fields[10].trim(),
                                    Integer.parseInt(fields[11].trim()), Integer.parseInt(fields[12].trim())
                            ));
                        }
                        break;
                }
            }
            System.out.println("Data loaded from " + filename);
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            System.exit(0);
        }
    }

    // отдельный метод для проверки числового инпута
    private static int readIntInput(int min, int max) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}