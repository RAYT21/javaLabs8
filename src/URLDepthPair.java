import java.net.MalformedURLException;
import java.net.URL;

public class URLDepthPair {
    private final String currURL;
    private final int currDepth;

    public URLDepthPair(String URL, int depth) {
        currURL = URL;
        currDepth = depth;
    }

    public String getURL() {
        return currURL;
    }

    public int getDepth() {
        return currDepth;
    }

    @Override
    public String toString() {
        return Integer.toString(currDepth) + '\t' + currURL;
    }

    public String getDocPath() {
        try {
            URL url = new URL(currURL);
            return url.getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getWebHost() {
        try {
            URL url = new URL(currURL);
            return url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
