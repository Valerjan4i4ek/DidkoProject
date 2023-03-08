import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface WordsParsing extends Remote {

    Map<String, Words> returnCyrillicWords(List<String> listLinks) throws RemoteException, IOException;
    List<Words> getLinkByWord(List<Words> list, String word) throws RemoteException;
}
