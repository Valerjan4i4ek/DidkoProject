import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RemoteWordsParsingServer implements WordsParsing{
    private final static String USER_AGENT = "Chrome/104.0.0.0";

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

    public StringBuffer parsingCyrillicWords(String link) throws IOException {
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

    public static Map<String, Words> addWord(StringBuffer stringBuffer, String link){
        Map<String, Words> map = new HashMap<>();
        String word = stringBuffer.toString().toLowerCase();
        String[] words = word.split(" ");
        for(String s : words){
            if(map != null && !map.isEmpty()){
                if(map.containsKey(s)){
                    map.replace(s, new Words(map.get(s).getId(), s, map.get(s).getWordCount()+1, link));
                }
                else {
                    map.put(s, new Words(map.size() + 1, s, 1, link));
                }
            }
            else{
                map.put(s, new Words(1, s, 1, link));
            }
        }
        return map;
    }

    @Override
    public Map<String, Words> returnCyrillicWords(String link) throws RemoteException, IOException {
        return addWord(parsingCyrillicWords(link), link);
    }

    @Override
    public Map<Integer, String> getLinkByWord(Map<String, Words> map, String word) throws RemoteException {
        Map<Integer, String> returnMap = new TreeMap<>();
        if (map.containsKey(word)){
            returnMap.put(map.get(word).getWordCount(), map.get(word).getLink());
        }

        return returnMap;
    }
}
