import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        args = input.split(" ");

        int maxDepthPair = 0;
        int numThreads = 0;
        if (args.length != 3) {
            System.out.println("Формат входных данных: <URL> <depth> <number of crawler threads>");
            System.exit(1);
        }

        try {
            maxDepthPair = Integer.parseInt(args[1]);
            numThreads = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
            System.out.println("Формат входных данных: <URL> <depth> <number of crawler threads>");
            System.exit(1);
        }

        URLDepthPair currentDepthPair = new URLDepthPair(args[0], 0);
        URLPool pool = new URLPool(maxDepthPair);
        pool.put(currentDepthPair);

        int initialActiveThreads = Thread.activeCount();
        while (pool.getWaitThreads() != numThreads) {
            if (Thread.activeCount() - initialActiveThreads < numThreads) {
                CrawlerTask crawler = new CrawlerTask(pool);
                new Thread(crawler).start();
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < pool.processedURLs.size(); i++){
            System.out.println(pool.processedURLs.get(i));
        }

        System.exit(0);
    }

    public static LinkedList<String> getAllLinks(URLDepthPair depthPair) {
        LinkedList<String> URLs = new LinkedList<>();
        Socket sock;
        try {
            sock = new Socket(depthPair.getWebHost(), 80);
        } catch (IOException e) {
            e.printStackTrace();
            return URLs;
        }


        try {
            sock.setSoTimeout(3000);
        } catch (SocketException e) {
            e.printStackTrace();
            return URLs;
        }

        String docPath = depthPair.getDocPath();
        String webHost = depthPair.getWebHost();

        OutputStream outStream;
        try {
            outStream = sock.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return URLs;
        }

        PrintWriter mWriter = new PrintWriter(outStream, true);
        if (docPath.length() == 0) {
            mWriter.println("GET / HTTP/1.1");
            mWriter.println("Host: " + webHost);
            mWriter.println("Connection: close");
            mWriter.println();
        } else {
            mWriter.println("GET " + docPath + " HTTP/1.1");
            mWriter.println("Host: " + webHost);
            mWriter.println("Connection: close");
            mWriter.println();
        }

        InputStream inStream;
        try {
            inStream = sock.getInputStream();
        } catch (IOException e){
            e.printStackTrace();
            return URLs;
        }

        InputStreamReader inStreamReader = new InputStreamReader(inStream);
        BufferedReader BuffReader = new BufferedReader(inStreamReader);

        while (true) {
            String line;
            try {
                line = BuffReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return URLs;
            }

            if (line == null) {
                break;
            }

            Pattern patternURL = Pattern.compile(
                    "<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1");

            Matcher matcherURL = patternURL.matcher(line);
            while (matcherURL.find()) {
                // обрезаем кавычки
                String newLink = line.substring(matcherURL.start() + 1, matcherURL.end() - 1);
                URLs.add(newLink);
            }
        }

        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return URLs;
    }
}
