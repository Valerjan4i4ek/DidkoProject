import org.w3c.dom.ls.LSOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class TaskCallable implements Callable<List<WordsAndLinks>> {
    private final static String USER_AGENT = "Chrome/104.0.0.0";
//    private String link;
//
//    public Task(String link) {
//        this.link = link;
//    }
//public String getLink() {
//    return link;
//}
    private List<String> links;

    public TaskCallable(List<String> links) {
        this.links = links;
    }

    public List<String> getLinks() {
        return links;
    }

    @Override
    public List<WordsAndLinks> call() throws Exception {
        return parsingCyrillicWords(getLinks());
    }
    public static List<WordsAndLinks> getURLData(List<String> listLinks) throws IOException{
        List<WordsAndLinks> list = new LinkedList<>();
        for(String link : listLinks){
            URL urlObject = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = connection.getResponseCode();
            if(responseCode == 404){
                throw new IllegalArgumentException();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            list.add(new WordsAndLinks(link, response));
            in.close();
        }
        System.out.println("return url list");
        return list;
    }

    public List<WordsAndLinks> parsingCyrillicWords(List<String> listLinks) throws IOException{
        List<WordsAndLinks> list = getURLData(listLinks);
        List<WordsAndLinks> returnList = new LinkedList<>();
        StringBuffer sb = new StringBuffer();
        StringBuffer result = new StringBuffer();

        for(WordsAndLinks wordsAndLinks : list){
            System.out.println("start 1 cycle in pars method");
            result = wordsAndLinks.getStringBuffer();

            for (int i = 0; i < result.length(); i++) {
//                System.out.println("start 2 cycle in pars method");
                if(Character.UnicodeBlock.of(result.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC)){
                    if(result.charAt(i+1)==' ' || result.charAt(i+1)=='-' || result.charAt(i+1)=='‑'){
                        sb.append(result.charAt(i) + " ");
                        returnList.add(new WordsAndLinks(wordsAndLinks.getLink(), sb));
                    }
                    else if(result.charAt(i-1)==0 && result.charAt(i+1)==0){
                        sb.append(result.charAt(i) + " ");
                        returnList.add(new WordsAndLinks(wordsAndLinks.getLink(), sb));
                    }
                    else if(result.charAt(i+1)=='.' || result.charAt(i+1)==',' || result.charAt(i+1)=='?'){
                        sb.append(result.charAt(i) + " ");
                        returnList.add(new WordsAndLinks(wordsAndLinks.getLink(), sb));
                    }
                    else{
                        sb.append(result.charAt(i));
                        returnList.add(new WordsAndLinks(wordsAndLinks.getLink(), sb));
                    }
                }
            }
            System.out.println("finish 1 cycle in pars method");
        }
        System.out.println("return pars list");
        return returnList;
    }
}
