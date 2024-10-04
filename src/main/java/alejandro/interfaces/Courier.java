package alejandro.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Courier extends Remote {
    void saveFileInNFS(byte[] fileData, String fileName, String uid) throws RemoteException;
}
