import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

public class Server {
    private final static String fileName = "Server/src/main/resources/database.properties";
    public static String UNIQUE_BINDING_NAME = null;
    public static int port = 0;

    static {
        FileInputStream fis;
        Properties property = new Properties();

        try {
            fis = new FileInputStream(fileName);
            property.load(fis);

            UNIQUE_BINDING_NAME = property.getProperty("unique_binding_name");
            port = Integer.parseInt(property.getProperty("port"));


        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
    }

    public static void main(String[] args) throws RemoteException, AlreadyBoundException, InterruptedException {


        final RemoteWordsParsingServer server = new RemoteWordsParsingServer();

        final Registry registry = LocateRegistry.createRegistry(port);

        Remote stub = UnicastRemoteObject.exportObject(server, 0);
        registry.bind(UNIQUE_BINDING_NAME, stub);

        Thread.sleep(Integer.MAX_VALUE);
    }
}
