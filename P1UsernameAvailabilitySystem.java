import java.util.*;

public class P1UsernameAvailabilitySystem {

    // Stores registered usernames
    private HashMap<String, Integer> usernames;

    // Stores attempt frequency
    private HashMap<String, Integer> attemptCount;

    public P1UsernameAvailabilitySystem() {
        usernames = new HashMap<>();
        attemptCount = new HashMap<>();
    }

    // Register a username
    public void registerUser(String username, int userId) {
        usernames.put(username, userId);
    }

    // Check if username is available
    public boolean checkAvailability(String username) {

        // Update attempt frequency
        attemptCount.put(username, attemptCount.getOrDefault(username, 0) + 1);

        return !usernames.containsKey(username);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        // Append numbers
        for (int i = 1; i <= 5; i++) {
            String newUsername = username + i;

            if (!usernames.containsKey(newUsername)) {
                suggestions.add(newUsername);
            }
        }

        // Replace underscore with dot
        String modified = username.replace("_", ".");

        if (!usernames.containsKey(modified)) {
            suggestions.add(modified);
        }

        // Add year
        String yearSuggestion = username + "2026";

        if (!usernames.containsKey(yearSuggestion)) {
            suggestions.add(yearSuggestion);
        }

        return suggestions;
    }

    // Find most attempted username
    public String getMostAttempted() {

        String mostAttempted = "";
        int maxAttempts = 0;

        for (Map.Entry<String, Integer> entry : attemptCount.entrySet()) {

            if (entry.getValue() > maxAttempts) {
                maxAttempts = entry.getValue();
                mostAttempted = entry.getKey();
            }

        }

        return mostAttempted + " (" + maxAttempts + " attempts)";
    }

    // Display all registered users
    public void displayUsers() {

        System.out.println("\nRegistered Users:");

        for (Map.Entry<String, Integer> entry : usernames.entrySet()) {
            System.out.println(entry.getKey() + " -> UserID: " + entry.getValue());
        }
    }

    // Main function for testing
    public static void main(String[] args) {

        P1UsernameAvailabilitySystem system = new P1UsernameAvailabilitySystem();

        // Pre-register some usernames
        system.registerUser("john_doe", 101);
        system.registerUser("admin", 102);
        system.registerUser("alex123", 103);

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== Username Availability System =====");
            System.out.println("1. Check Username Availability");
            System.out.println("2. Register Username");
            System.out.println("3. Suggest Alternatives");
            System.out.println("4. Most Attempted Username");
            System.out.println("5. Display Users");
            System.out.println("6. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:

                    System.out.print("Enter username to check: ");
                    String username = sc.nextLine();

                    boolean available = system.checkAvailability(username);

                    if (available) {
                        System.out.println("Username is available.");
                    } else {
                        System.out.println("Username already taken.");
                    }

                    break;

                case 2:

                    System.out.print("Enter username to register: ");
                    String newUser = sc.nextLine();

                    if (system.checkAvailability(newUser)) {

                        int userId = system.usernames.size() + 1;
                        system.registerUser(newUser, userId);

                        System.out.println("User registered successfully.");
                    } else {

                        System.out.println("Username already taken.");
                    }

                    break;

                case 3:

                    System.out.print("Enter username for suggestions: ");
                    String suggestUser = sc.nextLine();

                    List<String> suggestions = system.suggestAlternatives(suggestUser);

                    System.out.println("Suggested usernames:");
                    for (String s : suggestions) {
                        System.out.println(s);
                    }

                    break;

                case 4:

                    System.out.println("Most attempted username: " + system.getMostAttempted());
                    break;

                case 5:

                    system.displayUsers();
                    break;

                case 6:

                    System.out.println("Exiting...");
                    sc.close();
                    return;

                default:

                    System.out.println("Invalid choice.");
            }
        }
    }
}