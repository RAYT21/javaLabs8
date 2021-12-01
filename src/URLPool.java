import java.util.ArrayList;
import java.util.LinkedList;

public class URLPool {
    private final LinkedList<URLDepthPair> pendingURLs;
    public LinkedList<URLDepthPair> processedURLs;
    private final ArrayList<String> seenURLs = new ArrayList<>();
    private int waitingThreads;
    private int maxDepth;

    public URLPool(int maxDepthPair) {
        maxDepth = maxDepthPair;
        waitingThreads = 0;
        pendingURLs = new LinkedList<>();
        processedURLs = new LinkedList<>();
    }

    public synchronized int getWaitThreads() {
        return waitingThreads;
    }

    public synchronized void put(URLDepthPair depthPair) {
        if (waitingThreads != 0) {
            waitingThreads--;
            this.notify();
        }

        if (!seenURLs.contains(depthPair.getURL()) & !pendingURLs.contains(depthPair)) {
            if (depthPair.getDepth() < maxDepth) {
                pendingURLs.add(depthPair);
            } else {
                processedURLs.add(depthPair);
                seenURLs.add(depthPair.getURL());
            }
        }
    }

    public synchronized URLDepthPair get() {
        URLDepthPair depthPair;
        while (pendingURLs.isEmpty()) {
            waitingThreads++;
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        depthPair = pendingURLs.pop();
        while (seenURLs.contains(depthPair.getURL())) {
            depthPair = pendingURLs.pop();
        }

        processedURLs.add(depthPair);
        seenURLs.add(depthPair.getURL());

        return depthPair;
    }
}
