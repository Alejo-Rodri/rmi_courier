package alejandro.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Environment {
    private static Environment instance;
    private static final Logger logger = LoggerFactory.getLogger(Environment.class);
    private final HashMap<String, String> variables;

    private Environment() {
        variables = new HashMap<>();
        loadVariables(getPathProperties());
    }
    private String getPathProperties() {
        //return "src/main/resources/var.env";
        return "./var.env";
    }

    private void loadVariables(String path) {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            properties.load(fileInputStream);
            variables.put("THIS_IP", properties.getProperty("THIS_IP"));

            variables.put("COURIER_PORT", properties.getProperty("COURIER_PORT"));
            variables.put("COURIER_SERVICE", properties.getProperty("COURIER_SERVICE"));

            variables.put("CLUSTER_IP", properties.getProperty("CLUSTER_IP"));
            variables.put("CLUSTER_PORT", properties.getProperty("CLUSTER_PORT"));
            variables.put("CLUSTER_SERVICE", properties.getProperty("CLUSTER_SERVICE"));

            variables.put("GRPC_NFS_HOST", properties.getProperty("GRPC_NFS_HOST"));
            variables.put("GRPC_NFS_PORT", properties.getProperty("GRPC_NFS_PORT"));

            variables.put("THIS_GRPC_SERVER_PORT", properties.getProperty("THIS_GRPC_SERVER_PORT"));
            variables.put("THIS_GRPC_SERVER_HOST", properties.getProperty("THIS_GRPC_SERVER_HOST"));

            variables.put("MONGO_IP", properties.getProperty("MONGO_IP"));
            variables.put("MONGO_DB", properties.getProperty("MONGO_DB"));
            variables.put("MONGO_COLLECTION", properties.getProperty("MONGO_COLLECTION"));
        } catch (Exception exception) {
            logger.error("Singleton failed:c", exception);
        }
    }

    public static Environment getInstance() {
        if (Environment.instance == null) Environment.instance = new Environment();

        return Environment.instance;
    }

    public Map<String, String> getVariables() { return variables; }
}
