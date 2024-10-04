package alejandro;

import alejandro.grpc.GrpcServer;
import alejandro.rmi.RmiServer;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world:)");
        RmiServer rmiServer = new RmiServer();
        GrpcServer grpcServer = new GrpcServer();

        Thread[] threads = {
                new Thread(rmiServer),
                new Thread(grpcServer)
        };

        for (Thread thread : threads) thread.start();
    }
}