package alejandro.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Cluster extends Remote {
    void processWork(byte[] fileData, String fileName, String uid, String fingerprint) throws RemoteException;
}
