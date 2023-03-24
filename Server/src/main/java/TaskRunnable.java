import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskRunnable implements Runnable{
    private final List<String> listLinks;

    public TaskRunnable(List<String> listLinks) {
        this.listLinks = listLinks;
    }

    @Override
    public void run() {
        try {
            if (listLinks != null && !listLinks.isEmpty()){
                System.out.println("list is not empty yet");

                Thread.sleep(1000);
            }
            else {
                System.out.println("final/ list is empty");

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
