import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;

public class RemoteWordsParsingServer extends Thread implements WordsParsing {
    private final static String USER_AGENT = "Chrome/104.0.0.0";
    public static List<String> LIST_LINKS = new LinkedList<>();
    public List<String> LIST_LINKS_FOR_PARSING = new LinkedList<>();
    static MySQLClass sql = new MySQLClass();
    static WordsCache wordsCache = new WordsCache();
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);

    Thread thread;
    static {
        LIST_LINKS.add("https://javarush.com/");
        LIST_LINKS.add("https://music.youtube.com/");
        LIST_LINKS.add("https://vertex-academy.com/tutorials/ru/samouchitel-po-java-s-nulya/");
    }

    RemoteWordsParsingServer(){
        start();
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(LIST_LINKS != null && !LIST_LINKS.isEmpty()){
                LIST_LINKS_FOR_PARSING = getListLinks();
                System.out.println("gogaBau");
            }
        }
    }

    public List<String> getListLinks(){

        List<String> list = new ArrayList<>(LIST_LINKS);
        LIST_LINKS.clear();

        return list;
    }

    public static List<Words> addWord(StringBuffer stringBuffer, String link){
        String word = stringBuffer.toString().toLowerCase();
        String[] words = word.split(" ");
        Words wordAddToDatabase;
        List<Words> list = new ArrayList<>();
        Map<String, Words> map = new HashMap<>();

        for(String s : words){
            if(map != null && !map.isEmpty()){
                if(map.containsKey(s)){
                    wordAddToDatabase = new Words(map.get(s).getId(), map.get(s).getWordName(),
                            map.get(s).getWordCount()+1, map.get(s).getLink());
                    map.put(s, wordAddToDatabase);

                    if(getWordsCache(s) != null){
                        replaceWordsCache(wordAddToDatabase);
                    }
                    else{addWordsCache(wordAddToDatabase);}
//                    sql.replaceWord(wordAddToDatabase);
                }
                else{
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
//              sql.addWords(wordAddToDatabase);
            }
        }

        for(Map.Entry<String, Words> entry : map.entrySet()){
            list.add(entry.getValue());
        }

        return list;
    }
    @Override
    public void returnCyrillicWords() throws RemoteException, IOException {

        List<Words> addList = new ArrayList<>();
        List<Words> returnList = new ArrayList<>();
        List<WordsAndLinks> list = new LinkedList<>();

        if(LIST_LINKS_FOR_PARSING != null && !LIST_LINKS_FOR_PARSING.isEmpty()){
            System.out.println("3ae6ok");
        }
        else {
            System.out.println("nu4aJIbka");
        }

//        TaskRunnable taskRunnable = new TaskRunnable(listLinks);
        TaskCallable taskCallable = new TaskCallable(LIST_LINKS_FOR_PARSING);
        Future<List<WordsAndLinks>> sub = executorService.submit(taskCallable);
//        ses.scheduleAtFixedRate(taskRunnable, 1, 1, TimeUnit.SECONDS);

        try {
            list = sub.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        for(WordsAndLinks wordsAndLinks : list){
            System.out.println("addList");
            addList = addWord(wordsAndLinks.getStringBuffer(), wordsAndLinks.getLink());
            System.out.println("returnList");
            returnList.addAll(addList);
            System.out.println("removeLink");
            LIST_LINKS_FOR_PARSING.remove(wordsAndLinks.getLink());
        }

//        ses.shutdown();

        executorService.shutdown();

//        return returnList;
    }

    @Override
    public List<Words> getLinkByWord(String word) throws RemoteException {
        List<Words> returnList = new ArrayList<>();
        for(Words words : wordsCache.getListCache()){
            if (words.getWordName().equals(word)){
                returnList.add(words);
            }
        }
        Collections.sort(returnList);

        return returnList;
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
//    public StringBuffer parsingCyrillicWords(String link) throws IOException {
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

//    @Override
//    public Map<String, Words> returnCyrillicWords(String link) throws RemoteException, IOException {
//        Map<String, Words> map = new LinkedHashMap<>();
//        Map<String, Words> returnMap = new LinkedHashMap<>();
//        List<Future<StringBuffer>> futures = new ArrayList<>();
//        Callable<StringBuffer> callable = new Task(link);
//        StringBuffer sb = new StringBuffer();
//
//        for (int i = 0; i < 3; i++) {
//            Future<StringBuffer> future = executorService.submit(callable);
//            futures.add(future);
//        }
//        for(Future<StringBuffer> future : futures){
//            try {
//                sb = future.get();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } catch (ExecutionException e) {
//                throw new RuntimeException(e);
//            }
//            map = addWord(sb, link);
//            returnMap.putAll(map);
//        }
//        return returnMap;
////        try {
////            return addWord(new FutureTask<StringBuffer>(new Task(link)).get(), link);
////        } catch (InterruptedException e) {
////            throw new RuntimeException(e);
////        } catch (ExecutionException e) {
////            throw new RuntimeException(e);
////        }
//
//    }
}
