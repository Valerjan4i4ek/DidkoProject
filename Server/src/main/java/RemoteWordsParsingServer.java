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
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
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

    public static List<Words> addWord(StringBuffer stringBuffer, String link){
//        Map<String, Words> map = new HashMap<>();
        String word = stringBuffer.toString().toLowerCase();
        String[] words = word.split(" ");
        Words wordAddToDatabase;
        List<Words> list = new ArrayList<>();

        for(String s : words){
            if(!list.isEmpty()){

                for(Words w : list){
                    List<Words> list2 = new ArrayList<>(list);
                    if(w.getWordName().equals(s) && w.getLink().equals(link)){
                        wordAddToDatabase = new Words(w.getId(), w.getWordName(), w.getWordCount() + 1, w.getLink());
                        if(getWordsCache(wordAddToDatabase) == null){
                            addWordsCache(wordAddToDatabase);
                        }
                        System.out.println("list.size() " + list.size());
//                        list2.remove(w.getId()-1);
                        list2.set(w.getId() - 1, wordAddToDatabase);

                        //                    sql.replaceWord(wordAddToDatabase);
                    }
                    else{
                        wordAddToDatabase = new Words(list2.size() + 1, s, 1, link);
                        list2.add(wordAddToDatabase);
                        addWordsCache(wordAddToDatabase);
                        //                    sql.addWords(wordAddToDatabase);
                    }
                    list.clear();
                    list.addAll(list2);

                }

            }
            else{
                wordAddToDatabase = new Words(1, s, 1, link);
                list.add(wordAddToDatabase);
                addWordsCache(wordAddToDatabase);
                //                sql.addWords(wordAddToDatabase);
            }
        }
        return list;
    }
    @Override
    public List<Words> returnCyrillicWords(List<String> listLinks) throws RemoteException, IOException {
//        Map<String, Words> addMap = new HashMap<>();
//        Map<String, Words> returnMap = new HashMap<>();
        List<Words> addList = new ArrayList<>();
        List<Words> returnList = new ArrayList<>();
        List<WordsAndLinks> list = new LinkedList<>();

        TaskRunnable taskRunnable = new TaskRunnable(listLinks);
        TaskCallable taskCallable = new TaskCallable(listLinks);
        ses.scheduleAtFixedRate(taskRunnable, 1, 1, TimeUnit.SECONDS);
        System.out.println("scheduledFuture");
//        executorService.submit(taskCallable);
//        System.out.println("executorService");

        Future<List<WordsAndLinks>> sub = executorService.submit(taskCallable);
        System.out.println("executorService 1");


        try {
            list = sub.get();
            System.out.println("executorService 2");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        for(WordsAndLinks wordsAndLinks : list){
            addList = addWord(wordsAndLinks.getStringBuffer(), wordsAndLinks.getLink());
            returnList.addAll(addList);
            listLinks.remove(wordsAndLinks.getLink());
        }
        System.out.println("after try catch");



        ses.shutdown();
        System.out.println("FINISHED scheduledFuture");

        executorService.shutdown();
        System.out.println("FINISHED executorService");

        return returnList;
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
