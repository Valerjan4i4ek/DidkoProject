import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TaskRunnable implements Runnable{
    private List<String> listLinks;

    public TaskRunnable(List<String> listLinks) {
        this.listLinks = listLinks;
    }

    @Override
    public void run() {
        try {
            if (listLinks != null && !listLinks.isEmpty()){
                System.out.println("Start " + Thread.currentThread().getId());
                Thread.sleep(1000);
                System.out.println("Finish " + Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
