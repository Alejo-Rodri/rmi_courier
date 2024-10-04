package alejandro.services;

import alejandro.grpc.proto.*;
import alejandro.helper.FileValidator;
import alejandro.rmi.RmiClient;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServiceImpl extends FileServiceGrpc.FileServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public void processFile(FileRequest request, StreamObserver<FileResponse> responseObserver) {
        byte[] fileContent = request.getFileData().toByteArray();
        String fileName = request.getFileName();
        String uid = request.getUid();

        FileValidator fileValidator = new FileValidator();
        if (!fileValidator.validateFile(fileContent, fileName)) {
            FileResponse response = FileResponse.newBuilder()
                    .setMessage("We can not accept the file you are trying to upload.")
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            logger.info("The file {} was not accepted to be processed", fileName);
            return;
        }

        RmiClient rmiClient = new RmiClient();
        rmiClient.processWork(fileContent, fileName, uid);

        FileResponse response = FileResponse.newBuilder()
                .setMessage("The cluster is now processing your File.")
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        logger.info("The file {} is being processed", fileName);
    }

    @Override
    public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        PingResponse response = PingResponse.newBuilder()
                .setMessage("Pong!")
                .build();

        logger.info("Pong!!!");
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
