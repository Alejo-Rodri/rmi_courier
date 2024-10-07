package alejandro.rmi;

import alejandro.services.SaveInNFSImpl;
import alejandro.utils.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;

public class RmiServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RmiServer.class);
    private final String ip;
    private final String port;
    private final String uri;

    public RmiServer() {
        this.ip = Environment.getInstance().getVariables().get("THIS_IP");
        this.port = Environment.getInstance().getVariables().get("COURIER_PORT");
        String serviceName = Environment.getInstance().getVariables().get("COURIER_SERVICE");
        uri = "//" + this.ip + ":" + this.port + "/" + serviceName;
        logger.info("{} uri:{}", serviceName, uri);
    }

    private boolean deploy() {
        try {
            SaveInNFSImpl saveInNFS = new SaveInNFSImpl();
            System.setProperty("java.rmi.server.hostname", ip);
            LocateRegistry.createRegistry(Integer.parseInt(port));
            Naming.rebind(uri, saveInNFS);
            System.out.println("bound services" + Arrays.toString(Naming.list(uri)));
            return true;
        } catch (Exception e) {
            logger.error("Error deploying rmiServer", e);
            return false;
        }
    }

    public void run() {
        try {
            if (deploy()) logger.info("RmiServer started correctly");
        } catch (Exception e) {
            logger.error("RmiServer could not start", e);
        }
    }
}
