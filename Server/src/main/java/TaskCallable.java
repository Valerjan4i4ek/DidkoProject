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
        return list;
    }

    public List<WordsAndLinks> parsingCyrillicWords(List<String> listLinks) throws IOException{
        List<WordsAndLinks> list = getURLData(listLinks);
        List<WordsAndLinks> returnList = new LinkedList<>();
        StringBuffer sb = new StringBuffer();
        for(WordsAndLinks wordsAndLinks : list){
            sb = wordsAndLinks.getStringBuffer();
            for (int i = 0; i < sb.length(); i++) {
                if(Character.UnicodeBlock.of(sb.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC)){
                    if(sb.charAt(i+1)==' ' || sb.charAt(i+1)=='-' || sb.charAt(i+1)=='‑'){
                        sb.append(sb.charAt(i) + " ");
                        returnList.add(new WordsAndLinks(wordsAndLinks.getLink(), sb));
                    }
                    else if(sb.charAt(i-1)==0 && sb.charAt(i+1)==0){
                        sb.append(sb.charAt(i) + " ");
                        returnList.add(new WordsAndLinks(wordsAndLinks.getLink(), sb));
                    }
                    else if(sb.charAt(i+1)=='.' || sb.charAt(i+1)==',' || sb.charAt(i+1)=='?'){
                        sb.append(sb.charAt(i) + " ");
                        returnList.add(new WordsAndLinks(wordsAndLinks.getLink(), sb));
                    }
                    else{
                        sb.append(sb.charAt(i));
                        returnList.add(new WordsAndLinks(wordsAndLinks.getLink(), sb));
                    }
                }
            }
        }
        return returnList;
    }



//    public static String getURLData(String link) throws IOException {
//        URL urlObject = new URL(link);
//        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
//        connection.setRequestMethod("GET");
//        connection.setRequestProperty("User-Agent", USER_AGENT);
//
//        int responseCode = connection.getResponseCode();
//        if (responseCode == 404) {
//            throw new IllegalArgumentException();
//        }
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//        return response.toString();
//    }
//    public static StringBuffer parsingCyrillicWords(String link) throws IOException {
//        String result = getURLData(link);
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < result.length(); i++) {
//            if(Character.UnicodeBlock.of(result.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC)){
//                if(result.charAt(i+1)==' ' || result.charAt(i+1)=='-' || result.charAt(i+1)=='‑'){
//                    sb.append(result.charAt(i) + " ");
//                }
//                else if(result.charAt(i-1)==0 && result.charAt(i+1)==0){
//                    sb.append(result.charAt(i) + " ");
//                }
//                else if(result.charAt(i+1)=='.' || result.charAt(i+1)==',' || result.charAt(i+1)=='?'){
//                    sb.append(result.charAt(i) + " ");
//                }
//                else{
//                    sb.append(result.charAt(i));
//                }
//            }
//        }
//        return sb;
//    }

//    public static Map<String, List<StringBuffer>> getURLData(List<String> links) throws IOException {
//        List<StringBuffer> parsingTextFromLinkList = new LinkedList<>();
//        Map<String, List<StringBuffer>> map = new LinkedHashMap<>();
//        for(String link : links){
//            URL urlObject = new URL(link);
//            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("User-Agent", USER_AGENT);
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode == 404) {
//                throw new IllegalArgumentException();
//            }
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//                parsingTextFromLinkList.add(response);
//            }
//            map.put(link, parsingTextFromLinkList);
//            in.close();
//        }
//
//        return map;
//    }


//    public Map<String, List<StringBuffer>> parsingCyrillicWords(List<String> links) throws IOException {
//        Map<String, List<StringBuffer>> map = getURLData(links);
//        Map<String, List<StringBuffer>> returnMap = new LinkedHashMap<>();
//        List<StringBuffer> list = new ArrayList<>();
//        StringBuffer sb = new StringBuffer();
//        for(Map.Entry<String, List<StringBuffer>> entry : map.entrySet()){
//            for(StringBuffer result : entry.getValue()){
//                for (int i = 0; i < result.length(); i++) {
//                    if(Character.UnicodeBlock.of(result.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC)){
//                        if(result.charAt(i+1)==' ' || result.charAt(i+1)=='-' || result.charAt(i+1)=='‑'){
//                            sb.append(result.charAt(i) + " ");
//                            list.add(sb);
//                            returnMap.put(entry.getKey(), list);
//                        }
//                        else if(result.charAt(i-1)==0 && result.charAt(i+1)==0){
//                            sb.append(result.charAt(i) + " ");
//                            list.add(sb);
//                            returnMap.put(entry.getKey(), list);
//                        }
//                        else if(result.charAt(i+1)=='.' || result.charAt(i+1)==',' || result.charAt(i+1)=='?'){
//                            sb.append(result.charAt(i) + " ");
//                            list.add(sb);
//                            returnMap.put(entry.getKey(), list);
//                        }
//                        else{
//                            sb.append(result.charAt(i));
//                            list.add(sb);
//                            returnMap.put(entry.getKey(), list);
//                        }
//                    }
//                }
//            }
//        }
//        return returnMap;
//    }

}
