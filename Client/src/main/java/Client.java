import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.stream.Collectors;

public class Client {
    public static final String UNIQUE_BINDING_NAME = "server.WordsParsing";
    public static final List<String> LIST_LINKS = new LinkedList<>();
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    static Registry registry;
    static WordsParsing wordsParsing;
    static {
        try{
            registry = LocateRegistry.getRegistry("127.0.0.1", 2732);
            wordsParsing = (WordsParsing) registry.lookup(UNIQUE_BINDING_NAME);
            LIST_LINKS.add("https://javarush.com/");
            LIST_LINKS.add("https://music.youtube.com/");
            LIST_LINKS.add("https://vertex-academy.com/tutorials/ru/samouchitel-po-java-s-nulya/");
        }
        catch (RemoteException | NotBoundException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws RemoteException, IOException {
        List<Words> list = returnCyrillicWords(LIST_LINKS);
        String word = reader.readLine();
        List<Words> linkList = getLinkByWord(list, word);
    }
    public static List<Words> returnCyrillicWords(List<String> links) throws RemoteException, IOException {
        Map<String, Words> map = null;
        List<Words> list = new ArrayList<>();
        map = wordsParsing.returnCyrillicWords(links);
        for(Map.Entry<String, Words> entry : map.entrySet()){
            System.out.println(entry.getValue().getId() + " " + entry.getKey() + " " +  entry.getValue().getWordCount() + " " + entry.getValue().getLink());
            list.add(entry.getValue());
        }

        System.out.println("______________________________________________________");

//        for(String link : links){
//            map = wordsParsing.returnCyrillicWords(link);
//            for(Map.Entry<String, Words> entry : map.entrySet()){
//                System.out.println(entry.getValue().getId() + " " + entry.getKey() + " " +  entry.getValue().getWordCount() + " " + entry.getValue().getLink());
//                list.add(entry.getValue());
//            }
//
//            System.out.println("______________________________________________________");
//
//        }

        return list;
    }
    public static List<Words> getLinkByWord(List<Words> list, String word) throws RemoteException{
        List<Words> currentList = wordsParsing.getLinkByWord(list, word);
        for(Words words : currentList){
            System.out.println(words.getLink() + " " + words.getWordCount());
        }
        return currentList;
    }
}
