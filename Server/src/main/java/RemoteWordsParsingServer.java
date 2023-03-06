import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;

public class RemoteWordsParsingServer implements WordsParsing {
    private final static String USER_AGENT = "Chrome/104.0.0.0";
    static MySQLClass sql = new MySQLClass();
    static WordsCache wordsCache = new WordsCache();
    ExecutorService executorService = Executors.newFixedThreadPool(3);
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
        Words wordAddToDatabase;
        for(String s : words){
            if(map != null && !map.isEmpty()){
                if(map.containsKey(s) && map.get(s).getLink().equals(link)){
                    wordAddToDatabase = new Words(map.get(s).getId(), s, map.get(s).getWordCount()+1, link);
                    if(getWordsCache(wordAddToDatabase) == null){
                        addWordsCache(wordAddToDatabase);
                    }
                    map.replace(s, wordAddToDatabase);
//                    sql.replaceWord(wordAddToDatabase);
                }
                else {
                    wordAddToDatabase = new Words(map.size() + 1, s, 1, link);
                    map.put(s, wordAddToDatabase);
                    addWordsCache(wordAddToDatabase);
//                    sql.addWords(wordAddToDatabase);
                }
            }
            else{
                wordAddToDatabase = new Words(1, s, 1, link);
                map.put(s, wordAddToDatabase);
                addWordsCache(wordAddToDatabase);
//                sql.addWords(wordAddToDatabase);
            }
        }
        return map;
    }
    @Override
    public Map<String, Words> returnCyrillicWords(String link) throws RemoteException, IOException {
        Map<String, Words> map = new LinkedHashMap<>();
        Map<String, Words> returnMap = new LinkedHashMap<>();
        List<Future<StringBuffer>> futures = new ArrayList<>();
        Callable<StringBuffer> callable = new Task(link);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 3; i++) {
            Future<StringBuffer> future = executorService.submit(callable);
            futures.add(future);
        }
        for(Future<StringBuffer> future : futures){
            try {
                sb = future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            map = addWord(sb, link);
            returnMap.putAll(map);
        }
        return returnMap;
//        try {
//            return addWord(new FutureTask<StringBuffer>(new Task(link)).get(), link);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }

    }

    @Override
    public List<Words> getLinkByWord(List<Words> list, String word) throws RemoteException {
        List<Words> returnList = new ArrayList<>();
        for(Words words : list){
            if(words.getWordName().equals(word)){
                returnList.add(words);
            }
        }
        Collections.sort(returnList);

        return returnList;
    }

    public static void addWordsCache(Words words){
        wordsCache.addWordsCache(words);
    }

    public static Words getWordsCache(Words words){
        if(wordsCache.getWordsCache(words) != null){
            return wordsCache.getWordsCache(words);
        }
        return null;
    }
}
