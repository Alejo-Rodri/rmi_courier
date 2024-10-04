package alejandro.grpc;

import alejandro.services.MongoServices;
import alejandro.utils.Environment;
import com.grpc.demo.services.FilesRouteGrpc;
import com.grpc.demo.services.Fileserver;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcClient {
    private static final Logger logger = LoggerFactory.getLogger(GrpcClient.class);
    private final FilesRouteGrpc.FilesRouteStub asyncStub;

    public GrpcClient() {
        String host = Environment.getInstance().getVariables().get("GRPC_NFS_HOST");
        String port = Environment.getInstance().getVariables().get("GRPC_NFS_PORT");
        ManagedChannel channel = Grpc.newChannelBuilder(host + "+" + port, InsecureChannelCredentials.create()).build();
        asyncStub = FilesRouteGrpc.newStub(channel);
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

            int chunkSize = 1024;
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
}
