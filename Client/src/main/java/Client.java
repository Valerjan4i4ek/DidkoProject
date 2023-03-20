import org.w3c.dom.ls.LSOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.stream.Collectors;

public class Client implements Serializable {
    public static final String UNIQUE_BINDING_NAME = "server.WordsParsing";
    public static final List<String> LIST_LINKS = new LinkedList<>();
    public static List<Words> list = new ArrayList<>();
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
//        List<Words> list = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    list = returnCyrillicWords(LIST_LINKS);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Add the word");
                String word = null;
//                String exit = null;
                try {
                    word = reader.readLine();
                    List<Words> linkList = getLinkByWord(list, word);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    public static List<Words> returnCyrillicWords(List<String> links) throws RemoteException, IOException {
        System.out.println("bI4");
        List<Words> list = wordsParsing.returnCyrillicWords(links);
        System.out.println("bI4 2");
        for(Words words : list){
            System.out.println(words.getId() + " " + words.getWordName() + " " + words.getWordCount() + " " + words.getLink());
        }
        System.out.println("______________________________________________________");

        return list;
    }
    public static List<Words> getLinkByWord(List<Words> list, String word) throws RemoteException{
        List<Words> currentList = wordsParsing.getLinkByWord(list, word);
//        Scanner scanner = new Scanner(System.in);
        for(Words words : currentList){
            System.out.println(words.getLink() + " " + words.getWordCount());
        }
//        System.out.println("Again Y/N");
//        if(scanner.nextLine().equalsIgnoreCase("y")){
//            getLinkByWord(list, word);
//        }
//        else{
//            System.out.println("GOODBYE");
//        }
        return currentList;
    }
}
