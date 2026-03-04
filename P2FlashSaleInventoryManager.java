import java.util.*;

public class P2FlashSaleInventoryManager {

    // Product stock storage
    private HashMap<String, Integer> inventory;

    // Waiting list (FIFO)
    private LinkedHashMap<String, Queue<Integer>> waitingList;

    public P2FlashSaleInventoryManager() {
        inventory = new HashMap<>();
        waitingList = new LinkedHashMap<>();
    }

    // Add product to inventory
    public void addProduct(String productId, int stock) {
        inventory.put(productId, stock);
        waitingList.put(productId, new LinkedList<>());
    }

    // Check stock availability
    public int checkStock(String productId) {

        if (!inventory.containsKey(productId)) {
            System.out.println("Product not found.");
            return -1;
        }

        return inventory.get(productId);
    }

    // Purchase item (Thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        if (!inventory.containsKey(productId)) {
            return "Product not found";
        }

        int stock = inventory.get(productId);

        // If stock available
        if (stock > 0) {

            inventory.put(productId, stock - 1);

            return "Success: User " + userId +
                   " purchased item. Remaining stock: " + (stock - 1);
        }

        // Add to waiting list
        Queue<Integer> queue = waitingList.get(productId);
        queue.add(userId);

        return "Stock finished. User " + userId +
               " added to waiting list. Position #" + queue.size();
    }

    // Show waiting list
    public void showWaitingList(String productId) {

        if (!waitingList.containsKey(productId)) {
            System.out.println("Product not found.");
            return;
        }

        Queue<Integer> queue = waitingList.get(productId);

        if (queue.isEmpty()) {
            System.out.println("Waiting list is empty.");
        } else {
            System.out.println("Waiting list users: " + queue);
        }
    }

    // Main function for demo
    public static void main(String[] args) {

        P2FlashSaleInventoryManager system = new P2FlashSaleInventoryManager();

        // Add product with limited stock
        system.addProduct("IPHONE15_256GB", 5);

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== Flash Sale Inventory System =====");
            System.out.println("1. Check Stock");
            System.out.println("2. Purchase Item");
            System.out.println("3. Show Waiting List");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {

                case 1:

                    System.out.print("Enter Product ID: ");
                    String productId = sc.next();

                    int stock = system.checkStock(productId);

                    if (stock >= 0) {
                        System.out.println("Available stock: " + stock);
                    }

                    break;

                case 2:

                    System.out.print("Enter Product ID: ");
                    String pid = sc.next();

                    System.out.print("Enter User ID: ");
                    int userId = sc.nextInt();

                    String result = system.purchaseItem(pid, userId);

                    System.out.println(result);

                    break;

                case 3:

                    System.out.print("Enter Product ID: ");
                    String product = sc.next();

                    system.showWaitingList(product);

                    break;

                case 4:

                    System.out.println("Exiting...");
                    sc.close();
                    return;

                default:

                    System.out.println("Invalid choice.");
            }
        }
    }
}