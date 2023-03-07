import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class Task implements Callable<StringBuffer> {
    private final static String USER_AGENT = "Chrome/104.0.0.0";
    private String link;

    public Task(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    @Override
    public StringBuffer call() throws Exception {
        return parsingCyrillicWords(getLink());
    }

    public static String getURLData(String link) throws IOException {
        URL urlObject = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = connection.getResponseCode();
        if (responseCode == 404) {
            throw new IllegalArgumentException();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
    public static StringBuffer parsingCyrillicWords(String link) throws IOException {
        String result = getURLData(link);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length(); i++) {
            if(Character.UnicodeBlock.of(result.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC)){
                if(result.charAt(i+1)==' ' || result.charAt(i+1)=='-' || result.charAt(i+1)=='‑'){
                    sb.append(result.charAt(i) + " ");
                }
                else if(result.charAt(i-1)==0 && result.charAt(i+1)==0){
                    sb.append(result.charAt(i) + " ");
                }
                else if(result.charAt(i+1)=='.' || result.charAt(i+1)==',' || result.charAt(i+1)=='?'){
                    sb.append(result.charAt(i) + " ");
                }
                else{
                    sb.append(result.charAt(i));
                }
            }
        }
        return sb;
    }
//    public static Map<String, List<StringBuffer>> getURLData(List<String> links) throws IOException {
//        List<StringBuffer> listLinks = new LinkedList<>();
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
//                listLinks.add(response);
//            }
//            map.put(link, listLinks);
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
