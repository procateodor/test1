package services.user.models;

public class FileDTO {
    private final String path;
    private final String content;

    public FileDTO(String path, String content) {
        this.path = path;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }
}
