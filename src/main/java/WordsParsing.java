import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface WordsParsing extends Remote {

    Map<String, Words> returnCyrillicWords(String link) throws RemoteException, IOException;
    Map<Integer, String> getLinkByWord(Map<String, Words> map, String word) throws RemoteException;
}
