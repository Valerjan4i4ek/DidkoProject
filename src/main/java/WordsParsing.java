import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface WordsParsing extends Remote {

//    String returnCyrillicWords(List<String> listLinks) throws RemoteException, IOException;
    void returnCyrillicWords() throws RemoteException, IOException;
    List<Words> getLinkByWord(String word) throws RemoteException;
//    Words getLinkByWord(String word) throws RemoteException;
}
