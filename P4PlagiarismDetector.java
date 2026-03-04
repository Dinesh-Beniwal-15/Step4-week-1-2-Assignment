import java.util.*;

public class P4PlagiarismDetector {

    // n-gram -> set of document IDs
    private HashMap<String, Set<String>> ngramIndex;

    // document -> list of ngrams
    private HashMap<String, List<String>> documentNgrams;

    private int N = 5; // using 5-grams

    public P4PlagiarismDetector() {
        ngramIndex = new HashMap<>();
        documentNgrams = new HashMap<>();
    }

    // Break text into n-grams
    private List<String> generateNgrams(String text) {

        List<String> ngrams = new ArrayList<>();

        text = text.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "");

        String[] words = text.split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            ngramIndex.putIfAbsent(gram, new HashSet<>());

            ngramIndex.get(gram).add(docId);
        }

        System.out.println("Document " + docId + " indexed with " + ngrams.size() + " n-grams");
    }

    // Analyze document for plagiarism
    public void analyzeDocument(String docId) {

        if (!documentNgrams.containsKey(docId)) {
            System.out.println("Document not found.");
            return;
        }

        List<String> ngrams = documentNgrams.get(docId);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (ngramIndex.containsKey(gram)) {

                for (String otherDoc : ngramIndex.get(gram)) {

                    if (!otherDoc.equals(docId)) {

                        matchCount.put(otherDoc,
                                matchCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }

        System.out.println("\nAnalyzing document: " + docId);
        System.out.println("Extracted " + ngrams.size() + " n-grams\n");

        for (String otherDoc : matchCount.keySet()) {

            int matches = matchCount.get(otherDoc);

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + otherDoc + "\"");

            System.out.printf("Similarity: %.2f%%", similarity);

            if (similarity > 60) {
                System.out.println("  (PLAGIARISM DETECTED)");
            } else if (similarity > 10) {
                System.out.println("  (Suspicious)");
            } else {
                System.out.println();
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        P4PlagiarismDetector detector = new P4PlagiarismDetector();

        // Sample documents
        String doc1 = "Artificial intelligence is transforming technology and society. "
                + "Machine learning allows computers to learn from data.";

        String doc2 = "Artificial intelligence is transforming technology and society. "
                + "Machine learning systems learn patterns from data.";

        String doc3 = "Football is a popular sport played around the world.";

        detector.addDocument("essay_089.txt", doc1);
        detector.addDocument("essay_092.txt", doc2);
        detector.addDocument("essay_123.txt", doc3);

        detector.analyzeDocument("essay_123.txt");
    }
}