import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrawlerTask implements Runnable {
    private URLPool pool;

    public CrawlerTask (URLPool newPool) {
        pool = newPool;
    }

    @Override
    public void run() {
        URLDepthPair depthPair = pool.get();
        int depth = depthPair.getDepth();
        LinkedList<String> linksList = Crawler.getAllLinks(depthPair);


        for (String newURL : linksList) {
            URLDepthPair newDepthPair = new URLDepthPair(newURL, depth + 1);
            pool.put(newDepthPair);
        }
    }
}
