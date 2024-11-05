package alejandro.services;

import alejandro.grpc.GrpcClient;
import alejandro.interfaces.Courier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SaveInNFSImpl extends UnicastRemoteObject implements Courier {
    private static final Logger logger = LoggerFactory.getLogger(SaveInNFSImpl.class);

    public SaveInNFSImpl() throws RemoteException {
        super();
    }

    @Override
    public void saveFileInNFS(byte[] fileData, String fileName, String uid) {
        GrpcClient grpcClient = new GrpcClient();
        try {
            if (grpcClient.uploadFileToNFS(fileData, fileName, uid)) {
                logger.info("{} uploaded to nfs server correctly.", fileName);
            } else {
                logger.info("{} couldn't be uploaded to nfs server.", fileName);
            }
        } catch (Exception e) {
            logger.error("Error while saving file in nfs server", e);
        } finally {
            grpcClient.shutdown();
        }
    }

    @Override
    public void ping() throws RemoteException {
        GrpcClient grpcClient = new GrpcClient();
        try {
            grpcClient.ping();
        } catch (Exception e) {
            logger.error("Error while pinging.", e);
        } finally {
            grpcClient.shutdown();
        }
    }
}
