import java.util.*;

class DNSEntry {

    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class P3DNSCacheSystem {

    // Cache with LRU eviction
    private LinkedHashMap<String, DNSEntry> cache;

    private int capacity;

    private int cacheHits = 0;
    private int cacheMisses = 0;

    public P3DNSCacheSystem(int capacity) {

        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {

            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > P3DNSCacheSystem.this.capacity;
            }
        };
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        long startTime = System.nanoTime();

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {

                cacheHits++;

                long endTime = System.nanoTime();

                System.out.println("Cache HIT → " + entry.ipAddress +
                        " (retrieved in " + (endTime - startTime) / 1_000_000.0 + " ms)");

                return entry.ipAddress;
            }

            // Expired entry
            cache.remove(domain);
            System.out.println("Cache EXPIRED for " + domain);
        }

        // Cache miss
        cacheMisses++;

        String ip = queryUpstreamDNS(domain);

        DNSEntry newEntry = new DNSEntry(domain, ip, 10); // TTL = 10 seconds for demo

        cache.put(domain, newEntry);

        System.out.println("Cache MISS → Query upstream → " + ip + " (TTL: 10s)");

        return ip;
    }

    // Simulate upstream DNS query
    private String queryUpstreamDNS(String domain) {

        Random rand = new Random();

        return "172.217." + rand.nextInt(255) + "." + rand.nextInt(255);
    }

    // Remove expired entries
    public void cleanupExpiredEntries() {

        Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<String, DNSEntry> entry = iterator.next();

            if (entry.getValue().isExpired()) {
                iterator.remove();
            }
        }
    }

    // Cache statistics
    public void getCacheStats() {

        int total = cacheHits + cacheMisses;

        double hitRate = total == 0 ? 0 : (cacheHits * 100.0 / total);

        System.out.println("\nCache Statistics:");
        System.out.println("Cache Hits: " + cacheHits);
        System.out.println("Cache Misses: " + cacheMisses);
        System.out.println("Hit Rate: " + hitRate + "%");
        System.out.println("Cache Size: " + cache.size());
    }

    public static void main(String[] args) {

        P3DNSCacheSystem dnsCache = new P3DNSCacheSystem(5);

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== DNS Cache System =====");
            System.out.println("1. Resolve Domain");
            System.out.println("2. Cleanup Expired Entries");
            System.out.println("3. Show Cache Stats");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:

                    System.out.print("Enter domain: ");
                    String domain = sc.nextLine();

                    dnsCache.resolve(domain);
                    break;

                case 2:

                    dnsCache.cleanupExpiredEntries();
                    System.out.println("Expired entries removed.");
                    break;

                case 3:

                    dnsCache.getCacheStats();
                    break;

                case 4:

                    System.out.println("Exiting...");
                    sc.close();
                    return;

                default:

                    System.out.println("Invalid choice");
            }
        }
    }
}