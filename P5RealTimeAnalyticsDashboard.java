import java.util.*;

class PageEvent {

    String url;
    String userId;
    String source;

    public PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class P5RealTimeAnalyticsDashboard {

    // Page view counts
    private HashMap<String, Integer> pageViews;

    // Unique visitors
    private HashMap<String, Set<String>> uniqueVisitors;

    // Traffic source counts
    private HashMap<String, Integer> trafficSources;

    public P5RealTimeAnalyticsDashboard() {

        pageViews = new HashMap<>();
        uniqueVisitors = new HashMap<>();
        trafficSources = new HashMap<>();
    }

    // Process incoming event
    public void processEvent(PageEvent event) {

        // Count page views
        pageViews.put(event.url,
                pageViews.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Track traffic sources
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    // Get Top 10 Pages
    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<Map.Entry<String, Integer>> topPages = new ArrayList<>();

        int count = 0;

        while (!pq.isEmpty() && count < 10) {
            topPages.add(pq.poll());
            count++;
        }

        return topPages;
    }

    // Display Dashboard
    public void getDashboard() {

        System.out.println("\n===== REAL-TIME ANALYTICS DASHBOARD =====");

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(rank + ". " + url +
                    " - " + views + " views (" + unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int totalTraffic = 0;

        for (int count : trafficSources.values()) {
            totalTraffic += count;
        }

        for (String source : trafficSources.keySet()) {

            int count = trafficSources.get(source);

            double percentage = (count * 100.0) / totalTraffic;

            System.out.printf("%s: %.2f%%\n", source, percentage);
        }
    }

    public static void main(String[] args) {

        P5RealTimeAnalyticsDashboard dashboard =
                new P5RealTimeAnalyticsDashboard();

        // Simulated events
        dashboard.processEvent(new PageEvent(
                "/article/breaking-news", "user_123", "google"));

        dashboard.processEvent(new PageEvent(
                "/article/breaking-news", "user_456", "facebook"));

        dashboard.processEvent(new PageEvent(
                "/sports/championship", "user_123", "direct"));

        dashboard.processEvent(new PageEvent(
                "/sports/championship", "user_789", "google"));

        dashboard.processEvent(new PageEvent(
                "/sports/championship", "user_456", "google"));

        dashboard.processEvent(new PageEvent(
                "/tech/ai-future", "user_999", "twitter"));

        dashboard.processEvent(new PageEvent(
                "/tech/ai-future", "user_123", "google"));

        dashboard.processEvent(new PageEvent(
                "/tech/ai-future", "user_321", "facebook"));

        // Display dashboard
        dashboard.getDashboard();
    }
}