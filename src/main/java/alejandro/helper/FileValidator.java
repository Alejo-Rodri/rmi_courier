package alejandro.helper;

public class FileValidator {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public boolean isFileEmpty(byte[] fileData) { return fileData.length == 0; }

    public boolean isFileSizeValid(byte[] fileData) { return fileData.length <= MAX_FILE_SIZE; }

    public boolean isFileExtensionValid(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        // solo procesamos archivos .c
        return fileExtension.equalsIgnoreCase("c");
    }

    public boolean isFileNameValid(String fileName) { return !fileName.contains(" "); }

    public boolean validateFile(byte[] fileData, String fileName) {
        return isFileSizeValid(fileData) &&
                !isFileEmpty(fileData) &&
                isFileExtensionValid(fileName) &&
                isFileNameValid(fileName);
    }
}