import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class TaskCallable implements Callable<List<Words>> {
    private final static String USER_AGENT = "Chrome/104.0.0.0";
    private final List<String> listLinks;
    static MySQLClass sql = new MySQLClass();
    static WordsCache wordsCache = new WordsCache();
    public TaskCallable(List<String> links) {
        this.listLinks = links;
    }
    public List<String> getListLinks() {
        return listLinks;
    }

    @Override
    public synchronized List<Words> call() throws Exception {
        return returnCyrillicWords(getListLinks());
//        return parsingCyrillicWords(getListLinks());
    }
    public synchronized static List<WordsAndLinks> getURLData(List<String> listLinks) throws IOException{
        List<WordsAndLinks> list = new LinkedList<>();
        if(listLinks != null && !listLinks.isEmpty()){
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
        }

        return list;
    }

    public synchronized List<WordsAndLinks> parsingCyrillicWords(List<String> listLinks) throws IOException{
        List<WordsAndLinks> list = getURLData(listLinks);
        List<WordsAndLinks> returnList = new LinkedList<>();

        for(WordsAndLinks wordsAndLinks : list){
            StringBuffer result = wordsAndLinks.getStringBuffer();
            StringBuffer sb = new StringBuffer();

            if(result != null && !result.isEmpty()){
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
                returnList.add(new WordsAndLinks(wordsAndLinks.getLink(), sb));
            }
        }

        return returnList;
    }
    public List<Words> returnCyrillicWords(List<String> listLinks) throws IOException{
        List<Words> returnList = new ArrayList<>();
        List<WordsAndLinks> wordsAndLinksList = parsingCyrillicWords(listLinks);

        for(WordsAndLinks wordsAndLinks : wordsAndLinksList){
            returnList.addAll(addWord(wordsAndLinks.getStringBuffer(), wordsAndLinks.getLink()));
        }
        return returnList;
    }
    public static List<Words> addWord(StringBuffer stringBuffer, String link){
        String word = stringBuffer.toString().toLowerCase();
        String[] words = word.split(" ");
        Words wordAddToDatabaseAndCache;
        List<Words> list = new ArrayList<>();
        Map<String, Words> map = new HashMap<>();

        for(String s : words){
            if(map != null && !map.isEmpty()){
                if(map.containsKey(s)){
                    wordAddToDatabaseAndCache = new Words(map.get(s).getId(), map.get(s).getWordName(),
                            map.get(s).getWordCount()+1, map.get(s).getLink());
                    map.put(s, wordAddToDatabaseAndCache);

                    if(getWordsCache(s) != null){
                        replaceWordsCache(wordAddToDatabaseAndCache);
                    }
                    else{
                        addWordsCache(wordAddToDatabaseAndCache);}
//                    sql.replaceWord(wordAddToDatabase);
                }
                else{
                    wordAddToDatabaseAndCache = new Words(map.size() + 1, s, 1, link);
                    map.put(s, wordAddToDatabaseAndCache);
                    addWordsCache(wordAddToDatabaseAndCache);
//                    sql.addWords(wordAddToDatabase);
                }
            }
            else{
                wordAddToDatabaseAndCache = new Words(1, s, 1, link);
                map.put(s, wordAddToDatabaseAndCache);
                addWordsCache(wordAddToDatabaseAndCache);
//              sql.addWords(wordAddToDatabase);
            }
        }

        for(Map.Entry<String, Words> entry : map.entrySet()){
            list.add(entry.getValue());
        }

        return list;
    }
    public static void addWordsCache(Words words){
        wordsCache.addWordsCache(words);
    }
    public static void replaceWordsCache(Words words){
        wordsCache.replaceWordsCache(words);
    }

    public static Words getWordsCache(String words){
        if(wordsCache.getWordsCache(words) != null){
            return wordsCache.getWordsCache(words);
        }
        return null;
    }
}
