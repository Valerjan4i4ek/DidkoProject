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
    public static final List<String> LIST_LINKS = new ArrayList<>();
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
        Map<String, Words> map = returnCyrillicWords(LIST_LINKS);
        String word = reader.readLine();
        Map<Integer, String> treeMap = getLinkByWord(map, word);
    }

    public static Map<String, Words> returnCyrillicWords(List<String> links) throws RemoteException, IOException {
        Map<String, Words> map = null;
        List<Words> list = new ArrayList<>();
        for(String link : links){
            map = wordsParsing.returnCyrillicWords(link);
            for(Map.Entry<String, Words> entry : map.entrySet()){
                System.out.println(entry.getValue().getId() + " " + entry.getKey() + " " +  entry.getValue().getWordCount());
                list.add(entry.getValue());
            }

            System.out.println("______________________________________________________");

        }
        System.out.println("FINAL");
        System.out.println();
        for(Words words : list){
            System.out.println(words.getId() + " " + words.getWordName() + " " + words.getWordCount());
        }
        System.out.println();
        System.out.println("SECOND FINAL");

        return map;
    }

    public static Map<Integer, String> getLinkByWord(Map<String, Words> map, String word) throws RemoteException{
        Map<Integer, String> treeMap = wordsParsing.getLinkByWord(map, word);
        for(Map.Entry<Integer, String> entry : treeMap.entrySet()){
            System.out.println(entry.getValue() + " " + entry.getKey());
        }
        return treeMap;
    }

}
