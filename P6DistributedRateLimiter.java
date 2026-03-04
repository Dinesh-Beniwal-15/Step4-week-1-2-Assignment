import java.util.*;

class TokenBucket {

    private int maxTokens;
    private int tokens;
    private long lastRefillTime;
    private int refillRate; // tokens added per hour

    public TokenBucket(int maxTokens, int refillRate) {

        this.maxTokens = maxTokens;
        this.tokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // Refill tokens based on elapsed time
    private void refill() {

        long currentTime = System.currentTimeMillis();

        long elapsedTime = currentTime - lastRefillTime;

        long tokensToAdd = (elapsedTime * refillRate) / (3600 * 1000);

        if (tokensToAdd > 0) {

            tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);

            lastRefillTime = currentTime;
        }
    }

    // Check if request is allowed
    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {

            tokens--;
            return true;
        }

        return false;
    }

    public int getRemainingTokens() {
        return tokens;
    }
}

public class P6DistributedRateLimiter {

    // clientId -> token bucket
    private HashMap<String, TokenBucket> clients;

    private int maxRequests = 1000;

    public P6DistributedRateLimiter() {
        clients = new HashMap<>();
    }

    // Rate limit check
    public synchronized void checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId,
                new TokenBucket(maxRequests, maxRequests));

        TokenBucket bucket = clients.get(clientId);

        boolean allowed = bucket.allowRequest();

        if (allowed) {

            System.out.println("Allowed (" +
                    bucket.getRemainingTokens() +
                    " requests remaining)");
        } else {

            System.out.println("Denied (Rate limit exceeded)");
        }
    }

    // Status of a client
    public void getRateLimitStatus(String clientId) {

        if (!clients.containsKey(clientId)) {

            System.out.println("Client not found");
            return;
        }

        TokenBucket bucket = clients.get(clientId);

        int used = maxRequests - bucket.getRemainingTokens();

        System.out.println("Rate Limit Status:");
        System.out.println("Used: " + used);
        System.out.println("Limit: " + maxRequests);
        System.out.println("Remaining: " + bucket.getRemainingTokens());
    }

    public static void main(String[] args) {

        P6DistributedRateLimiter limiter = new P6DistributedRateLimiter();

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== API Rate Limiter =====");
            System.out.println("1. Send API Request");
            System.out.println("2. Check Rate Limit Status");
            System.out.println("3. Exit");

            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:

                    System.out.print("Enter Client ID: ");
                    String clientId = sc.nextLine();

                    limiter.checkRateLimit(clientId);
                    break;

                case 2:

                    System.out.print("Enter Client ID: ");
                    String id = sc.nextLine();

                    limiter.getRateLimitStatus(id);
                    break;

                case 3:

                    System.out.println("Exiting...");
                    sc.close();
                    return;

                default:

                    System.out.println("Invalid choice");
            }
        }
    }
}