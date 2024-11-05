package alejandro.grpc;

import alejandro.services.FileServiceImpl;
import alejandro.utils.Environment;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class GrpcServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(GrpcServer.class);
    private Server server;

    @Override
    public void run() {
        try { start(); }
        catch (Exception e) { logger.error("gRPC server failed to start", e); }
    }

    public void start() throws Exception {
        String port = Environment.getInstance().getVariables().get("THIS_GRPC_SERVER_PORT");
        String host = Environment.getInstance().getVariables().get("THIS_GRPC_SERVER_HOST");
        server = NettyServerBuilder.forAddress(new InetSocketAddress(host, Integer.parseInt(port)))
                .addService(new FileServiceImpl())
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server...");
            GrpcServer.this.stop();
            logger.info("gRPC server shut down.");
        }));

        server.awaitTermination();
    }

    public void stop() { if (server != null) server.shutdown(); }
}
