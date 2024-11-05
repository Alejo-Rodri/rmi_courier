package alejandro.grpc;

import alejandro.services.MongoServices;
import alejandro.utils.Environment;
import com.grpc.demo.services.FilesRouteGrpc;
import com.grpc.demo.services.Fileserver;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class GrpcClient {
    private static final Logger logger = LoggerFactory.getLogger(GrpcClient.class);
    private final FilesRouteGrpc.FilesRouteStub asyncStub;
    private final FilesRouteGrpc.FilesRouteBlockingStub blockingStub;
    private final ManagedChannel channel;

    public GrpcClient() {
        String host = Environment.getInstance().getVariables().get("GRPC_NFS_HOST");
        System.out.println("Host: " + host);
        String port = Environment.getInstance().getVariables().get("GRPC_NFS_PORT");
        System.out.println("Port: " + port);
        logger.info("Attempting to connect to gRPC server at {}:{}", host, port);
        System.out.println("Attempting to connect to gRPC server at " + host + ":" + port);

        this.channel = ManagedChannelBuilder.forAddress(host, Integer.parseInt(port))
                .usePlaintext()
                .build();
        asyncStub = FilesRouteGrpc.newStub(channel);
        blockingStub = FilesRouteGrpc.newBlockingStub(channel);
    }

    public boolean uploadFileToNFS(byte[] fileData, String fileName, String uid) {
        try {
            StreamObserver<Fileserver.FileUploadResponse> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(Fileserver.FileUploadResponse response) {
                    logger.info("File upload status: {}", response.getFile().getName());
                }

                @Override
                public void onError(Throwable t) { logger.error("File upload failed: {}", t.getMessage()); }

                @Override
                public void onCompleted() { logger.info("File uploaded successfully {}", fileName); }
            };

            StreamObserver<Fileserver.FileUploadRequest> requestObserver = this.asyncStub.upload(responseObserver);

            MongoServices mongoServices = new MongoServices();
            String folderFingerprint = mongoServices.getFingerprint(uid);

            int chunkSize = 4096;
            int offset = 0;

            while (offset < fileData.length) {
                int bytesToSend = Math.min(chunkSize, fileData.length - offset);
                byte[] chunk = new byte[bytesToSend];
                System.arraycopy(fileData, offset, chunk, 0, bytesToSend);

                Fileserver.FileUploadRequest request = Fileserver.FileUploadRequest.newBuilder()
                        .setChunk(com.google.protobuf.ByteString.copyFrom(chunk))
                        .setFileName(fileName)
                        .setFolderFingerprint(folderFingerprint)
                        .setUsername(uid)
                        .build();

                requestObserver.onNext(request);
                offset += bytesToSend;
            }

            requestObserver.onCompleted();
            return true;
        } catch (Exception e) {
            logger.error("Error uploading the file", e);
            return false;
        }
    }

    public void ping() {
        Fileserver.PingRequest request = Fileserver.PingRequest.newBuilder().build();
        Fileserver.PingReply reply;

        try {
            reply = blockingStub.ping(request);
            logger.info("Response from ping!!! {}", reply);
            System.out.println("Response from ping!!! " + reply);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            logger.error("Error while pinging.", e);
        }
    }

    public void shutdown() {
        try {
            channel.shutdown();
            if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                channel.shutdownNow();
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.error("Channel did not terminate");
                }
            }
        } catch (InterruptedException e) {
            channel.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
