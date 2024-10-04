package alejandro.rmi;

import alejandro.interfaces.Cluster;
import alejandro.utils.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RmiClient {
    private static final Logger logger = LoggerFactory.getLogger(RmiClient.class);
    private Cluster clusterInterface;

    public RmiClient() {
        try {
            String ip = Environment.getInstance().getVariables().get("CLUSTER_IP");
            String port = Environment.getInstance().getVariables().get("CLUSTER_PORT");
            String serviceName = Environment.getInstance().getVariables().get("CLUSTER_SERVICE");
            String url = "rmi://" + ip + ":" + port + "/" + serviceName;
            logger.info("url client: {}", url);
            clusterInterface = (Cluster) Naming.lookup(url);
        } catch (MalformedURLException | NotBoundException | RemoteException exception) {
            logger.error("Error creating RmiClient class", exception);
        }
    }

    public void processWork(byte[] fileData, String fileName, String uid) {
        try {
            clusterInterface.processWork(fileData, fileName, uid);
        } catch (Exception exception) {
            logger.error("Error in processWork", exception);
        }
    }
}
